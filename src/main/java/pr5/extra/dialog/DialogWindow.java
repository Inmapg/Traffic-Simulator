package pr5.extra.dialog;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
/*
 * Rellenar un ini con los selected y hacer el toString de ese ini para añadirlo
 * al componente
 */
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import pr5.model.Junction;
import pr5.model.Road;
import pr5.model.Vehicle;

public class DialogWindow extends JDialog {
    private MyListModel<Vehicle> vehiclesListModel = new MyListModel<>();
    private MyListModel<Road> roadsListModel = new MyListModel<>();
    private MyListModel<Junction> junctionsListModel = new MyListModel<>();

    private int status = 0;
    private JList<Vehicle> vehiclesList;
    private JList<Road> roadsList;
    private JList<Junction> junctionsList;

    static final private char clearSectionKey = 'c';
    private Border defaultBorder = BorderFactory.createLineBorder(Color.black, 1);

    public DialogWindow(Frame parent) {
        super(parent, true);
        initGUI();
    }

    private void initGUI() {
        setTitle("Generate Reports");
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel vehiclesPanel = new JPanel(new BorderLayout());
        JPanel roadsPanel = new JPanel(new BorderLayout());
        JPanel junctionsPanel = new JPanel(new BorderLayout());

        contentPanel.add(vehiclesPanel);
        contentPanel.add(roadsPanel);
        contentPanel.add(junctionsPanel);

        vehiclesPanel.setBorder(
                BorderFactory.createTitledBorder(defaultBorder, "Vehicles",
                        TitledBorder.LEFT, TitledBorder.TOP));
        roadsPanel.setBorder(
                BorderFactory.createTitledBorder(defaultBorder, "Roads",
                        TitledBorder.LEFT, TitledBorder.TOP));

        junctionsPanel.setBorder(
                BorderFactory.createTitledBorder(defaultBorder, "Junctions",
                        TitledBorder.LEFT, TitledBorder.TOP));
        
        vehiclesPanel.setMinimumSize(new Dimension(100, 100));
        roadsPanel.setMinimumSize(new Dimension(100, 100));
        junctionsPanel.setMinimumSize(new Dimension(100, 100));

        vehiclesList = new JList<>(vehiclesListModel);
        roadsList = new JList<>(roadsListModel);
        junctionsList = new JList<>(junctionsListModel);

        addCleanSelectionListener(vehiclesList);
        addCleanSelectionListener(roadsList);
        addCleanSelectionListener(junctionsList);
        
        vehiclesPanel.add(new JScrollPane(vehiclesList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

        roadsPanel.add(new JScrollPane(roadsList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
        
        junctionsPanel.add(new JScrollPane(junctionsList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        mainPanel.add(buttonsPanel, BorderLayout.PAGE_END);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                status = 0;
                DialogWindow.this.setVisible(false);
            }
        });
        buttonsPanel.add(cancelButton);

        JButton generateButton = new JButton("Generate");
        generateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                status = 1;
                DialogWindow.this.setVisible(false);
                // Igual aquí habría quue
            }
        });
        buttonsPanel.add(generateButton);

        mainPanel.add(buttonsPanel, BorderLayout.PAGE_END);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        mainPanel.add(infoPanel, BorderLayout.PAGE_START);

        infoPanel.add(new JLabel("Select items for which you want to process."));
        infoPanel.add(new JLabel("Use '" + clearSectionKey + "' to deselect all."));
        infoPanel.add(new JLabel("Use Ctrl+A to select all"));
        infoPanel.add(new JLabel(" "));

        setContentPane(mainPanel);
        setMinimumSize(new Dimension(700, 400));
        setVisible(false);
    }

    private void addCleanSelectionListener(JList<?> list) {
        list.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == clearSectionKey) {
                    list.clearSelection();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });

    }

    public void setData(List<Vehicle> vehicles, List<Road> roads, List<Junction> junctions) {
        vehiclesListModel.setList(vehicles);
        roadsListModel.setList(roads);
        junctionsListModel.setList(junctions);
    }

    public List<Vehicle> getSelectedVehiclesID() {
        int[] indices = vehiclesList.getSelectedIndices();
        List<Vehicle> v = new ArrayList<>();//[indices.length];
        for (int i = 0; i < indices.length; i++) {
            v.add(vehiclesListModel.getElementAt(indices[i]));
        }
        return v;
    }

    public List<Road> getSelectedRoads() {
        int[] indices = roadsList.getSelectedIndices();
        List<Road> r = new ArrayList<>();
        for (int i = 0; i < indices.length; i++) {
            r.add(roadsListModel.getElementAt(indices[i]));
        }
        return r;
    }

    public List<Junction> getSelectedJunctions() {
        int[] indices = junctionsList.getSelectedIndices();
        List<Junction> j = new ArrayList<>();
        for (int i = 0; i < indices.length; i++) {
            j.add(junctionsListModel.getElementAt(indices[i]));
        }
        return j;
    }
    
    public int open() {
        setLocation(getParent().getLocation().x + 50, getParent().getLocation().y + 50);
        pack();
        setVisible(true);
        return status;
    }

}
