package myPackage;

import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import model.testrail.RailConnection;

import java.util.ArrayList;
import java.util.List;
import view.DemoPanel;


public class MyToolWindowFactory implements ToolWindowFactory {


    private static final String[] text = {"line1", "line2"};
    private DemoPanel panel = new DemoPanel();
    List<String> projectsName = new ArrayList<>();

    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        ContentFactory factory = ContentFactory.SERVICE.getInstance();
        Content content = factory.createContent(panel, "", true);
        toolWindow.getContentManager().addContent(content);
        RailConnection.getTestRail(project).projects().list().execute().forEach(project1 -> projectsName.add(project1.getName()));
        panel.setText(projectsName);
    }

    public void init(ToolWindow window) {

    }
}
