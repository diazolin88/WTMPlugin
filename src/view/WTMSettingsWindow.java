package view;

import action.TestConnection;
import com.intellij.openapi.project.Project;
import model.TMConnectable;
import model.testrail.RailConnection;

import javax.swing.*;

public class WTMSettingsWindow extends WindowPanelAbstract {
    private JPanel mainPanel;
    private JTextField userNameTextField;
    private JTextField urlTextField;
    private JPasswordField passwordField;
    private JButton testConnectionButton;
    private JTextPane debugTextPane;

    public WTMSettingsWindow(Project project) {
        super(project);
        setContent(mainPanel);
        testConnectionButton.addActionListener(e -> {
           // RailConnection connectable = RailConnection.getInstance(project);
            //connectable.login(settings.getUserName(),settings.getPassword(),settings.getUrl());

        });
    }

    public void setSettings(){
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
