package model.testrail;

import com.codepine.api.testrail.model.Case;
import com.codepine.api.testrail.model.Project;
import com.codepine.api.testrail.model.Section;
import com.codepine.api.testrail.model.Suite;

import java.util.List;

/**
 * Storage for rail data (sections and test cases).
 */
public final class RailDataStorage {
    private List<Section> sections;
    private List<Case> cases;

    private static RailDataStorage instance = null;
    private static RailClient client = null;

    private RailDataStorage() {
    }

    public static RailDataStorage getInstance(RailClient railClient) {
        if (instance == null) {
            instance = new RailDataStorage();
            client = railClient;
        }

        return instance;
    }

    // region sections and cases

    public RailDataStorage setSections(List<Section> sections) {
        this.sections = sections;
        return this;
    }

    public RailDataStorage setCases(List<Case> cases) {
        this.cases = cases;
        return this;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Case> getCases() {
        return cases;
    }

    // endregion

    public final Integer getProjectIdByProjectName(String projectName) {
        return client
                .getProjectList()
                .stream()
                .filter(projectItem -> projectItem.getName().equals(projectName))
                .map(Project::getId)
                .findFirst().orElse(null);
    }

    public final Integer getSuiteIdBySuiteName(String projectName, String suiteName) {
        return client.getSuitesList(projectName)
                .stream()
                .filter(suite -> suite.getName().equals(suiteName))
                .map(Suite::getId)
                .findFirst().orElse(null);
    }
}
