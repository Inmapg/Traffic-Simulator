package pr5.view;

import pr5.control.Controller;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import pr5.control.SimulatorAction;
import pr5.events.Event;
import pr5.view.graphlayout.*;
import pr5.model.TrafficSimulator;
import pr5.model.TrafficSimulator.TrafficSimulatorListener;

/**
 * SimulatedWindow object which represents a GUI interface for the user. This
 * window provides a new way to configurate a simulator apart from the batch
 * mode.
 */
public class SimWindow extends JFrame implements TrafficSimulatorListener {

    // Toolkit allows us to get the screen size so size is relative
    // to the computer which executes the program
    // Width will 2/3 of the Screen Size Width
    private static final int DEFAULT_WIDTH = 2 * Toolkit.getDefaultToolkit().getScreenSize().width / 3;
    // Height will 5/6 of the Screen Size Height
    private static final int DEFAULT_HEIGHT = 5 * Toolkit.getDefaultToolkit().getScreenSize().height / 6;
    private TrafficSimulator.UpdateEvent lastUpdateEvent;
    private TrafficSimulator.UpdateEvent initialUpdateEvent;
    private final String[] EVENTS_HEADER = {"#", "Time", "Type"};
    private final String[] VEHICLES_HEADER = {"ID", "Road", "Location", "Speed", "Km", "Faulty units", "Itinerary"};
    private final String[] ROADS_HEADER = {"ID", "Source", "Target", "Length", "Max speed", "Vehicles"};
    private final String[] JUNCTIONS_HEADER = {"ID", "Green", "Red"};

    private enum OUTPUT_TYPE {
        reports, events
    }

    private File inFile;
    private JCheckBoxMenuItem redirect;
    private JSpinner stepsSpinner;
    private JTextField timeViewer;
    private JFileChooser fileChooser = new JFileChooser();
    private JTextArea eventsEditorArea;
    private JPanel upperPanel = new JPanel(new GridLayout(1, 3));
    private JPanel lowerPanel = new JPanel(new GridLayout(1, 2));
    private JPanel tablesPanel = new JPanel(new GridLayout(3, 1));
    private Controller controller;
    private TrafficModelTable eventsTable;
    private TrafficModelTable roadsTable;
    private TrafficModelTable vehiclesTable;
    private TrafficModelTable junctionsTable;
    private GraphLayout graph;
    private JTextArea reportsArea;
    
    // Event Actions and Object Creation and Instantiation 
    private final Action loadEvents = new SimulatorAction(
            "Load Events", "open.png", "Load events from file",
            KeyEvent.VK_L, "alt L", () -> {
                try {
                    loadFile();
                } catch (IOException | NoSuchElementException e) {
                    JOptionPane.showMessageDialog(this, "There was a problem "
                            + "while reading the file...",
                            "File cannot be read!",
                            JOptionPane.WARNING_MESSAGE);
                }
            });

    private final Action saveEvents = new SimulatorAction(
            "Save Events", "save.png", "Save events to file",
            KeyEvent.VK_S, "alt S",
            () -> {
                try {
                    saveFile(".ini", OUTPUT_TYPE.events);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this,
                            e.getMessage(),
                            "File cannot be saved!",
                            JOptionPane.WARNING_MESSAGE);
                }
            });

    private final Action clearEvents = new SimulatorAction(
            "Clear", "clear.png", "Clear events",
            () -> {
                clearEvents();
            });

    private final Action checkInEvents = new SimulatorAction(
            "Events", "events.png", "Show the events",
            () -> System.out.println("'Check in events' is not suported yet"));

    // Report Actions and Object Creation and Instantiation
    private final Action saveReport = new SimulatorAction(
            "Save Report", "save_report.png", "Save last report to file",
            KeyEvent.VK_R, "alt R",
            () -> System.out.println("'Save report' is not supported yet"));

    private final Action generateReport = new SimulatorAction(
            "Generate", "report.png", "Generate report",
            () -> System.out.println("'Generate report' is not supported yet"));

    private final Action clearReport = new SimulatorAction(
            "Clear", "delete_report.png", "Clear report",
            () -> System.out.println("'Clear report' is not supported yet"));

    // Traffic Simulator and configuration Object creation and instantiation
    private final Action exit = new SimulatorAction(
            "Exit", "exit.png", "Terminate the execution",
            KeyEvent.VK_E, "alt E", () -> System.exit(0));

    private final Action run = new SimulatorAction(
            "Run", "play.png", "Start simulation",
            () -> System.out.println("'Run' is not supported yet"));

    private final Action stop = new SimulatorAction(
            "Stop", "stop.png", "Stop simulation",
            () -> System.out.println("'Stop' is not supported yet"));

    private final Action reset = new SimulatorAction(
            "Reset", "reset.png", "Reset simulation",
            () -> System.out.println("'Reset' is not supported yet"));

    /**
     * Class constructor specifying input file and default time value.
     *
     * @param inFile
     */
    public SimWindow(String inFile, Controller controller) {
        super("Traffic Simulator");
        this.controller = controller;
        this.inFile = new File(inFile);
        initGUI();
    }

    /**
     * Initializes the GUI.
     */
    private void initGUI() {
        // disable not available initial actions
        saveEvents.setEnabled(false);
        clearEvents.setEnabled(false);
        checkInEvents.setEnabled(false);
        run.setEnabled(false);
        reset.setEnabled(false);
        stop.setEnabled(false);
        saveReport.setEnabled(false);
        clearReport.setEnabled(false);
        generateReport.setEnabled(false);
        try {
            controller.addSimulatorListener(this);
        } catch (Exception e) {
            System.err.println("TIERRA BURRITO!");
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        splitUpWindow();
        addBars();
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setVisible(true);
    }

    /**
     * Splits up the window in two different panels.
     */
    private void splitUpWindow() {

        addEventsEditor(); // upperPanel.add(eventsEditor)
        addEventsTableModel(); // upperPanel.add(eventsQueue)
        addReports();
        
        JPanel graph = new JPanel();
        graph.setBackground(Color.yellow);
        
        addTables();
        addGraph();
        JSplitPane windowSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, lowerPanel);

        add(windowSplit);
        windowSplit.setDividerLocation(DEFAULT_HEIGHT / 3);
    }

    
    private void addGraph(){
        graph = new GraphLayout(lastUpdateEvent.getRoadMap());
         lowerPanel.add(graph);
    }
    /**
     * Creates and adds the menu and tool bar to the main window.
     */
    private void addBars() {

        JMenuBar menu = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu simulator = new JMenu("Simulator");
        JMenu report = new JMenu("Reports");
        JToolBar bar = new JToolBar();

        redirect = new JCheckBoxMenuItem("Redirect Output", false);

        stepsSpinner = new JSpinner(new SpinnerNumberModel(controller.getDefaultTime(),
                1, 1000, 1));
        stepsSpinner.setMaximumSize(new Dimension(50, 40));
        timeViewer = new JTextField("0", controller.getDefaultTime());
        timeViewer.setMaximumSize(new Dimension(60, 40));

        // Adding options to File section in MenuBar
        file.add(loadEvents);
        file.add(saveEvents);
        file.addSeparator();
        file.add(saveReport);
        file.addSeparator();
        file.add(exit);
        // Adding options to Simulator section in MenuBar
        simulator.add(run);
        simulator.add(reset);
        simulator.add(redirect);
        // Adding options to Reports section in MenuBar
        report.add(generateReport);
        report.add(clearReport);
        // Adding sections to MenuBar
        menu.add(file);
        menu.add(simulator);
        menu.add(report);
        // Setting in MenuBar in the Window
        setJMenuBar(menu);

        bar.add(loadEvents);
        bar.add(saveEvents);
        bar.add(clearEvents);
        bar.addSeparator();
        bar.add(checkInEvents);
        bar.add(run);
        bar.add(stop);
        bar.add(reset);
        bar.add(new JLabel(" Steps: "));
        bar.add(stepsSpinner);
        bar.add(new JLabel(" Time: "));
        bar.add(timeViewer);
        bar.addSeparator();
        bar.add(generateReport);
        bar.add(clearReport);
        bar.add(saveReport);
        bar.addSeparator();
        bar.add(exit);

        // Pinning up the bar to the window
        bar.setFloatable(false);
        // Setting in ToolBar in the Window 

        add(bar, BorderLayout.PAGE_START);

    }

    /**
     * Clears the event editor area.
     */
    private void clearEvents() {
        eventsEditorArea.setText("");
        updateComponentBorder(eventsEditorArea, "Events");
        saveEvents.setEnabled(false);
        clearEvents.setEnabled(false);
        checkInEvents.setEnabled(false);
        run.setEnabled(false);
        reset.setEnabled(false);
    }

    /**
     * Sets a component border.
     *
     * @param c Component
     * @param text Text to fill in
     */
    private void updateComponentBorder(JComponent c, String text) {
        c.setBorder(BorderFactory.createTitledBorder(BorderFactory
                .createLineBorder(Color.black, 1), text));
    }

    /**
     * Gives format to the event editor area.
     */
    private void addEventsEditor() {
        // Text Area creation, if a File was specified it will be loaded
        eventsEditorArea = new JTextArea("");
        updateComponentBorder(eventsEditorArea, "Events");
        if (!"".equals(inFile.getName())) {
            // Trying to read the file with the given name by inFile
            try {
                eventsEditorArea.setText(readFile(inFile));
                updateComponentBorder(eventsEditorArea, "Events: " + inFile.getName());
                // update actions available
                saveEvents.setEnabled(true);
                clearEvents.setEnabled(true);
                checkInEvents.setEnabled(true);
                run.setEnabled(true);
                reset.setEnabled(true);
            } // Trying to capture and control the exception
            catch (IOException | NoSuchElementException e) {
                JOptionPane.showMessageDialog(this, "There was a problem "
                        + "while reading the file...",
                        "File cannot be read!",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
        // Text Area configuration
        eventsEditorArea.setEditable(true);
        eventsEditorArea.setLineWrap(true);
        eventsEditorArea.setWrapStyleWord(true);
        // To allow scrolling we use a ScrollPane
        upperPanel.add(new JScrollPane(eventsEditorArea));
    }

    private void addEventsTableModel() {
        eventsTable = new TrafficModelTable(EVENTS_HEADER, lastUpdateEvent.getEventQueue());
        updateComponentBorder(eventsTable, "Events queue");
        upperPanel.add(eventsTable);
    }
    
    private void addReports(){
        reportsArea = new JTextArea("");
        updateComponentBorder(reportsArea, "Reports");
        reportsArea.setEditable(false);
        upperPanel.add(reportsArea);
    }

    private void addTables(){
            roadsTable = new TrafficModelTable(ROADS_HEADER, lastUpdateEvent.getRoadMap().getRoads());
         updateComponentBorder(roadsTable, "Roads");
         vehiclesTable = new TrafficModelTable(VEHICLES_HEADER, lastUpdateEvent.getRoadMap().getVehicles());
         updateComponentBorder(vehiclesTable, "Vehicles");
         junctionsTable = new TrafficModelTable(JUNCTIONS_HEADER, lastUpdateEvent.getRoadMap().getJunctions());
         updateComponentBorder(junctionsTable, "Junctions");
         tablesPanel.add(vehiclesTable);
         tablesPanel.add(roadsTable);
         tablesPanel.add(junctionsTable);
        lowerPanel.add(tablesPanel);
    }
    

    
    /**
     * Saves the current state of events or reports.
     *
     * @param ext File extension
     * @param type Events/Reports
     * @throws IOException if file cannot be opened
     */
    private void saveFile(String ext, OUTPUT_TYPE type) throws IOException {
        int returnVal = fileChooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            writeFile(file, eventsEditorArea.getText());

            // Update the GUI
            switch (type) {
                case events:
                    updateComponentBorder(eventsEditorArea, "Events: " + file.getName());
                    break;
                case reports:
                    // TODO
                    break;
                default:
                // you shouldn't be here
            }

        }
    }

    /**
     * Loads the events from a given file.
     *
     * @throws IOException if file cannot be opened
     * @throws NoSuchElementException if file does not exist
     */
    private void loadFile() throws IOException, NoSuchElementException {
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String s = readFile(file);
            eventsEditorArea.setText(s);
            updateComponentBorder(eventsEditorArea, "Events: " + file.getName());
            saveEvents.setEnabled(true);
            clearEvents.setEnabled(true);
            checkInEvents.setEnabled(true);
            run.setEnabled(true);
            reset.setEnabled(true);
        }
    }

    /**
     * Reads a file.
     *
     * @param fileName
     * @return content of the file
     * @throws IOException if file cannot be opened
     */
    private String readFile(File fileName) throws IOException {
        String s = "";
        s = new Scanner(fileName).useDelimiter("\\A").next();
        return s;
    }

    /**
     * Writes a content on a given file.
     *
     * @param file
     * @param content
     * @throws IOException if file cannot be opened
     */
    private static void writeFile(File file, String content) throws IOException {
        PrintWriter pw = new PrintWriter(file);
        pw.print(content);
        pw.close();
    }

    public void registered(TrafficSimulator.UpdateEvent ue) {
        // initialUpdateEvent = ue.clone();
        lastUpdateEvent = ue;
    }

    @Override
    public void reset(TrafficSimulator.UpdateEvent ue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void newEvent(TrafficSimulator.UpdateEvent ue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void advanced(TrafficSimulator.UpdateEvent ue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void error(TrafficSimulator.UpdateEvent ue, String error) {
        JOptionPane.showMessageDialog(this, "Error",
                error,
                JOptionPane.ERROR_MESSAGE);
    }
}
