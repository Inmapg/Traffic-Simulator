package pr4.events;

import pr4.ini.IniSection;
import pr4.model.Junction;
import pr4.model.RoadMap;

/**Creates a new junction.
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 */
public class JunctionEvent extends Event {
    /**Identification value*/
    private final String id;
    
    /**Class constructor specifying time and id.
     * 
     * @param time When the event occurs
     * @param id Identification value
     */
    public JunctionEvent(int time, String id){
        super(time);
        this.id = id;
    }

    @Override
    public void execute(RoadMap roadmap) {
        roadmap.addJunction(new Junction(id));
    }

    /**Builds the junction event.
     * @see Event.Builder
     */
    public static class Builder implements Event.Builder {
    
        @Override
        public Event parse(IniSection sec) {
            if ( ! "new_junction".equals(sec.getTag())){
                return null;
            }
            return new JunctionEvent(
                    parseInt(sec, "time", 0),
                    parseString(sec, "id")
            );
        }
    }

}
