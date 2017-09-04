package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import utils.GuiUtil;
import view.TestRailWindow;

import javax.swing.*;

public class CreateDraftClassAction extends AnAction {
    private static final Icon ICON = GuiUtil.loadIcon("draft.png");

    public CreateDraftClassAction(){
        super("Draft", "Create draft class", ICON);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        //Instead of createDraftClasses() use method which builds draft classes
        TestRailWindow.getInstance(e.getProject()).createDraftClasses();
    }
}
