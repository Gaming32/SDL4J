package io.github.gaming32.sdl4j;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import io.github.gaming32.sdl4j.LowLevel.SDL2Library;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Event;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Point;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Rect;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Renderer;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Surface;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Texture;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Window;
import io.github.gaming32.sdl4j.enums.DisplayFlags;
import io.github.gaming32.sdl4j.math.Vector2;
import io.github.gaming32.sdl4j.modules.DisplayModule;
import io.github.gaming32.sdl4j.sdl_enums.SDL_EventType;
import io.github.gaming32.sdl4j.sdl_enums.SDL_HintPriority;

public final class Display {
    private Display() {}
    private static final Display STATE = new Display();
    private static final Pointer MARKER = new Memory(1);

    private String title;
    private Object icon;
    private short gammaRamp;
    private Pointer glContext;
    private int toggleWindowedW;
    private int toggleWindowedH;
    private boolean usingGl;
    private boolean scaledGl;
    private int scaledGlW;
    private int scaledGlH;
    private int fullscreenBackupX;
    private int fullscreenBackupY;
    private boolean autoResize;

    static SDL_Renderer renderer = null;
    static SDL_Texture texture = null;

    public static void init() {
        DisplayModule.getInstance().init();
    }

    public static void quit() {
        DisplayModule.getInstance().quit();
    }

    public static Surface setMode() {
        return setMode(Vector2.IDENTITY, 0, -1, 0);
    }

    public static Surface setMode(Vector2 size) {
        return setMode(size, 0, -1, 0);
    }

    public static Surface setMode(Vector2 size, int flags) {
        return setMode(size, flags, -1, 0);
    }

    public static Surface setMode(Vector2 size, int flags, int display) {
        return setMode(size, flags, display, 0);
    }

    public static Surface setMode(Vector2 size, int flags, int display, int depth) {
        SDL2Library lib = LowLevel.getInstance();
        final String DEFAULT_TITAL = "SDL4J";

        SDL_Window win = SDL4J.getDefaultWindow();
        Surface surface = SDL4J.getDefaultWindowSurface();
        SDL_Surface surf = null;
        SDL_Surface newOwnedSurf = null;
        int w, h;
        boolean vsync = false;
        if (display == -1) {
            display = getDisplay(win);
        }
        String title = STATE.title;
        boolean initFlip = false;
        String scaleEnv;

        scaleEnv = lib.SDL_getenv("SDL4J_FORCE_SCALE");
        if (scaleEnv != null) {
            flags |= DisplayFlags.SCALED;
            if (scaleEnv.equals("photo")) {
                lib.SDL_SetHintWithPriority(SDL2Library.SDL_HINT_RENDER_SCALE_QUALITY, "best", SDL_HintPriority.OVERRIDE);
            }
        }

        if (size != null) {
            w = (int)size.x;
            h = (int)size.y;
            if (w < 0 || h < 0) {
                throw new IllegalArgumentException("Cannot set negative sized display mode");
            }
        } else {
            w = 0;
            h = 0;
        }

        if (lib.SDL_WasInit(SDL2Library.SDL_INIT_VIDEO) == 0) {
            DisplayModule.getInstance().init();
        }

        STATE.usingGl = (flags & DisplayFlags.OPENGL) != 0;
        STATE.scaledGl = STATE.usingGl && (flags & DisplayFlags.SCALED) != 0;

        if (STATE.title == null) {
            title = STATE.title = DEFAULT_TITAL;
        }

        STATE.toggleWindowedW = 0;
        STATE.toggleWindowedH = 0;

        if (texture != null) {
            lib.SDL_DestroyTexture(texture);
            texture = null;
        }

        if (renderer != null) {
            lib.SDL_DestroyRenderer(renderer);
            renderer = null;
        }

        lib.SDL_DelEventWatch(Display::resizeEventWatch, MARKER);

        return surface;
    }

    private static int getDisplay(SDL_Window win) {
        SDL2Library lib = LowLevel.getInstance();
        String displayEnv = lib.SDL_getenv("SDL4J_DISPLAY");
        int display = 0;

        if (win != null) {
            display = lib.SDL_GetWindowDisplayIndex(win);
        } else if (displayEnv != null) {
            display = Integer.parseInt(displayEnv);
        } else if (lib.SDL_WasInit(SDL2Library.SDL_INIT_VIDEO) != 0) {
            SDL_Rect displayBounds = new SDL_Rect();
            SDL_Point mousePosition = new SDL_Point();
            IntByReference mouseX = new IntByReference();
            IntByReference mouseY = new IntByReference();
            lib.SDL_GetGlobalMouseState(mouseX, mouseY);
            mousePosition.writeField("x", mouseX.getValue());
            mousePosition.writeField("y", mouseY.getValue());
            int numDisplays = lib.SDL_GetNumVideoDisplays();

            for (int i = 0; i < numDisplays; i++) {
                if (lib.SDL_GetDisplayBounds(i, displayBounds) == 0) {
                    if (lib.SDL_PointInRect(mousePosition, displayBounds)) {
                        display = i;
                        break;
                    }
                }
            }
        }
        return display;
    }

    protected static boolean resizeEventWatch(Pointer userdata, SDL_Event event) {
        if (event.getType() != SDL_EventType.WINDOWEVENT) {
            return false;
        }

        SDL_Window window = SDL4J.getDefaultWindow();
        return false;
    }
}
