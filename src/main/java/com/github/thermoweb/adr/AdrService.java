package com.github.thermoweb.adr;

import java.util.List;

import com.intellij.openapi.components.Service;

@Service(Service.Level.PROJECT)
public final class AdrService {

    public List<Adr> getAdrs() {
        return List.of(new Adr("20241009", "my first fake adr !"), new Adr("20240101", "my older adr"));
    }
}
