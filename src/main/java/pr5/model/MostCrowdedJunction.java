package pr5.model;

import java.util.Iterator;
import pr5.ini.IniSection;

/**Creates a new most crowded junction.
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 */
public class MostCrowdedJunction extends TimeSliceJunction{
    
    private static final String TYPE = "mc";
    
    public MostCrowdedJunction(String id) {
        super(id);
        
    }
 @Override
    protected void switchLights() {
        // Ops... Error 404 code not found
    }
    
    
     protected void fillReportDetails(IniSection sec) {
        sec.setValue("type", TYPE);
        super.fillReportDetails(sec);
    }
}
