package plugincomponents;

import actions.OpenRailSettingsWindowAction;
import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import exceptions.AuthorizationException;
import model.testrail.RailConnection;
import settings.WTMSettings;


public class WTMPluginComponent implements ProjectComponent {
    private static final NotificationGroup GROUP_DISPLAY_ID_INFO = new NotificationGroup("WTMplugin_group", NotificationDisplayType.BALLOON, true);
    private RailConnection conn;
    private Project project;
    private WTMSettings settings;

    public WTMPluginComponent(Project project) {
        this.project = project;
        conn = RailConnection.getInstance(project);
        settings = WTMSettings.getInstance(project);
    }

    @Override
    public void projectOpened() {
        try {
            conn.login(settings);
        } catch (AuthorizationException e) {
            showMyMessage();
        }
    }

    private void showMyMessage() {
        ApplicationManager.getApplication().invokeLater(() -> {
            com.intellij.notification.Notification notification = GROUP_DISPLAY_ID_INFO
                    .createNotification("<html>TestRail login failed", " Go to <a href=\"" + " LINK!!!" + "\" target=\"blank\">Settings</a> to setup login data!</html>",
                            NotificationType.ERROR,
                            new NotificationListener.UrlOpeningListener(true));
            Project[] projects = ProjectManager.getInstance().getOpenProjects();
            Notifications.Bus.notify(notification, projects[0]);
        });
    }
}
