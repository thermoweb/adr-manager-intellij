package com.github.thermoweb.adr;

import com.intellij.openapi.vfs.VirtualFile;

public record Adr(String id, String title, VirtualFile file) {
}
