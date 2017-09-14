package plugincomponents;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
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
    private final Project project;

    private WTMPluginComponent(Project project) {
        this.project = project;
        settings = WTMSettings.getInstance(project);
    }

    @Override
    public void projectOpened() {
        try {
            RailClient.getInstance(project).login(settings);
        } catch (AuthorizationException e) {
            showMyMessage();
        }
    }

    public void projectClosed() {
        //TODO add logic when project get closed.
    }

    private void showMyMessage() {
        Project[] project = new Project[1];
        ApplicationManager.getApplication().invokeLater(() -> {
            com.intellij.notification.Notification notification = GROUP_DISPLAY_ID_INFO
                    .createNotification("<html>TestRail login failed", " Go to settings to setup login data!</html>",
                            NotificationType.ERROR,
                            new NotificationListener.UrlOpeningListener(true))
                    .addAction(new NotificationAction("Settings") {
                        @Override
                        public void actionPerformed(@NotNull AnActionEvent anActionEvent, @NotNull Notification notification) {
                            DataContext dataContext = anActionEvent.getDataContext();
                            project[0] = PlatformDataKeys.PROJECT.getData(dataContext);
                            ShowSettingsUtil.getInstance().showSettingsDialog(project[0], WTMSettingsWindowRenderer.class);
                        }
                    });
            Notifications.Bus.notify(notification, project[0]);
        });
    }
}
