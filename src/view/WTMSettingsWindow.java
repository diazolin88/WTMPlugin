package view;

import action.TestConnection;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import exceptions.AuthorizationException;
import model.testrail.RailConnection;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

import static java.awt.Font.ITALIC;

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
            RailConnection conn = new RailConnection();
            try {
                conn.login(settings.getUrl(), settings.getUserName(), settings.getPassword());
                debugTextPane.setText("Connected!");
            }catch (AuthorizationException exception){
                debugTextPane.setText(exception.getMessage());
            }
        });
    }

    public void setSettings(){
        settings.setPassword(passwordField.getPassword());
        settings.setUserName(userNameTextField.getText());
        settings.setUrl(urlTextField.getText());
    }

    public boolean isModified(){
        return !userNameTextField.getText().equals(settings.getUserName())
        || passwordField.getPassword() != settings.getPassword().toCharArray()
        || !urlTextField.getText().equals(settings.getUrl());
    }

    public void reset() {
        passwordField.setText(settings.getPassword());
        userNameTextField.setText(settings.getUserName());
        urlTextField.setText(settings.getUrl());
    }
}
