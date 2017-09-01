package model.testrail;

import com.codepine.api.testrail.TestRail;
import com.codepine.api.testrail.model.*;
import utils.ToolWindowData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static model.testrail.RailConstants.*;

/**
 * Test Rail client.
 */
public final class RailClient {
    private TestRail client;
    private ToolWindowData data;

    private static List<Section> sectionList = new ArrayList<>();
    private static List<User> userList = new ArrayList<>();

    public RailClient(TestRail client) {
        this.client = client;
    }

    public RailClient(TestRail client, ToolWindowData data) {
        this.data = data;
        this.client = client;
    }

    public List<Project> getProjectList() {
        return this.client.projects().list().execute();
    }

    public List<Suite> getSuitesList(String projectName) {
        int projectId = client.projects().list().execute().stream()
                .filter(project1 -> project1.getName().equals(projectName))
                .map(com.codepine.api.testrail.model.Project::getId)
                .findFirst().get();

        return this.client.suites().list(projectId).execute();
    }

    private List<User> getUsers(){
        if(userList.isEmpty()){
            userList = client.users().list().execute();
            return userList;
        }else{
            return userList;
        }
    }

    public List<Case> getCases(int projectId, int suiteId) {
        List<CaseField> caseFieldList = this.client.caseFields().list().execute();
        return this.client.cases().list(projectId, suiteId, caseFieldList).execute();
    }

    public List<Section> getSections(int projectID, int suiteID) {
        return this.client.sections().list(projectID, suiteID).execute();
    }


    public List<RailTestCase> getTestCasesBySectionId(int id) {
        List<CaseField> caseFieldList = client.caseFields().list().execute();
        List<Case> cases = client.cases().list(RAIL_PROJECT_ID, SUITE_ID, caseFieldList).execute();
        return cases.stream()
                .filter(aCase -> aCase.getSectionId() == id)
                .map(aCase -> new RailTestCase(aCase.getId(), getUserName(aCase.getCreatedBy()) , aCase.getTitle(), aCase.getCustomField(STEPS_SEPARATED_FIELD),aCase.getCustomField(PRECONDITION_FIELD) , aCase.getCustomField(KEYWORDS), getStoryNameBySectionId(aCase.getSectionId())))
                .collect(Collectors.toList());
    }

    private List<Section> getSections() {
        if (sectionList.isEmpty()) {
            sectionList = client.sections().list(RAIL_PROJECT_ID, SUITE_ID).execute();
            return sectionList;
        } else {
            return sectionList;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private String getStoryNameBySectionId(int sectionId) {
        return getSections()
                .stream()
                .filter(section -> section.getId() == sectionId)
                .map(Section::getName)
                .collect(Collectors.toList())
                .get(0);
    }

    private String getUserName(int userId){
        return getUsers()
                .stream()
                .filter(user -> user.getId() == userId)
                .map(User::getName)
                .collect(Collectors.toList())
                .get(0);
    }
}
