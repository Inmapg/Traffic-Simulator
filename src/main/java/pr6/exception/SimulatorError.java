package pr5.exception;

/**
 * Creates a simulation error exception.
 */
public class SimulatorError extends RuntimeException {

    /**
     * Class constructor.
     */
    public SimulatorError() {
    }

    /**
     * Class constructor specifying message.
     *
     * @param message
     */
    public SimulatorError(String message) {
        super(message);
    }

    /**
     * Class constructor specifying message and cause of exception.
     *
     * @param message
     * @param cause
     */
    public SimulatorError(String message, Throwable cause) {
        super(message, cause);
    }
}
