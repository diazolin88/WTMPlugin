package utils;

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
}
