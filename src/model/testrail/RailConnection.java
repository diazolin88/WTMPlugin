package model.testrail;

import com.codepine.api.testrail.TestRail;
import com.intellij.openapi.project.ProjectManager;
import model.TMConnectable;


public class RailConnection implements TMConnectable<TestRail>{

//    public static RailConnection getInstance(Project project) {
//        return ServiceManager.getService(project, RailConnection.class);
//    }

    @Override
    public TestRail login(String user, String password, String url) {
        RailSettings settings = RailSettings.getSafeInstance(ProjectManager.getInstance().getOpenProjects()[0]);
        return TestRail.builder(url,user,password).build();
    }
}
