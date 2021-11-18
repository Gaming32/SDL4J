package io.github.gaming32.sdl4j.modules;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;

import io.github.gaming32.sdl4j.LowLevel;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Event;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_KeyboardEvent;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_WindowEvent;
import io.github.gaming32.sdl4j.LowLevel.Util;
import io.github.gaming32.sdl4j.SDL4J.Module;
import io.github.gaming32.sdl4j.sdl_enums.SDL4J_EventCode;
import io.github.gaming32.sdl4j.sdl_enums.SDL_EventType;
import io.github.gaming32.sdl4j.sdl_enums.SDL_WindowEventID;

public final class EventModule implements Module {
    private static EventModule INSTANCE = null;
    private boolean isInit;
    private int keyRepeatDelay, keyRepeatInterval;
    private int repeatTimer;
    private SDL_Event repeatEvent, lastKeyDownEvent;

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

    private boolean eventFilter(Pointer ignored, SDL_Event event) {
        SDL2Library lib = LowLevel.getInstance();

        SDL_Event newdownevent, newupevent, newevent = Util.copyStructure(SDL_Event.class, event);
        int x, y, i;

        if (event.getType() == SDL_EventType.WINDOWEVENT) {
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
        } else if (event.getType() == SDL_EventType.KEYDOWN) {
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
        }
        // TODO: finish this code

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
}
