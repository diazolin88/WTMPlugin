package actions;

import com.intellij.ide.actions.ShowPopupMenuAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import utils.GuiUtil;
import view.TestRailWindow;

import javax.swing.*;

import static utils.ComponentUtil.repaintComponent;

public class CreateDraftClassAction extends AnAction {
    private static final Icon ICON = GuiUtil.loadIcon("draft.png");

    public CreateDraftClassAction(){
        super("Draft", "Create draft class", ICON);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        //TODO createDraftClasses should throw and exception if something goes wrong, so we will handle it and show on baloon message
        TestRailWindow.getInstance(e.getProject()).createDraftClasses();

        StatusBar statusBar = WindowManager.getInstance()
                .getStatusBar(e.getProject());

        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder("<html>Draft classes created! <br>Please sync if not appeared</html>", MessageType.INFO, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()),
                        Balloon.Position.atRight);
    }

    @Override
    public void update(AnActionEvent e){
        TestRailWindow window = TestRailWindow.getInstance(e.getProject());
        if(null == window.getSectionTree().getSelectionPaths() || 0 == window.getSectionTree().getSelectionPaths().length){
            e.getPresentation().setEnabled(false);
        }else {
            e.getPresentation().setEnabled(true);
        }
    }
}
