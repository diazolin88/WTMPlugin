package view;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;

import static utils.ComponentUtil.repaintComponent;

/**
 * This class should be used instead of TestRailWindow
 */
public class ToolWindow extends WindowPanelAbstract {
    private JPanel mainPanel;
    private JSplitPane split;
    private JPanel topMainPanel;
    private JPanel bottomMainPanel;

    private ToolWindow(Project project) {
        super(project);
        displayTopPanelDependsOnSettings(project);
        setDividerSize();
        setContent(mainPanel);
    }

    public static ToolWindow getInstance(Project project) {
        return ServiceManager.getService(project, ToolWindow.class);
    }

    private void displayTopPanelDependsOnSettings(Project project) {
        topMainPanel.setLayout(new BorderLayout());
        topMainPanel.add(MainPanel.getInstance(project));
        bottomMainPanel.setLayout(new BorderLayout());
        bottomMainPanel.add(TestRailWindow.getInstance(project).getDetailsPanel());
        repaintComponent(mainPanel);
    }

    private void setDividerSize() {
        split.setDividerSize(2);
    }
}
