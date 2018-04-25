package pr5.control;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
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

    private int defaultTimeValue;
    private String inFile;
    private JCheckBoxMenuItem redirect;
    private JSpinner stepsSpinner;
    private JTextField timeViewer;
    private JPanel mainPanel;
    private JPanel contentPanel_1;
    private JFileChooser fileChooser; 
    
    public SimWindow(String inFile, int defaultTimeValue) {
        super("Traffic Simulator");
        this.defaultTimeValue = defaultTimeValue;
        this.inFile = inFile;
        initGUI();
    }

    private void initGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createMainPanel();
        addBars();
        setSize(1000, 1000);
        setVisible(true);
    }
    
    private void createMainPanel(){
        mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);
        contentPanel_1 = new JPanel();
        contentPanel_1.setLayout(new BoxLayout(contentPanel_1, BoxLayout.Y_AXIS));
        // Split up the window in more panels and set up the layout 
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
                KeyEvent.VK_L, "alt L",
                () -> System.out.println("AQUÍ CARGA LOS EVENTOS GUAPI"));

        SimulatorAction saveEvents = new SimulatorAction(
                "Save Events", "save.png", "Save events to file",
                KeyEvent.VK_S, "alt S",
                () -> System.out.println("AQUÍ GUARDA LOS EVENTOS GUAPI"));

        SimulatorAction clearEvents = new SimulatorAction(
                "Clear", "clear.png", "Clear events",
                () -> System.out.println("Generate baby"));

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
                () -> System.out.println("AQUÍ TERMINA LA EJECUCIÓN GUAPI, "
                        + "HAY QUE PREGUNTAR SI QUIERE GUARDAR ALGO???"));
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

    private void saveFile() {
        /* int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			writeFile(file, textArea.getText());
		}
        */
	}

    private void loadFile() {
        /*
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String s = readFile(file);
			textArea.setText(s);
		}
        */
	}

    private static String readFile(File file) {
        /*
		String s = "";
		try {
			s = new Scanner(file).useDelimiter("\\A").next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return s;
        */
        return "";
	}

    private static void writeFile(File file, String content) {
         /*
		try {
			PrintWriter pw = new PrintWriter(file);
			pw.print(content);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} */
         
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
