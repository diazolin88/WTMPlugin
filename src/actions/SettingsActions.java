package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import org.jetbrains.annotations.Nullable;
import settings.WTMSettingsWindowRenderer;

import javax.swing.*;

import static com.intellij.util.PlatformIcons.SHOW_SETTINGS_ICON;

public class SettingsActions extends AnAction{

    public SettingsActions() {
        super("Settings", "Open settings", SHOW_SETTINGS_ICON);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        ShowSettingsUtil.getInstance().showSettingsDialog(anActionEvent.getProject(), WTMSettingsWindowRenderer.class);
    }
}
