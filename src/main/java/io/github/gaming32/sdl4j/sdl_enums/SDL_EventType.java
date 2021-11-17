package io.github.gaming32.sdl4j.sdl_enums;

public final class SDL_EventType {
    public static final int

    FIRSTEVENT     = 0,     /** Unused (do not remove) */

    /* Application events */
    QUIT           = 0x100, /**< User-requested quit */

    /* These application events have special meaning on iOS, see README-ios.md for details */
    APP_TERMINATING = 0x101,        /**< The application is being terminated by the OS
                                    Called on iOS in applicationWillTerminate()
                                    Called on Android in onDestroy()
                                */
    APP_LOWMEMORY = 0x102,          /**< The application is low on memory, free memory if possible.
                                    Called on iOS in applicationDidReceiveMemoryWarning()
                                    Called on Android in onLowMemory()
                                */
    APP_WILLENTERBACKGROUND = 0x103, /**< The application is about to enter the background
                                    Called on iOS in applicationWillResignActive()
                                    Called on Android in onPause()
                                */
    APP_DIDENTERBACKGROUND = 0x104, /**< The application did enter the background and may not get CPU for some time
                                    Called on iOS in applicationDidEnterBackground()
                                    Called on Android in onPause()
                                */
    APP_WILLENTERFOREGROUND = 0x105, /**< The application is about to enter the foreground
                                    Called on iOS in applicationWillEnterForeground()
                                    Called on Android in onResume()
                                */
    APP_DIDENTERFOREGROUND = 0x106, /**< The application is now interactive
                                    Called on iOS in applicationDidBecomeActive()
                                    Called on Android in onResume()
                                */

    LOCALECHANGED = 0x107,  /**< The user's locale preferences have changed. */

    /* Display events */
    DISPLAYEVENT   = 0x150,  /**< Display state change */

    /* Window events */
    WINDOWEVENT    = 0x200, /**< Window state change */
    SYSWMEVENT = 0x201,             /**< System specific event */

    /* Keyboard events */
    KEYDOWN        = 0x300, /**< Key pressed */
    KEYUP = 0x301,                  /**< Key released */
    TEXTEDITING = 0x302,            /**< Keyboard text editing (composition) */
    TEXTINPUT = 0x303,              /**< Keyboard text input */
    KEYMAPCHANGED = 0x304,          /**< Keymap changed due to a system event such as an
                                    input language or keyboard layout change.
                                */

    /* Mouse events */
    MOUSEMOTION    = 0x400, /**< Mouse moved */
    MOUSEBUTTONDOWN = 0x401,        /**< Mouse button pressed */
    MOUSEBUTTONUP = 0x402,          /**< Mouse button released */
    MOUSEWHEEL = 0x403,             /**< Mouse wheel motion */

    /* Joystick events */
    JOYAXISMOTION  = 0x600, /**< Joystick axis motion */
    JOYBALLMOTION = 0x601,          /**< Joystick trackball motion */
    JOYHATMOTION = 0x602,           /**< Joystick hat position change */
    JOYBUTTONDOWN = 0x603,          /**< Joystick button pressed */
    JOYBUTTONUP = 0x604,            /**< Joystick button released */
    JOYDEVICEADDED = 0x605,         /**< A new joystick has been inserted into the system */
    JOYDEVICEREMOVED = 0x606,       /**< An opened joystick has been removed */

    /* Game controller events */
    CONTROLLERAXISMOTION  = 0x650, /**< Game controller axis motion */
    CONTROLLERBUTTONDOWN = 0x651,          /**< Game controller button pressed */
    CONTROLLERBUTTONUP = 0x652,            /**< Game controller button released */
    CONTROLLERDEVICEADDED = 0x653,         /**< A new Game controller has been inserted into the system */
    CONTROLLERDEVICEREMOVED = 0x654,       /**< An opened Game controller has been removed */
    CONTROLLERDEVICEREMAPPED = 0x655,      /**< The controller mapping was updated */
    CONTROLLERTOUCHPADDOWN = 0x656,        /**< Game controller touchpad was touched */
    CONTROLLERTOUCHPADMOTION = 0x657,      /**< Game controller touchpad finger was moved */
    CONTROLLERTOUCHPADUP = 0x658,          /**< Game controller touchpad finger was lifted */
    CONTROLLERSENSORUPDATE = 0x659,        /**< Game controller sensor was updated */

    /* Touch events */
    FINGERDOWN      = 0x700,
    FINGERUP = 0x701,
    FINGERMOTION = 0x702,

    /* Gesture events */
    DOLLARGESTURE   = 0x800,
    DOLLARRECORD = 0x801,
    MULTIGESTURE = 0x802,

    /* Clipboard events */
    CLIPBOARDUPDATE = 0x900, /**< The clipboard changed */

    /* Drag and drop events */
    DROPFILE        = 0x1000, /**< The system requests a file open */
    DROPTEXT = 0x1001,                 /**< text/plain drag-and-drop event */
    DROPBEGIN = 0x1002,                /**< A new set of drops is beginning (NULL filename) */
    DROPCOMPLETE = 0x1003,             /**< Current set of drops is now complete (NULL filename) */

    /* Audio hotplug events */
    AUDIODEVICEADDED = 0x1100, /**< A new audio device is available */
    AUDIODEVICEREMOVED = 0x1101,        /**< An audio device has been removed. */

    /* Sensor events */
    SENSORUPDATE = 0x1200,     /**< A sensor was updated */

    /* Render events */
    RENDER_TARGETS_RESET = 0x2000, /**< The render targets have been reset and their contents need to be updated */
    RENDER_DEVICE_RESET = 0x2001, /**< The device has been reset and all textures need to be recreated */

    /** Events ::USEREVENT through ::LASTEVENT are for your use,
     *  and should be allocated with RegisterEvents()
     */
    USEREVENT    = 0x8000,

    /**
     *  This last event is only for bounding internal arrays
     */
    LASTEVENT    = 0xFFFF;
}
