package model.testrail;

import com.codepine.api.testrail.TestRail;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import model.TMConnectable;
import org.jetbrains.annotations.NotNull;


public class RailConnection implements TMConnectable<TestRail>{

//    public static RailConnection getInstance(Project project) {
//        return ServiceManager.getService(project, RailConnection.class);
//    }

    @Override
    public TestRail login(String user, String password, String url) {
        return TestRail.builder(url,user,password).build();
    }
}
