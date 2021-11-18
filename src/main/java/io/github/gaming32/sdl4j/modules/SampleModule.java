package io.github.gaming32.sdl4j.modules;

import io.github.gaming32.sdl4j.SDL4J.Module;

public final class SampleModule implements Module {
    private static SampleModule INSTANCE = null;

    SampleModule() {
        if (INSTANCE != null) {
            throw new IllegalStateException("SampleModule instance already exists. Did you mean to use getInstance()?");
        }
    }

    public static SampleModule getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SampleModule();
        }
        return INSTANCE;
    }

    @Override
    public void init() {
    }

    @Override
    public void quit() {
    }
}
