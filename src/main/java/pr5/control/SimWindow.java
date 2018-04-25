package pr5.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import pr5.events.Event;
import pr5.exception.SimulatorError;
import pr5.model.RoadMap;
import pr5.model.TrafficSimulator.TrafficSimulatorObserver;

/**
 * SimulatedWindow object which represents a GUI interface for the user. This
 * window provides a new way to configurate a simulator apart from the batch
 * mode.
 */
public class SimWindow extends JFrame implements TrafficSimulatorObserver {

    private final int defaultTimeValue;
    // Toolkit allows us to get the screen size so size is relative
    // to the computer which executes the program
    // Width will 2/3 of the Screen Size Width
    private static final int DEFAULT_WIDTH = 2 * Toolkit.getDefaultToolkit().getScreenSize().width / 3;
    // Height will 5/6 of the Screen Size Height
    private static final int DEFAULT_HEIGHT = 5 * Toolkit.getDefaultToolkit().getScreenSize().height / 6;
    private enum OUTPUT_TYPE { reports , events }
    private File inFile;
    private JCheckBoxMenuItem redirect;
    private JSpinner stepsSpinner;
    private JTextField timeViewer;
    private JFileChooser fileChooser;
    private JTextArea eventsEditorArea;
    private JPanel upperPanel;
    private JPanel lowerPanel;

    public SimWindow(String inFile, int defaultTimeValue) {
        super("Traffic Simulator");
        this.defaultTimeValue = defaultTimeValue;
        this.inFile = new File(inFile);
        initializeFileChooser();
        initGUI();
    }

    private void initializeFileChooser(){
        fileChooser = new JFileChooser();
       /* fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return ".ini".equals(pathname.getName().toLowerCase()) ||
                        ".ini.eout".equals((pathname.getName().toLowerCase()));
            }

            @Override
            public String getDescription() {
               return ".ini for events or .ini.eout for reports";
            }

        });
        */     
    }
    
    private void initGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        splitUpWindow();
        addBars();
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setVisible(true);

    }

    private void splitUpWindow() {
        upperPanel = new JPanel(new GridLayout(1, 3));

        JPanel eventsQueue = new JPanel();
        JPanel reportsArea = new JPanel();

        eventsQueue.setBackground(Color.blue);
        reportsArea.setBackground(Color.green);

        addEventEditor(); // upperPanel.add(eventsEditor)
        upperPanel.add(eventsQueue);
        upperPanel.add(reportsArea);

        lowerPanel = new JPanel(new GridLayout(1, 2));
        JPanel tables = new JPanel();
        JPanel graph = new JPanel();
        tables.setBackground(Color.orange);
        graph.setBackground(Color.yellow);
        lowerPanel.add(tables);
        lowerPanel.add(graph);
        JSplitPane windowSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, lowerPanel);

        add(windowSplit);
        windowSplit.setDividerLocation(DEFAULT_HEIGHT / 3);

    }

    private void addBars() {
        JMenuBar menu = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu simulator = new JMenu("Simulator");
        JMenu report = new JMenu("Reports");
        JToolBar bar = new JToolBar();

        // Event Actions and Object Creation and Instantiation 
        SimulatorAction loadEvents = new SimulatorAction(
                "Load Events", "open.png", "Load events from file",
                KeyEvent.VK_L, "alt L", () -> {
                    try {
                        loadFile();
                    } catch (IOException | NoSuchElementException e) {
                        JOptionPane.showMessageDialog(this, "There was a problem "
                                + "when reading the file...",
                                "File cannot be read!",
                                JOptionPane.WARNING_MESSAGE);
                    }
                    
                });

        SimulatorAction saveEvents = new SimulatorAction(
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

        SimulatorAction clearEvents = new SimulatorAction(
                "Clear", "clear.png", "Clear events",
                () -> {
                    clearEvents();
                        });

        SimulatorAction checkInEvents = new SimulatorAction(
                "Events", "events.png", "Show the events",
                () -> System.out.println("showing events..."));

        // Report Actions and Object Creation and Instantiation
        SimulatorAction saveReport = new SimulatorAction(
                "Save Report", "save_report.png", "Save last report to file",
                KeyEvent.VK_R, "alt R",
                () -> System.out.println("AQUÍ GUARDA EL REPORT GUAPI"));

        SimulatorAction generateReport = new SimulatorAction(
                "Generate", "report.png", "Generate report",
                () -> System.out.println("Generate baby"));

        SimulatorAction clearReport = new SimulatorAction(
                "Clear", "delete_report.png", "Clear report",
                () -> System.out.println("Clearing report"));

        // Traffic Simulator and configuration Object creation and instantiation
        SimulatorAction exit = new SimulatorAction(
                "Exit", "exit.png", "Terminate the execution",
                KeyEvent.VK_E, "alt E",
                () -> System.exit(0));
        SimulatorAction run = new SimulatorAction(
                "Run", "play.png", "Start simulation",
                () -> System.out.println("Holoo coches incoming"));

        SimulatorAction stop = new SimulatorAction(
                "Stop", "stop.png", "Stop simulation",
                () -> System.out.println("Simulación detenida."));

        SimulatorAction reset = new SimulatorAction(
                "Reset", "reset.png", "Reset simulation",
                () -> System.out.println("Reseeeeeet baby"));

        redirect = new JCheckBoxMenuItem("Redirect Output", false);

        stepsSpinner = new JSpinner(new SpinnerNumberModel(defaultTimeValue,
                1, 1000, 1));
        stepsSpinner.setMaximumSize(new Dimension(50, 40));
        timeViewer = new JTextField("0", defaultTimeValue);
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
        bar.addSeparator();
        bar.add(exit);

        // Pinning up the bar to the window
        bar.setFloatable(false);
        // Setting in ToolBar in the Window 

        add(bar, BorderLayout.PAGE_START);

    }

    private void clearEvents(){
        eventsEditorArea.setText("");
        updateComponentBorder(eventsEditorArea, "Events");
    }
    private void updateComponentBorder(JComponent c, String text) {
        c.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black, 1), text));
    }

    private void addEventEditor() {
        // Text Area creation, if a File was specified it will be loaded
        eventsEditorArea = new JTextArea("");
        updateComponentBorder(eventsEditorArea, "Events");
        if (!"".equals(inFile.getName())) {
            // We try to read the file with the given name by inFile
            try {
                eventsEditorArea.setText(readFile(inFile));
                updateComponentBorder(eventsEditorArea, "Events: " + inFile.getName());
            } // We capture and control the exception
            catch (IOException | NoSuchElementException e) {
               JOptionPane.showMessageDialog(this, "There was a problem "
                                + "when reading the file...",
                                "File cannot be read!",
                                JOptionPane.WARNING_MESSAGE);
            }
        }
        // Text Area configuration
        eventsEditorArea.setEditable(true);
        eventsEditorArea.setLineWrap(true);
        eventsEditorArea.setWrapStyleWord(true);
        // To allow scrolling we use a ScrollPane
        JScrollPane area = new JScrollPane(eventsEditorArea);
        upperPanel.add(area);
    }

    private void saveFile(String ext, OUTPUT_TYPE type) throws IOException {
        int returnVal = fileChooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            writeFile(file, eventsEditorArea.getText());
            
            // Update the GUI
            switch(type){
                case events:
                    updateComponentBorder(eventsEditorArea, "Events: " + file.getName());
                    break;
                case reports:
                    
                    break;
                default:
                    // you shouldn't be here
            }
            
        }
    }

  
    private void loadFile() throws IOException, NoSuchElementException {
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String s = readFile(file);
            eventsEditorArea.setText(s);
            updateComponentBorder(eventsEditorArea, "Events: " + file.getName());
        }
    }

    private String readFile(File fileName) throws IOException {
        String s = "";
        s = new Scanner(fileName).useDelimiter("\\A").next();
        return s;
    }

    private static void writeFile(File file, String content) throws IOException {
        PrintWriter pw = new PrintWriter(file);
        pw.print(content);
        pw.close();
    }

    @Override
    public void registered(int time, RoadMap map, List<Event> events) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reset(int time, RoadMap map, List<Event> events) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void eventAdded(int time, RoadMap map, List<Event> events) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void advanced(int time, RoadMap map, List<Event> events) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void simulatorError(int time, RoadMap map, List<Event> events, SimulatorError e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
