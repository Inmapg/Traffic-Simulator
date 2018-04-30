package pr5.view;

import java.util.Map;


public interface Describable{
    
    /**
     * Returns the passed-in map, with all fields filled out
     * @param out a map to fill in with key-value pairs
    */
    void describe(Map<String, String> out);
}