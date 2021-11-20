package io.github.gaming32.sdl4j.sdl_enums;

/**
 * The flags on a window
 *
 * @see LowLevel.SDL2Library#SDL_GetWindowFlags()
 */
public final class SDL_WindowFlags {
    public static final int

    FULLSCREEN = 0x00000001,
    OPENGL = 0x00000002,
    SHOWN = 0x00000004,
    HIDDEN = 0x00000008,
    BORDERLESS = 0x00000010,
    RESIZABLE = 0x00000020,
    MINIMIZED = 0x00000040,
    MAXIMIZED = 0x00000080,
    MOUSE_GRABBED = 0x00000100,
    INPUT_FOCUS = 0x00000200,
    MOUSE_FOCUS = 0x00000400,
    FULLSCREEN_DESKTOP = (FULLSCREEN | 0x00001000),
    FOREIGN = 0x00000800,
    ALLOW_HIGHDPI = 0x00002000,
    MOUSE_CAPTURE    = 0x00004000,
    ALWAYS_ON_TOP    = 0x00008000,
    SKIP_TASKBAR     = 0x00010000,
    UTILITY          = 0x00020000,
    TOOLTIP          = 0x00040000,
    POPUP_MENU       = 0x00080000,
    KEYBOARD_GRABBED = 0x00100000,
    VULKAN           = 0x10000000,
    METAL            = 0x20000000,

    INPUT_GRABBED = MOUSE_GRABBED;
}
