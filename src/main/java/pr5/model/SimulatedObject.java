package pr5.model;

import java.util.Objects;
import pr5.ini.*;

/**
 * Defines different types of object for the simulator.
 */
public abstract class SimulatedObject {
    protected final String id;
    
    /**
     * Class constructor specifying id.
     * 
     * @param id 
     */
    public SimulatedObject(String id){
        this.id = id;
    }
    
    /**
     * @return Identifier
     */
    public String getId(){
        return id;
    }
    
    @Override
    public int hashCode() {
        return 59 * 3 + Objects.hashCode(this.id);
    }
    
    @Override
    /**
     * Comparator.
     * @return ID.equals(other.ID) not necesary due to the uniqueness of the ID 
     */
    public boolean equals(Object obj) {
        return (this == obj || !(obj == null || getClass() != obj.getClass()));
    }
    
    /**
     * Generates the report.
     * 
     * @param time
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
    
    /**
     * @return Report section tag
     */
    protected abstract String getReportSectionTag();
    
    /**
     * Executes the simulated object.
     */
    abstract void advance();
    
    /**
     * Fills in the report.
     * 
     * @param sec Section
     */
    protected abstract void fillReportDetails(IniSection sec);
    
}
