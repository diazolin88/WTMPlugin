package view;

import actions.CreateDraftClassAction;
import actions.OpenTestCaseInBrowserAction;
import actions.RefreshFolderAction;
import actions.SettingsActions;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import model.testrail.RailClient;
import settings.WTMSettings;
import settings.WTMSettingsWindowRenderer;
import utils.GuiUtil;

import javax.swing.*;
import java.awt.*;

import static utils.ComponentUtil.repaintComponent;

public class MainPanel extends WindowPanelAbstract implements View {
    private JPanel mainPanel;
    private final Project project;
    private RailClient client;

    private MainPanel(Project project) {
        super(project);
        this.project = project;
        client = RailClient.getInstance(project);
        setContent(mainPanel);
        mainPanel.setLayout(new CardLayout());
        renderComponentDependsOnLoginState();
        addToolBar();
        WTMSettingsWindowRenderer.getInstance(project).addSubcsr(this);
    }

    public static MainPanel getInstance(Project project) {
        return ServiceManager.getService(project, MainPanel.class);
    }

    public void refreshSelectedFolder(AnActionEvent e) {
        GuiUtil.runInSeparateThread(() -> TestRailWindow.getInstance(project).refreshSelectedFolder(e));
    }

    private void addToolBar() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.addAction(new CreateDraftClassAction());
        group.addAction(new RefreshFolderAction());
        group.addAction(new OpenTestCaseInBrowserAction());
        group.addSeparator();
        group.addAction(new SettingsActions());

        GuiUtil.installActionGroupInToolBar(group, this, ActionManager.getInstance(), "TestRailWindowToolBar");
    }

    private void renderComponentDependsOnLoginState() {
        renderWindowsDependsOnSettings(settings);
    }

    @Override
    public void update(WTMSettings settingsWindow) {
        renderWindowsDependsOnSettings(settingsWindow);
    }

    private void renderWindowsDependsOnSettings(WTMSettings settingsWindow) {
        if (client.isLoggedIn()) {
            mainPanel.removeAll();
            TestRailWindow testRailWindow = TestRailWindow.getInstance(project);
            testRailWindow.setDefaultFields(project);
            mainPanel.add(testRailWindow);
        } else {
            mainPanel.removeAll();
            mainPanel.add(NotLoggedIn.getInstance(project));
        }
        repaintComponent(mainPanel);
    }
}
