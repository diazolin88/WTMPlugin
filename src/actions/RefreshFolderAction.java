package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import model.section.OurSection;
import view.MainPanel;
import view.TestRailWindow;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import static com.intellij.util.PlatformIcons.SYNCHRONIZE_ICON;

public class RefreshFolderAction extends AnAction {

    public RefreshFolderAction() {
        super("Refresh folder", "Refresh folder, add new cases", SYNCHRONIZE_ICON);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        MainPanel.getInstance(e.getProject()).refreshSelectedFolder(e);
    }

    public void update(AnActionEvent e) {
        TestRailWindow window = TestRailWindow.getInstance(e.getProject());
        TreePath[] paths = window.getSectionTree().getSelectionPaths();
        if (paths == null || paths.length > 1) {
            e.getPresentation().setEnabled(false);
        } else if (paths.length == 1) {
            DefaultMutableTreeNode node = ((DefaultMutableTreeNode) window.getSectionTree().getLastSelectedPathComponent());
            if (null != node && node.getUserObject() instanceof OurSection && ((OurSection) node.getUserObject()).getId() != -1) {
                e.getPresentation().setEnabled(true);
            } else {
                e.getPresentation().setEnabled(false);
            }
        }
    }
}
