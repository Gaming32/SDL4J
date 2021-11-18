package io.github.gaming32.sdl4j.modules;

import com.sun.jna.Pointer;

import io.github.gaming32.sdl4j.LowLevel;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library;
import io.github.gaming32.sdl4j.SDL4J.Module;
import io.github.gaming32.sdl4j.SDLException;

public final class TimeModule implements Module {
    private static TimeModule INSTANCE = null;
    private Pointer timerMutex;

    TimeModule() {
        if (INSTANCE != null) {
            throw new IllegalStateException("TimeModule instance already exists. Did you mean to use getInstance()?");
        }
    }

    public static TimeModule getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TimeModule();
        }
        return INSTANCE;
    }

    @Override
    public void init() {
        SDL2Library lib = LowLevel.getInstance();
        if (timerMutex == null) {
            timerMutex = lib.SDL_CreateMutex();
            if (timerMutex == null) {
                SDLException.throwNew();
            }
        }
    }

    @Override
    public void quit() {
        SDL2Library lib = LowLevel.getInstance();
        lib.SDL_LockMutex(timerMutex);
        // TODO: Event timers
        lib.SDL_UnlockMutex(timerMutex);
        lib.SDL_DestroyMutex(timerMutex);
        timerMutex = null;
    }
}
