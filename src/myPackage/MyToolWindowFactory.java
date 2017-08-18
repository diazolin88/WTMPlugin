package myPackage;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import settings.WTMSettings;
import view.DemoPanel;


public class MyToolWindowFactory implements ToolWindowFactory {

    private WTMSettings settings;
    private String[] text = {"line1", "line2"};
    private DemoPanel panel = new DemoPanel();

    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        ContentFactory factory = ContentFactory.SERVICE.getInstance();
        Content content = factory.createContent(panel, "", true);
        toolWindow.getContentManager().addContent(content);
        settings = ServiceManager.getService(project, WTMSettings.class);

    }

    public void init(ToolWindow window) {
        settings = WTMSettings.getInstance(ProjectManager.getInstance().getOpenProjects()[0]);
        text[1] = settings.getUrl();
        panel.setText(text);

    }
}
