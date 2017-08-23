package settings;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view.WTMSettingsWindow;

import javax.swing.*;

/**
 * Class implementing @SearchableConfigurable to be able to see
 *
 * @settingsComponent on Settings > Tool window
 * <p>
 * Following line should be added to plugin.xml
 * <applicationConfigurable groupId="tools" displayName="Single File Execution Plugin" id="preferences.SingleFileExecutionConfigurable" instance="settings.WTMSettings" />
 */
public class WTMSettingsWindowRenderer implements SearchableConfigurable,ProjectComponent {

    public final WTMSettings wtmSettings;
    private WTMSettingsWindow settingsComponent;
    private Project project;

    public WTMSettingsWindowRenderer(@NotNull Project project) {
        this.project = project;
        this.wtmSettings = WTMSettings.getInstance(project);

    }

    @NotNull
    public String getId() {
        return settingsComponent.getUIClassID();
    }

    @Nls
    @Override
    public String getDisplayName() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (settingsComponent == null) {
            settingsComponent = new WTMSettingsWindow(project);
            settingsComponent.railTestConnectionButtonClickedAction(project, settingsComponent);
        }
        return settingsComponent;
    }

    @Override
    public boolean isModified() {
        return settingsComponent.isModified();
    }

    /**
     * It is called when “setSettings” or “ok” button is pressed.
     * Implement a logic to update configuration.
     */
    @Override
    public void apply() throws ConfigurationException {
        settingsComponent.setSettings();
    }

    /**
     * It is called when “setSettings” or “ok” button is pressed.
     * Implement a logic to reset the configuration.
     */
    @Override
    public void reset() {
        settingsComponent.reset();
    }

    @Override
    public void disposeUIResources() {
        WTMSettingsWindow.getInstance(project).dispose();
    }
}
