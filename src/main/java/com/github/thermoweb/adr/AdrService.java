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
                .map(Adr::fromVirtualFile)
                .collect(Collectors.toMap(Adr::id, Function.identity(), (existingValue, newValue) -> newValue));
        adrCache.clear();
        adrCache.putAll(adrs);
        return new ArrayList<>(adrs.values());
    }

    public Optional<Adr> getAdrFromId(String id) {
        if (adrCache.isEmpty()) {
            log.warn("ADRs cache was empty!");
            getAdrs();
        }
        Adr adrById = adrCache.get(id);
        if (adrById != null) {
            return Optional.of(adrById);
        }
        return findOneById(id);
    }

    private Optional<Adr> findOneById(String id) {
        List<Adr> adrs = adrCache.values()
                .stream()
                .filter(adr -> adr.id().startsWith(id))
                .toList();
        if (adrs.size() != 1) {
            return Optional.empty();
        }
        return Optional.ofNullable(adrs.get(0));
    }

    private List<VirtualFile> listFilesIn(Project project) {
        if (project == null || project.getBasePath() == null) {
            log.error("Project path is null");
            return Collections.emptyList();
        }
        VirtualFile projectDir = LocalFileSystem.getInstance().findFileByPath(project.getBasePath() + "/docs/adr"); //FIXME: make this configurable
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
