package actions;

import com.codepine.api.testrail.model.Case;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import utils.GuiUtil;
import view.TestRailWindow;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * AnAction for create draft class.
 */
public class CreateDraftClassAction extends AnAction {

    private static final Icon ICON = GuiUtil.loadIcon("draft.png");

    public CreateDraftClassAction(){
        super("Draft classes for package", "Create draft class", ICON);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        //TODO createDraftClasses should throw and exception if something goes wrong, so we will handle it and show on baloon message
        TestRailWindow.getInstance(e.getProject()).createDraftClasses(e);
    }

    @Override
    public void update(AnActionEvent event){
        if (isTestCaseSelectedByEvent(event)) {
            event.getPresentation().setEnabled(false);
        }else {
            event.getPresentation().setEnabled(true);
        }
    }

    private boolean isTestCaseSelectedByEvent(AnActionEvent event) {
        TestRailWindow window = TestRailWindow.getInstance(event.getProject());
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) window.getSectionTree().getLastSelectedPathComponent();

        return null == window.getSectionTree().getSelectionPaths() || 0 == window.getSectionTree().getSelectionPaths().length || node.getUserObject() instanceof Case;
    }
}
