package view;

import com.intellij.openapi.ui.SimpleToolWindowPanel;

import javax.swing.*;

public class FailedTestForm extends SimpleToolWindowPanel {
    private JList failedTestListView;
    private JButton run;
    private JPanel main;

    public FailedTestForm(boolean vertical) {
        super(vertical);

        // The controller =)
        String[] model = {"Test 1", "Test 2"};
        failedTestListView.setListData(model);
        failedTestListView.setVisibleRowCount(5);

        setContent(main);
    }
}
