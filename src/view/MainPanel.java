package view;

import actions.CreateDraftClassAction;
import actions.RefreshToolWindowState;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import utils.GuiUtil;

import javax.swing.*;
import java.awt.*;

import static utils.ComponentUtil.repaintComponent;

public class MainPanel extends WindowPanelAbstract {
    private JPanel mainPanel;
    private Project project;
    private TestRailWindow testRailWindow;
    private NotLoggedIn notLoggedIn;

    public MainPanel(Project project) {
        super(project);
        this.project = project;

        initViewVariants(project);
        setContent(mainPanel);
        mainPanel.setLayout(new CardLayout());

        renderComponentDependsOnLoginState(project);
        addToolBar();
    }

    public TestRailWindow getTestRailWindow() {
        return testRailWindow;
    }

    public NotLoggedIn getNotLoggedIn() {
        return notLoggedIn;
    }

    public static MainPanel getInstance(Project project) {
        return ServiceManager.getService(project, MainPanel.class);
    }

    public void refresh(){
        renderComponentDependsOnLoginState(project);
    }


    private void addToolBar() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.addAction(new CreateDraftClassAction());
        group.addAction(new RefreshToolWindowState());
        GuiUtil.installActionGroupInToolBar(group, this, ActionManager.getInstance(), "TestRailWindowToolBar");
    }

    private void renderComponentDependsOnLoginState(Project project) {
        if (settings.isLogged) {
            mainPanel.removeAll();
            mainPanel.add(TestRailWindow.getInstance(project));
        } else {
            mainPanel.removeAll();
            mainPanel.setLayout(new CardLayout());
            mainPanel.add(NotLoggedIn.getInstance(project));
        }
        repaintComponent(mainPanel);
    }


    /**
     * Initialize windows which will be used in mainWindow depends on login state
     * */
    private void initViewVariants(Project project) {
        testRailWindow = TestRailWindow.getInstance(project);
        notLoggedIn = NotLoggedIn.getInstance(project);
    }
}
