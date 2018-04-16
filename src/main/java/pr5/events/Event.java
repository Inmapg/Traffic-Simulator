package pr5.events;

import pr5.ini.IniSection;
import pr5.model.RoadMap;

/**Creates and executes the different types of events.
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 */
public abstract class Event {
    /**Internal time of the event*/
    private final Integer internalTime;

    /**Class constructor specifying time
     * 
     * @param internalTime Time
     */
    public Event(int internalTime){
        this.internalTime = internalTime;
    }
    
    /**Executes the event.
     * 
     * @param roadmap Information about the current situation in the simulator
     */
    public abstract void execute(RoadMap roadmap);
    
    /**Returns the time of the event.
     * 
     * @return current internal time
     */
    public int getScheduleTime(){
        return internalTime;
    }
    
    /**Compares the internal time of two events.
     * 
     * @param e Event to compare with
     * @return Result of comparison
     */
    public int compareTo(Event e){
        return internalTime.compareTo(e.getScheduleTime());
    }
    
    public static interface Builder {
    
    /**Parses an Event given an IniSection.
     * 
     * @param ini Information about the event
     * @return parsed event
     */
    public abstract Event parse(IniSection ini);
   
    /**Parses an identification.
     * 
     * @param sec Information about the event
     * @param key Identification value
     * @return Valid ID
     */
    default String parseString(IniSection sec, String key) {
        String v = sec.getValue(key);
            if(!v.matches("[a-zA-Z1-9_]+")){ // probar dos más ++ 
                throw new IllegalArgumentException(v + " is not a valid " + key);
            }
        return v;
    }
    
    /**Parses an integer value
     * 
     * @param sec Information about the event
     * @param key Identification word
     * @param minValue Minimum value accepted
     * @return Valid integer value
     */
    default int parseInt(IniSection sec, String key, int minValue) {
       String s = sec.getValue(key);
       if(s == null){
           throw new NullPointerException("Error at parseInt() with key \"" + key + "\" in " + sec.getTag());
       }
       int v = Integer.parseInt(s);
       if(v < minValue)
       {
            throw new IllegalArgumentException(v + " is not a valid " + key);
       }
        return v;
    }
    
    /**Parses a list of strings
     * 
     * @param sec Information about the event
     * @param key Identification word
     * @return List of correct identification words
     */
    default String[] parseStringList(IniSection sec, String key) {
        String[] v = sec.getValue(key).split("[, ]+");
        for(String c : v){
            if(!c.matches("[a-zA-Z1-9_]+")){
                throw new IllegalArgumentException(c + " is not a valid id in the list " + key);
            }
        }
        return v;
    }
    
    /**Parses a double value
     * 
     * @param sec Information about the event
     * @param key Identification word
     * @param min Minimum value of range
     * @param max Maximum value of range
     * @return Valid value
     */
    default double parseDouble(IniSection sec, String key, double min, double max){
        double v = Double.parseDouble(sec.getValue(key));
        if(v < min || v > max){
            throw new IllegalArgumentException(v + " is not a valid " + key + " it must be contained in [" + min + "," + max + "]");
        }
        return v;
    }
    
    /**Parses a long value
     * 
     * @param sec Information about the event
     * @param key Identificacion word
     * @return Valid value
     */
    default long parseLongOrMills(IniSection sec, String key){
        String parv = sec.getValue(key);
        Long v;
        if(parv == null){
             v = System.currentTimeMillis();  
        }
        else{
            v = Long.parseLong(parv);
            if(v < 0){
                throw new IllegalArgumentException(v + " is not a valid " + key + " it must be positive");
            }
        }
        return v;
    }
    
    }

    
    
}
