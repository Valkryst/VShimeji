package com.group_finity.mascot.menu;

import com.group_finity.mascot.Main;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * A class that provides scrolling capabilities to a long menu dropdown or popup
 * menu. A number of items can optionally be frozen at the top and/or bottom of
 * the menu.
 * <p>
 * <b>Implementation note:</b> The default number of items to display at a time
 * is 15, and the default scrolling interval is 125 milliseconds.
 *
 * @version 1.5.0 04/05/12
 * @author Darryl
 * @version 1.5.1 07/20/17 - added scrollwheel support
 * @author George Warner
 */
// NOTE: Provided by Darryl Burke from <https://tips4java.wordpress.com/2009/02/01/menu-scroller/>
// (Thank you DarrylB! ;-)
public class MenuScroller
        implements MouseWheelListener {

    // private JMenu menu;
    private JPopupMenu menu;
    private Component[] menuItems;
    private final MenuScrollerItem upItem;
    private final MenuScrollerItem downItem;
    private final MenuScrollerPopupMenuListener menuScrollerPopupMenuListener = new MenuScrollerPopupMenuListener();
    private final MenuScrollerMenuKeyListener menuScrollerMenuKeyListener = new MenuScrollerMenuKeyListener();
    /** The number of items in the scrolling portion of the menu. */
    @Getter private int scrollCount;
    /** The scroll interval in milliseconds. */
    @Getter private int interval;
    /** The number of items fixed at the top of the menu or popup menu. */
    @Getter private int topFixedCount;
    /** The number of items fixed at the bottom of the menu or popup menu. */
    @Getter @Setter private int bottomFixedCount;
    private int firstIndex = 0;
    private int keepVisibleIndex = -1;

//     private Component lastFocused = null;

    /**
     * Register a menu to be scrolled with the default number of items to
     * display at a time and the default scrolling interval.
     *
     * @param menu the menu
     * @return the MenuScroller
     */
    public static MenuScroller setScrollerFor(JMenu menu) {
        return new MenuScroller(menu);
    }

    /**
     * Register a popup menu to be scrolled with the default number of items to
     * display at a time and the default scrolling interval.
     *
     * @param menu the popup menu
     * @return the MenuScroller
     */
    public static MenuScroller setScrollerFor(JPopupMenu menu) {
        return new MenuScroller(menu);
    }

    /**
     * Register a menu to be scrolled with the default number of items to
     * display at a time and the specified scrolling interval.
     *
     * @param menu        the menu
     * @param scrollCount the number of items to display at a time
     * @return the MenuScroller
     * @throws IllegalArgumentException if scrollCount is 0 or negative
     */
    public static MenuScroller setScrollerFor(JMenu menu, int scrollCount) {
        return new MenuScroller(menu, scrollCount);
    }

    /**
     * Register a popup menu to be scrolled with the default number of items to
     * display at a time and the specified scrolling interval.
     *
     * @param menu        the popup menu
     * @param scrollCount the number of items to display at a time
     * @return the MenuScroller
     * @throws IllegalArgumentException if scrollCount is 0 or negative
     */
    public static MenuScroller setScrollerFor(JPopupMenu menu, int scrollCount) {
        return new MenuScroller(menu, scrollCount);
    }

    /**
     * Register a menu to be scrolled, with the specified number of items to
     * display at a time and the specified scrolling interval.
     *
     * @param menu        the menu
     * @param scrollCount the number of items to be displayed at a time
     * @param interval    the scroll interval, in milliseconds
     * @return the MenuScroller
     * @throws IllegalArgumentException if scrollCount or interval is 0 or
     *                                  negative
     */
    public static MenuScroller setScrollerFor(JMenu menu, int scrollCount, int interval) {
        return new MenuScroller(menu, scrollCount, interval);
    }

    /**
     * Register a popup menu to be scrolled, with the specified number of items
     * to display at a time and the specified scrolling interval.
     *
     * @param menu        the popup menu
     * @param scrollCount the number of items to be displayed at a time
     * @param interval    the scroll interval, in milliseconds
     * @return the MenuScroller
     * @throws IllegalArgumentException if scrollCount or interval is 0 or
     *                                  negative
     */
    public static MenuScroller setScrollerFor(JPopupMenu menu, int scrollCount, int interval) {
        return new MenuScroller(menu, scrollCount, interval);
    }

    /**
     * Register a menu to be scrolled, with the specified number of items to
     * display in the scrolling region, the specified scrolling interval, and
     * the specified numbers of items fixed at the top and bottom of the menu.
     *
     * @param menu             the menu
     * @param scrollCount      the number of items to display in the scrolling
     *                         portion
     * @param interval         the scroll interval, in milliseconds
     * @param topFixedCount    the number of items to fix at the top. May be 0.
     * @param bottomFixedCount the number of items to fix at the bottom. May be
     *                         0.
     * @throws IllegalArgumentException if scrollCount or interval is 0 or
     *                                  negative or if topFixedCount or
     *                                  bottomFixedCount is negative
     * @return the MenuScroller
     */
    public static MenuScroller setScrollerFor(JMenu menu, int scrollCount, int interval,
                                              int topFixedCount, int bottomFixedCount) {
        return new MenuScroller(menu, scrollCount, interval,
                topFixedCount, bottomFixedCount);
    }

    /**
     * Register a popup menu to be scrolled, with the specified number of items
     * to display in the scrolling region, the specified scrolling interval, and
     * the specified numbers of items fixed at the top and bottom of the popup
     * menu.
     *
     * @param menu             the popup menu
     * @param scrollCount      the number of items to display in the scrolling
     *                         portion
     * @param interval         the scroll interval, in milliseconds
     * @param topFixedCount    the number of items to fix at the top. May be 0.
     * @param bottomFixedCount the number of items to fix at the bottom. May be
     *                         0.
     * @throws IllegalArgumentException if scrollCount or interval is 0 or
     *                                  negative or if topFixedCount or
     *                                  bottomFixedCount is negative
     * @return the MenuScroller
     */
    public static MenuScroller setScrollerFor(JPopupMenu menu, int scrollCount, int interval,
                                              int topFixedCount, int bottomFixedCount) {
        return new MenuScroller(menu, scrollCount, interval,
                topFixedCount, bottomFixedCount);
    }

    /**
     * Construct a {@code MenuScroller} that scrolls a menu with the
     * default number of items to display at a time, and default scrolling
     * interval.
     *
     * @param menu the menu
     */
    public MenuScroller(JMenu menu) {
        this(menu, 15);
    }

    /**
     * Construct a {@code MenuScroller} that scrolls a popup menu with the
     * default number of items to display at a time, and default scrolling
     * interval.
     *
     * @param menu the popup menu
     */
    public MenuScroller(JPopupMenu menu) {
        this(menu, 15);
    }

    /**
     * Construct a {@code MenuScroller} that scrolls a menu with the
     * specified number of items to display at a time, and default scrolling
     * interval.
     *
     * @param menu        the menu
     * @param scrollCount the number of items to display at a time
     * @throws IllegalArgumentException if scrollCount is 0 or negative
     */
    public MenuScroller(JMenu menu, int scrollCount) {
        this(menu, scrollCount, 150);
    }

    /**
     * Construct a {@code MenuScroller} that scrolls a popup menu with the
     * specified number of items to display at a time, and default scrolling
     * interval.
     *
     * @param menu        the popup menu
     * @param scrollCount the number of items to display at a time
     * @throws IllegalArgumentException if scrollCount is 0 or negative
     */
    public MenuScroller(JPopupMenu menu, int scrollCount) {
        this(menu, scrollCount, 150);
    }

    /**
     * Construct a {@code MenuScroller} that scrolls a menu with the
     * specified number of items to display at a time, and specified scrolling
     * interval.
     *
     * @param menu        the menu
     * @param scrollCount the number of items to display at a time
     * @param interval    the scroll interval, in milliseconds
     * @throws IllegalArgumentException if scrollCount or interval is 0 or
     *                                  negative
     */
    public MenuScroller(JMenu menu, int scrollCount, int interval) {
        this(menu, scrollCount, interval, 0, 0);
    }

    /**
     * Construct a {@code MenuScroller} that scrolls a popup menu with the
     * specified number of items to display at a time, and specified scrolling
     * interval.
     *
     * @param menu        the popup menu
     * @param scrollCount the number of items to display at a time
     * @param interval    the scroll interval, in milliseconds
     * @throws IllegalArgumentException if scrollCount or interval is 0 or
     *                                  negative
     */
    public MenuScroller(JPopupMenu menu, int scrollCount, int interval) {
        this(menu, scrollCount, interval, 0, 0);
    }

    /**
     * Construct a {@code MenuScroller} that scrolls a menu with the
     * specified number of items to display in the scrolling region, the
     * specified scrolling interval, and the specified numbers of items fixed at
     * the top and bottom of the menu.
     *
     * @param menu             the menu
     * @param scrollCount      the number of items to display in the scrolling
     *                         portion
     * @param interval         the scroll interval, in milliseconds
     * @param topFixedCount    the number of items to fix at the top. May be 0.
     * @param bottomFixedCount the number of items to fix at the bottom. May be
     *                         0.
     * @throws IllegalArgumentException if scrollCount or interval is 0 or
     *                                  negative or if topFixedCount or
     *                                  bottomFixedCount is negative
     */
    public MenuScroller(JMenu menu, int scrollCount, int interval,
                        int topFixedCount, int bottomFixedCount) {
        this(menu.getPopupMenu(), scrollCount, interval, topFixedCount, bottomFixedCount);
    }

    /**
     * Construct a {@code MenuScroller} that scrolls a popup menu with the
     * specified number of items to display in the scrolling region, the
     * specified scrolling interval, and the specified numbers of items fixed at
     * the top and bottom of the popup menu.
     *
     * @param menu             the popup menu
     * @param scrollCount      the number of items to display in the scrolling
     *                         portion
     * @param interval         the scroll interval, in milliseconds
     * @param topFixedCount    the number of items to fix at the top. May be 0.
     * @param bottomFixedCount the number of items to fix at the bottom. May be
     *                         0.
     * @throws IllegalArgumentException if scrollCount or interval is 0 or
     *                                  negative or if topFixedCount or
     *                                  bottomFixedCount is negative
     */
    public MenuScroller(JPopupMenu menu, int scrollCount, int interval,
                        int topFixedCount, int bottomFixedCount) {
        if (scrollCount <= 0 || interval <= 0) {
            throw new IllegalArgumentException(Main.getInstance().getLanguageBundle().getString("ScrollCountIntervalBelowZeroErrorMessage"));
        }
        if (topFixedCount < 0 || bottomFixedCount < 0) {
            throw new IllegalArgumentException(Main.getInstance().getLanguageBundle().getString("CountsCannotBeNegativeErrorMessage"));
        }

        upItem = new MenuScrollerItem(MenuScrollerIcon.UP, -1);
        downItem = new MenuScrollerItem(MenuScrollerIcon.DOWN, +1);
        setScrollCount(scrollCount);
        setInterval(interval);
        setTopFixedCount(topFixedCount);
        setBottomFixedCount(bottomFixedCount);

        this.menu = menu;

        installListeners();

        // Ensure that the menu's preferred size has been recalculated before the menu is first shown;
        // otherwise, it will initially be in the wrong position.
        setMenuItems();
        restoreMenuItems();
    }

    private void installListeners() {

        // remove all menu key listeners
        for (MenuKeyListener mkl : menu.getMenuKeyListeners()) {
            menu.removeMenuKeyListener(mkl);
        }

        // add our menu key listener
        menu.addMenuKeyListener(menuScrollerMenuKeyListener);

// Dead code commented out 2021-12-13
//        if (false) {    // not working
//        KeyListenerInstaller.installKeyListenerOnAllComponents(new KeyAdapter() {
//            @Override
//            public void keyTyped(KeyEvent e) {
//                int keyCode = e.getKeyCode();
//                log.debug("keyTyped(" + keyCode + ")");
//            }
//        }, menu);
//
//             menu.addKeyListener(new KeyAdapter() {
//                 @Override
//                 public void keyTyped(KeyEvent e) {
//                     int keyCode = e.getKeyCode();
//                     log.debug("keyTyped({})", keyCode);
//                 }
//             });
//
//         }

        // add a Popup Menu listener
        menu.addPopupMenuListener(menuScrollerPopupMenuListener);

        // add my mouse wheel listener
        // (so mouseWheelMoved (below) will be called)
        menu.addMouseWheelListener(this);

//         lastFocused = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
//         if (menu.isFocusable()) {
//             menu.requestFocus();
//         }
    }

    private void removeListeners() {
//         lastFocused.requestFocus();
//         menu.removeMenuKeyListener(menuScrollerMenuKeyListener);
        menu.removePopupMenuListener(menuScrollerPopupMenuListener);
        menu.removeMouseWheelListener(this);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // compute how much to scroll the menu
        int amount = e.getScrollAmount() * e.getWheelRotation();
        firstIndex += amount;
        if (menuItems.length > topFixedCount + scrollCount + bottomFixedCount) {
            refreshMenu();
        }
        e.consume();
    }

    /**
     * Set the scroll interval in milliseconds.
     *
     * @param interval the scroll interval in milliseconds
     * @throws IllegalArgumentException if interval is 0 or negative
     */
    public void setInterval(int interval) {
        if (interval <= 0) {
            throw new IllegalArgumentException(Main.getInstance().getLanguageBundle().getString("IntervalBelowZeroErrorMessage"));
        }
        upItem.setInterval(interval);
        downItem.setInterval(interval);
        this.interval = interval;
    }

    /**
     * Set the number of items in the scrolling portion of the menu.
     *
     * @param scrollCount the number of items to display at a time
     * @throws IllegalArgumentException if scrollCount is 0 or negative
     */
    public void setScrollCount(int scrollCount) {
        if (scrollCount <= 0) {
            throw new IllegalArgumentException(Main.getInstance().getLanguageBundle().getString("ScrollCountErrorMessage"));
        }
        this.scrollCount = scrollCount;
        MenuSelectionManager.defaultManager().clearSelectedPath();
    }

    /**
     * Set the number of items to fix at the top of the menu or popup menu.
     *
     * @param topFixedCount the number of items
     */
    public void setTopFixedCount(int topFixedCount) {
        if (firstIndex <= topFixedCount) {
            firstIndex = topFixedCount;
        } else {
            firstIndex += topFixedCount - this.topFixedCount;
        }
        this.topFixedCount = topFixedCount;
    }

    /**
     * Scroll the specified item into view each time the menu is opened. Call
     * this method with {@code null} to restore the default behavior, which
     * is to show the menu as it last appeared.
     *
     * @param item the item to keep visible
     * @see #keepVisible(int)
     */
    public void keepVisible(JMenuItem item) {
        if (item == null) {
            keepVisibleIndex = -1;
        } else {
            keepVisibleIndex = menu.getComponentIndex(item);
        }
    }

    /**
     * Scroll the item at the specified index into view each time the menu is
     * opened. Call this method with {@code -1} to restore the default
     * behavior, which is to show the menu as it last appeared.
     *
     * @param index the index of the item to keep visible
     * @see #keepVisible(JMenuItem)
     */
    public void keepVisible(int index) {
        keepVisibleIndex = index;
    }

    /**
     * Remove this MenuScroller from the associated menu and restores the
     * default behavior of the menu.
     */
    public void dispose() {
        if (menu != null) {
            removeListeners();
            menu = null;
        }
    }

    /**
     * Ensure that the {@link #dispose()} method of this MenuScroller is
     * called when there are no more references to it.
     *
     * @throws Throwable if an error occurs.
     * @see #dispose()
     */
    @Override
    @SuppressWarnings("deprecation") // Object.finalize
    protected void finalize() throws Throwable {
        dispose();
    }

    private void refreshMenu() {
        if (menuItems != null && menuItems.length > 0) {
            firstIndex = Math.max(topFixedCount, firstIndex);
            firstIndex = Math.min(menuItems.length - bottomFixedCount - scrollCount, firstIndex);

            upItem.setEnabled(firstIndex > topFixedCount);
            downItem.setEnabled(firstIndex + scrollCount < menuItems.length - bottomFixedCount);

            menu.removeAll();
            for (int i = 0; i < topFixedCount; i++) {
                menu.add(menuItems[i]);
            }
            if (topFixedCount > 0) {
                menu.addSeparator();
            }

            menu.add(upItem);
            for (int i = firstIndex; i < scrollCount + firstIndex; i++) {
                menu.add(menuItems[i]);
            }
            menu.add(downItem);

            if (bottomFixedCount > 0) {
                menu.addSeparator();
            }
            for (int i = menuItems.length - bottomFixedCount; i < menuItems.length; i++) {
                menu.add(menuItems[i]);
            }

            int maxPreferredWidth = 0;
            for (Component item : menuItems) {
                maxPreferredWidth = Math.max(maxPreferredWidth, item.getPreferredSize().width);
            }
            menu.setPreferredSize(new Dimension(maxPreferredWidth, menu.getPreferredSize().height));

            Container cont = upItem.getParent();
            if (cont instanceof JComponent) {
                cont.revalidate();
                cont.repaint();
            }
        }
    }

    private void setMenuItems() {
        menuItems = menu.getComponents();

        // Dead code commented out 2021-12-13
        // if (false) {    // not working
        //     for (Component c : menuItems) {
        //         JMenuItem jmi = (JMenuItem) c;
        //         // remove all menu key listeners
        //         for (MenuKeyListener mkl : jmi.getMenuKeyListeners()) {
        //             jmi.removeMenuKeyListener(mkl);
        //         }
        //         jmi.addMenuKeyListener(menuScrollerMenuKeyListener);
        //     }
        // }

        if (keepVisibleIndex >= topFixedCount
                && keepVisibleIndex <= menuItems.length - bottomFixedCount
                && (keepVisibleIndex > firstIndex + scrollCount
                || keepVisibleIndex < firstIndex)) {
            firstIndex = Math.min(firstIndex, keepVisibleIndex);
            firstIndex = Math.max(firstIndex, keepVisibleIndex - scrollCount + 1);
        }
        if (menuItems.length > topFixedCount + scrollCount + bottomFixedCount) {
            refreshMenu();
        }
    }

    private void restoreMenuItems() {
        menu.removeAll();
        for (Component c : menuItems) {

            // Dead code commented out 2021-12-13
            // if (false) {    // not working
            //     JMenuItem jmi = (JMenuItem) c;
            //    jmi.removeMenuKeyListener(menuScrollerMenuKeyListener);
            // }

            menu.add(c);
        }
    }

    private class MenuScrollerPopupMenuListener implements PopupMenuListener {

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            setMenuItems();
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            restoreMenuItems();
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            restoreMenuItems();
        }
    }

    private class MenuScrollerTimer extends Timer {

        public MenuScrollerTimer(final int increment, int delay) {
            // NOTE: This ActionListener needs to be an anonymous class instead of lambda when on Java 8, due to some
            // sort of compilation bug
            super(delay, e -> {
                firstIndex += increment;
                refreshMenu();
            });
        }
    }

    private class MenuScrollerItem extends JMenuItem
            implements ChangeListener {

        private final MenuScrollerTimer timer;

        public MenuScrollerItem(MenuScrollerIcon icon, int increment) {
            setIcon(icon);
            setDisabledIcon(icon);
            timer = new MenuScrollerTimer(increment, interval);
            addChangeListener(this);
        }

        public void setInterval(int interval) {
            timer.setDelay(interval);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (isArmed() && !timer.isRunning()) {
                timer.start();
            }
            if (!isArmed() && timer.isRunning()) {
                timer.stop();
            }
        }
    }   // class MenuScrollerItem

    // TODO: Determine why these methods are not being called
    private class MenuScrollerMenuKeyListener implements MenuKeyListener {

        @Override
        public void menuKeyTyped(MenuKeyEvent e) {
            // int keyCode = e.getKeyCode();
            // log.debug("MenuScroller.keyTyped({})", keyCode);
        }

        @Override
        public void menuKeyPressed(MenuKeyEvent e) {
            // int keyCode = e.getKeyCode();
            // log.debug("MenuScroller.keyPressed({})", keyCode);
        }

        @Override
        public void menuKeyReleased(MenuKeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_UP: {
                    // log.debug("MenuScroller.keyReleased(VK_UP)");
                    firstIndex--;
                    if (menuItems.length > topFixedCount + scrollCount + bottomFixedCount) {
                        refreshMenu();
                    }
                    e.consume();
                    break;
                }

                case KeyEvent.VK_DOWN: {
                    // log.debug("MenuScroller.keyReleased(VK_DOWN)");
                    firstIndex++;
                    if (menuItems.length > topFixedCount + scrollCount + bottomFixedCount) {
                        refreshMenu();
                    }
                    e.consume();
                    break;
                }

                default: {
                    // log.debug("MenuScroller.keyReleased({})", keyCode);
                    break;
                }
            }   // switch
        }
    }

    private enum MenuScrollerIcon implements Icon {

        UP(9, 1, 9),
        DOWN(1, 9, 1);
        final int[] xPoints = {1, 5, 9};
        final int[] yPoints;

        MenuScrollerIcon(int... yPoints) {
            this.yPoints = yPoints;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Dimension size = c.getSize();
            Graphics g2 = g.create(size.width / 2 - 5, size.height / 2 - 5, 10, 10);
            g2.setColor(Color.GRAY);
            g2.drawPolygon(xPoints, yPoints, 3);
            if (c.isEnabled()) {
                g2.setColor(Color.BLACK);
                g2.fillPolygon(xPoints, yPoints, 3);
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 0;
        }

        @Override
        public int getIconHeight() {
            return 10;
        }
    }
}
