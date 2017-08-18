package view;

import com.intellij.openapi.project.Project;

import javax.swing.*;

public class WTMSettingsWindow extends WindowPanelAbstract {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JTextField urlTextField;
    private JPasswordField passwordField;
    private JTextField userNameTextField;
    private JButton railTestConnectionButton;
    private JTextPane debugTextPane;
    //Jira  tab fields
    private JTextField jiraUrlTextField;
    private JTextField jiraUserNameTextField;
    private JPasswordField jiraPasswordField;
    private JButton jiraTestConnectionButton;

    public WTMSettingsWindow(Project project) {
        super(project);
        setContent(mainPanel);
        railTestConnectionButton.addActionListener(e -> {

           debugTextPane.setText("TEXT");

        });
    }

    public void setSettings(){
        mainPanel.getComponents();
        settings.setPassword(passwordField.getPassword());
        settings.setUserName(userNameTextField.getText());
        settings.setUrl(urlTextField.getText());
    }

    public boolean isModified(){
        boolean modified = !userNameTextField.getText().equals(settings.getUserName());
        modified |= passwordField.getPassword() != settings.getPassword().toCharArray();
        modified |= !urlTextField.getText().equals(settings.getUrl());
        return modified;
    }

    public void reset() {
        passwordField.setText(settings.getPassword());
        userNameTextField.setText(settings.getUserName());
        urlTextField.setText(settings.getUrl());
    }
}
