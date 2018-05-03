package pr5.view;

/**
 * FileNameExtensionFilter filter = new FileNameExtensionFilter("Ini files",
 * "ini"); fileChooser.setFileFilter(filter);
 *
 *
 * From Java API: JFileChooser chooser = new JFileChooser();
 * FileNameExtensionFilter filter = new FileNameExtensionFilter( "JPG & GIF
 * Images", "jpg", "gif"); chooser.setFileFilter(filter); int returnVal =
 * chooser.showOpenDialog(parent); if(returnVal == JFileChooser.APPROVE_OPTION)
 * { System.out.println("You chose to open this file: " +
 * chooser.getSelectedFile().getName()); }
 *
 * hacer una para read y otra para save
 */
import pr5.control.Controller;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import pr5.control.SimulatorAction;
import pr5.events.Event;
import pr5.ini.Ini;
import pr5.ini.IniSection;
import pr5.model.Junction;
import pr5.model.Road;
import pr5.view.graphlayout.*;
import pr5.model.TrafficSimulator;
import pr5.model.TrafficSimulator.TrafficSimulatorListener;
import pr5.model.Vehicle;

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
    private final String[] VEHICLES_HEADER = {"ID", "Road", "Location", "Speed", "Km", "Faulty Units", "Itinerary"};
    private final String[] ROADS_HEADER = {"ID", "Source", "Target", "Length", "Max Speed", "Vehicles"};
    private final String[] JUNCTIONS_HEADER = {"ID", "Green", "Red"};

    private enum OUTPUT_TYPE {
        reports, events
    }

    private FileNameExtensionFilter filter = new FileNameExtensionFilter(".ini", "ini");

    private JToolBar statusBar = new JToolBar();
    private JLabel statusBarMessage = new JLabel("Welcome to the traffic simulator!");
    private File inFile;
    private JCheckBoxMenuItem redirect;
    private JSpinner stepsSpinner;
    private JTextField timeViewer;
    private JFileChooser fileChooser = new JFileChooser();
    private JTextArea eventsEditorArea;
    private JPanel eventsPanel = new JPanel(new BorderLayout());
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
    private JPanel reportsPanel = new JPanel(new BorderLayout());
    private List<Event> eventsList = new ArrayList<>();
    private TextAreaPrintStream outputReports;
    private ByteArrayOutputStream defaultOutputSimulator = new ByteArrayOutputStream();
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
                            "Events cannot be saved!",
                            JOptionPane.WARNING_MESSAGE);
                }
            });

    private final Action clearEvents = new SimulatorAction(
            "Clear", "clear.png", "Clear events",
            () -> {
                clearEvents();
            });

    private final Action checkInEvents = new SimulatorAction(
            "Events", "events.png", "Check in events in the simulator",
            () -> checkInEvents());

    // Report Actions and Object Creation and Instantiation
    private final Action saveReport = new SimulatorAction(
            "Save Report", "save_report.png", "Save last report to file",
            KeyEvent.VK_R, "alt R",
            () -> {
                try {
                    saveFile(".ini.eout", OUTPUT_TYPE.reports);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this,
                            e.getMessage(),
                            "Reports cannot be saved!",
                            JOptionPane.WARNING_MESSAGE);
                }
            });

    private final Action generateReport = new SimulatorAction(
            "Generate", "report.png", "Generate report",
            () -> generateReport());

    private final Action clearReport = new SimulatorAction(
            "Clear", "delete_report.png", "Clear report",
            () -> clearReport());

    // Traffic Simulator and configuration Object creation and instantiation
    private final Action exit = new SimulatorAction(
            "Exit", "exit.png", "Terminate the execution",
            KeyEvent.VK_E, "alt E", () -> System.exit(0));

    private final Action run = new SimulatorAction(
            "Run", "play.png", "Start simulation",
            () -> runSimWindow());

    private final Action stop = new SimulatorAction(
            "Stop", "stop.png", "Stop simulation",
            () -> System.out.println("'Stop' is not supported yet"));

    private final Action reset = new SimulatorAction(
            "Reset", "reset.png", "Reset simulation",
            () -> reset());

    /**
     * Class constructor specifying input file and default time value.
     *
     * @param inFile
     * @param controller
     */
    public SimWindow(String inFile, Controller controller) {
        super("Traffic Simulator");
        this.controller = controller;
        controller.setOutputStream(defaultOutputSimulator);
        this.inFile = new File(inFile);
        initGUI();
    }

    /**
     * Initializes the GUI.
     */
    private void initGUI() {
        try {
            controller.addSimulatorListener(this);
        } catch (Exception e) {
            System.err.println("We should print an error message here"); // Finish it
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        splitUpWindow();
        addBars();
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        outputReports = new TextAreaPrintStream(reportsArea);
        setVisible(true);

        clearEvents.setEnabled(false);
        saveEvents.setEnabled(false);
        reset.setEnabled(false);
        stop.setEnabled(false);
        checkInEvents.setEnabled(false);
        generateReport.setEnabled(false);
        saveReport.setEnabled(false);
        run.setEnabled(false);
        clearReport.setEnabled(false);
        //pack();

        createPopup();
    }

    /**
     * Splits up the window in two different panels.
     */
    private void splitUpWindow() {

        addEventsEditor(); // upperPanel.add(eventsEditor)
        addEventsTableModel(); // upperPanel.add(eventsQueue)
        addReports();

        addTables();
        addGraph();
        JSplitPane windowSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, lowerPanel);

        add(windowSplit);

        windowSplit.setDividerLocation(DEFAULT_HEIGHT / 3);
    }

    private void addGraph() {
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

        statusBar.add(statusBarMessage);
        add(statusBar, BorderLayout.SOUTH);

        redirect = new JCheckBoxMenuItem("Redirect Output", false);
        redirect.setAction(new SimulatorAction("Redirect output", () -> {
            if (redirect.getState()) {
                controller.setOutputStream(outputReports);
            } else {
                controller.setOutputStream(null);
            }
        }));
        stepsSpinner = new JSpinner(new SpinnerNumberModel(controller.getDefaultTime(),
                1, 1000, 1));
        stepsSpinner.setMaximumSize(new Dimension(50, 40));
        timeViewer = new JTextField("0", controller.getDefaultTime());
        timeViewer.setEditable(false);
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
        updatePanelBorder(eventsPanel, "Events");
        statusBarMessage.setText("Events have been cleared!");
        clearEvents.setEnabled(false);
        saveEvents.setEnabled(false);
        reset.setEnabled(false);
        stop.setEnabled(false);
        checkInEvents.setEnabled(false);
        generateReport.setEnabled(false);
        saveReport.setEnabled(false);
        run.setEnabled(false);
        clearReport.setEnabled(false);
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
     * Sets a panel border.
     *
     * @param panel
     * @param text Text to fill in
     */
    private void updatePanelBorder(JPanel panel, String text) {
        panel.setBorder(javax.swing.BorderFactory.createTitledBorder(text));
    }

    /**
     * Gives format to the event editor area.
     */
    private void addEventsEditor() {
        updatePanelBorder(eventsPanel, "Events");
        // Text Area creation, if a File was specified it will be loaded
        eventsEditorArea = new JTextArea("");
        eventsPanel.add(new JScrollPane(eventsEditorArea));

        //updateComponentBorder(eventsEditorArea, "Events");
        if (!"".equals(inFile.getName())) {
            // Trying to read the file with the given name by inFile
            try {
                eventsEditorArea.setText(readFile(inFile));
                updatePanelBorder(eventsPanel, "Events " + inFile.getName());
                statusBarMessage.setText("Events have been loaded to the simulator!");
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
        upperPanel.add(eventsPanel);
    }

    private void addEventsTableModel() {
        eventsTable = new TrafficModelTable(EVENTS_HEADER, eventsList);
        updateComponentBorder(eventsTable, "Events queue");
        upperPanel.add(eventsTable);
    }

    private void addReports() { // Removed updateComponentBorder :S
        JPanel reportsPanel = new JPanel(new BorderLayout());
        updatePanelBorder(reportsPanel, "Reports");
        reportsArea = new JTextArea("");
        reportsArea.setEditable(false);
        reportsPanel.add(new JScrollPane(reportsArea));
        upperPanel.add(reportsPanel);
    }

    private void addTables() {
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
            switch (type) {
                case events:
                    updatePanelBorder(eventsPanel, "Events: " + file.getName());
                    statusBarMessage.setText("Events have been saved at"
                            + file.getName() + "!");
                    break;
                case reports:
                    // TODO !!!!!!!!!!!!!!!!
                    updatePanelBorder(reportsPanel, "Reports " + file.getName());
                    statusBarMessage.setText("Reports have been saved!");
                    break;
                default:
                // you shouldn't arrive here
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
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String s = readFile(file);
            eventsEditorArea.setText(s);
            updatePanelBorder(eventsPanel, "Events: " + file.getName());
            statusBarMessage.setText("Events have been loaded from file"
                    + file.getName() + " to the simulator!");
            checkInEvents.setEnabled(true);
            clearEvents.setEnabled(true);
            saveEvents.setEnabled(true);
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
        return new Scanner(fileName).useDelimiter("\\A").next();
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

    @Override
    public void registered(TrafficSimulator.UpdateEvent ue) {
        // initialUpdateEvent = ue.clone();
        lastUpdateEvent = ue;
        //statusBarMessage.setText("");
    }

    @Override
    public void reset(TrafficSimulator.UpdateEvent ue) {
        // TODO
    }

    @Override
    public void newEvent(TrafficSimulator.UpdateEvent ue) {
        updateEventsQueue(ue.getEventQueue());
    }

    @Override
    public void advanced(TrafficSimulator.UpdateEvent ue) {
        updateEventsQueue(ue.getEventQueue());
        updateVehiclesTable(ue.getRoadMap().getVehicles());
        updateJunctionsTable(ue.getRoadMap().getJunctions());
        updateRoadsTable(ue.getRoadMap().getRoads());
        timeViewer.setText(String.valueOf(ue.getCurrentTime()));
        graph.update(ue.getRoadMap());
    }

    @Override
    public void error(TrafficSimulator.UpdateEvent ue, String error) {
        JOptionPane.showMessageDialog(this, "Error",
                error,
                JOptionPane.ERROR_MESSAGE);
        // Habría que resetear el controller?
    }

    // UPDATE COMPONENTS
    private void updateEventsQueue(List<Event> l) {
        eventsList = l;
        eventsTable.setElements(eventsList);
        eventsTable.update();
    }

    private void updateVehiclesTable(List<Vehicle> l) {
        vehiclesTable.setElements(l);
        vehiclesTable.update();
    }

    private void updateJunctionsTable(List<Junction> l) {
        junctionsTable.setElements(l);
        junctionsTable.update();
    }

    private void updateRoadsTable(List<Road> l) {
        roadsTable.setElements(l);
        roadsTable.update();
    }

    // EVENTS METHODS
    private void checkInEvents() {
        try { // EXCEPTION HERE MUST BE CONSIDERED, THROW AN UPDATE EVENT WHEN ERROR
            controller.loadEvents(new ByteArrayInputStream(eventsEditorArea
                    .getText().getBytes()));
            reset.setEnabled(true);
            run.setEnabled(true);
            //Aquí ponemos mensaje en la status bar?
        } catch (IOException e) {
        }
    }

    private void generateReport() {
        clearReport.setEnabled(true);
        saveReport.setEnabled(true);
        reportsArea.setText(new String(defaultOutputSimulator.toByteArray()));
        statusBarMessage.setText("Reports have been generated!");
    }

    private void clearReport() {
        clearReport.setEnabled(false);
        saveReport.setEnabled(false);
        reportsArea.setText("");
        statusBarMessage.setText("Reports have been cleared!");
    }

    private void runSimWindow() {
        stop.setEnabled(true);
        generateReport.setEnabled(true);
        if (redirect.getState()) {
            saveReport.setEnabled(true);
        }
        controller.run((int) stepsSpinner.getValue());
        statusBarMessage.setText("Advanced " + stepsSpinner.getValue() + " steps");
    }

    private void reset() {
        // Not finished
        reportsArea.setText("");
        eventsEditorArea.setText("");
        controller.reset();
        timeViewer.setText("0");
        stepsSpinner.setValue(controller.getDefaultTime());
        // Falta hacer el reset de las tablas y del grafo
        clearEvents.setEnabled(false);
        saveEvents.setEnabled(false);
        reset.setEnabled(false);
        stop.setEnabled(false);
        checkInEvents.setEnabled(false);
        generateReport.setEnabled(false);
        saveReport.setEnabled(false);
        run.setEnabled(false);
        clearReport.setEnabled(false);

        statusBarMessage.setText("The simulator has been reset!");
    }

    private void createPopup() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenu subMenu = new JMenu("Add Templates");
        JMenuItem loadOption = new JMenuItem("Load");
        loadOption.addActionListener(loadEvents);
        JMenuItem saveOption = new JMenuItem("Save");
        saveOption.addActionListener(saveEvents);
        JMenuItem clearOption = new JMenuItem("Clear");
        clearOption.addActionListener(clearEvents);
        Ini sec = null;

        try {
            sec = new Ini(new FileInputStream("src/main/resources/templates/templates.ini"));
        } catch (IOException e) {
            // TODO
        }

        List<IniSection> sectionsList = sec.getSections();
        for (IniSection s : sectionsList) {
            JMenuItem menuItem = new JMenuItem(s.getValue("simulatorName"));
            s.erase("simulatorName"); // Remove it because it is an additional section which is not showed on the events area
            menuItem.addActionListener((ActionEvent e) -> {
                eventsEditorArea.append(s.toString());
                saveEvents.setEnabled(true);
                clearEvents.setEnabled(true);
                checkInEvents.setEnabled(true);
            });
            subMenu.add(menuItem);
        }

        popupMenu.add(subMenu);
        popupMenu.addSeparator();
        popupMenu.add(loadOption);
        popupMenu.add(saveOption);
        popupMenu.add(clearOption);

        // Connect the popup menu to the text eventsEditorArea
        eventsEditorArea.addMouseListener(new MouseListener() {
            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger() && popupMenu.isEnabled()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }
        });

    }
}
