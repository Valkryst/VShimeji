package com.group_finity.mascot;

import com.group_finity.mascot.animation.Animation;
import com.group_finity.mascot.behavior.Behavior;
import com.group_finity.mascot.config.Configuration;
import com.group_finity.mascot.environment.MascotEnvironment;
import com.group_finity.mascot.exception.BehaviorInstantiationException;
import com.group_finity.mascot.exception.CantBeAliveException;
import com.group_finity.mascot.hotspot.Hotspot;
import com.group_finity.mascot.image.MascotImage;
import com.group_finity.mascot.image.TranslucentWindow;
import com.group_finity.mascot.menu.MenuScroller;
import com.group_finity.mascot.sound.Sounds;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * Mascot object.
 * <p>
 * Mascots move using {@link Behavior}, which represents long-term and complex behavior,
 * and {@link Action}, which represents short-term and monotonous movements.
 * <p>
 * Mascots have an internal timer and call {@link Action} at regular intervals.
 * {@link Action} animates the mascot by calling {@link Animation}.
 * <p>
 * When {@link Action} ends or at other specific times, {@link Behavior} is called and moves to the next {@link Action}.
 *
 * @author Yuki Yamada of <a href="http://www.group-finity.com/Shimeji/">Group Finity</a>
 * @author Shimeji-ee Group
 * @author Valkryst
 */
@Log
public class Mascot {
    /**
     * Whether to draw the mascots' bounds and hotspots, for debugging purposes.
     */
    public static final boolean DRAW_DEBUG = false;

    /**
     * The ID of the last generated {@code Mascot}.
     */
    private static AtomicInteger lastId = new AtomicInteger();

    /**
     * The {@code Mascot}'s ID.
     * Exists only to make it easier to view debug logs.
     */
    private final int id;

    @Getter @Setter private String imageSet;

    /**
     * The window that displays the {@code Mascot}.
     */
    @Getter private final TranslucentWindow window = NativeFactory.getInstance().newTransparentWindow();

    /**
     * The {@link Manager} that manages this {@code Mascot}.
     */
    @Getter @Setter private Manager manager = null;

    /**
     * The {@code Mascot}'s ground coordinates.
     * For example, its feet or its hands when hanging.
     */
    @Getter @Setter private Point anchor = new Point(0, 0);

    /**
     * The image to display.
     */
    @Getter @Setter private MascotImage image = null;

    /**
     * Whether the {@code Mascot} is facing right.
     * The original image is treated as facing left, so setting this to {@code true} will cause it to be reversed.
     */
    @Getter @Setter private boolean lookRight = false;

    /**
     * An object that represents the long-term behavior of this {@code Mascot}.
     */
    @Getter private Behavior behavior = null;

    /**
     * <p>Time that increases every tick of the timer.</p>
     *
     * <p>
     *     While it's technically possible for this to overflow, the user would need to keep the application running
     *     for the following amount of time for it to happen:
     * </p>
     *
     * <pre>
     *     Max Integer Value: 2,147,483,647
     *     FPS: 60
     *
     *     2,147,483,647 / 60 = ~35,791,394.1 seconds
     *     ~35,791,394.1 / 60 = ~596,523.2 minutes
     *     ~596,523.2 / 60 = ~9,942.0 hours
     *     ~9,942.0 / 24 = ~414.2 days
     * </pre>
     */
    @Getter @Setter private int time = 0;

    /**
     * Whether the animation is running.
     */
    @Getter @Setter private boolean animating = true;

    @Getter @Setter private boolean paused = false;

    /**
     * Set by behaviours when the {@code Mascot} is being dragged by the mouse cursor,
     * as opposed to hotspots or the like.
     */
    @Getter @Setter private boolean dragging = false;

    /**
     * Mascot display environment.
     */
    @Getter private MascotEnvironment environment = new MascotEnvironment(this);

    @Getter @Setter private String sound = null;

    protected DebugWindow debugWindow = null;

    @Getter private final List<String> affordances = new ArrayList<>(5);

    @Getter private final List<Hotspot> hotspots = new ArrayList<>(5);

    /**
     * Set by behaviours when the user has triggered a hotspot on this {@code Mascot},
     * so that the {@code Mascot} knows to check for any new hotspots that emerge while
     * the mouse is held down.
     */
    private Point cursor = null;

    public Mascot(final String imageSet) {
        id = lastId.incrementAndGet();
        this.imageSet = imageSet;

        log.log(Level.INFO, "Created mascot \"{0}\" with image set \"{1}\"", new Object[]{this, imageSet});

        // Always show on top
        getWindow().setAlwaysOnTop(true);

        // Register the mouse handler
        getWindow().asComponent().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                Mascot.this.mousePressed(e);
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                Mascot.this.mouseReleased(e);
            }
        });
        getWindow().asComponent().addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(final MouseEvent e) {
                if (paused) {
                    refreshCursor(false);
                } else {
                    if (isHotspotClicked()) {
                        setCursorPosition(e.getPoint());
                    } else {
                        refreshCursor(e.getPoint());
                    }
                }
            }

            @Override
            public void mouseDragged(final MouseEvent e) {
                if (paused) {
                    refreshCursor(false);
                } else {
                    if (isHotspotClicked()) {
                        setCursorPosition(e.getPoint());
                    } else {
                        refreshCursor(e.getPoint());
                    }
                }
            }
        });

        if (DRAW_DEBUG) {
            // For drawing the outlines of hotspots and the mascot's bounds, for debugging purposes
            JComponent debugComp = new JComponent() {
                @Override
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    // Draw hotspots
                    g.setColor(Color.BLUE);
                    Dimension imageSize = getImage().getSize();
                    for (Hotspot hotspot : getHotspots()) {
                        Shape shape = hotspot.getShape();
                        if (shape instanceof Rectangle) {
                            Rectangle rectangle = (Rectangle) shape;
                            int x = lookRight ? imageSize.width - rectangle.x - rectangle.width : rectangle.x;
                            g.drawRect(x, rectangle.y, rectangle.width, rectangle.height);
                        } else if (shape instanceof Ellipse2D) {
                            Ellipse2D ellipse = (Ellipse2D) shape;
                            double x = lookRight ? imageSize.width - ellipse.getX() - ellipse.getWidth() : ellipse.getX();
                            g.drawOval((int) x, (int) ellipse.getY(), (int) ellipse.getWidth(), (int) ellipse.getHeight());
                        }
                    }

                    // Draw bounds
                    g.setColor(Color.RED);
                    Rectangle bounds = getBounds();
                    g.drawRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);

                    // Draw image anchor
                    g.setColor(Color.GREEN);
                    Point imageAnchor = getImage().getCenter();
                    // Because the image anchor is a single point, it is drawn as a circle and several lines for visibility
                    g.drawOval(imageAnchor.x - 5, imageAnchor.y - 5, 10, 10);
                    g.drawLine(imageAnchor.x - 10, imageAnchor.y, imageAnchor.x + 10, imageAnchor.y);
                    g.drawLine(imageAnchor.x, imageAnchor.y - 10, imageAnchor.x, imageAnchor.y + 10);
                    g.drawLine(imageAnchor.x - 10, imageAnchor.y - 10, imageAnchor.x + 10, imageAnchor.y + 10);
                    g.drawLine(imageAnchor.x - 10, imageAnchor.y + 10, imageAnchor.x + 10, imageAnchor.y - 10);
                }
            };
            debugComp.setBackground(new Color(0, 0, 0, 0));
            debugComp.setOpaque(false);
            debugComp.setPreferredSize(getWindow().asComponent().getPreferredSize());
            getWindow().asComponent().addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    super.componentResized(e);
                    debugComp.setPreferredSize(e.getComponent().getPreferredSize());
                }
            });
            ((Container) getWindow().asComponent()).add(debugComp);
        }
    }

    @Override
    public String toString() {
        return "mascot" + id;
    }

    private void mousePressed(final MouseEvent event) {
        // Check for popup triggers in both mousePressed and mouseReleased
        // because it works differently on different systems
        if (event.isPopupTrigger()) {
            SwingUtilities.invokeLater(() -> showPopup(event.getX(), event.getY()));
        } else {
            // Switch to drag animation when mouse is pressed
            if (!isPaused() && getBehavior() != null) {
                try {
                    getBehavior().mousePressed(event);
                } catch (final CantBeAliveException e) {
                    log.log(Level.SEVERE, "Severe error in mouse press handler for mascot \"" + this + "\"", e);
                    Main.showError(Main.getInstance().getLanguageBundle().getString("SevereShimejiErrorErrorMessage") + "\n" + e.getMessage() + "\n" + Main.getInstance().getLanguageBundle().getString("SeeLogForDetails"));
                    dispose();
                }
            }
        }
    }

    private void mouseReleased(final MouseEvent event) {
        // Check for popup triggers in both mousePressed and mouseReleased
        // because it works differently on different systems
        if (event.isPopupTrigger()) {
            SwingUtilities.invokeLater(() -> showPopup(event.getX(), event.getY()));
        } else {
            if (!isPaused() && getBehavior() != null) {
                try {
                    getBehavior().mouseReleased(event);
                } catch (final CantBeAliveException e) {
                    log.log(Level.SEVERE, "Severe error in mouse release handler for mascot \"" + this + "\"", e);
                    Main.showError(Main.getInstance().getLanguageBundle().getString("SevereShimejiErrorErrorMessage") + "\n" + e.getMessage() + "\n" + Main.getInstance().getLanguageBundle().getString("SeeLogForDetails"));
                    dispose();
                }
            }
        }
    }

    private void showPopup(final int x, final int y) {
        final JPopupMenu popup = new JPopupMenu();
        final ResourceBundle languageBundle = Main.getInstance().getLanguageBundle();

        popup.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuCanceled(final PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
                setAnimating(true);
            }

            @Override
            public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
                setAnimating(false);
            }
        });

        // "Another One!" menu item
        final JMenuItem increaseMenu = new JMenuItem(languageBundle.getString("CallAnother"));
        increaseMenu.addActionListener(event -> Main.getInstance().createMascot(imageSet));

        // "Bye Bye!" menu item
        final JMenuItem disposeMenu = new JMenuItem(languageBundle.getString("Dismiss"));
        disposeMenu.addActionListener(e -> dispose());

        // "Follow Mouse!" menu item
        final JMenuItem gatherMenu = new JMenuItem(languageBundle.getString("FollowCursor"));
        gatherMenu.addActionListener(event -> getManager().setBehaviorAll(Main.getInstance().getConfiguration(imageSet), Main.BEHAVIOR_GATHER, imageSet));

        // "Reduce to One!" menu item
        final JMenuItem oneMenu = new JMenuItem(languageBundle.getString("DismissOthers"));
        oneMenu.addActionListener(event -> getManager().remainOne(imageSet, this));

        // "Reduce to One!" menu item
        final JMenuItem onlyOneMenu = new JMenuItem(languageBundle.getString("DismissAllOthers"));
        onlyOneMenu.addActionListener(event -> getManager().remainOne(this));

        // "Restore IE!" menu item
        final JMenuItem restoreMenu = new JMenuItem(languageBundle.getString("RestoreWindows"));
        restoreMenu.addActionListener(event -> NativeFactory.getInstance().getEnvironment().restoreIE());

        // Debug menu item
        final JMenuItem debugMenu = new JMenuItem(languageBundle.getString("RevealStatistics"));
        debugMenu.addActionListener(event -> {
            if (debugWindow == null) {
                debugWindow = new DebugWindow(id);

                debugWindow.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(final WindowEvent e) {
                        debugWindow = null;
                    }
                });
            }
        });

        // "Bye Everyone!" menu item
        final JMenuItem closeMenu = new JMenuItem(languageBundle.getString("DismissAll"));
        closeMenu.addActionListener(e -> Main.getInstance().exit());

        // "Paused" Menu item
        final JMenuItem pauseMenu = new JMenuItem(isAnimating() ? languageBundle.getString("PauseAnimations") : languageBundle.getString("ResumeAnimations"));
        pauseMenu.addActionListener(event -> setPaused(!isPaused()));

        // Add the Behaviors submenu. It is currently slightly buggy; sometimes the menu ghosts.
        // JLongMenu submenu = new JLongMenu(languageBundle.getString("SetBehaviour"), 30);
        JMenu submenu = new JMenu(languageBundle.getString("SetBehaviour"));
        JMenu allowedSubmenu = new JMenu(languageBundle.getString("AllowedBehaviours"));
        submenu.setAutoscrolls(true);
        JMenuItem item;
        JCheckBoxMenuItem toggleItem;
        final Configuration config = Main.getInstance().getConfiguration(getImageSet());
        for (String behaviorName : config.getBehaviorNames()) {
            final String command = behaviorName;
            try {
                if (!config.isBehaviorHidden(command)) {
                    String caption = behaviorName.replaceAll("([a-z])(IE)?([A-Z])", "$1 $2 $3").replaceAll(" {2}", " ");
                    if (config.isBehaviorEnabled(command, this) && !command.contains("/")) {
                        item = new JMenuItem(languageBundle.containsKey(behaviorName) ?
                                languageBundle.getString(behaviorName) :
                                caption);
                        item.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                try {
                                    setBehavior(config.buildBehavior(command));
                                } catch (BehaviorInstantiationException | CantBeAliveException ex) {
                                    // TODO Determine whether this catch block is supposed to dispose of the mascot
                                    log.log(Level.SEVERE, "Failed to set behavior to \"" + command + "\" for mascot \"" + this + "\"", ex);
                                    Main.showError(languageBundle.getString("CouldNotSetBehaviourErrorMessage") + "\n" + ex.getMessage() + "\n" + languageBundle.getString("SeeLogForDetails"));
                                }
                            }
                        });
                        submenu.add(item);
                    }
                    if (config.isBehaviorToggleable(command) && !command.contains("/")) {
                        toggleItem = new JCheckBoxMenuItem(caption, config.isBehaviorEnabled(command, this));
                        toggleItem.addItemListener(e -> Main.getInstance().setMascotBehaviorEnabled(command, this, !config.isBehaviorEnabled(command, this)));
                        allowedSubmenu.add(toggleItem);
                    }
                }
            } catch (RuntimeException e) {
                // just skip if something goes wrong
            }
        }
        // Create the MenuScroller after adding all the items to the submenu, so it is positioned correctly when first shown.
        MenuScroller.setScrollerFor(submenu, 30, 125);
        MenuScroller.setScrollerFor(allowedSubmenu, 30, 125);

        popup.add(increaseMenu);
        popup.addSeparator();
        popup.add(gatherMenu);
        popup.add(restoreMenu);
        popup.add(debugMenu);
        popup.addSeparator();
        if (submenu.getMenuComponentCount() > 0) {
            popup.add(submenu);
        }
        if (allowedSubmenu.getMenuComponentCount() > 0) {
            popup.add(allowedSubmenu);
        }
        // Only add a second separator if either menu has a component count greater than 0. Just in case!
        if (submenu.getMenuComponentCount() > 0 || allowedSubmenu.getMenuComponentCount() > 0) {
            popup.addSeparator();
        }
        popup.add(pauseMenu);
        popup.addSeparator();
        popup.add(disposeMenu);
        popup.add(oneMenu);
        popup.add(onlyOneMenu);
        popup.add(closeMenu);

        // TODO Get the popup to close when clicking outside of it
        getWindow().asComponent().requestFocus();

        // Lightweight popups expect the shimeji window to draw them if they fall inside the shimeji window's boundary.
        // As the shimeji window can't support this, we need to set them to heavyweight.
        popup.setLightWeightPopupEnabled(false);
        popup.show(getWindow().asComponent(), x, y);
    }

    void tick() {
        synchronized (this) {
            if (isAnimating()) {
                if (behavior != null) {
                    try {
                        behavior.next();
                    } catch (final CantBeAliveException e) {
                        log.log(Level.SEVERE, "Could not get next behavior for mascot \"" + this + "\"", e);
                        Main.showError(Main.getInstance().getLanguageBundle().getString("CouldNotGetNextBehaviourErrorMessage") + "\n" + e.getMessage() + "\n" + Main.getInstance().getLanguageBundle().getString("SeeLogForDetails"));
                        dispose();
                    }

                    time++;
                }

                if (debugWindow != null) {
                    debugWindow.update(
                        behavior,
                        anchor,
                        environment.getWorkArea(),
                        environment.getActiveIETitle(),
                        environment.getActiveIE()
                    );
                }
            }
        }
    }

    public void apply() {
        if (!this.isAnimating()) {
            return;
        }

        if (image != null) {
            this.window.asComponent().setBounds(this.getBounds()); // Set the bounds of the window to the mascot's bounds
            window.updateImage(); // Redraw
        }

        // play sound if requested
        if (!Sounds.isMuted() && sound != null && Sounds.contains(sound)) {
            synchronized (log) {
                Clip clip = Sounds.getSound(sound);
                if (!clip.isRunning()) {
                    clip.stop();
                    clip.setMicrosecondPosition(0);
                    clip.start();
                }
            }
        }
    }

    public void dispose() {
        synchronized (this) {
            log.log(Level.INFO, "Destroying mascot \"{0}\"", this);

            if (debugWindow != null) {
                debugWindow.dispose();
                debugWindow = null;
            }

            animating = false;
            getWindow().dispose();
            getAffordances().clear();
            if (getManager() != null) {
                getManager().remove(this);
            }
        }
    }

    private void refreshCursor(Point position) {
        synchronized (getHotspots()) {
            boolean useHand = hotspots.stream().anyMatch(hotspot -> hotspot.contains(this, position) &&
                    Main.getInstance().getConfiguration(imageSet).isBehaviorEnabled(hotspot.getBehaviour(), this));

            refreshCursor(useHand);
        }
    }

    private void refreshCursor(Boolean useHand) {
        getWindow().asComponent().setCursor(Cursor.getPredefinedCursor(useHand ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
    }

    public void setImage(final MascotImage image) {
        if (this.image == null && image == null) {
            return;
        }

        if (this.image != null && image != null && this.image.getImage().equals(image.getImage())) {
            return;
        }

        this.image = image;

        final var windowComponent = window.asComponent();
        if (image == null) {
            windowComponent.setVisible(false);
            return;
        }

        window.setImage(image.getImage());
        windowComponent.setVisible(true);
        window.updateImage();
    }

    public Rectangle getBounds() {
        if (getImage() != null) {
            // Find the window area from the ground coordinates and image center coordinates. The center has already been adjusted for scaling.
            final int top = getAnchor().y - getImage().getCenter().y;
            final int left = getAnchor().x - getImage().getCenter().x;

            return new Rectangle(left, top, getImage().getSize().width, getImage().getSize().height);
        } else {
            // as we have no image let's return what we were last frame
            return getWindow().asComponent().getBounds();
        }
    }

    public void setBehavior(final Behavior behavior) throws CantBeAliveException {
        this.behavior = behavior;
        this.behavior.init(this);
    }

    public boolean isHotspotClicked() {
        return cursor != null;
    }

    public Point getCursorPosition() {
        return cursor;
    }

    public void setCursorPosition(final Point point) {
        cursor = point;

        if (point == null) {
            refreshCursor(false);
        } else {
            refreshCursor(point);
        }
    }
}
