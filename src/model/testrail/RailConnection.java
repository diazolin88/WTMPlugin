package model.testrail;

import com.codepine.api.testrail.TestRail;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import exceptions.AuthorizationException;
import settings.WTMSettings;
import view.WTMSettingsWindow;


public final class RailConnection {

    private RailConnection(){}

    public static RailConnection getInstance(Project project) {
        return ServiceManager.getService(project, RailConnection.class);
    }

    public TestRail login(WTMSettings state) throws AuthorizationException {
        try {
            TestRail testRail = TestRail.builder(state.getRailUrl(), state.getRailUserName(), state.getRailPassword()).build();
            testRail.projects();
            return testRail;
        } catch (Exception e) {
            throw new AuthorizationException("Unable to login due to invalid login data or url");
        }
    }

    public TestRail login(WTMSettingsWindow window) throws AuthorizationException {
        try {
            TestRail testRail = TestRail.builder(window.getRailUrlTextField().getText(), window.getRailUserNameTextField().getText(), String.valueOf(window.getRailPasswordField().getPassword())).build();
            testRail.projects().list().execute();
            return testRail;
        } catch (Exception e) {
            throw new AuthorizationException("Unable to login due to invalid login data or url");
        }
    }
}
