package toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import exceptions.AuthorizationException;
import model.testrail.RailClient;
import model.testrail.RailConnection;
import settings.WTMSettings;
import view.TestRailWindow;

import static utils.ComponentUtil.disableComponent;


public class WTMToolWindowFactory implements ToolWindowFactory {
    private TestRailWindow testRailWindow;
    private RailConnection railConnection;
    private WTMSettings settings;
    private RailClient client;


    @SuppressWarnings("unchecked")
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        initDesiredFields(project);
        setClient();

        ContentFactory factory = ContentFactory.SERVICE.getInstance();
        Content content = factory.createContent(testRailWindow, "", true);
        toolWindow.getContentManager().addContent(content);

        //Render default items
        testRailWindow.getProjectCB().addItem("Select project...");
        client.getProjectList().forEach(var -> testRailWindow.getProjectCB().addItem(var.getName()));
        disableComponent(testRailWindow.getSuitesCB());
    }


    public void init(ToolWindow window) {
        window.hide(null);
        window.setAvailable(true, null);
    }

    private void initDesiredFields(Project project) {
        testRailWindow = TestRailWindow.getInstance(project);
        railConnection = RailConnection.getInstance(project);
        settings = WTMSettings.getInstance(project);
    }

    private void setClient() {
        try {
            client = new RailClient(railConnection.login(settings));
        } catch (AuthorizationException e) {
            e.printStackTrace();
        }
    }
}
