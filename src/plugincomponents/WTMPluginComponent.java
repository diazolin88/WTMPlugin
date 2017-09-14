package plugincomponents;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import exceptions.AuthorizationException;
import model.testrail.RailClient;
import org.jetbrains.annotations.NotNull;
import settings.WTMSettings;
import settings.WTMSettingsWindowRenderer;

public class WTMPluginComponent implements ProjectComponent {
    private static final NotificationGroup GROUP_DISPLAY_ID_INFO = new NotificationGroup("WTMplugin_group", NotificationDisplayType.STICKY_BALLOON, true);
    private WTMSettings settings;
    private Project project;
    private RailClient client;

    public WTMPluginComponent(Project project) {
        settings = WTMSettings.getInstance(project);
        client = RailClient.getInstance(project);
    }

    @Override
    public void projectOpened() {
        try {
            client.login(settings);
        } catch (AuthorizationException e) {
            showMyMessage();
        }
    }

    public void projectClosed() {
        //TODO add logic when project get closed.
    }

    private void showMyMessage() {
        ApplicationManager.getApplication().invokeLater(() -> {
            com.intellij.notification.Notification notification = GROUP_DISPLAY_ID_INFO
                    .createNotification("<html>TestRail login failed", " Go to settings to setup login data!</html>",
                            NotificationType.ERROR,
                            new NotificationListener.UrlOpeningListener(true))
                    .addAction(new NotificationAction("Settings") {
                        @Override
                        public void actionPerformed(@NotNull AnActionEvent anActionEvent, @NotNull Notification notification) {
                            ShowSettingsUtil.getInstance().showSettingsDialog(project, WTMSettingsWindowRenderer.class);
                        }
                    });
            Notifications.Bus.notify(notification, project);
        });
    }
}
