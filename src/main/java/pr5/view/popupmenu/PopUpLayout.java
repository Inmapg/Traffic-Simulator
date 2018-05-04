package pr5.view.popupmenu;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import pr5.ini.Ini;
import pr5.ini.IniSection;

public class PopUpLayout {

    private Action loadEvents;
    private Action saveEvents;
    private Action clearEvents;
    private Action checkInEvents;
    private JTextArea eventsEditorArea;

    public PopUpLayout(Action loadEvents, Action saveEvents, Action clearEvents,
            Action checkInEvents, JTextArea eventsEditorArea) {
        this.loadEvents = loadEvents;
        this.saveEvents = saveEvents;
        this.clearEvents = clearEvents;
        this.checkInEvents = checkInEvents;
        this.eventsEditorArea = eventsEditorArea;
    }

    public void createPopUp() {
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

        for (IniSection s : sec.getSections()) {
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
