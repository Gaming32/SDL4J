package io.github.gaming32.sdl4j.modules;

import com.sun.jna.Pointer;

import io.github.gaming32.sdl4j.LowLevel;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Event;
import io.github.gaming32.sdl4j.SDL4J.Module;

public final class EventModule implements Module {
    private static EventModule INSTANCE = null;
    private boolean isInit;
    private int keyRepeatDelay, keyRepeatInterval;

    EventModule() {
        if (INSTANCE != null) {
            throw new IllegalStateException("EventModule instance already exists. Did you mean to use getInstance()?");
        }
    }

    public static EventModule getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EventModule();
        }
        return INSTANCE;
    }

    @Override
    public void init() {
        SDL2Library lib = LowLevel.getInstance();
        if (!isInit) {
            keyRepeatDelay = 0;
            keyRepeatInterval = 0;
            lib.SDL_SetEventFilter(this::eventFilter, null);
        }
        isInit = true;
    }

    private boolean eventFilter(Pointer ignored, SDL_Event event) {
        return true; // for now
    }
}
