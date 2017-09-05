package model.testrail;

import com.codepine.api.testrail.TestRail;
import com.codepine.api.testrail.model.*;
import utils.ToolWindowData;

import java.util.*;
import java.util.stream.Collectors;

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

    @SuppressWarnings("ConstantConditions")
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

    public List<CaseType> getCaseTypes(){
        return client.caseTypes().list().execute();
    }

    public List<Case> getCases(int projectId, int suiteId) {
        List<CaseField> caseFieldList = this.client.caseFields().list().execute();
        return this.client.cases().list(projectId, suiteId, caseFieldList).execute();
    }

    //TODO check if this fields created for all projects or for just one
    public Set<String> getCustomFields(int projectId, int suiteId){
        Set<String> customFields = new HashSet<>();
        List<CaseField> caseFieldList = this.client.caseFields().list().execute();
        this.client.cases().list(projectId, suiteId, caseFieldList).execute()
                .forEach(aCase -> customFields.addAll(aCase.getCustomFields().keySet()));
        return customFields;
    }

    public List<Section> getSections(int projectID, int suiteID) {
        return this.client.sections().list(projectID, suiteID).execute();
    }


//    public List<RailTestCase> getTestCasesBySectionId(int id) {
//        List<CaseField> caseFieldList = client.caseFields().list().execute();
//        List<Case> cases = client.cases().list(RAIL_PROJECT_ID, SUITE_ID, caseFieldList).execute();
//        return cases.stream()
//                .filter(aCase -> aCase.getSectionId() == id)
//                .map(aCase -> new RailTestCase(aCase.getId(), getUserName(aCase.getCreatedBy()) , aCase.getTitle(), aCase.getCustomField(STEPS_SEPARATED_FIELD),aCase.getCustomField(PRECONDITION_FIELD) , aCase.getCustomField(KEYWORDS), getStoryNameBySectionId(aCase.getSectionId())))
//                .collect(Collectors.toList());
//    }

    @SuppressWarnings("ConstantConditions")
    public String getStoryNameBySectionId(int projectId, int suiteId, int sectionId) {
        return getSections(projectId, suiteId)
                .stream()
                .filter(section -> section.getId() == sectionId)
                .map(Section::getName)
                .collect(Collectors.toList())
                .get(0);
    }

    public String getUserName(int userId){
        return getUsers()
                .stream()
                .filter(user -> user.getId() == userId)
                .map(User::getName)
                .collect(Collectors.toList())
                .get(0);
    }

    //probably it's better to return caseFields as id to handle it later
    public List<CaseFieldCustom> getCustomFieldNamesMap(int projectID){
        List<CaseFieldCustom> caseFieldCustoms = new ArrayList<>();
       client.caseFields().list().execute().stream()
                .filter(caseField ->
                        caseField.getConfigs()
                        .stream()
                        .filter(config -> config.getContext().getProjectIds() != null)
                                .filter(config ->
                                        config.getOptions().getClass().isAssignableFrom(Field.Config.DropdownOptions.class)
                                        || config.getOptions().getClass().isAssignableFrom(Field.Config.MultiSelectOptions.class))
                        .anyMatch(config -> config.getContext().getProjectIds().contains(projectID)))
               .forEach(caseField -> caseFieldCustoms.add(new CaseFieldCustom(caseField.getId(),caseField.getSystemName(),caseField.getLabel(),caseField.getConfigs())));
       return caseFieldCustoms;
    }

    public class CaseFieldCustom{
        private int id;
        private String systemName;
        private String displayedName;
        private List<Field.Config> configs;

        public CaseFieldCustom(int id, String sysName, String dispName, List<Field.Config> configs){
            this.id = id;
            this.systemName = sysName;
            this.displayedName = dispName;
            this.configs = configs;
        }

        public List<Field.Config> getConfigs() {
            return configs;
        }

        public int getId() {
            return id;
        }

        public String getSystemName() {
            return systemName;
        }

        public String getDisplayedName() {
            return displayedName;
        }
    }

}
