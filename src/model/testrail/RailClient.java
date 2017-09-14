package model.testrail;

import com.codepine.api.testrail.TestRail;
import com.codepine.api.testrail.model.*;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import exceptions.AuthorizationException;
import settings.LoginData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Test Rail client.
 */
public final class RailClient implements Loginable<RailClient> {
    private TestRail client;
    private boolean isLoggedIn = false;

    private static List<Section> sectionList = new ArrayList<>();
    private static List<User> userList = new ArrayList<>();

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public RailClient(Project project) {
    }

    public static RailClient getInstance(com.intellij.openapi.project.Project project) {
        return ServiceManager.getService(project, RailClient.class);
    }

    public List<com.codepine.api.testrail.model.Project> getProjectList() {
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

    private List<User> getUsers() {
        if (userList.isEmpty()) {
            userList = client.users().list().execute();
            return userList;
        } else {
            return userList;
        }
    }

    public List<CaseType> getCaseTypes() {
        return client.caseTypes().list().execute();
    }

    public List<Case> getCases(int projectId, int suiteId) {
        List<CaseField> caseFieldList = this.client.caseFields().list().execute();
        return this.client.cases().list(projectId, suiteId, caseFieldList).execute();
    }

    //TODO check if this fields created for all projects or for just one
    public Set<String> getCustomFields(int projectId, int suiteId) {
        Set<String> customFields = new HashSet<>();
        List<CaseField> caseFieldList = this.client.caseFields().list().execute();
        this.client.cases().list(projectId, suiteId, caseFieldList).execute()
                .forEach(aCase -> customFields.addAll(aCase.getCustomFields().keySet()));
        return customFields;
    }

    public List<Section> getSections(int projectID, int suiteID) {
        return this.client.sections().list(projectID, suiteID).execute();
    }

    @SuppressWarnings("ConstantConditions")
    public String getStoryNameBySectionId(int projectId, int suiteId, int sectionId) {
        return getSections(projectId, suiteId)
                .stream()
                .filter(section -> section.getId() == sectionId)
                .map(Section::getName)
                .collect(Collectors.toList())
                .get(0);
    }

    public String getUserName(int userId) {
        return getUsers()
                .stream()
                .filter(user -> user.getId() == userId)
                .map(User::getName)
                .collect(Collectors.toList())
                .get(0);
    }

    //probably it's better to return caseFields as id to handle it later
    public List<CaseFieldCustom> getCustomFieldNamesMap(int projectID) {
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
                .forEach(caseField -> caseFieldCustoms.add(new CaseFieldCustom(caseField.getId(), caseField.getSystemName(), caseField.getLabel(), caseField.getConfigs())));
        return caseFieldCustoms;
    }

    @Override
    public RailClient login(LoginData data) throws AuthorizationException {
        try {
            client = TestRail.builder(data.getURL(), data.getUserName(), data.getPassword()).build();
            client.projects().list().execute();
            isLoggedIn = true;
        } catch (Exception e) {
            isLoggedIn = false;
            throw new AuthorizationException("Unable to login due to invalid login data or url");
        }
        return this;
    }

    public class CaseFieldCustom {
        private int id;
        private String systemName;
        private String displayedName;
        private List<Field.Config> configs;

        public CaseFieldCustom(int id, String sysName, String dispName, List<Field.Config> configs) {
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
