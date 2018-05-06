package pr5.view;

import pr5.control.Controller;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import pr5.control.SimulatorAction;
import pr5.events.Event;
import pr5.view.dialog.DialogWindow;
import pr5.ini.Ini;
import pr5.model.Junction;
import pr5.model.Road;
import pr5.model.RoadMap;
import pr5.view.graphlayout.*;
import pr5.model.TrafficSimulator;
import pr5.model.TrafficSimulator.TrafficSimulatorListener;
import pr5.model.Vehicle;
import pr5.view.popupmenu.PopUpLayout;
import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

/**
 * SimulatedWindow object which represents a GUI interface for the user. This
 * window provides a new way to configurate a simulator apart from the batch
 * mode.
 */
public class SimWindow extends JFrame implements TrafficSimulatorListener {
    private static final int DEFAULT_DELAY = 500;
    /**
     * Toolkit allows us to get the screen size so size is relative to the
     * computer which executes the program Width will 3/4 of the Screen Size
     * Width
     */
    private static final int DEFAULT_WIDTH = 3*Toolkit.getDefaultToolkit().getScreenSize().width /4;
    /**
     * Height will 9/10 of the Screen Size Height
     */
    private static final int DEFAULT_HEIGHT = 9*Toolkit.getDefaultToolkit().getScreenSize().height/10;
    private TrafficSimulator.UpdateEvent lastUpdateEvent;
    /**
     * Event table header
     */
    private final String[] EVENTS_HEADER = {"#", "Time", "Type"};
    /**
     * Vehicles table header
     */
    private final String[] VEHICLES_HEADER = {"ID", "Road", "Location", "Speed", "Km", "Faulty Units", "Itinerary"};
    /**
     * Roads table header
     */
    private final String[] ROADS_HEADER = {"ID", "Source", "Target", "Length", "Max Speed", "Vehicles"};
    /**
     * Junctions table header
     */
    private final String[] JUNCTIONS_HEADER = {"ID", "Green", "Red"};

    @Override
    public void endRunning() {
        if(reportsArea.getText().length() > 0){
            clearReport.setEnabled(true);
            saveReport.setEnabled(true);
        }
        generateReport.setEnabled(true);
        stepsSpinner.setEnabled(true);
        delaySpinner.setEnabled(true);
        run.setEnabled(true);
    }

    /**
     * Two different types of output. Used to change the output stream.
     */
    private enum OUTPUT_TYPE {
        reports, events
    }
    private final FileNameExtensionFilter eventsFilter = new FileNameExtensionFilter(".ini", "ini");
    private final FileNameExtensionFilter reportsFilter = new FileNameExtensionFilter(".ini.out", "ini.out");
    private final JFileChooser eventsFileChooser = new JFileChooser();
    private final JFileChooser reportsFileChooser = new JFileChooser();
    private final JToolBar statusBar = new JToolBar();
    private final JLabel statusBarMessage = new JLabel("Welcome to the traffic simulator!");
    private final File inFile;
    private JCheckBoxMenuItem redirect;
    private final JSpinner delaySpinner= new JSpinner(new SpinnerNumberModel(DEFAULT_DELAY,
                0, 5000, 1));
    private JSpinner stepsSpinner;
    private JTextField timeViewer;
    private JTextArea eventsEditorArea;
    private final JPanel eventsPanel = new JPanel(new BorderLayout());
    private final JPanel reportsPanel = new JPanel(new BorderLayout());
    private final JPanel upperPanel = new JPanel(new GridLayout(1, 3));
    private final JPanel lowerPanel = new JPanel(new GridLayout(1, 2));
    private final JPanel tablesPanel = new JPanel(new GridLayout(3, 1));
    private JSplitPane windowSplit;
    private final Controller controller;
    private TrafficModelTable eventsTable;
    private TrafficModelTable roadsTable;
    private TrafficModelTable vehiclesTable;
    private TrafficModelTable junctionsTable;
    private GraphLayout graph;
    private JTextArea reportsArea;
    private List<Event> eventsList = new ArrayList<>();
    private TextAreaPrintStream outputReports;
    private final ByteArrayOutputStream defaultOutputSimulator = new ByteArrayOutputStream();
    private final Action loadEvents = new SimulatorAction(
            "Load Events", "open.png", "Load events from file", KeyEvent.VK_L, "alt L",
            () -> {
                try {
                    loadEventsFile();
                } catch (IOException | NoSuchElementException e) {
                    JOptionPane.showMessageDialog(this, "There was a problem "
                            + "while reading the file...",
                            "File cannot be read!", JOptionPane.WARNING_MESSAGE);
                }
            });
    private final Action saveEvents = new SimulatorAction(
            "Save Events", "save.png", "Save events to file", KeyEvent.VK_S, "alt S",
            () -> {
                try {
                    saveFile(OUTPUT_TYPE.events);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(),
                            "Events cannot be saved!", JOptionPane.WARNING_MESSAGE);
                }
            });
    private final Action clearEvents = new SimulatorAction(
            "Clear", "clear.png", "Clear events", () -> clearEvents());
    private final Action checkInEvents = new SimulatorAction(
            "Events", "events.png", "Check in events in the simulator",
            () -> checkInEvents());
    private final Action saveReport = new SimulatorAction(
            "Save Report", "save_report.png", "Save last report to file",
            KeyEvent.VK_R, "alt R",
            () -> {
                try {
                    saveFile(OUTPUT_TYPE.reports);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(),
                            "Reports cannot be saved!", JOptionPane.WARNING_MESSAGE);
                }
            });
    private final Action generateReport = new SimulatorAction(
            "Generate", "report.png", "Generate report", () -> generateReport());
    private final Action clearReport = new SimulatorAction(
            "Clear", "delete_report.png", "Clear report", () -> clearReport());
    private final Action exit = new SimulatorAction(
            "Exit", "exit.png", "Terminate the execution",
            KeyEvent.VK_E, "alt E", () -> System.exit(0));
    private final Action run = new SimulatorAction(
            "Run", "play.png", "Start simulation",
            () -> runSimWindow());
    private final Action stop = new SimulatorAction(
            "Stop", "stop.png", "Stop simulation",
            () -> stop() );
    private final Action reset = new SimulatorAction(
            "Reset", "reset.png", "Reset simulation", () -> reset());

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
        controller.addSimulatorListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        splitUpWindow();
        addBars();
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        outputReports = new TextAreaPrintStream(reportsArea);
        setVisible(true);
        createPopup();
        eventsFileChooser.setFileFilter(eventsFilter);
        reportsFileChooser.setFileFilter(reportsFilter);
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
     * Splits up the window in two different panels.
     */
    private void splitUpWindow() {
        addEventsEditor();
        addEventsTableModel();
        addReports();
        addsSimObjectTables();
        addGraph();
        windowSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel,
                lowerPanel);
        add(windowSplit);
        windowSplit.setDividerLocation(DEFAULT_HEIGHT / 4);
    }

    /**
     * Creates and adds the graph to the simulator.
     */
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
        Dimension spinnerDim = new Dimension(65, 40);
        delaySpinner.setPreferredSize(spinnerDim);
        delaySpinner.setMinimumSize(spinnerDim);
        delaySpinner.setMaximumSize(spinnerDim);
        stepsSpinner = new JSpinner(new SpinnerNumberModel(controller.getDefaultTime(),
                1, 1000, 1));
        stepsSpinner.setPreferredSize(spinnerDim);
        stepsSpinner.setMinimumSize(spinnerDim);
        stepsSpinner.setMaximumSize(spinnerDim);
        // Only numeric format is allowed
        ((NumberFormatter) ((JSpinner.NumberEditor) delaySpinner.getEditor())
                .getTextField().getFormatter()).setAllowsInvalid(false);
        ((NumberFormatter) ((JSpinner.NumberEditor) stepsSpinner.getEditor())
                .getTextField().getFormatter()).setAllowsInvalid(false);
        timeViewer = new JTextField("0", controller.getDefaultTime());
        Dimension timeViewerDim = new Dimension(70, 40);
        timeViewer.setMinimumSize(timeViewerDim);
        timeViewer.setPreferredSize(timeViewerDim);
        timeViewer.setMaximumSize(timeViewerDim);
        timeViewer.setEditable(false);
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
        // Adding options to Reports section in MenuBars
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
        bar.add(new JLabel(" Delay: "));
        bar.add(delaySpinner);
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
     * Adds the event editor and gives it its format.
     */
    private void addEventsEditor() {
        updatePanelBorder(eventsPanel, "Events");
        eventsEditorArea = new JTextArea("");
        eventsPanel.add(new JScrollPane(eventsEditorArea));
        // Usar este listener para ver cuándo hay texto en el eventseditor area para activar el clear
        eventsEditorArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                clearEvents.setEnabled(true);
                saveEvents.setEnabled(true);
                checkInEvents.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (e.getDocument().getLength() == 0) {
                    clearEvents.setEnabled(false);
                    saveEvents.setEnabled(false);
                    checkInEvents.setEnabled(false);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // This event will not be fired as we use a PlainDocument
            }
        });
        if (!"".equals(inFile.getName())) {
            try {
                eventsEditorArea.setText(readFile(inFile));
                updatePanelBorder(eventsPanel, "Events " + inFile.getName());
                statusBarMessage.setText("Events have been loaded to the simulator!");
            } catch (IOException | NoSuchElementException e) {
                JOptionPane.showMessageDialog(this, "There was a problem "
                        + "while reading the file...",
                        "File cannot be read!",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
        eventsEditorArea.setEditable(true);
        eventsEditorArea.setLineWrap(true);
        eventsEditorArea.setWrapStyleWord(true);
        upperPanel.add(eventsPanel);
    }

    /**
     * Creates and adds the event table.
     */
    private void addEventsTableModel() {
        eventsTable = new TrafficModelTable(EVENTS_HEADER, eventsList);
        updateComponentBorder(eventsTable, "Events queue");
        upperPanel.add(eventsTable);
    }

    /**
     * Creates and adds the report area.
     */
    private void addReports() {
        updatePanelBorder(reportsPanel, "Reports");
        reportsArea = new JTextArea("");
        reportsArea.setEditable(false);
        reportsArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(!stop.isEnabled() || (stop.isEnabled() && run.isEnabled())){
                    clearReport.setEnabled(true);
                    saveReport.setEnabled(true);
                } 
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (e.getDocument().getLength() == 0) {
                    clearReport.setEnabled(false);
                    saveReport.setEnabled(false);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // This event will not be fired as we use a PlainDocument
            }
        });
        reportsPanel.add(new JScrollPane(reportsArea));
        upperPanel.add(reportsPanel);
    }

    /**
     * Creates and adds the different tables for the simulated objects.
     */
    private void addsSimObjectTables() {
        roadsTable = new TrafficModelTable(ROADS_HEADER, lastUpdateEvent
                .getRoadMap().getRoads());
        updateComponentBorder(roadsTable, "Roads");
        vehiclesTable = new TrafficModelTable(VEHICLES_HEADER, lastUpdateEvent
                .getRoadMap().getVehicles());
        updateComponentBorder(vehiclesTable, "Vehicles");
        junctionsTable = new TrafficModelTable(JUNCTIONS_HEADER, lastUpdateEvent
                .getRoadMap().getJunctions());
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
    private void saveFile(OUTPUT_TYPE type) throws IOException {
        switch (type) {
            case events: {
                saveFile(eventsFileChooser, eventsEditorArea, eventsPanel, "Event");
                break;
            }
            case reports: {
                reportsFileChooser.setSelectedFile(new File(eventsFileChooser
                        .getSelectedFile().getName() + ".out")); // Recommendable name to save file
                saveFile(reportsFileChooser, reportsArea, reportsPanel, "Report");
                break;
            }
        }
    }

    private void saveFile(JFileChooser chooser, JTextArea textArea,
            JPanel componentModified, String name) throws IOException {
        if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(null)) {
            writeFile(chooser.getSelectedFile(), textArea.getText());
            updatePanelBorder(componentModified, name + ": " + chooser.getSelectedFile().getName());
            statusBarMessage.setText(name + " have been saved at " + chooser.getSelectedFile().getName() + "!");
        }
    }

    /**
     * Loads the events from a given file.
     *
     * @throws IOException if file cannot be opened
     * @throws NoSuchElementException if file does not exist
     */
    private void loadEventsFile() throws IOException, NoSuchElementException {
        if (eventsFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = eventsFileChooser.getSelectedFile();
            eventsEditorArea.setText(readFile(file));
            updatePanelBorder(eventsPanel, "Events: " + file.getName());
            statusBarMessage.setText("Events have been loaded from file "
                    + file.getName() + " to the simulator!");
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
        lastUpdateEvent = ue;
    }
    public void stop(){
        run.setEnabled(true);
        stop.setEnabled(false);
        loadEvents.setEnabled(true);
        checkInEvents.setEnabled(true);
        clearEvents.setEnabled(true);
        saveEvents.setEnabled(true);
        generateReport.setEnabled(true);
        reset.setEnabled(true);
        controller.stop();
        stepsSpinner.setEnabled(true);
        delaySpinner.setEnabled(true);
        statusBarMessage.setText("The simulator has been stopped!");
    }
    @Override
    public void reset(TrafficSimulator.UpdateEvent ue) {
        updatePanelBorder(reportsPanel, "Reports");
        reportsArea.setText("");
        timeViewer.setText("0");
        stepsSpinner.setValue(controller.getDefaultTime());
        eventsTable.clear();
        eventsTable.update();
        vehiclesTable.clear();
        vehiclesTable.update();
        roadsTable.clear();
        roadsTable.update();
        junctionsTable.clear();
        junctionsTable.update();
        reset.setEnabled(false);
        stop.setEnabled(false);
        generateReport.setEnabled(false);
        run.setEnabled(false);
        graph.update(new RoadMap());
        statusBarMessage.setText("The simulator has been reset!");
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
    // Finish it
    public void error(TrafficSimulator.UpdateEvent ue, String error) {
        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Updates the events table.
     *
     * @param updatedEvents
     */
    private void updateEventsQueue(List<Event> updatedEvents) {
        eventsList = updatedEvents;
        eventsTable.setElements(eventsList);
        eventsTable.update();
    }

    /**
     * Updates the vehicles table.
     *
     * @param updatedVehicle
     */
    private void updateVehiclesTable(List<Vehicle> updatedVehicle) {
        vehiclesTable.setElements(updatedVehicle);
        vehiclesTable.update();
    }

    /**
     * Updates the junctions table.
     *
     * @param updatedJunctions
     */
    private void updateJunctionsTable(List<Junction> updatedJunctions) {
        junctionsTable.setElements(updatedJunctions);
        junctionsTable.update();
    }

    /**
     * Updates the roads table.
     *
     * @param updatedRoads
     */
    private void updateRoadsTable(List<Road> updatedRoads) {
        roadsTable.setElements(updatedRoads);
        roadsTable.update();
    }

    /**
     * Deals with the events from events editor area.
     */
    // Finish it
    private void checkInEvents() {
        try { 
            controller.loadEvents(new ByteArrayInputStream(eventsEditorArea
                    .getText().getBytes()));
            reset.setEnabled(true);
            run.setEnabled(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                           "Error at check-in events", 
                           JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Generates the report of the current state of the simulator.
     */
    private void generateReport() {
        Ini ini = new Ini();
        DialogWindow dialog = new DialogWindow(this);
        dialog.setData(lastUpdateEvent.getRoadMap().getVehicles(),
                lastUpdateEvent.getRoadMap().getRoads(),
                lastUpdateEvent.getRoadMap().getJunctions());
        dialog.setModal(true);
        if (dialog.open() > 0) {
            controller.writeReport(ini, dialog.getSelectedVehicles());
            controller.writeReport(ini, dialog.getSelectedRoads());
            controller.writeReport(ini, dialog.getSelectedJunctions());
            reportsArea.setText(ini.toString());
            statusBarMessage.setText("Reports have been generated!");
        }
    }

    /**
     * Clears the reports area.
     */
    private void clearReport() {
        reportsArea.setText("");
        statusBarMessage.setText("Reports have been cleared!");
    }

    /**
     * Runs the simulation.
     */
    private void runSimWindow() {
        saveReport.setEnabled(false);
        clearReport.setEnabled(false);
        generateReport.setEnabled(false);
        run.setEnabled(false);
        stop.setEnabled(true);
        loadEvents.setEnabled(false);
        checkInEvents.setEnabled(false);
        clearEvents.setEnabled(false);
        saveEvents.setEnabled(false);
        reset.setEnabled(false);
        stepsSpinner.setEnabled(false);
        delaySpinner.setEnabled(false);
        controller.run((int) stepsSpinner.getValue(), 
                (int) delaySpinner.getValue());
        statusBarMessage.setText("Advanced " + stepsSpinner.getValue() + " steps");
    }

    /**
     * Resets the window.
     */
    private void reset() {
        updatePanelBorder(eventsPanel, "Events");
        eventsEditorArea.setText("");
        controller.reset();
    }

    /**
     * Creates the popup menu for the events editor area. Options available: add
     * template, load, save and clear.
     */
    private void createPopup() {
        (new PopUpLayout(loadEvents, saveEvents, clearEvents,
                checkInEvents, eventsEditorArea)).createPopUp();
    }
}
