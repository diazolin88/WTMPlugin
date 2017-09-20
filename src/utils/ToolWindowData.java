package utils;

/**
 * This class should present current selected values in window
 */
public final class ToolWindowData {
    private final String suiteName;
    private final String projectName;

    public ToolWindowData(String suiteName, String projectName) {
        this.suiteName = suiteName;
        this.projectName = projectName;
    }

    public final String getSuiteName() {
        return suiteName;
    }

    public final String getProjectName() {
        return projectName;
    }
}
