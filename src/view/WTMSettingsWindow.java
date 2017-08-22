package view;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import exceptions.AuthorizationException;
import model.CredentialsData;
import model.testrail.RailConnection;
import settings.WTMSettings;

import javax.swing.*;
import java.util.NoSuchElementException;

public class WTMSettingsWindow extends WindowPanelAbstract implements CredentialsData,Disposable {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JTextField railUrlTextField;
    private JPasswordField railPasswordField;
    private JTextField railUserNameTextField;
    private JButton railTestConnectionButton;
    private JTextPane railDebugTextPane;

    //TODO add new settings window for Jira tab fields
    private JTextField jiraUrlTextField;
    private JTextField jiraUserNameTextField;
    private JPasswordField jiraPasswordField;
    private JButton jiraTestConnectionButton;
    private JTextPane jiraDebugTextPane;

    private Project project;

    public WTMSettingsWindow(Project project) {
        super(project);
        this.project = project;
        setContent(mainPanel);
    }

    public JTextField getRailUrlTextField() {

        return railUrlTextField;
    }

    public JPasswordField getRailPasswordField() {
        return railPasswordField;
    }

    public JTextField getRailUserNameTextField() {
        return railUserNameTextField;
    }

    public JButton getRailTestConnectionButton() {
        return railTestConnectionButton;
    }

    public JTextPane getRailDebugTextPane() {
        return railDebugTextPane;
    }

    public JTextField getJiraUrlTextField() {
        return jiraUrlTextField;
    }

    public JTextField getJiraUserNameTextField() {
        return jiraUserNameTextField;
    }

    public JPasswordField getJiraPasswordField() {
        return jiraPasswordField;
    }

    public JButton getJiraTestConnectionButton() {
        return jiraTestConnectionButton;
    }

    public JTextPane getJiraDebugTextPane() {
        return jiraDebugTextPane;
    }

    public void setSettings() {
        settings.setRailPassword(railPasswordField.getPassword());
        settings.setRailUserName(railUserNameTextField.getText());
        settings.setRailUrl(railUrlTextField.getText());

        settings.setJiraPassword(jiraPasswordField.getPassword());
        settings.setJiraUserName(jiraUserNameTextField.getText());
        settings.setJiraUrl(jiraUrlTextField.getText());
    }

    public static WTMSettingsWindow getInstance(Project project) {
        return ServiceManager.getService(project, WTMSettingsWindow.class);
    }

    @Override
    public void dispose() {
        ToolWindowManager.getInstance(project).unregisterToolWindow(this.getName());
    }

    public boolean isModified() {
        return !railUserNameTextField.getText().equals(settings.getRailUserName())
                || railPasswordField.getPassword() != settings.getRailPassword().toCharArray()
                || !railUrlTextField.getText().equals(settings.getRailUrl());
    }

    public void reset() {
        railPasswordField.setText(settings.getRailPassword());
        railUserNameTextField.setText(settings.getRailUserName());
        railUrlTextField.setText(settings.getRailUrl());

        jiraUrlTextField.setText(settings.getJiraUrl());
        jiraPasswordField.setText(settings.getJiraPassword());
        jiraUserNameTextField.setText(settings.getJiraUserName());
    }

    public void railTestConnectionButtonClickedAction(Project project, WTMSettingsWindow component) {
        if (isModified()) {
            railTestConnectionButton.addActionListener(listener ->
            {
                try {
                    RailConnection.getInstance(project).login(component);
                } catch (AuthorizationException e) {
                    railDebugTextPane.setText(e.getMessage());
                }
            });
        } else {
            railTestConnectionButton.addActionListener(listener -> {
                try {
                    RailConnection.getInstance(project).login(settings);
                } catch (AuthorizationException e) {
                    railDebugTextPane.setText(e.getMessage());
                }
            });
        }
    }

    public void jiraTestConnectionButtonClickedAction(Project project) {
        return;
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
