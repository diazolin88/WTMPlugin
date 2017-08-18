package settings;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

import static settings.WTMSettings.PASS_KEYS.RAIL_PASSWORD_KEY;

/**
 * Class needs for save user settings in file WTMSettings.xml such as:
 * @username
 * @url
 * @password
 *
 * All settings saved under project, so for new project need setup
 * plugin again
 *
 * All fields that should be saved should be public
 * */
@State(
        name = "WTM.Settings",
        storages = {
                @Storage("WTMSettings.xml")
        }
)
public class WTMSettings implements PersistentStateComponent<WTMSettings.State> {
    private State state = new State();

    public static WTMSettings getInstance(Project project) {
        return ServiceManager.getService(project, WTMSettings.class);
    }

    @Nullable
    @Override
    public State getState() {
        return this.state;
    }


    @Override
    public void loadState(State state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getUserName() {
        return this.state.userName;
    }

    public void setUserName(String userName) {
        this.state.userName = userName;
    }

    public String getUrl() {
        return this.state.url;
    }

    public void setUrl(String url) {
        this.state.url = url;
    }

    public void setPassword(char[] password) {
            CredentialAttributes attributes = new CredentialAttributes(RAIL_PASSWORD_KEY.getValue(), this.state.userName, this.state.getClass(), false);
            Credentials saveCredentials = new Credentials(attributes.getUserName(), password);
            PasswordSafe.getInstance().set(attributes, saveCredentials);
    }

    public String getPassword() {
        CredentialAttributes attributes = new CredentialAttributes(RAIL_PASSWORD_KEY.getValue(), this.state.userName, this.state.getClass(), false);
        String password = PasswordSafe.getInstance().getPassword(attributes);
        return  null == password ? "" : password;
    }

    public class State{
        public static final String DEFAULT_VALUE = "";
        public String userName = DEFAULT_VALUE;
        public String url = DEFAULT_VALUE;
    }

    public enum PASS_KEYS{
        RAIL_PASSWORD_KEY{
            public String getValue() {
                return "wtm.settings.rail.password.key";
            }
        },
        JIRA_PASSWORD_FIELD{
            public String getValue() {
                return "wtm.settings.jira.password.key";
            }
        };

        public String getValue(){
            return null;
        }
    }
}
