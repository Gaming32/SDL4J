package io.github.gaming32.sdl4j.modules;

import com.sun.jna.Platform;

import io.github.gaming32.sdl4j.LowLevel;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library;
import io.github.gaming32.sdl4j.SDL4J.Module;
import io.github.gaming32.sdl4j.SDLException;

public final class DisplayModule implements Module {
    private static DisplayModule INSTANCE = null;

    DisplayModule() {
        if (INSTANCE != null) {
            throw new IllegalStateException("DisplayModule instance already exists. Did you mean to use getInstance()?");
        }
    }

    public static DisplayModule getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DisplayModule();
        }
        return INSTANCE;
    }

    private void macInit() {
        if (Platform.isMac()) {
            // Empty for now
        }
    }

    @Override
    public void init() {
        SDL2Library lib = LowLevel.getInstance();
        String driverName = lib.SDL_getenv("SDL_VIDEODRIVER");
        if (driverName != null && !driverName.equalsIgnoreCase("windib")) {
            lib.SDL_setenv("SDL_VIDEODRIVER", "windows", true);
        }
        if (lib.SDL_WasInit(SDL2Library.SDL_INIT_VIDEO) == 0) {
            macInit();

            if (lib.SDL_InitSubSystem(SDL2Library.SDL_INIT_VIDEO) != 0) {
                SDLException.throwNew();
            }
        }

        TimeModule.getInstance().init();
        EventModule.getInstance().init();
    }

    @Override
    public void quit() {
        SDL2Library lib = LowLevel.getInstance();
        // TODO: Manage display state

        TimeModule.getInstance().quit();
        EventModule.getInstance().quit();

        if (lib.SDL_WasInit(SDL2Library.SDL_INIT_VIDEO) != 0) {
            lib.SDL_QuitSubSystem(SDL2Library.SDL_INIT_VIDEO);
        }
    }
}
