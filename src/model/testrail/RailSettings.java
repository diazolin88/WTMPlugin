package model.testrail;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

/**
 * Class should represent plugin settings window and contains following
 * class should be initialized on plugin start
 * @companyUrl
 * @projectName(optional) can be set later
 * @login
 * @password
 * */
@State(
        name = "WTMplugin.application.settings",
        storages = {
                @Storage(id = "WTMpluginAppSettings", file = "$PROJECT_FILES$"),
                @Storage(id = "$PROJECT_CONFIG_DIR$/wtmpluginsettings.xml", scheme = StorageScheme.DIRECTORY_BASED)
        }
)
public class RailSettings implements PersistentStateComponent<RailSettings.State> {
    private String companyUrl = "https://company.testrail.net/";


    private State state = new State();

    public static RailSettings getSafeInstance(Project project) {
        RailSettings settings = ServiceManager.getService(project, RailSettings.class);
        return settings != null ? settings : new RailSettings();
    }

    @Nullable
    @Override
    public State getState() {
        return this.state;
    }

    @Override
    public void loadState(State state) {
        this.state = state;
    }

    public class State{
        public String companyUrl;
        public String username;
        public String password;
    }
}
