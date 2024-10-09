package com.github.thermoweb.adr;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

@Service(Service.Level.PROJECT)
public final class AdrService {
    public static final Logger log = Logger.getInstance(AdrService.class);

    public final Project project;

    public AdrService(Project project) {
        this.project = project;
    }

    public List<Adr> getAdrs() {
        return listFilesIn(project).stream()
                .map(adrFile -> new Adr(adrFile.getName(), adrFile.getName()))
                .toList();
    }

    private List<VirtualFile> listFilesIn(Project project) {
        if (project == null || project.getBasePath() == null) {
            log.error("Project path is null");
            return Collections.emptyList();
        }
        VirtualFile projectDir = LocalFileSystem.getInstance().findFileByPath(project.getBasePath() + "/docs/adr");
        if (projectDir != null && projectDir.isDirectory()) {
            return Arrays.stream(projectDir.getChildren())
                    .filter(f -> f.getName().endsWith(".md"))
                    .filter(f -> !f.isDirectory())
                    .toList();
        }
        return Collections.emptyList();
    }
}
