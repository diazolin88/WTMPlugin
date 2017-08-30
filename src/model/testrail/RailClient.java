package model.testrail;

import com.codepine.api.testrail.TestRail;
import com.codepine.api.testrail.model.*;
import utils.ToolWindowData;

import java.util.List;

public final class RailClient {
   private TestRail client;
   private ToolWindowData data;

    public RailClient(TestRail client){
        this.client = client;
    }

    public RailClient(TestRail client, ToolWindowData data){
        this.data = data;
        this.client = client;
    }

    public List<Project> getProjectList(){
        return this.client.projects().list().execute();
    }

    public List<Suite> getSuitesList(String projectName){
            int projectId = client.projects().list().execute().stream()
                    .filter(project1 -> project1.getName().equals(projectName))
                    .map(com.codepine.api.testrail.model.Project::getId)
                    .findFirst().get();

        return this.client.suites().list(projectId).execute();
    }

    public List<User> getUsers(){
        return this.client.users().list().execute();
    }

    public List<Case> getCases(int projectId, int suiteId){
        List<CaseField> caseFieldList = this.client.caseFields().list().execute();
        return this.client.cases().list(projectId, suiteId, caseFieldList).execute();
    }

    public List<Section> getSections(int projectID, int suiteID){
        return this.client.sections().list(projectID,suiteID).execute();
    }
}
