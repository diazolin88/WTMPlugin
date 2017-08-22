package view;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import settings.WTMSettings;

public abstract class WindowPanelAbstract extends SimpleToolWindowPanel {
    settings.WTMSettings settings;

    WindowPanelAbstract(Project project) {
        super(true, true);
        settings = WTMSettings.getInstance(project);
    }
}
