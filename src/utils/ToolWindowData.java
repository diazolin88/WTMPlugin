package utils;

import com.codepine.api.testrail.model.Suite;
import model.testrail.RailClient;

/**
 * This class should present current selected values in window
 *
 * */
public final class ToolWindowData {
    private final String suiteName;
    private final String projectName;
    private final RailClient client;

    public ToolWindowData(String suiteName, String projectName, RailClient client) {
        this.suiteName = suiteName;
        this.projectName = projectName;
        this.client = client;
    }

    public final String getSuiteName() {
        return suiteName;
    }

    public final String getProjectName() {
        return projectName;
    }

    public final Integer getProjectId() {
        return client.getProjectList().stream()
                .filter(project1 -> project1.getName().equals(projectName))
                .map(com.codepine.api.testrail.model.Project::getId)
                .findFirst().orElse(null);

    }

    public final Integer getSuiteId() {
        return client.getSuitesList(projectName)
                .stream()
                .filter(suite -> suite.getName().equals(suiteName))
                .map(Suite::getId)
                .findFirst().orElse(null);
    }

}
