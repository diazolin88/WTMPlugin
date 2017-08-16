package settings;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.passwordSafe.PasswordSafeException;
import com.intellij.ide.passwordSafe.impl.PasswordSafeImpl;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.diagnostic.Logger;
import sun.rmi.runtime.Log;

@State(
        name = "WTM.Settings",
        storages = {
                @Storage("WTMSettings.xml")
        }
)
public class WTMSettings implements PersistentStateComponent<WTMSettings> {
    public static final String DEFAULT_VALUE = "";
    public String userName = DEFAULT_VALUE;
    public String url = DEFAULT_VALUE;
    public String password;
    private static final Logger LOG = Logger.getInstance(WTMSettings.class);
    private static final String PASSWORD_KEY = "wtm.settings.password.key";

    WTMSettings() {

    }

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
        return  null == password ? "" : password;
    }


}
