package settings;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.components.*;
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
    private static final String DEFAULT_VALUE = "";
    private static final String PASSWORD_KEY = "wtm.settings.password.key";

    private String userName = DEFAULT_VALUE;
    private String url = DEFAULT_VALUE;

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

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPassword(char[] password) {
        CredentialAttributes attributes = new CredentialAttributes(PASSWORD_KEY, this.userName, this.getClass(), false);
        Credentials saveCredentials = new Credentials(attributes.getUserName(), password);
        PasswordSafe.getInstance().set(attributes, saveCredentials);
    }

    public String getPassword() {
        CredentialAttributes attributes = new CredentialAttributes(PASSWORD_KEY, this.userName, this.getClass(), false);
        String password = PasswordSafe.getInstance().getPassword(attributes);
        return null == password ? "" : password;
    }
}
