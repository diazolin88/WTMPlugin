package view;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBColor;
import exceptions.AuthorizationException;
import model.testrail.RailConnection;

import javax.swing.*;

public class WTMSettingsWindow extends WindowPanelAbstract implements Disposable {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JTextField railUrlTextField;
    private JPasswordField railPasswordField;
    private JTextField railUserNameTextField;
    private JButton railTestConnectionButton;
    private JTextPane railDebugTextPane;
    private JTextArea temlateTextArea;

    private Project project;

    public WTMSettingsWindow(Project project) {
        super(project);
        this.project = project;
        railTestConnectionButtonClickedAction(project);
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
    //Mb need to remove
    @Deprecated
    public JButton getRailTestConnectionButton() {
        return railTestConnectionButton;
    }

    //Mb need to remove
    @Deprecated
    public JTextPane getRailDebugTextPane() {
        return railDebugTextPane;
    }

   public void setSettings() {
        settings.setRailPassword(railPasswordField.getPassword());
        settings.setRailUserName(railUserNameTextField.getText());
        settings.setRailUrl(railUrlTextField.getText());
        settings.setTemplate(temlateTextArea.getText());
       try {
           RailConnection.getInstance(project).login(this);
           settings.setLogged(true);
       } catch (AuthorizationException e) {
           settings.setLogged(false);
       }
   }

    public static WTMSettingsWindow getInstance(Project project) {
        return ServiceManager.getService(project, WTMSettingsWindow.class);
    }

    @Override
    public void dispose() {
        ToolWindowManager.getInstance(project).unregisterToolWindow("WTM plugin");
    }

    public boolean isModified() {
                return !railUserNameTextField.getText().equals(settings.getRailUserName())
                || !String.valueOf(railPasswordField.getPassword()).equals(settings.getRailPassword())
                || !railUrlTextField.getText().equals(settings.getRailUrl())
                ||!temlateTextArea.getText().equals(settings.getTemplate());
    }

    public void reset() {
        railPasswordField.setText(settings.getRailPassword());
        railUserNameTextField.setText(settings.getRailUserName());
        railUrlTextField.setText(settings.getRailUrl());
        temlateTextArea.setText(settings.getTemplate());
        settings.setLogged(settings.isLogged());
    }

    private void railTestConnectionButtonClickedAction(Project project) {
            railTestConnectionButton.addActionListener(listener ->
            {
                try {
                    RailConnection.getInstance(project).login(this);
                    railDebugTextPane.setForeground(JBColor.GREEN);
                    railDebugTextPane.setText("Connected!");
                } catch (AuthorizationException e) {
                    railDebugTextPane.setForeground(JBColor.RED);
                    railDebugTextPane.setText(e.getMessage());
                }
            });
    }


}
