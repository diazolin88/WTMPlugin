package model.jira;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import exceptions.AuthorizationException;
import model.TMConnectable;
import org.jetbrains.annotations.NotNull;
import ru.wiley.jira.BasicCredentials;
import ru.wiley.jira.JiraClient;
import ru.wiley.jira.JiraException;
import settings.WTMSettings;
import view.WTMSettingsWindow;

import javax.swing.*;

public class JiraConnection implements TMConnectable<JiraClient> {

    public static JiraConnection getInstance(Project project) {
        return ServiceManager.getService(project, JiraConnection.class);
    }

    @Override
    public JiraClient login(WTMSettings state, JComponent component) {
        if (null == component) {
            try {
                return getJiraClient(state);
            } catch (Exception e) {
                throw new AuthorizationException("Unable to login ti jira by given credentials");
            }
        } else {
            return getJiraClient(state);
        }
    }

    @NotNull
    private JiraClient getJiraClient(WTMSettings state) throws JiraException {
        JiraClient client = new JiraClient(state.getJiraUrl(), new BasicCredentials(state.getJiraUserName(), state.getJiraPassword()));
        client.getProjects();
        return new JiraClient(state.getJiraUrl(), new BasicCredentials(state.getJiraUserName(), state.getJiraPassword()));
    }

    private JiraClient getJiraClientByComponent(WTMSettingsWindow component){
        JiraClient client = null;
        try {
            client = new JiraClient(component.getJiraUrlTextField().getText(), new BasicCredentials(component.getJiraUserNameTextField().getText(), component.getJiraPasswordField().getPassword().toString()));
            client.getProjects();
            return new JiraClient(state.getJiraUrl(), new BasicCredentials(state.getJiraUserName(), state.getJiraPassword()));
        } catch (JiraException e) {
            e.printStackTrace();
        }
    }
}
