package pr5.exception;

/**Creates a simulation error exception.
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 */
public class SimulatorError extends RuntimeException {
    /**Class constructor.
     */
    public SimulatorError(){
    }
    
    /**Class constructor specifying message.
     * 
     * @param s Message
     */
    public SimulatorError(String s){
        super(s);
    }
    
    /**Class constructor specifying message and cause of exception.
     * 
     * @param s Message
     * @param cause Cause of the exception
     */
    public SimulatorError(String s, Throwable cause){
        super(s, cause);
    }
}
