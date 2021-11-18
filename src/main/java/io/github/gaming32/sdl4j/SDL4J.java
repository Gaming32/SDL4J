package io.github.gaming32.sdl4j;

import io.github.gaming32.sdl4j.LowLevel.SDL2Library;
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

    private static int wasInit;

    public static ImportSuccess init() {
        SDL2Library lib = LowLevel.getInstance();
        int i = 0, success = 0, fail = 0;

        final Module[] modules = new Module[] {
            DisplayModule.getInstance(),
            JoystickModule.getInstance()
        };

        wasInit = lib.SDL_Init(SDL2Library.SDL_INIT_TIMER);

        for (i = 0; i < modules.length; i++) {
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
        int num, i;

        final Module[] modules = new Module[] {
            JoystickModule.getInstance(),
            DisplayModule.getInstance()
        };

        // TODO: Implement quit handlers

        for (i = 0; i < modules.length; i++) {
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
}
