package com.github.thermoweb.adr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

import org.jetbrains.annotations.NotNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;

public class CreateAdrAction extends AnAction {
    public static final Logger log = Logger.getInstance(CreateAdrAction.class);

    public CreateAdrAction() {
        super("Create Adr", "Create new adr", AllIcons.General.Add);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project != null) {
            createNewAdr(project);
        }
    }

    private void createNewAdr(Project project) {
        String adrTitle = JOptionPane.showInputDialog(AdrBundle.message("adrTitleInput"));
        if (adrTitle != null && !adrTitle.isEmpty()) {
            generateAdrFile(project, adrTitle);
        }
    }

    private void generateAdrFile(Project project, String adrTitle) {
        try {
            copyTemplate(project, adrTitle);
        } catch (IOException e) {
            log.error("an error occurred.", e);
            throw new RuntimeException(e);
        }
    }

    private void copyTemplate(Project project, String adrTitle) throws IOException {
        String adrDirectoryPath = project.getBasePath() + "/docs/adr/";
        File adrDirectory = new File(adrDirectoryPath);

        if (!adrDirectory.exists()) {
            adrDirectory.mkdirs();
        }

        String newAdrFileName = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + adrTitle.replaceAll("\\s+", "-") + ".md";
        Path adrPath = new File(adrDirectory, newAdrFileName).toPath();

        Files.copy(Paths.get(adrDirectoryPath + "template.md"), adrPath);
        VirtualFileManager.getInstance().syncRefresh();
    }
}
