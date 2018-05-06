package pr6.view;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * Gives format to the table of the simulated objects used in the simulator.
 */
public class TrafficModelTable extends JPanel {

    private final ListOfMapsTableModel model = new ListOfMapsTableModel();
    private final JTable table;
    private final String[] fieldNames;
    private List<? extends Describable> elements;

    /**
     * Class constructor specifying the header of the table and its elements.
     *
     * @param fieldNames Header
     * @param elements List of elements to fill out the table
     */
    public TrafficModelTable(String[] fieldNames, List<? extends Describable> elements) {
        super(new BorderLayout());
        this.fieldNames = fieldNames;
        this.elements = elements;
        table = new JTable(model);
        add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    }

    /**
     * Update the table.
     */
    public void update() {
        model.update();
    }

    /**
     * Sets the list of elements.
     *
     * @param elements
     */
    public void setElements(List<? extends Describable> elements) {
        this.elements = elements;
    }

    /**
     * Clear the table.
     */
    public void clear() {
        elements = new ArrayList<>();
    }

    /**
     * Sets the table format.
     */
    private class ListOfMapsTableModel extends AbstractTableModel {

        @Override
        public String getColumnName(int columnIndex) {
            return fieldNames[columnIndex];
        }

        @Override
        public int getRowCount() {
            return elements.size();
        }

        @Override
        public int getColumnCount() {
            return fieldNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if ("#".equals(fieldNames[columnIndex])) {
                return rowIndex;
            }
            Map output = new HashMap<String, String>();
            elements.get(rowIndex).describe(output);
            return output.get(fieldNames[columnIndex]);
        }

        /**
         * Updates the table.
         */
        public void update() {
            fireTableDataChanged();
        }
    }

}
