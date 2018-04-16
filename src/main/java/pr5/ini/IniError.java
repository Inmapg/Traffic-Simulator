package pr5.ini;

/*Creates an ini error exception*/
public class IniError extends RuntimeException {
    /**Serial version*/
    private static final long serialVersionUID = 1L;
    
    /**Class constructor
     * 
     * @param msg Message
     */
    IniError(String msg) {
	super(msg);
    }
}
