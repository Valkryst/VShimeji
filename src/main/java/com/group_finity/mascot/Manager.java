package com.group_finity.mascot;

import com.group_finity.mascot.behavior.Behavior;
import com.group_finity.mascot.config.Configuration;
import com.group_finity.mascot.display.window.WindowFactory;
import com.group_finity.mascot.exception.BehaviorInstantiationException;
import com.group_finity.mascot.exception.CantBeAliveException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * An object that manages the list of {@link Mascot Mascots} and takes timing.
 * If each {@link Mascot} moves asynchronously, there will be various problems (such as when throwing a window),
 * so this class adjusts the timing of the entire mascot.
 * <p>
 * The {@link #tick()} method first retrieves the latest environment information and then moves all {@link Mascot Mascots}.
 *
 * @author Yuki Yamada of <a href="http://www.group-finity.com/Shimeji/">Group Finity</a>
 * @author Shimeji-ee Group
 * @author Valkryst
 */
@Log
@NoArgsConstructor
public class Manager {
    /**
     * The duration of each tick, in milliseconds.
     */
    public static final int TICK_INTERVAL = 40;

    /**
     * A list of {@link Mascot Mascots} which are managed by this {@code Manager}.
     */
    private final List<Mascot> mascots = new CopyOnWriteArrayList<>();

    /**
     * Whether the program should exit when the last {@link Mascot} is deleted.
     * If you fail to create a tray icon, the process will remain forever unless you close the program when the {@link Mascot} disappears.
     */
    @Getter @Setter private boolean exitOnLastRemoved = true;

    /** {@link ScheduledExecutorService} which calls the {@link #tick()} method. */
    private ScheduledExecutorService executorService;

    /**
     * Starts the thread.
     */
    public void start() {
        if (executorService != null && !executorService.isShutdown()) {
            log.warning("An attempt was made to start the scheduler, but it is already running.");
            return;
        }

        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(() -> {
            try {
                final long startTime = System.currentTimeMillis();
                this.tick();
                final long tickTime = System.currentTimeMillis() - startTime;

                log.fine("Ending Tick (" + tickTime + "ms)");
                if (tickTime > TICK_INTERVAL) {
                    log.warning("Tick took " + tickTime + "ms, which is longer than the expected " + TICK_INTERVAL + "ms.");
                }
            } catch (final Exception e) {
                log.log(Level.SEVERE, "An error occurred while running the tick method.", e);
                this.stop();
            }
        }, 0, TICK_INTERVAL, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the thread.
     */
    public void stop() {
        if (executorService == null || executorService.isShutdown()) {
            return;
        }

        try {
            executorService.shutdownNow();

            if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                log.log(Level.WARNING, "The executor service did not terminate in the allotted time.");
            }
        } catch (final InterruptedException | SecurityException e) {
            log.log(Level.SEVERE, "Failed to shutdown the executor service.", e);
        }

        executorService = null;
    }

    /**
     * Advances the {@link Mascot Mascots} by one frame.
     */
    private void tick() {
        // Update the environmental information first
        WindowFactory.getInstance().getEnvironment().tick();

        for (final Mascot mascot : mascots) {
            mascot.tick();
            mascot.render();
        }

        if (isExitOnLastRemoved() && mascots.isEmpty()) {
            // exitOnLastRemoved is true and there are no mascots left, so exit.
            Main.getInstance().exit();
        }
    }

    /**
     * Adds a {@link Mascot}.
     * Addition is done at the next {@link #tick()} timing.
     *
     * @param mascot the {@link Mascot} to add
     */
    public void add(final Mascot mascot) {
        mascots.add(mascot);
        mascot.setManager(this);
    }

    /**
     * Removes a {@link Mascot}.
     * Removal is done at the next {@link #tick()} timing.
     *
     * @param mascot the {@link Mascot} to remove
     */
    public void remove(final Mascot mascot) {
        mascots.remove(mascot);

        mascot.setManager(null);
        // Clear affordances so the mascot is not participating in any interactions, as that can cause an NPE
        mascot.getAffordances().clear();
    }

    /**
     * Sets the {@link Behavior} for all {@link Mascot Mascots}.
     *
     * @param name the name of the {@link Behavior}
     */
    public void setBehaviorAll(final String name) {
        for (final Mascot mascot : mascots) {
            try {
                Configuration configuration = Main.getInstance().getConfiguration(mascot.getImageSet());
                mascot.setBehavior(configuration.buildBehavior(configuration.getSchema().getString(name), mascot));
            } catch (final BehaviorInstantiationException | CantBeAliveException e) {
                log.log(Level.SEVERE, "Failed to set behavior to \"" + name + "\" for mascot \"" + mascot + "\"", e);
                Main.showError(Main.getInstance().getLanguageBundle().getString("FailedSetBehaviourErrorMessage") + "\n" + e.getMessage() + "\n" + Main.getInstance().getLanguageBundle().getString("SeeLogForDetails"));

                mascots.remove(mascot);
                mascot.dispose();
            }
        }
    }

    /**
     * Sets the {@link Behavior} for all {@link Mascot Mascots} with the specified image set.
     *
     * @param configuration the {@link Configuration} to use to build the {@link Behavior}
     * @param name the name of the {@link Behavior}
     * @param imageSet the image set for which to check
     */
    public void setBehaviorAll(final Configuration configuration, final String name, String imageSet) {
        for (final Mascot mascot : mascots) {
            try {
                if (mascot.getImageSet().equals(imageSet)) {
                    mascot.setBehavior(configuration.buildBehavior(configuration.getSchema().getString(name), mascot));
                }
            } catch (final BehaviorInstantiationException | CantBeAliveException e) {
                log.log(Level.SEVERE, "Failed to set behavior to \"" + name + "\" for mascot \"" + mascot + "\"", e);
                Main.showError(Main.getInstance().getLanguageBundle().getString("FailedSetBehaviourErrorMessage") + "\n" + e.getMessage() + "\n" + Main.getInstance().getLanguageBundle().getString("SeeLogForDetails"));

                mascots.remove(mascot);
                mascot.dispose();
            }
        }
    }

    /**
     * Dismisses mascots until one remains.
     */
    public void remainOne() {
        int totalMascots = mascots.size();
        for (int i = totalMascots - 1; i > 0; i--) {
            mascots.get(i).dispose();
        }
    }

    /**
     * Dismisses all mascots except for the one specified.
     *
     * @param mascot the mascot to not dismiss
     */
    public void remainOne(Mascot mascot) {
        int totalMascots = mascots.size();
        for (int i = totalMascots - 1; i >= 0; i--) {
            if (!mascots.get(i).equals(mascot)) {
                mascots.get(i).dispose();
            }
        }
    }

    /**
     * Dismisses mascots which use the specified image set until one mascot remains.
     *
     * @param imageSet the image set for which to check
     */
    public void remainOne(String imageSet) {
        int totalMascots = mascots.size();
        boolean isFirst = true;
        for (int i = totalMascots - 1; i >= 0; i--) {
            Mascot m = mascots.get(i);
            if (m.getImageSet().equals(imageSet) && isFirst) {
                isFirst = false;
            } else if (m.getImageSet().equals(imageSet)) {
                m.dispose();
            }
        }
    }

    /**
     * Dismisses mascots which use the specified image set until only the specified mascot remains.
     *
     * @param imageSet the image set for which to check
     * @param mascot   the mascot to not dismiss
     */
    public void remainOne(String imageSet, Mascot mascot) {
        int totalMascots = mascots.size();
        for (int i = totalMascots - 1; i >= 0; i--) {
            Mascot m = mascots.get(i);
            if (m.getImageSet().equals(imageSet) && !m.equals(mascot)) {
                m.dispose();
            }
        }
    }

    /**
     * Dismisses all mascots which use the specified image set.
     *
     * @param imageSet the image set for which to check
     */
    public void remainNone(String imageSet) {
        int totalMascots = mascots.size();
        for (int i = totalMascots - 1; i >= 0; i--) {
            Mascot m = mascots.get(i);
            if (m.getImageSet().equals(imageSet)) {
                m.dispose();
            }
        }
    }

    /**
     * Disposes all {@link Mascot Mascots}.
     */
    public void disposeAll() {
        for (int i = mascots.size() - 1; i >= 0; i--) {
            mascots.get(i).dispose();
        }
    }

    public void togglePauseAll() {
        boolean isPaused = false;
        if (!mascots.isEmpty()) {
            isPaused = mascots.get(0).isPaused();
        }

        for (final Mascot mascot : mascots) {
            mascot.setPaused(!isPaused);
        }
    }

    public boolean isPaused() {
        boolean isPaused = false;

        if (!mascots.isEmpty()) {
            isPaused = mascots.get(0).isPaused();
        }

        return isPaused;
    }

    /**
     * Gets the current number of {@link Mascot Mascots}.
     *
     * @return the current number of {@link Mascot Mascots}
     */
    public int getCount() {
        return getCount(null);
    }

    /**
     * Gets the current number of {@link Mascot Mascots} with the given image set.
     *
     * @param imageSet the image set for which to check
     * @return the current number of {@link Mascot Mascots}
     */
    public int getCount(String imageSet) {
        if (imageSet == null) {
            return mascots.size();
        } else {
            return (int) mascots.stream().filter(m -> m.getImageSet().equals(imageSet)).count();
        }
    }

    /**
     * Returns a Mascot with the given affordance.
     *
     * @param affordance the affordance for which to check
     * @return a {@link WeakReference} to a mascot with the required affordance, or {@code null} if none was found
     */
    public WeakReference<Mascot> getMascotWithAffordance(String affordance) {
        for (final Mascot mascot : mascots) {
            if (mascot.getAffordances().contains(affordance)) {
                return new WeakReference<>(mascot);
            }
        }

        return null;
    }

    public boolean hasOverlappingMascotsAtPoint(Point anchor) {
        int count = 0;

        for (final Mascot mascot : mascots) {
            // TODO Have this account for the entirety of the mascots' windows instead of just a single point
            if (mascot.getAnchor().equals(anchor)) {
                count++;
            }
            if (count > 1) {
                return true;
            }
        }

        return false;
    }
}
