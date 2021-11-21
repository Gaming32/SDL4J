package io.github.gaming32.sdl4j.enums;

import io.github.gaming32.sdl4j.LowLevel.SDL2Library;

public class MouseFlags {
    public static final int

    LEFT = SDL2Library.SDL_BUTTON_LEFT,
    RIGHT = SDL2Library.SDL_BUTTON_RIGHT,
    MIDDLE = SDL2Library.SDL_BUTTON_MIDDLE,
    WHEELUP = 4,
    WHEELDOWN = 5,
    X1 = SDL2Library.SDL_BUTTON_X1 + 2,
    X2 = SDL2Library.SDL_BUTTON_X2 + 2,
    KEEP = 0x80;
}
