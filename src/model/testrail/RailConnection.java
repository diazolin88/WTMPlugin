package model.testrail;

import com.codepine.api.testrail.TestRail;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import model.TMConnectable;
import settings.WTMSettings;


public class RailConnection implements TMConnectable<TestRail>{

    public static RailConnection getInstance(Project project) {
        return ServiceManager.getService(project, RailConnection.class);
    }

    @Override
    public TestRail login(WTMSettings state) {
        return TestRail.builder(state.getRailUrl(),state.getRailUserName(), state.getRailPassword()).build();
    }
}
