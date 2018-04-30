package pr5.view;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class TrafficModelTable extends JPanel {
    
    private ListOfMapsTableModel model = new ListOfMapsTableModel();
    private JTable table;
    private final String[] fieldNames;
    private List<? extends Describable> elements;
    
    public TrafficModelTable(String[] fieldNames, List<? extends Describable> elements){
        super(new BorderLayout());
        this.fieldNames = fieldNames;
         this.elements = elements;
         table = new JTable(model);
         add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    }
    
    public void update(){
        model.update();
    }
    
    public void setElements(List <? extends Describable> elements){
        this.elements = elements;
    }
    
    private class ListOfMapsTableModel extends AbstractTableModel {
                
        @Override // fieldNames es un String[] con nombrs de col. 
        public String getColumnName(int columnIndex) {
            return fieldNames[columnIndex];
        }

        @Override // elements contiene la lista de elementos
        public int getRowCount() {
            return elements.size();
        }

        @Override
        public int getColumnCount() {
            return fieldNames.length;
        }

        @Override 
        public Object getValueAt(int rowIndex, int columnIndex) {
            if("#".equals(fieldNames[columnIndex])){
                return rowIndex;
            }   
            Map output = new HashMap<String, String>();
            elements.get(rowIndex).describe(output);
            return output.get(fieldNames[columnIndex]);
        }
        
        public void update(){
            fireTableDataChanged();
        }
    }
    
}
