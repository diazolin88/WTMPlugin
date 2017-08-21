package settings;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;


/**
 * Class needs for save user settings in file WTMSettings.xml such as:
 *
 * @username
 * @url
 * @password All settings saved under project, so for new project need setup
 * plugin again
 * <p>
 * All fields that should be saved should be public
 */
@State(
        name = "WTM.Settings",
        storages = {
                @Storage("WTMSettings.xml")
        }
)
public class WTMSettings implements PersistentStateComponent<WTMSettings> {
    public static final String DEFAULT_VALUE = "";
    public String railUserName = DEFAULT_VALUE;
    public String railUrl = DEFAULT_VALUE;
    public String jiraUserName = DEFAULT_VALUE;
    public String jiraUrl = DEFAULT_VALUE;

    private static final String RAIL_P_KEY = "wtm.settings.rail.password.key";
    private static final String JIRA_P_KEY = "wtm.settings.jira.password.key";

    public static WTMSettings getInstance(Project project) {
        return ServiceManager.getService(project, WTMSettings.class);
    }

    @Nullable
    @Override
    public WTMSettings getState() {
        return this;
    }

    @Override
    public void loadState(WTMSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getRailUserName() {
        return this.railUserName;
    }

    public void setRailUserName(String userName) {
        this.railUserName = userName;
    }

    public String getRailUrl() {
        return this.railUrl;
    }

    public void setRailUrl(String url) {
        this.railUrl = url;
    }

    public String getRailPassword() {
        CredentialAttributes attributes = new CredentialAttributes(RAIL_P_KEY, this.railUserName, this.getClass(), false);
        String password = PasswordSafe.getInstance().getPassword(attributes);
        return null == password ? "" : password;
    }

    public void setRailPassword(char[] password) {
        CredentialAttributes attributes = new CredentialAttributes(RAIL_P_KEY, this.railUserName, this.getClass(), false);
        Credentials saveCredentials = new Credentials(attributes.getUserName(), password);
        PasswordSafe.getInstance().set(attributes, saveCredentials);
    }

    public String getJiraUrl() {
        return this.jiraUrl;
    }

    public void setJiraUrl(String url) {
        this.jiraUrl = url;
    }

    public String getJiraUserName() {
        return this.jiraUserName;
    }

    public void setJiraUserName(String userName) {
        this.jiraUserName = userName;
    }

    public String getJiraPassword() {
        CredentialAttributes attributes = new CredentialAttributes(JIRA_P_KEY, this.jiraUserName, this.getClass(), false);
        String password = PasswordSafe.getInstance().getPassword(attributes);
        return null == password ? "" : password;
    }

    public void setJiraPassword(char[] password) {
        CredentialAttributes attributes = new CredentialAttributes(JIRA_P_KEY, this.jiraUserName, this.getClass(), false);
        Credentials saveCredentials = new Credentials(attributes.getUserName(), password);
        PasswordSafe.getInstance().set(attributes, saveCredentials);
    }
}
