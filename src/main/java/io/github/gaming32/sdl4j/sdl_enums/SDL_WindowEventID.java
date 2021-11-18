package io.github.gaming32.sdl4j.sdl_enums;

public class SDL_WindowEventID {
    public static final int

    /** Never used */
    NONE = 0,
    /** Window has been shown */
    SHOWN = 1,
    /** Window has been hidden */
    HIDDEN = 2,
    /** Window has been exposed and should be redrawn */
    EXPOSED = 3,
    /** Window has been moved to data1, data2 */
    MOVED = 4,
    /** Window has been resized to data1xdata2 */
    RESIZED = 5,
    /** The window size has changed, either as
        a result of an API call or through the
        system or user changing the window size. */
    SIZE_CHANGED = 6,
    /** Window has been minimized */
    MINIMIZED = 7,
    /** Window has been maximized */
    MAXIMIZED = 8,
    /** Window has been restored to normal size and position */
    RESTORED = 9,
    /** Window has gained mouse focus */
    ENTER = 10,
    /** Window has lost mouse focus */
    LEAVE = 11,
    /** Window has gained keyboard focus */
    FOCUS_GAINED = 12,
    /** Window has lost keyboard focus */
    FOCUS_LOST = 13,
    /** The window manager requests that the window be closed */
    CLOSE = 14,
    /** Window is being offered a focus (should SetWindowInputFocus() on itself or a subwindow, or ignore) */
    TAKE_FOCUS = 15,
    /** Window had a hit test that wasn't SDL_HITTEST_NORMAL. */
    HIT_TEST = 16;
}
