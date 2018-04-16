package pr5.model;

import java.util.Objects;
import pr5.ini.*;

/**Defines different types of object for the simulator
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 */
public abstract class SimulatedObject {
    /**Identification*/
    protected final String id;
    
    /**Class constructor specifying id.
     * 
     * @param id Identification
     */
    public SimulatedObject(String id){
        this.id = id;
    }
    
    /**Returns the id.
     * 
     * @return Identification
     */
    public String getId(){
        return id;
    }
    
    @Override
    public int hashCode() {
        return 59 * 3 + Objects.hashCode(this.id);
    }
    
    @Override
    /**Comparator.
     * Return ID.equals(other.ID) not necesary due to the uniqueness of the ID 
     */
    public boolean equals(Object obj) {
        return (this == obj || !(obj == null || getClass() != obj.getClass()));
    }
    
    /**Generates the report.
     * 
     * @param time Time
     * @return Fullfilled section
     * @see IniSection
     */
    public IniSection generateReport(int time){
        IniSection ini = new IniSection(getReportSectionTag());
        ini.setValue("id", id);
        ini.setValue("time", time);
        fillReportDetails(ini);
        return ini;
    }
    
    /**Returns the report section tag.
     * 
     * @return Section tag
     */
    protected abstract String getReportSectionTag();
    
    /**Executes the simulated object.
     * 
     */
    abstract void advance();
    
    /**Fills in the report.
     * 
     * @param sec Section
     */
    protected abstract void fillReportDetails(IniSection sec);
    
}
