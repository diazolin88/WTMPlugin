package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import view.MainPanel;

import static com.intellij.util.PlatformIcons.SYNCHRONIZE_ICON;

public class RefreshToolWindowState extends AnAction {
    private com.intellij.openapi.project.Project project;

    public RefreshToolWindowState(){
        super("Refresh", "Create draft class", SYNCHRONIZE_ICON);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        this.project = e.getProject();
        MainPanel.getInstance(e.getProject()).refreshPanel();
    }

}
