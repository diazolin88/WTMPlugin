package toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import model.testrail.RailClient;
import settings.WTMSettings;
import view.MainPanel;

public class WTMToolWindowFactory implements ToolWindowFactory{
    private MainPanel mainWindow;
    private WTMSettings settings;
    private RailClient client;

    @SuppressWarnings("unchecked")
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        initDesiredFields(project);
        ContentFactory factory = ContentFactory.SERVICE.getInstance();
        Content testRailWindowContent = factory.createContent(mainWindow, "", true);
        toolWindow.getContentManager().addContent(testRailWindowContent);
        //Render default items
    }

    public void init(ToolWindow window) {
        window.hide(null);
        window.setAvailable(true, null);
    }

    private void initDesiredFields(Project project) {
        mainWindow = MainPanel.getInstance(project);
        client = RailClient.getInstance(project);
        settings = WTMSettings.getInstance(project);
    }
}
