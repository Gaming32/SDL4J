package io.github.gaming32.sdl4j.modules;

import java.nio.charset.StandardCharsets;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.ptr.IntByReference;

import io.github.gaming32.sdl4j.LowLevel;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Event;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_KeyboardEvent;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Keysym;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_MouseButtonEvent;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_MouseWheelEvent;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_TextInputEvent;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_WindowEvent;
import io.github.gaming32.sdl4j.LowLevel.Util;
import io.github.gaming32.sdl4j.SDL4J.Module;
import io.github.gaming32.sdl4j.enums.MouseFlags;
import io.github.gaming32.sdl4j.sdl_enums.SDL4J_EventCode;
import io.github.gaming32.sdl4j.sdl_enums.SDL_EventType;
import io.github.gaming32.sdl4j.sdl_enums.SDL_KeyCode;
import io.github.gaming32.sdl4j.sdl_enums.SDL_Keymod;
import io.github.gaming32.sdl4j.sdl_enums.SDL_WindowEventID;

public final class EventModule implements Module {
    private static final int MAX_SCAN_UNICODE = 15;

    private static EventModule INSTANCE = null;
    private boolean isInit;
    private int keyRepeatDelay, keyRepeatInterval;
    private int repeatTimer;
    private SDL_Event repeatEvent, lastKeyDownEvent;

    private final class ScanAndUnicode {
        int key;
        String unicode;

        ScanAndUnicode(int key, String unicode) {
            this.key = key;
            this.unicode = unicode;
        }
    }
    private ScanAndUnicode[] scanUnicode = new ScanAndUnicode[MAX_SCAN_UNICODE];

    EventModule() {
        if (INSTANCE != null) {
            throw new IllegalStateException("EventModule instance already exists. Did you mean to use getInstance()?");
        }
    }

    public static EventModule getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EventModule();
        }
        return INSTANCE;
    }

    @Override
    public void init() {
        SDL2Library lib = LowLevel.getInstance();
        if (!isInit) {
            keyRepeatDelay = 0;
            keyRepeatInterval = 0;
            lib.SDL_SetEventFilter(this::eventFilter, null);
        }
        isInit = true;
    }

    @Override
    public void quit() {
        SDL2Library lib = LowLevel.getInstance();
        if (isInit) {
            if (repeatTimer != 0) {
                lib.SDL_RemoveTimer(repeatTimer);
                repeatTimer = 0;
            }
        }
        isInit = false;
    }

    private boolean eventFilter(Pointer ignored, SDL_Event event) {
        SDL2Library lib = LowLevel.getInstance();
        SDL_Event newevent = Util.copyStructure(SDL_Event.class, event);

        switch (event.getType()) {
            case SDL_EventType.WINDOWEVENT:
                SDL_WindowEvent windowEvent = event.getProperValue(SDL_WindowEvent.class);
                switch (windowEvent.event) {
                    case SDL_WindowEventID.RESIZED:
                        lib.SDL_FilterEvents(this::removePendingVideoResize, newevent.getPointer());

                        newevent.writeField("type", SDL4J_EventCode.VIDEORESIZE);
                        lib.SDL_PushEvent(newevent);
                        break;
                    case SDL_WindowEventID.EXPOSED:
                        lib.SDL_FilterEvents(this::removePendingVideoExpose, newevent.getPointer());

                        newevent.writeField("type", SDL4J_EventCode.VIDEOEXPOSE);
                        lib.SDL_PushEvent(newevent);
                        break;
                    case SDL_WindowEventID.ENTER:
                    case SDL_WindowEventID.LEAVE:
                    case SDL_WindowEventID.FOCUS_GAINED:
                    case SDL_WindowEventID.FOCUS_LOST:
                    case SDL_WindowEventID.MINIMIZED:
                    case SDL_WindowEventID.RESTORED:
                        newevent.writeField("type", SDL4J_EventCode.ACTIVEEVENT);
                        lib.SDL_PushEvent(newevent);
                }
                break;
            case SDL_EventType.KEYDOWN:
                if (event.getProperValue(SDL_KeyboardEvent.class).repeat != 0) {
                    return false;
                }

                if (keyRepeatDelay > 0) {
                    if (repeatTimer != 0) {
                        lib.SDL_RemoveTimer(repeatTimer);
                    }

                    Util.copyStructureInPlace(event, repeatEvent);
                    repeatTimer = lib.SDL_AddTimer(keyRepeatDelay, this::repeatCallback, null);
                }

                if (lastKeyDownEvent == null) {
                    lastKeyDownEvent = Union.newInstance(SDL_Event.class);
                }
                Util.copyStructureInPlace(event, lastKeyDownEvent);
                break;
            case SDL_EventType.TEXTINPUT:
                if (lastKeyDownEvent != null) {
                    putEventUnicode(lastKeyDownEvent, event.getProperValue(SDL_TextInputEvent.class).text);
                    lastKeyDownEvent = null;
                }
                break;
            case SDL4J_EventCode.KEYREPEAT:
                event.writeField("type", SDL_EventType.KEYDOWN);
                break;
            case SDL_EventType.KEYUP:
                if (repeatTimer != 0 && repeatEvent.getProperValue(SDL_KeyboardEvent.class).keysym.scancode == event.getProperValue(SDL_KeyboardEvent.class).keysym.scancode) {
                    lib.SDL_RemoveTimer(repeatTimer);
                    repeatTimer = 0;
                }
                break;
            case SDL_EventType.MOUSEBUTTONDOWN:
            case SDL_EventType.MOUSEBUTTONUP:
                SDL_MouseButtonEvent buttonEvent = event.getProperValue(SDL_MouseButtonEvent.class);
                buttonEvent.readField("button");
                if ((buttonEvent.button & MouseFlags.KEEP) != 0) {
                    buttonEvent.writeField("button", buttonEvent.button ^ MouseFlags.KEEP);
                } else if (buttonEvent.button >= MouseFlags.WHEELUP) {
                    buttonEvent.writeField("button", buttonEvent.button + (MouseFlags.X1 - MouseFlags.WHEELUP));
                }
                break;
            case SDL_EventType.MOUSEWHEEL:
                SDL_MouseWheelEvent wheelEvent = event.getProperValue(SDL_MouseWheelEvent.class);
                if ((int)wheelEvent.readField("y") == 0 && (int)wheelEvent.readField("x") == 0) {
                    return false;
                }

                IntByReference xRef = new IntByReference();
                IntByReference yRef = new IntByReference();
                lib.SDL_GetMouseState(xRef, yRef);
                int x = xRef.getValue(), y = yRef.getValue();

                int which = (int)event.getProperValue(SDL_MouseButtonEvent.class).readField("which");

                SDL_MouseButtonEvent newDownEvent = new SDL_MouseButtonEvent();
                newDownEvent.type = SDL_EventType.MOUSEBUTTONDOWN;
                newDownEvent.x = x;
                newDownEvent.y = y;
                newDownEvent.state = SDL2Library.SDL_PRESSED;
                newDownEvent.clicks = 1;
                newDownEvent.which = which;

                SDL_MouseButtonEvent newUpEvent = new SDL_MouseButtonEvent();
                newUpEvent.type = SDL_EventType.MOUSEBUTTONUP;
                newUpEvent.x = x;
                newUpEvent.y = y;
                newUpEvent.state = SDL2Library.SDL_RELEASED;
                newUpEvent.clicks = 1;
                newUpEvent.which = which;

                if (wheelEvent.y > 0) {
                    newDownEvent.button = (byte)(MouseFlags.WHEELUP | MouseFlags.KEEP);
                    newUpEvent.button = (byte)(MouseFlags.WHEELUP | MouseFlags.KEEP);
                } else {
                    newDownEvent.button = (byte)(MouseFlags.WHEELDOWN | MouseFlags.KEEP);
                    newUpEvent.button = (byte)(MouseFlags.WHEELDOWN | MouseFlags.KEEP);
                }
                newDownEvent.write();
                newUpEvent.write();

                SDL_Event newDownEventUnion = new SDL_Event(newDownEvent.getPointer());
                SDL_Event newUpEventUnion = new SDL_Event(newUpEvent.getPointer());
                for (int i = 0; i < Math.abs(wheelEvent.y); i++) {
                    lib.SDL_PushEvent(newDownEventUnion);
                    lib.SDL_PushEvent(newUpEventUnion);
                }
                break;
        }
        return lib.SDL_EventState(event.getType(), SDL2Library.SDL_QUERY);
    }

    private boolean removePendingVideoResize(Pointer userdata, SDL_Event event) {
        SDL_Event newEvent = Structure.newInstance(SDL_Event.class, userdata);

        if (event.getType() == SDL4J_EventCode.VIDEORESIZE &&
            ((SDL_WindowEvent)event.getTypedValue(SDL_WindowEvent.class)).windowID == ((SDL_WindowEvent)newEvent.getTypedValue(SDL_WindowEvent.class)).windowID) {
            return false;
        }
        return true;
    }

    private boolean removePendingVideoExpose(Pointer userdata, SDL_Event event) {
        SDL_Event newEvent = Structure.newInstance(SDL_Event.class, userdata);

        if (event.getType() == SDL4J_EventCode.VIDEOEXPOSE &&
            ((SDL_WindowEvent)event.getTypedValue(SDL_WindowEvent.class)).windowID == ((SDL_WindowEvent)newEvent.getTypedValue(SDL_WindowEvent.class)).windowID) {
            return false;
        }
        return true;
    }

    private int repeatCallback(int interval, Pointer param) {
        SDL2Library lib = LowLevel.getInstance();
        repeatEvent.writeField("type", SDL4J_EventCode.KEYREPEAT);
        SDL_KeyboardEvent keyEvent = (SDL_KeyboardEvent)repeatEvent.getTypedValue(SDL_KeyboardEvent.class);
        keyEvent.writeField("state", SDL2Library.SDL_PRESSED);
        keyEvent.writeField("repeat", 1);
        lib.SDL_PushEvent(repeatEvent);
        return keyRepeatInterval;
    }

    private boolean putEventUnicode(SDL_Event event, byte[] uniData) {
        String uni = new String(uniData, StandardCharsets.UTF_8);
        SDL_KeyboardEvent keyEvent = event.getProperValue(SDL_KeyboardEvent.class);
        for (int i = 0; i < MAX_SCAN_UNICODE; i++) {
            if (scanUnicode[i] == null) {
                scanUnicode[i] = new ScanAndUnicode(keyEvent.keysym.scancode, uni);
                return true;
            }
        }
        return false;
    }

    public String getEventUnicode(SDL_Event event) {
        return getEventUnicode(event, event.getType(), (SDL_Keysym)event.getProperValue(SDL_KeyboardEvent.class).readField("keysym"));
    }

    public String getEventUnicode(SDL_Event event, int eventType, SDL_Keysym keysym) {
        for (int i = 0; i < MAX_SCAN_UNICODE; i++) {
            if (scanUnicode[i].key == keysym.scancode) {
                if (eventType == SDL_EventType.KEYUP) {
                    scanUnicode[i].key = 0;
                }
                return scanUnicode[i].unicode;
            }
        }
        return new String(new char[] { unicodeFromEvent(keysym) });
    }

    private static char unicodeFromEvent(SDL_Keysym keysym) {
        boolean capsHeld = (keysym.mod & SDL_Keymod.CAPS) != 0;
        boolean shiftHeld = (keysym.mod & SDL_Keymod.SHIFT) != 0;

        boolean capitalize = (capsHeld && !shiftHeld) || (shiftHeld && !capsHeld);
        int key = keysym.sym;

        if ((keysym.mod & SDL_Keymod.CTRL) != 0) {
            if (key >= SDL_KeyCode.a && key <= SDL_KeyCode.z) {
                return (char)(key - SDL_KeyCode.a + 1);
            } else {
                switch (key) {
                    case SDL_KeyCode.SDLK_2:
                    case SDL_KeyCode.AT:
                        return '\0';
                    case SDL_KeyCode.SDLK_3:
                    case SDL_KeyCode.LEFTBRACKET:
                        return 0x1b;
                    case SDL_KeyCode.SDLK_4:
                    case SDL_KeyCode.BACKSLASH:
                        return 0x1c;
                    case SDL_KeyCode.SDLK_5:
                    case SDL_KeyCode.RIGHTBRACKET:
                        return 0x1d;
                    case SDL_KeyCode.SDLK_6:
                    case SDL_KeyCode.CARET:
                        return 0x1e;
                    case SDL_KeyCode.SDLK_7:
                    case SDL_KeyCode.UNDERSCORE:
                        return 0x1f;
                    case SDL_KeyCode.SDLK_8:
                        return 0x7f;
                }
            }
        }
        if (key < 128) {
            if (capitalize && key >= SDL_KeyCode.a && key <= SDL_KeyCode.z) {
                return (char)(key + 'A' - 'a');
            }
            return (char)key;
        }

        switch (key) {
            case 1073741923: // SDL_KeyCode.KP_PERIOD
                return '.';
            case 1073741908: // SDL_KeyCode.KP_DIVIDE
                return '/';
            case 1073741909: // SDL_KeyCode.KP_MULTIPLY
                return '*';
            case 1073741910: // SDL_KeyCode.KP_MINUS
                return '-';
            case 1073741911: // SDL_KeyCode.KP_PLUS
                return '+';
            case 1073741912: // SDL_KeyCode.KP_ENTER
                return '\r';
            case 1073741927: // SDL_KeyCode.KP_EQUALS
                return '=';
        }
        return '\0';
    }
}
