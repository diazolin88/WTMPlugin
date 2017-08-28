package model.testrail;

import com.codepine.api.testrail.TestRail;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import exceptions.AuthorizationException;
import settings.WTMSettings;
import view.WTMSettingsWindow;


public final class RailConnection {
    private boolean isLoggedIn = false;
    private TestRail client;

    private RailConnection() {
    }

    public static RailConnection getInstance(Project project) {
        return ServiceManager.getService(project, RailConnection.class);
    }

    /**
     * Connecting to test rail once and then use static member if connection is
     */
    public TestRail login(WTMSettings state) throws AuthorizationException {
        try {
            client = TestRail.builder(state.getRailUrl(), state.getRailUserName(), state.getRailPassword()).build();
            client.projects().list().execute();
            isLoggedIn = true;
            return client;
        } catch (Exception e) {
            isLoggedIn = false;
            throw new AuthorizationException("Unable to login due to invalid login data or url");
        }
    }

    public TestRail login(WTMSettingsWindow window) throws AuthorizationException {
            try {
                client = TestRail.builder(window.getRailUrlTextField().getText(), window.getRailUserNameTextField().getText(), String.valueOf(window.getRailPasswordField().getPassword())).build();
                client.projects().list().execute();
                isLoggedIn = true;
                return client;
            } catch (Exception e) {
                isLoggedIn = false;
                throw new AuthorizationException("Unable to login due to invalid login data or url");
            }
    }

    public boolean isLoggedIn() {
      return isLoggedIn;
    }

    public TestRail getClient() {
        return client;
    }
}
