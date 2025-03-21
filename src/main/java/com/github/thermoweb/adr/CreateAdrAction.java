package com.github.thermoweb.adr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.swing.JOptionPane;

import org.jetbrains.annotations.NotNull;

import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindowManager;

public class CreateAdrAction extends AnAction {
    public static final Logger log = Logger.getInstance(CreateAdrAction.class);

    public CreateAdrAction() {
        super("Create Adr", "Create new adr", AllIcons.General.Add);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project != null) {
            try {
                createNewAdr(project)
                        .map(adrPath -> LocalFileSystem.getInstance().findFileByIoFile(adrPath.toFile()))
                        .ifPresent(adrFile -> FileEditorManager.getInstance(project).openFile(adrFile, true));
            } catch (IOException e) {
                NotificationGroupManager.getInstance()
                        .getNotificationGroup("adr info")
                        .createNotification("ADR manager", "An error occured", NotificationType.ERROR)
                        .notify(project);
                log.error(e.getMessage(), e);
            }
        }
    }

    private Optional<Path> createNewAdr(Project project) throws IOException {
        String adrTitle = JOptionPane.showInputDialog(AdrBundle.message("adrTitleInput"));
        if (adrTitle != null && !adrTitle.isEmpty()) {
            return Optional.of(generateAdrFile(project, adrTitle));
        }
        return Optional.empty();
    }

    private Path generateAdrFile(Project project, String adrTitle) throws IOException {
        Path adrPath = copyTemplate(project, adrTitle);
        ToolWindowManager.getInstance(project).notifyByBalloon("ADR Manager", MessageType.INFO, "ADR created!");
        NotificationGroupManager.getInstance()
                .getNotificationGroup("adr info")
                .createNotification("ADR manager", "N'oublie pas tes imputations !!!", NotificationType.WARNING)
                .notify(project);
        return adrPath;

    }

    private Path copyTemplate(Project project, String adrTitle) throws IOException {
        String adrDirectoryPath = project.getBasePath() + "/docs/adr/";
        File adrDirectory = new File(adrDirectoryPath);

        if (!adrDirectory.exists()) {
            adrDirectory.mkdirs();
        }

        String newAdrFileName = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + adrTitle.replaceAll("\\s+", "-") + ".md";
        Path adrPath = new File(adrDirectory, newAdrFileName).toPath();

        Files.copy(Paths.get(adrDirectoryPath + "template.md"), adrPath);
        VirtualFileManager.getInstance().syncRefresh();
        return adrPath;
    }
}
