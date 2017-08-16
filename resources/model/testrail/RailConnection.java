package model.testrail;

import com.codepine.api.testrail.TestRail;
import com.intellij.openapi.project.Project;
import exceptions.AuthorizationException;
import model.TMConnectable;
import settings.WTMSettings;


public class RailConnection {
    private static TestRail testRail = null;
    private static WTMSettings settings;

    public static TestRail getTestRail(Project project) {
        settings = WTMSettings.getInstance(project);
        login(settings.getUrl(),settings.getUserName(),settings.getPassword());
        return testRail;
    }

    public static TestRail login(String url, String user, String upas) throws AuthorizationException{
        try{
            TestRail.builder(url,user,upas).build().projects().list().execute();
            testRail = TestRail.builder(url,user,upas).build();
            return testRail;
        }catch (RuntimeException e){
            throw new AuthorizationException(user);
        }
    }
}
