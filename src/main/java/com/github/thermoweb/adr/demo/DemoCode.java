package com.github.thermoweb.adr.demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.github.thermoweb.adr.Adr;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

public class DemoCode {

    private DemoCode() {

    }

    public static JBScrollPane createPanelFromAdrs(List<Adr> adrs) {
        JBTable adrTable = new JBTable();
        DefaultTableModel defaultTableModel = new DefaultTableModel(new Object[]{"Id", "Title"}, 0);
        adrs.forEach(adr -> defaultTableModel.addRow(new Object[]{adr.id(), adr.title()}));
        adrTable.setModel(defaultTableModel);
        return new JBScrollPane(adrTable);
    }

    public static Path copyTemplate(Project project, String adrTitle) throws IOException {
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

    public static List<VirtualFile> listFilesIn(String path) {
        VirtualFile projectDir = LocalFileSystem.getInstance().findFileByPath(path);
        if (projectDir != null && projectDir.isDirectory()) {
            return Arrays.stream(projectDir.getChildren())
                    .filter(f -> f.getName().endsWith(".md"))
                    .filter(f -> !f.getName().equalsIgnoreCase("readme.md"))
                    .filter(f -> !f.getName().equalsIgnoreCase("template.md"))
                    .filter(f -> !f.getName().equalsIgnoreCase("index.md"))
                    .filter(f -> !f.isDirectory())
                    .toList();
        }
        return Collections.emptyList();
    }
}
