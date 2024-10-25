package com.github.thermoweb.adr;

import java.util.Arrays;

import com.intellij.openapi.vfs.VirtualFile;

public record Adr(String id, String shortId, String title, VirtualFile file) {

    public static Adr fromVirtualFile(VirtualFile virtualFile) {
        String[] motifs = virtualFile.getName().split("-");
        String title = String.join(" ", Arrays.asList(motifs).subList(1, motifs.length)).replace(".md", "");
        String id = virtualFile.getName().replace(".md", "");
        String shortId = motifs[0];
        return new Adr(id, shortId, title, virtualFile);
    }
}
