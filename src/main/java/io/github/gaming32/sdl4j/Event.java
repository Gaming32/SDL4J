package io.github.gaming32.sdl4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jna.Pointer;

import io.github.gaming32.sdl4j.LowLevel.SDL2Library;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_AudioDeviceEvent;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Event;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_JoyAxisEvent;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_JoyBallEvent;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_JoyButtonEvent;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_JoyHatEvent;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_KeyboardEvent;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Keysym;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_MouseButtonEvent;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_MouseMotionEvent;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_WindowEvent;
import io.github.gaming32.sdl4j.math.Vector2;
import io.github.gaming32.sdl4j.modules.EventModule;
import io.github.gaming32.sdl4j.sdl_enums.SDL4J_AppCode;
import io.github.gaming32.sdl4j.sdl_enums.SDL4J_EventCode;
import io.github.gaming32.sdl4j.sdl_enums.SDL_EventType;
import io.github.gaming32.sdl4j.sdl_enums.SDL_WindowEventID;
import io.github.gaming32.sdl4j.sdl_enums.SDL_eventaction;

public class Event {
    private static final int GET_LIST_LEN = 128;

    protected final int type;
    protected final Map<String, Object> data;

    protected Event(SDL_Event event) {
        if (event != null) {
            this.type = event.type;
            this.data = dataFromEvent(event);
        } else {
            this.type = SDL4J_EventCode.NOEVENT;
            this.data = new HashMap<>();
        }
    }

    @Override
    public String toString() {
        String dataString = data.toString();
        if (dataString.length() > 2) {
            return "Event{type=" + type + ", " + dataString.substring(1, dataString.length() - 1) + "}";
        }
        return "Event{type=" + type + "}";
    }

    protected static Map<String, Object> dataFromEvent(SDL_Event event) {
        Map<String, Object> result = new HashMap<>();
        SDL_WindowEvent windowEvent;

        int eventType = event.getType();
        switch (eventType) {
            case SDL4J_EventCode.VIDEORESIZE:
                windowEvent = event.getProperValue(SDL_WindowEvent.class);
                int w = (int)windowEvent.readField("data1");
                int h = (int)windowEvent.readField("data2");
                result.put("size", new Vector2(w, h));
                result.put("w", w);
                result.put("h", h);
                break;
            case SDL4J_EventCode.ACTIVEEVENT:
                boolean gain;
                int state;
                windowEvent = event.getProperValue(SDL_WindowEvent.class);
                switch ((int)windowEvent.readField("event")) {
                    case SDL_WindowEventID.ENTER:
                        gain = true;
                        state = SDL4J_AppCode.APPFOCUSMOUSE;
                        break;
                    case SDL_WindowEventID.LEAVE:
                        gain = false;
                        state = SDL4J_AppCode.APPFOCUSMOUSE;
                        break;
                    case SDL_WindowEventID.FOCUS_GAINED:
                        gain = true;
                        state = SDL4J_AppCode.APPINPUTFOCUS;
                        break;
                    case SDL_WindowEventID.FOCUS_LOST:
                        gain = false;
                        state = SDL4J_AppCode.APPINPUTFOCUS;
                        break;
                    case SDL_WindowEventID.MINIMIZED:
                        gain = false;
                        state = SDL4J_AppCode.APPACTIVE;
                        break;
                    default:
                        assert windowEvent.event == SDL_WindowEventID.RESTORED;
                        gain = true;
                        state = SDL4J_AppCode.APPACTIVE;
                }
                result.put("gain", gain);
                result.put("state", state);
                break;
            case SDL_EventType.KEYDOWN:
            case SDL_EventType.KEYUP:
                SDL_Keysym keysym = (SDL_Keysym)event.getProperValue(SDL_KeyboardEvent.class).readField("keysym");
                result.put("unicode", EventModule.getInstance().getEventUnicode(event, eventType, keysym));
                result.put("key", keysym.sym);
                result.put("mod", (int)keysym.mod);
                result.put("scancode", keysym.scancode);
                break;
            case SDL_EventType.MOUSEMOTION:
                SDL_MouseMotionEvent motionEvent = event.getProperValue(SDL_MouseMotionEvent.class);
                result.put("pos", new Vector2(motionEvent.x, motionEvent.y));
                result.put("x", motionEvent.x);
                result.put("y", motionEvent.y);
                result.put("rel", new Vector2(motionEvent.xrel, motionEvent.yrel));
                result.put("relX", motionEvent.xrel);
                result.put("relY", motionEvent.yrel);
                result.put("buttons", motionEvent.state);
                result.put("touch", motionEvent.which == SDL2Library.SDL_TOUCH_MOUSEID);
                break;
            case SDL_EventType.MOUSEBUTTONDOWN:
            case SDL_EventType.MOUSEBUTTONUP:
                SDL_MouseButtonEvent buttonEvent = event.getProperValue(SDL_MouseButtonEvent.class);
                result.put("pos", new Vector2(buttonEvent.x, buttonEvent.y));
                result.put("x", buttonEvent.x);
                result.put("y", buttonEvent.y);
                result.put("touch", buttonEvent.which == SDL2Library.SDL_TOUCH_MOUSEID);
                break;
            case SDL_EventType.JOYAXISMOTION:
                SDL_JoyAxisEvent jaxisEvent = event.getProperValue(SDL_JoyAxisEvent.class);
                result.put("instanceId", jaxisEvent.which);
                result.put("axis", (int)jaxisEvent.axis);
                result.put("valu", jaxisEvent.value / 32767.0);
                break;
            case SDL_EventType.JOYBALLMOTION:
                SDL_JoyBallEvent jballEvent = event.getProperValue(SDL_JoyBallEvent.class);
                result.put("instanceId", jballEvent.which);
                result.put("ball", (int)jballEvent.ball);
                result.put("rel", new Vector2(jballEvent.xrel, jballEvent.yrel));
                result.put("relX", (int)jballEvent.xrel);
                result.put("relY", (int)jballEvent.yrel);
                break;
            case SDL_EventType.JOYHATMOTION:
                SDL_JoyHatEvent jhatEvent = event.getProperValue(SDL_JoyHatEvent.class);
                result.put("instanceId", jhatEvent.which);
                result.put("hat", (int)jhatEvent.hat);
                int hx = 0, hy = 0;
                if ((jhatEvent.value & SDL2Library.SDL_HAT_UP) != 0) {
                    hy = 1;
                } else if ((jhatEvent.value & SDL2Library.SDL_HAT_DOWN) != 0) {
                    hy = -1;
                }
                if ((jhatEvent.value & SDL2Library.SDL_HAT_RIGHT) != 0) {
                    hx = 1;
                } else if ((jhatEvent.value & SDL2Library.SDL_HAT_LEFT) != 0) {
                    hx = -1;
                }
                result.put("value", new Vector2(hx, hy));
                result.put("x", hx);
                result.put("y", hy);
                break;
            case SDL_EventType.JOYBUTTONUP:
            case SDL_EventType.JOYBUTTONDOWN:
                SDL_JoyButtonEvent jbuttonEvent = event.getProperValue(SDL_JoyButtonEvent.class);
                result.put("instanceId", jbuttonEvent.which);
                result.put("button", (int)jbuttonEvent.button);
                break;
            case SDL4J_EventCode.WINDOWMOVED:
            case SDL4J_EventCode.WINDOWRESIZED:
            case SDL4J_EventCode.WINDOWRESTORED:
                windowEvent = event.getProperValue(SDL_WindowEvent.class);
                result.put("pos", new Vector2(windowEvent.data1, windowEvent.data2));
                result.put("x", windowEvent.data1);
                result.put("y", windowEvent.data2);
                break;
            case SDL_EventType.AUDIODEVICEADDED:
            case SDL_EventType.AUDIODEVICEREMOVED:
                SDL_AudioDeviceEvent adeviceEvent = event.getProperValue(SDL_AudioDeviceEvent.class);
                result.put("which", adeviceEvent.which);
                result.put("isCapture", adeviceEvent.iscapture);
                break;
            // TODO Finish events
        }

        return result;
    }

    public static List<Event> get() {
        return get(-1, true, -1);
    }

    public static List<Event> get(int include) {
        return get(include, true, -1);
    }

    public static List<Event> get(int include, boolean pump) {
        return get(include, pump, -1);
    }

    public static List<Event> get(boolean pump, int exclude) {
        return get(-1, pump, exclude);
    }

    public static List<Event> get(int include, boolean pump, int exclude) {
        SDL4J.videoInitCheck();
        eventPump(pump);
        if (include == -1) {
            if (exclude != -1) {
                // return getAllEventsExcept(exclude);
            }
            return getAllEvents();
        } else {
            if (exclude != -1) {
                throw new IllegalArgumentException("Cannot use include and exclude at the same time!");
            }
            // return getSeqEvents(exclude);
        }
        return null;
    }

    private static List<Event> getAllEvents() {
        SDL2Library lib = LowLevel.getInstance();
        SDL_Event bufFirst = new SDL_Event();
        SDL_Event[] buf = (SDL_Event[])bufFirst.toArray(GET_LIST_LEN);
        List<Event> result = new ArrayList<>();
        int len = GET_LIST_LEN;

        while (len == GET_LIST_LEN) {
            len = lib.SDL_PeepEvents(bufFirst, GET_LIST_LEN, SDL_eventaction.GETEVENT);
            if (len == -1) {
                SDLException.throwNew();
            }
            for (int i = 0; i < len; i++) {
                addEventToList(result, buf[i]);
            }
        }
        return result;
    }

    private static void addEventToList(List<Event> list, SDL_Event event) {
        list.add(new Event(event));
    }

    private static void eventPump(boolean pump) {
        SDL2Library lib = LowLevel.getInstance();
        if (pump) {
            lib.SDL_PumpEvents();
        }
        lib.SDL_FilterEvents(Event::translateWindowEvent, null);
    }

    private static boolean translateWindowEvent(Pointer ignored, SDL_Event event) {
        SDL2Library lib = LowLevel.getInstance();
        if (event.getType() == SDL_EventType.WINDOWEVENT) {
            SDL_WindowEvent windowEvent = event.getProperValue(SDL_WindowEvent.class);
            event.writeField("type", SDL4J_EventCode.WINDOWSHOWN + (byte)windowEvent.readField("event") - 1);
            return lib.SDL_EventState(event.type, SDL2Library.SDL_QUERY);
        }
        return true;
    }
}
