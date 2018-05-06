package pr6.view;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

/**
 * Gives the output stream to the text area.
 */
public class TextAreaPrintStream extends OutputStream {

    private JTextArea textArea;

    /**
     * Class constructor specifying the text area.
     *
     * @param textArea
     */
    public TextAreaPrintStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        // redirects data to the text area
        textArea.append(String.valueOf((char) b));
        // scrolls the text area to the end of data
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
