package pr6.control;

public class Stepper {

    private Runnable before;
    private Runnable after;
    private Runnable during;
    private volatile boolean stopRequested = false;
    private int steps;

    public Stepper(Runnable before, Runnable after, Runnable during) {
        this.after = after;
        this.before = before;
        this.during = during;
    }

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
                /*log.warn("Exception while stepping, " + steps + " remaining: " +
                        e, e);*/
                // suponiendo que tengamos un logger
            } finally {
                after.run();
            }
        });
        t.start();
        return t;
    }

    public void stop() {
        stopRequested = true;
    }
}