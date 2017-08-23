package view;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBColor;
import exceptions.AuthorizationException;
import model.CredentialsData;
import model.testrail.RailConnection;
import settings.WTMSettings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.NoSuchElementException;

public class WTMSettingsWindow extends WindowPanelAbstract implements Disposable {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JTextField railUrlTextField;
    private JPasswordField railPasswordField;
    private JTextField railUserNameTextField;
    private JButton railTestConnectionButton;
    private JTextPane railDebugTextPane;

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

   public void setSettings() {
        settings.setRailPassword(railPasswordField.getPassword());
        settings.setRailUserName(railUserNameTextField.getText());
        settings.setRailUrl(railUrlTextField.getText());
    }

    public static WTMSettingsWindow getInstance(Project project) {
        return ServiceManager.getService(project, WTMSettingsWindow.class);
    }

    @Override
    public void dispose() {
        ToolWindowManager.getInstance(project).unregisterToolWindow("WTM plugin");
    }

    public boolean isModified() {
        return mainPanel.isDisplayable() &&
                !railUserNameTextField.getText().equals(settings.getRailUserName())
                || String.valueOf(railPasswordField.getPassword()).equals(settings.getRailPassword())
                || !railUrlTextField.getText().equals(settings.getRailUrl());
    }

    public void reset() {
        railPasswordField.setText(settings.getRailPassword());
        railUserNameTextField.setText(settings.getRailUserName());
        railUrlTextField.setText(settings.getRailUrl());
    }

    public void railTestConnectionButtonClickedAction(Project project, WTMSettingsWindow component) {
         if (isModified()) {
            railTestConnectionButton.addActionListener(listener ->
            {
                try {
                    RailConnection.getInstance(project).login(component);
                    railDebugTextPane.setText("Connected!");
                } catch (AuthorizationException e) {
                    railDebugTextPane.setForeground(JBColor.RED);
                    railDebugTextPane.setText(e.getMessage());
                }
            });
        }
    }


}
