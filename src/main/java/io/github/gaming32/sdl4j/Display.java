package io.github.gaming32.sdl4j;

import static io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_WINDOWPOS_CENTERED_DISPLAY;
import static io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_WINDOWPOS_UNDEFINED_DISPLAY;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import io.github.gaming32.sdl4j.LowLevel.OpenGL.GL_glViewport_Func;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_DisplayMode;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Event;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Point;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Rect;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Renderer;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Surface;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Texture;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Window;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_WindowEvent;
import io.github.gaming32.sdl4j.enums.DisplayFlags;
import io.github.gaming32.sdl4j.math.Vector2;
import io.github.gaming32.sdl4j.modules.DisplayModule;
import io.github.gaming32.sdl4j.sdl_enums.SDL_EventType;
import io.github.gaming32.sdl4j.sdl_enums.SDL_GLattr;
import io.github.gaming32.sdl4j.sdl_enums.SDL_HintPriority;
import io.github.gaming32.sdl4j.sdl_enums.SDL_PixelFormatEnum;
import io.github.gaming32.sdl4j.sdl_enums.SDL_TextureAccess;
import io.github.gaming32.sdl4j.sdl_enums.SDL_WindowEventID;
import io.github.gaming32.sdl4j.sdl_enums.SDL_WindowFlags;

public final class Display {
    private Display() {}
    private static final Display STATE = new Display();
    private static final Pointer MARKER = new Memory(1);
    private static final int SHORT_SIZE = Native.getNativeSize(short.class);

    private String title;
    private Surface icon;
    private Memory gammaRamp;
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

        {
            int sdlFlags = 0;
            SDL_DisplayMode displayMode = new SDL_DisplayMode();

            if (lib.SDL_GetDesktopDisplayMode(display, displayMode) != 0) {
                SDLException.throwNew();
            }
            displayMode.read();

            if (w == 0 && h == 0 && (flags & DisplayFlags.SCALED) == 0) {
                w = displayMode.w;
                h = displayMode.h;
            }

            if ((flags & DisplayFlags.FULLSCREEN) != 0) {
                if ((flags & DisplayFlags.SCALED) != 0) {
                    sdlFlags |= SDL_WindowFlags.FULLSCREEN_DESKTOP;
                } else if (w == displayMode.w && h == displayMode.h) {
                    sdlFlags |= SDL_WindowFlags.FULLSCREEN_DESKTOP;
                } else {
                    sdlFlags |= SDL_WindowFlags.FULLSCREEN;
                }
            }

            if ((flags & DisplayFlags.SCALED) != 0) {
                if (w == 0 || h == 0) {
                    throw new IllegalArgumentException("Cannot set 0 sized SCALED display mode");
                }
            }

            if ((flags & DisplayFlags.OPENGL) != 0) {
                sdlFlags |= SDL_WindowFlags.OPENGL;
            }
            if ((flags & DisplayFlags.NOFRAME) != 0) {
                sdlFlags |= SDL_WindowFlags.BORDERLESS;
            }
            if ((flags & DisplayFlags.RESIZABLE) != 0) {
                sdlFlags |= SDL_WindowFlags.RESIZABLE;
                if (STATE.autoResize) {
                    lib.SDL_AddEventWatch(Display::resizeEventWatch, MARKER);
                }
            }
            if ((flags & DisplayFlags.SHOWN) != 0) {
                sdlFlags |= SDL_WindowFlags.SHOWN;
            }
            if ((flags & DisplayFlags.HIDDEN) != 0) {
                sdlFlags |= SDL_WindowFlags.HIDDEN;
            }
            if ((sdlFlags & SDL_WindowFlags.HIDDEN) == 0) {
                sdlFlags |= SDL_WindowFlags.SHOWN;
            }
            if ((flags & DisplayFlags.OPENGL) != 0) {
                if ((flags & DisplayFlags.DOUBLEBUF) != 0) {
                    flags &= ~DisplayFlags.DOUBLEBUF;
                    lib.SDL_GL_SetAttribute(SDL_GLattr.DOUBLEBUFFER, 1);
                } else {
                    lib.SDL_GL_SetAttribute(SDL_GLattr.DOUBLEBUFFER, 0);
                }
            }

            {
                int w1 = w, h1 = h;
                int scale = 1;
                boolean centerWindow = false;
                int x = SDL_WINDOWPOS_UNDEFINED_DISPLAY(display);
                int y = SDL_WINDOWPOS_UNDEFINED_DISPLAY(display);

                int[] winfo = new int[] { x, y, centerWindow ? 1 : 0 };
                getVideoWindowPos(winfo);
                x = winfo[0];
                y = winfo[1];
                centerWindow = winfo[2] != 0;

                if (centerWindow) {
                    x = SDL_WINDOWPOS_CENTERED_DISPLAY(display);
                    y = SDL_WINDOWPOS_CENTERED_DISPLAY(display);
                }

                if (win != null) {
                    if (lib.SDL_GetWindowDisplayIndex(win) == display) {
                        if ((lib.SDL_GetWindowFlags(win) & (SDL_WindowFlags.FULLSCREEN | SDL_WindowFlags.FULLSCREEN_DESKTOP)) != 0) {
                            x = STATE.fullscreenBackupX;
                            y = STATE.fullscreenBackupY;

                            if (x == SDL_WINDOWPOS_UNDEFINED_DISPLAY(display)) {
                                x = SDL_WINDOWPOS_CENTERED_DISPLAY(display);
                            }
                            if (y == SDL_WINDOWPOS_UNDEFINED_DISPLAY(display)) {
                                y = SDL_WINDOWPOS_CENTERED_DISPLAY(display);
                            }
                        } else {
                            IntByReference xRef = new IntByReference(x);
                            IntByReference yRef = new IntByReference(y);
                            lib.SDL_GetWindowPosition(win, xRef, yRef);
                            x = xRef.getValue();
                            y = yRef.getValue();
                        }
                    }
                    if (((flags & DisplayFlags.OPENGL) == 0) != ((lib.SDL_GetWindowFlags(win) & SDL_WindowFlags.OPENGL) == 0)) {
                        SDL4J.setDefaultWindow(null);
                        win = null;
                    }
                }

                if ((flags & DisplayFlags.SCALED) != 0 && (flags & DisplayFlags.FULLSCREEN) == 0) {
                    SDL_Rect displayBounds = new SDL_Rect();
                    boolean fractionalScaling = false;

                    if (lib.SDL_GetDisplayUsableBounds(display, displayBounds) != 0) {
                        SDLException.throwNew();
                    }
                    displayBounds.read();

                    displayBounds.w = displayMode.w - 80;
                    displayBounds.h = displayMode.h - 30;

                    if (lib.SDL_GetHintBoolean("SDL_HINT_RENDER_SCALE_QUALITY", false)) {
                        fractionalScaling = true;
                    }

                    if (STATE.scaledGl) {
                        fractionalScaling = true;
                    }

                    if (fractionalScaling) {
                        double aspectRatio = (double)w / (double)h;

                        w1 = displayBounds.w;
                        h1 = displayBounds.h;

                        if ((double)w1 / (double)h1 > aspectRatio) {
                            w1 = (int)(h1 * aspectRatio);
                        } else {
                            h1 = (int)(w1 / aspectRatio);
                        }
                    } else {
                        int xScale, yScale;

                        xScale = displayBounds.w / w;
                        yScale = displayBounds.h / h;

                        scale = Math.min(xScale, yScale);

                        if (scale < 1) {
                            scale = 1;
                        }

                        w1 = w * scale;
                        h1 = h * scale;
                    }
                }

                if ((sdlFlags & (SDL_WindowFlags.FULLSCREEN | SDL_WindowFlags.FULLSCREEN_DESKTOP)) != 0) {
                    STATE.fullscreenBackupX = x;
                    STATE.fullscreenBackupY = y;
                }

                if (win == null) {
                    win = lib.SDL_CreateWindow(title, x, y, w1, h1, sdlFlags);
                    if (win == null) {
                        SDLException.throwNew();
                    }
                    initFlip = true;
                } else {
                    lib.SDL_SetWindowMinimumSize(win, 1, 1);

                    lib.SDL_SetWindowTitle(win, title);
                    lib.SDL_SetWindowSize(win, w1, h1);

                    lib.SDL_SetWindowResizable(win, (flags & DisplayFlags.RESIZABLE) != 0);
                    lib.SDL_SetWindowBordered(win, (flags & DisplayFlags.NOFRAME) == 0);

                    if ((flags & DisplayFlags.SHOWN) != 0 || (flags & DisplayFlags.HIDDEN) == 0) {
                        lib.SDL_ShowWindow(win);
                    } else {
                        lib.SDL_HideWindow(win);
                    }

                    if (lib.SDL_SetWindowFullscreen(win, sdlFlags & (SDL_WindowFlags.FULLSCREEN | SDL_WindowFlags.FULLSCREEN_DESKTOP)) != 0) {
                        SDLException.throwNew();
                    }

                    lib.SDL_SetWindowPosition(win, x, y);

                    assert surface != null;
                }
            }

            if (STATE.usingGl) {
                if (STATE.glContext == null) {
                    STATE.glContext = lib.SDL_GL_CreateContext(win);
                    if (STATE.glContext == null) {
                        setModeFailure(lib, win);
                    }
                    surf = lib.SDL_CreateRGBSurface(SDL2Library.SDL_SWSURFACE, w, h, 32, 0xff << 16, 0xff << 8, 0xff, 0);
                    newOwnedSurf = surf;
                } else {
                    surf = surface.surf;
                }
                if ((flags & DisplayFlags.SCALED) != 0) {
                    STATE.scaledGlW = w;
                    STATE.scaledGlH = h;
                }
                lib.SDL_GL_SetSwapInterval(0);
            } else {
                if (STATE.glContext != null) {
                    lib.SDL_GL_DeleteContext(STATE.glContext);
                    STATE.glContext = null;
                }

                if ((flags & DisplayFlags.SCALED) != 0) {
                    if (renderer == null) {
                        lib.SDL_SetHintWithPriority(SDL2Library.SDL_HINT_RENDER_SCALE_QUALITY, "nearest", SDL_HintPriority.DEFAULT);

                        renderer = lib.SDL_CreateRenderer(win, -1, 0);
                        if (renderer == null) {
                            SDLException.throwNew();
                        }

                        lib.SDL_RenderSetIntegerScale(renderer, !((flags & DisplayFlags.FULLSCREEN) != 0 || lib.SDL_GetHintBoolean("SDL_HINT_RENDER_SCALE_QUALITY", false)));
                        lib.SDL_RenderSetLogicalSize(renderer, w, h);
                        lib.SDL_SetWindowMinimumSize(win, w, h);

                        texture = lib.SDL_CreateTexture(renderer, SDL_PixelFormatEnum.ARGB8888, SDL_TextureAccess.STREAMING, w, h);
                    }
                    surf = lib.SDL_CreateRGBSurface(SDL2Library.SDL_SWSURFACE, w, h, 32, 0xff << 16, 0xff << 8, 0xff, 0);
                    newOwnedSurf = surf;
                } else {
                    surf = lib.SDL_GetWindowSurface(win);
                }
            }
            if (STATE.gammaRamp != null) {
                int result = lib.SDL_SetWindowGammaRamp(win, STATE.gammaRamp, STATE.gammaRamp.share(256 * SHORT_SIZE), STATE.gammaRamp.share(512 * SHORT_SIZE));
                if (result != 0) {
                    setModeFailure(lib, win);
                }
            }

            if (STATE.usingGl && renderer != null) {
                stateCleanup();
                destroyWindow(lib, win);
                throw new IllegalStateException("GL context and SDL_Renderer created at the same time");
            }

            if (surf == null) {
                setModeFailure(lib, win);
            }
            if (surface == null) {
                surface = new Surface(surf, newOwnedSurf != null);
            } else {
                surface.setSurface(surf, newOwnedSurf != null);
            }

            SDL4J.setDefaultWindow(win);
            SDL4J.setDefaultWindowSurface(surface);

            if (initFlip) {
                // TODO Implement flipInternal
                // flipInternal();
            }
        }

        if (STATE.icon != null) {
            lib.SDL_SetWindowIcon(win, STATE.icon.surf);
        }
        lib.SDL_PumpEvents();
        return surface;
    }

    private static final void setModeFailure(SDL2Library lib, SDL_Window win) {
        stateCleanup();
        String error = lib.SDL_GetError();
        destroyWindow(lib, win);
        throw new SDLException(error);
    }

    private static final void destroyWindow(SDL2Library lib, SDL_Window win) {
        if (win.equals(SDL4J.getDefaultWindow())) {
            SDL4J.setDefaultWindow(null);
        } else if (win != null) {
            lib.SDL_DestroyWindow(win);
        }
    }

    private static void stateCleanup() {
        SDL2Library lib = LowLevel.getInstance();
        if (STATE.title != null) {
            STATE.title = null;
        }
        if (STATE.icon != null) {
            STATE.icon = null;
        }
        if (STATE.glContext != null) {
            lib.SDL_GL_DeleteContext(STATE.glContext);
            STATE.glContext = null;
        }
        if (STATE.gammaRamp != null) {
            STATE.gammaRamp = null;
        }
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
        SDL2Library lib = LowLevel.getInstance();
        if (event.getType() != SDL_EventType.WINDOWEVENT) {
            return false;
        }

        SDL_WindowEvent windowEvent = event.getProperValue(SDL_WindowEvent.class);

        SDL_Window sdl4jWindow = SDL4J.getDefaultWindow();
        SDL_Window window = lib.SDL_GetWindowFromID(windowEvent.windowID);
        if (!window.equals(sdl4jWindow)) {
            return false;
        }

        if (renderer != null) {
            if (windowEvent.event == SDL_WindowEventID.MAXIMIZED) {
                lib.SDL_RenderSetIntegerScale(renderer, false);
            } else if (windowEvent.event == SDL_WindowEventID.RESTORED) {
                lib.SDL_RenderSetIntegerScale(renderer, !lib.SDL_GetHintBoolean("SDL_HINT_RENDER_SCALE_QUALITY", false));
            }
            return false;
        }

        if (STATE.usingGl) {
            if (windowEvent.event == SDL_WindowEventID.SIZE_CHANGED) {
                GL_glViewport_Func glViewport = (GL_glViewport_Func)lib.SDL_GL_GetProcAddress("glVieweport");
                int wNew = windowEvent.data1;
                int hNew = windowEvent.data2;
                lib.SDL_GL_MakeCurrent(sdl4jWindow, STATE.glContext);
                if (STATE.scaledGl) {
                    double savedAspectRatio = (double)STATE.scaledGlW / (double)STATE.scaledGlH;
                    double windowAspectRatio = (double)wNew / (double)hNew;

                    if (windowAspectRatio > savedAspectRatio) {
                        int width = (int)(hNew * savedAspectRatio);
                        glViewport.invoke((wNew - width) / 2, 0, width, hNew);
                    } else {
                        glViewport.invoke(0, 0, wNew, (int)(wNew / savedAspectRatio));
                    }
                } else {
                    glViewport.invoke(0, 0, wNew, hNew);
                }
            }
            return false;
        }

        if (windowEvent.event == SDL_WindowEventID.SIZE_CHANGED) {
            if (window.equals(sdl4jWindow)) {
                SDL_Surface sdlSurface = lib.SDL_GetWindowSurface(window);
                Surface oldSurface = SDL4J.getDefaultWindowSurface();
                if (!sdlSurface.equals(oldSurface.surf)) {
                    oldSurface.surf = sdlSurface;
                }
            }
        }
        return false;
    }

    private static int getVideoWindowPos(int[] winfo) {
        SDL2Library lib = LowLevel.getInstance();
        final String videoWindowPos = lib.SDL_getenv("SDL_VIDEO_WINDOW_POS");
        String videoCentered = lib.SDL_getenv("SDL_VIDEO_CENTERED");
        int xx, yy;
        if (videoWindowPos != null) {
            try {
                String[] twoParts = videoWindowPos.split(",");
                xx = Integer.parseInt(twoParts[0]);
                yy = Integer.parseInt(twoParts[1]);
                winfo[0] = xx;
                winfo[1] = yy;
                winfo[2] = 0;
                return 1;
            } catch (Exception e) {
            }
            if (videoWindowPos.equals("center")) {
                videoCentered = videoWindowPos;
            }
        }
        if (videoCentered != null) {
            winfo[2] = 1;
            return 2;
        }
        return 0;
    }
}
