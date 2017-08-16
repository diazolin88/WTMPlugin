package settings;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

@State(
        name = "WTM.Settings",
        storages = {
                @Storage("WTMSettings.xml")
        }
)
public class WTMSettings implements PersistentStateComponent<WTMSettings> {
    public static final String DEFAULT_VALUE = "";
    private static final String PASSWORD_KEY = "wtm.settings.password.key";
    public String userName = DEFAULT_VALUE;
    public String url = DEFAULT_VALUE;




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

    public String getPassword() {
        CredentialAttributes attributes = new CredentialAttributes(PASSWORD_KEY, this.userName, this.getClass(), false);
        String password = PasswordSafe.getInstance().getPassword(attributes);
        return null == password ? "" : password;
    }

    public void setPassword(char[] password) {
        CredentialAttributes attributes = new CredentialAttributes(PASSWORD_KEY, this.userName, this.getClass(), false);
        Credentials saveCredentials = new Credentials(attributes.getUserName(), password);
        PasswordSafe.getInstance().set(attributes, saveCredentials);
    }


}
