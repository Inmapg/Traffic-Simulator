package pr5.control;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * A simple action. Once created, can be added multiple times; and enabled and
 * disabled at all places at once.
 */
public class SimulatorAction extends AbstractAction {

    private static final String ICON_BASE = "icons/";

    private Runnable callback;

    /**
     * Creates an action for use in buttons, toolbar-buttons, and menus of all
     * stripes
     *
     * @param name to use in menus (via toString), and as identifier when
     * registering.
     * @param iconName used to locate an icon
     * @param tooltip to display when hovered-over
     * @param mnemonic for menus: a letter that will be underlined. Example:
     * KeyEvent.VK_L is an 'l', KeyEvent.VK_A is an 'a', ...
     * @param accelerator for calling the action without using the mouse at all.
     * See https://docs.oracle.com/javase/8/docs/api/index.html for details.
     * Examples include: "control DELETE" or "alt shift X"
     */
    public SimulatorAction(Object name, String iconName,
            String tooltip, Integer mnemonic, String accelerator,
            Runnable callback) {
        super("" + name);
        putValue(Action.SHORT_DESCRIPTION, tooltip);
        putValue(Action.MNEMONIC_KEY, mnemonic);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator));
        BufferedImage icon = loadImage(iconName);
        BufferedImage small = scaleImage(icon, 16, 16);
        putValue(Action.LARGE_ICON_KEY, new ImageIcon(icon));
        putValue(Action.SMALL_ICON, new ImageIcon(small));
        this.callback = callback;
    }

    public SimulatorAction(Object name, String iconName,
            String tooltip, Runnable callback) {
        super("" + name);
        putValue(Action.SHORT_DESCRIPTION, tooltip);
        BufferedImage icon = loadImage(iconName);
        BufferedImage small = scaleImage(icon, 16, 16);
        putValue(Action.LARGE_ICON_KEY, new ImageIcon(icon));
        putValue(Action.SMALL_ICON, new ImageIcon(small));
        this.callback = callback;
    }

    // adapted from https://stackoverflow.com/a/35637914/15472
    private static BufferedImage scaleImage(BufferedImage original, int w, int h) {
        BufferedImage scaled = new BufferedImage(w, h, original.getType());
        Graphics2D g2d = scaled.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(original, 0, 0, w, h, null);
        g2d.dispose();
        return scaled;
    }

    private static BufferedImage loadImage(String iconName) {
        try {
            InputStream is = SimulatorAction.class.getClassLoader()
                    .getResourceAsStream(ICON_BASE + iconName);
            return ImageIO.read(is);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "No such image: '" + ICON_BASE + iconName + "'");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        callback.run();
    }

    /**
     * Register this action at a given component.
     *
     * @param component
     */
    public void register(JComponent component) {
        component.getActionMap().put(
                getValue(Action.NAME), this);
    }
}
