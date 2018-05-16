package pr6.control;

import javax.swing.JOptionPane;

/**
 * Stepper class.
 * <a href=https://github.com/manuel-freire>github</a>
 *
 * @author Manuel Freire
 */
public class Stepper {

    private final Runnable before;
    private final Runnable after;
    private final Runnable during;
    private volatile boolean stopRequested = false;
    private int steps;

    private static final Runnable EMPTY = () -> {
    };

    /**
     * Sets up the runnable operations. When receiving a null runnable it is set
     * as an empty runnable.
     *
     * @param before
     * @param after
     * @param during
     */
    public Stepper(Runnable before, Runnable after, Runnable during) {
        this.before = (before == null) ? EMPTY : before;
        this.after = (after == null) ? EMPTY : after;
        this.during = (during == null) ? EMPTY : during;
    }

    /**
     * Starts execution in a new Thread. Calls the runnable before to prepare
     * the execution and from steps to zero it will call during runnable and
     * delaying the given amount of ms. After the execution after runnable will
     * be called.
     *
     * @param steps
     * @param delay
     * @return
     */
    public Thread start(int steps, int delay) {
        this.steps = steps;
        stopRequested = false;
        Thread t = new Thread(() -> {
            try {
                before.run();
                while (!stopRequested && Stepper.this.steps > 0) {
                    during.run();
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException is) {
                        // Ignore and continue
                    }
                    Stepper.this.steps--;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "There was a problem "
                            + "while stepping, " + steps + " remaining: " + e,
                            "Exception while stepping!", JOptionPane.WARNING_MESSAGE);
            } finally {
                after.run();
            }
        });
        t.start();
        return t;
    }

    /**
     * Called whenever the stepper needs to be stopped.
     */
    public void stop() {
        stopRequested = true;
    }
}
