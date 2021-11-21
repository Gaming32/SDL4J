package io.github.gaming32.sdl4j;

import io.github.gaming32.sdl4j.LowLevel.SDL2Library;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Window;
import io.github.gaming32.sdl4j.modules.DisplayModule;
import io.github.gaming32.sdl4j.modules.JoystickModule;

public final class SDL4J {
    public static interface Module {
        public void init();
        public void quit();
    }

    static {
        // Is using a shutdown hook for this the best way to do it?
        Runtime.getRuntime().addShutdownHook(new Thread(SDL4J::shutdownQuit));
    }

    private static int wasInit = 0;
    private static SDL_Window defaultWindow = null;
    private static Surface defaultScreen = null;

    public static ImportSuccess init() {
        SDL2Library lib = LowLevel.getInstance();
        int success = 0, fail = 0;

        final Module[] modules = new Module[] {
            DisplayModule.getInstance(),
            JoystickModule.getInstance()
        };

        wasInit = lib.SDL_Init(SDL2Library.SDL_INIT_TIMER);

        for (int i = 0; i < modules.length; i++) {
            try {
                modules[i].init();
                success++;
            } catch (Exception e) {
                fail++;
            }
        }

        wasInit = 1;
        return new ImportSuccess(success, fail);
    }

    public static void quit() {
        quit0();
    }

    private static void quit0() {
        final Module[] modules = new Module[] {
            JoystickModule.getInstance(),
            DisplayModule.getInstance()
        };

        // TODO: Implement quit handlers

        for (int i = 0; i < modules.length; i++) {
            modules[i].quit();
        }

        shutdownQuit();
    }

    private static void shutdownQuit() {
        SDL2Library lib = LowLevel.getInstance();
        if (wasInit != 0) {
            wasInit = 0;
            lib.SDL_Quit();
        }
    }

    static SDL_Window getDefaultWindow() {
        return defaultWindow;
    }

    static Surface getDefaultWindowSurface() {
        return defaultScreen;
    }

    static void setDefaultWindow(SDL_Window win) {
        SDL2Library lib = LowLevel.getInstance();
        if (win.equals(defaultWindow)) {
            return;
        }
        if (defaultWindow != null) {
            lib.SDL_DestroyWindow(defaultWindow);
        }
        defaultWindow = win;
    }

    static void setDefaultWindowSurface(Surface screen) {
        if (screen == defaultScreen) {
            return;
        }
        defaultScreen = screen;
    }

    static void videoInitCheck() {
        SDL2Library lib = LowLevel.getInstance();
        if (lib.SDL_WasInit(SDL2Library.SDL_INIT_VIDEO) == 0) {
            throw new IllegalStateException("Video system not initialized");
        }
    }
}
