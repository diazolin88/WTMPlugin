package myPackage;

import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import view.DemoPanel;


public class MyToolWindowFactory implements ToolWindowFactory {


    private static final String[] text = {"line1", "line2"};
    private DemoPanel panel = new DemoPanel();

    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        ContentFactory factory = ContentFactory.SERVICE.getInstance();
        Content content = factory.createContent(panel, "", true);
        toolWindow.getContentManager().addContent(content);


    }

    public void init(ToolWindow window) {
        panel.setText(text);

    }
}
