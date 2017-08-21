package view;

import com.intellij.openapi.project.Project;
import exceptions.AuthorizationException;
import model.CredentialsData;
import model.jira.JiraConnection;
import model.testrail.RailConnection;

import javax.swing.*;
import java.util.NoSuchElementException;

public class WTMSettingsWindow extends WindowPanelAbstract implements CredentialsData {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JTextField railUrlTextField;
    private JPasswordField railPasswordField;
    private JTextField railUserNameTextField;
    private JButton railTestConnectionButton;
    private JTextPane railDebugTextPane;
    //Jira  tab fields
    private JTextField jiraUrlTextField;
    private JTextField jiraUserNameTextField;
    private JPasswordField jiraPasswordField;
    private JButton jiraTestConnectionButton;
    private JTextPane jiraDebugTextPane;

    public WTMSettingsWindow(Project project) {
        super(project);
        setContent(mainPanel);
        railTestConnectionButtonClickedAction(project);
        jiraTestconnectionButtonClickedActuion(project);
    }

    public void setSettings() {
        settings.setRailPassword(railPasswordField.getPassword());
        settings.setRailUserName(railUserNameTextField.getText());
        settings.setRailUrl(railUrlTextField.getText());

        settings.setJiraPassword(jiraPasswordField.getPassword());
        settings.setJiraUserName(jiraUserNameTextField.getText());
        settings.setJiraUrl(jiraUrlTextField.getText());
    }

    public boolean isModified() {
        return !railUserNameTextField.getText().equals(settings.getRailUserName())
                || railPasswordField.getPassword() != settings.getRailPassword().toCharArray()
                || !railUrlTextField.getText().equals(settings.getRailUrl())

                || !jiraUrlTextField.getText().equals(settings.getJiraUrl())
                || jiraPasswordField.getPassword() != settings.getJiraPassword().toCharArray()
                || !jiraUserNameTextField.getText().equals(settings.getJiraUserName());
    }

    public void reset() {
        railPasswordField.setText(settings.getRailPassword());
        railUserNameTextField.setText(settings.getRailUserName());
        railUrlTextField.setText(settings.getRailUrl());

        jiraUrlTextField.setText(settings.getJiraUrl());
        jiraPasswordField.setText(settings.getJiraPassword());
        jiraUserNameTextField.setText(settings.getJiraUserName());
    }

    private void railTestConnectionButtonClickedAction(Project project) {
        railTestConnectionButton.addActionListener(listener ->
                {
                    RailConnection.getInstance(project).login(settings).projects();
                }
        );
    }

    private void jiraTestconnectionButtonClickedActuion(Project project) {
        jiraTestConnectionButton.addActionListener(listener -> {
            try {
                JiraConnection.getInstance(project).login(settings, this);
            } catch (AuthorizationException e) {
                jiraDebugTextPane.setText(e.getMessage());
            }
        });
    }

    @Override
    public String getUserName(int tab) {
        switch (tab) {
            case 0:
                return railUserNameTextField.getText();
            case 1:
                return jiraUserNameTextField.getText();
            default:
                throw new NoSuchElementException("There is no such tab with id: " + tab);
        }
    }

    @Override
    public String getPassword(int tab) {
        switch (tab) {
            case 0:
                return railPasswordField.getPassword().toString();
            case 1:
                return jiraPasswordField.getPassword().toString();
            default:
                throw new NoSuchElementException("There is no such tab with id: " + tab);
        }
    }

    @Override
    public String getUrl(int tab) {
        switch (tab) {
            case 0:
                return railUrlTextField.getText();
            case 1:
                return jiraUrlTextField.getText();
            default:
                throw new NoSuchElementException("There is no such tab with id: " + tab);
        }
    }
}
