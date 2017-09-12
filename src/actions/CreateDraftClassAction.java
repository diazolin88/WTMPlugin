package actions;

import com.codepine.api.testrail.model.Case;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import utils.GuiUtil;
import view.TestRailWindow;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

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
    public void update(AnActionEvent e){
        TestRailWindow window = TestRailWindow.getInstance(e.getProject());
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) window.getSectionTree().getLastSelectedPathComponent();
        if (null == window.getSectionTree().getSelectionPaths() || 0 == window.getSectionTree().getSelectionPaths().length || node.getUserObject() instanceof Case) {
            e.getPresentation().setEnabled(false);
        }else {
            e.getPresentation().setEnabled(true);
        }
    }
}
