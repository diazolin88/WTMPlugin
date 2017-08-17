package view;

import com.intellij.openapi.ui.SimpleToolWindowPanel;

import javax.swing.*;
import java.util.Arrays;

public class DemoPanel extends SimpleToolWindowPanel {
    private JPanel panel1;
    private JTextArea textArea;
    private StringBuilder builder = new StringBuilder();

    public DemoPanel() {
        super(true);
        this.setName("TEXT");
        setContent(panel1);
    }

    public void setText(String... text) {
        Arrays.stream(text).forEach(s -> builder.append(s).append("\n"));
        textArea.setText(builder.toString());
    }

    public JTextArea getTextAria() {
        return this.textArea;
    }

}
