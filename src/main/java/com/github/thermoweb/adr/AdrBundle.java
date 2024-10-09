package com.github.thermoweb.adr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import com.intellij.DynamicBundle;

public class AdrBundle {
    private static final String BUNDLE = "messages.AdrBundle";
    private static final DynamicBundle INSTANCE = new DynamicBundle(AdrBundle.class, BUNDLE);

    private AdrBundle() {
    }

    public static String message(@PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params) {
        return INSTANCE.getMessage(key, params);
    }
}
