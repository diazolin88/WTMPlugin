package view;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import org.jetbrains.annotations.Nullable;
import settings.WTMSettings;

import javax.swing.*;

public abstract class WindowPanelAbstract extends SimpleToolWindowPanel {
    public settings.WTMSettings settings;

    WindowPanelAbstract(Project project) {
        super(true, true);
        settings = WTMSettings.getInstance(project);
    }
}
