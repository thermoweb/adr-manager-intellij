package com.github.thermoweb.adr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

@Service(Service.Level.PROJECT)
public final class AdrService {
    public static final Logger log = Logger.getInstance(AdrService.class);

    public final Project project;
    private final ConcurrentHashMap<String, Adr> adrCache = new ConcurrentHashMap<>();

    public AdrService(Project project) {
        this.project = project;
    }

    public List<Adr> getAdrs() {
        Map<String, Adr> adrs = listFilesIn(project).stream()
                .map(AdrService::fromVirtualFile)
                .collect(Collectors.toMap(Adr::id, Function.identity(), (existingValue, newValue) -> newValue));
        adrCache.clear();
        adrCache.putAll(adrs);
        return new ArrayList<>(adrs.values());
    }

    public Optional<Adr> getAdrFromId(String id) {
        if (adrCache.isEmpty()) {
            log.warn("Cache is empty!");
        }
        return Optional.ofNullable(adrCache.get(id));
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
                    .filter(f -> !f.getName().equalsIgnoreCase("readme.md"))
                    .filter(f -> !f.getName().equalsIgnoreCase("template.md"))
                    .filter(f -> !f.getName().equalsIgnoreCase("index.md"))
                    .filter(f -> !f.isDirectory())
                    .toList();
        }
        return Collections.emptyList();
    }

    private static Adr fromVirtualFile(VirtualFile virtualFile) {
        String[] motifs = virtualFile.getName().split("-");
        String title = String.join(" ", Arrays.asList(motifs).subList(1, motifs.length)).replace(".md", "");
        return new Adr(motifs[0], title);
    }
}
