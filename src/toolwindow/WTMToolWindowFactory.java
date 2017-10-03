package toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

public class WTMToolWindowFactory implements ToolWindowFactory{
    private view.ToolWindow mainWindow;

    @SuppressWarnings("unchecked")
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        initDesiredFields(project);
        ContentFactory factory = ContentFactory.SERVICE.getInstance();
        Content testRailWindowContent = factory.createContent(mainWindow, "", true);
        toolWindow.getContentManager().addContent(testRailWindowContent);
    }

    public void init(ToolWindow window) {
        window.hide(null);
        window.setAvailable(true, null);
    }

    private void initDesiredFields(Project project) {
        mainWindow = view.ToolWindow.getInstance(project);
    }
}
