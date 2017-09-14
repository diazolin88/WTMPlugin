package view;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

import javax.swing.*;

public class NotLoggedIn extends WindowPanelAbstract{

    private JPanel mainPanel;

    private NotLoggedIn(Project project) {
        super(project);
        setContent(mainPanel);
    }

    public static NotLoggedIn getInstance(Project project) {
        return ServiceManager.getService(project, NotLoggedIn.class);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
