package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import utils.GuiUtil;
import view.TestRailWindow;

import javax.swing.*;

public class CreateDraftClass extends AnAction {
    private static final Icon ICON = GuiUtil.loadIcon("Untitled.png");

    public CreateDraftClass(){
        super("Draft", "Create draft class", ICON);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
    }
}
