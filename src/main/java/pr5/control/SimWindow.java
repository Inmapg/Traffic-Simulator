package pr5.control;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

/**
 * SimulatedWindow object which represents a GUI interface for the user. This
 * window provides a new way to configurate a simulator apart from the batch
 * mode.
 */
public class SimWindow extends JFrame {

    int defaultTimeValue;
    String inFile;

    public SimWindow(String inFile, int defaultTimeValue) {
        super("Traffic Simulator");
        this.defaultTimeValue = defaultTimeValue;
        this.inFile = inFile;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addBars();

        setSize(1000, 1000);
        setVisible(true);
    }

    private void addBars() {
        // instantiate actions

        /*SimulatorAction salir = new SimulatorAction(
				"Salir", "exit.png", "Salir de la aplicacion",
				KeyEvent.VK_A, "control shift X", 
				()-> System.exit(0));
		SimulatorAction guardar = new SimulatorAction(
				"Guardar", "save.png", "Guardar cosas",
				KeyEvent.VK_S, "control S", 
				()-> System.err.println("guardando..."));*/
        // add actions to toolbar, and bar to window
        JToolBar bar = new JToolBar();
        //bar.add(salir);
        //bar.add(guardar);
        add(bar, BorderLayout.NORTH);

        // add actions to menubar, and bar to window
        JMenu file = new JMenu("File");
        //file.add(guardar);		
        //file.add(salir);		
        JMenuBar menu = new JMenuBar();
        menu.add(file);
        setJMenuBar(menu);
    }
}
