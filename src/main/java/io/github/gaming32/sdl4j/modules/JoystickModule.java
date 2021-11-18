package io.github.gaming32.sdl4j.modules;

import io.github.gaming32.sdl4j.LowLevel;
import io.github.gaming32.sdl4j.SDLException;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library;
import io.github.gaming32.sdl4j.SDL4J.Module;

public final class JoystickModule implements Module {
    private static JoystickModule INSTANCE = null;

    JoystickModule() {
        if (INSTANCE != null) {
            throw new IllegalStateException("JoystickModule instance already exists. Did you mean to use getInstance()?");
        }
    }

    public static JoystickModule getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JoystickModule();
        }
        return INSTANCE;
    }

    @Override
    public void init() {
        SDL2Library lib = LowLevel.getInstance();
        if (lib.SDL_WasInit(SDL2Library.SDL_INIT_JOYSTICK) == 0) {
            if (lib.SDL_InitSubSystem(SDL2Library.SDL_INIT_JOYSTICK) != 0) {
                SDLException.throwNew();
            }
            lib.SDL_JoystickEventState(SDL2Library.SDL_ENABLE);
        }
    }

    @Override
    public void quit() {
        SDL2Library lib = LowLevel.getInstance();
        // TODO: Close joysticks

        if (lib.SDL_WasInit(SDL2Library.SDL_INIT_JOYSTICK) != 0) {
            lib.SDL_JoystickEventState(SDL2Library.SDL_ENABLE);
            lib.SDL_QuitSubSystem(SDL2Library.SDL_INIT_JOYSTICK);
        }
    }
}
