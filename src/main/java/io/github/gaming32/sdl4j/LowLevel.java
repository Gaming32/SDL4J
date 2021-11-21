package io.github.gaming32.sdl4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.PointerType;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.Union;
import com.sun.jna.ptr.IntByReference;

import io.github.gaming32.sdl4j.sdl_enums.SDL_EventType;

public final class LowLevel {
    public static interface SDL2Library extends Library {
        //#region SDL.h
        public static final int SDL_INIT_TIMER          = 0x00000001;
        public static final int SDL_INIT_AUDIO          = 0x00000010;
        /** SDL_INIT_VIDEO implies SDL_INIT_EVENTS */
        public static final int SDL_INIT_VIDEO          = 0x00000020;
        /** SDL_INIT_JOYSTICK implies SDL_INIT_EVENTS */
        public static final int SDL_INIT_JOYSTICK       = 0x00000200;
        public static final int SDL_INIT_HAPTIC         = 0x00001000;
        /** SDL_INIT_GAMECONTROLLER implies SDL_INIT_JOYSTICK */
        public static final int SDL_INIT_GAMECONTROLLER = 0x00002000;
        public static final int SDL_INIT_EVENTS         = 0x00004000;
        public static final int SDL_INIT_SENSOR         = 0x00008000;
        /** compatibility; this flag is ignored. */
        public static final int SDL_INIT_NOPARACHUTE    = 0x00100000;
        public static final int SDL_INIT_EVERYTHING = (
            SDL_INIT_TIMER | SDL_INIT_AUDIO | SDL_INIT_VIDEO | SDL_INIT_EVENTS |
            SDL_INIT_JOYSTICK | SDL_INIT_HAPTIC | SDL_INIT_GAMECONTROLLER | SDL_INIT_SENSOR
        );

        /**
         * <p>Initialize the SDL library.</p>
         *
         * <p>SDL_Init() simply forwards to calling SDL_InitSubSystem(). Therefore, the
         * two may be used interchangeably. Though for readability of your code
         * SDL_InitSubSystem() might be preferred.</p>
         *
         * <p>The file I/O (for example: SDL_RWFromFile) and threading (SDL_CreateThread)
         * subsystems are initialized by default. Message boxes
         * (SDL_ShowSimpleMessageBox) also attempt to work without initializing the
         * video subsystem, in hopes of being useful in showing an error dialog when
         * SDL_Init fails. You must specifically initialize other subsystems if you
         * use them in your application.</p>
         *
         * <p>Logging (such as SDL_Log) works without initialization, too.</p>
         *
         * <p>{@code flags} may be any of the following OR'd together:</p>
         *
         * <ul>
         *   <li>{@code SDL_INIT_TIMER}: timer subsystem</li>
         *   <li>{@code SDL_INIT_AUDIO}: audio subsystem</li>
         *   <li>{@code SDL_INIT_VIDEO}: video subsystem; automatically initializes the events</li>
         *   <li>subsystem</li>
         *   <li>{@code SDL_INIT_JOYSTICK}: joystick subsystem; automatically initializes the</li>
         *   <li>events subsystem</li>
         *   <li>{@code SDL_INIT_HAPTIC}: haptic (force feedback) subsystem</li>
         *   <li>{@code SDL_INIT_GAMECONTROLLER}: controller subsystem; automatically</li>
         *   <li>initializes the joystick subsystem</li>
         *   <li>{@code SDL_INIT_EVENTS}: events subsystem</li>
         *   <li>{@code SDL_INIT_EVERYTHING}: all of the above subsystems</li>
         *   <li>{@code SDL_INIT_NOPARACHUTE}: compatibility; this flag is ignored</li>
         * </ul>
         *
         * <p>Subsystem initialization is ref-counted, you must call SDL_QuitSubSystem()
         * for each SDL_InitSubSystem() to correctly shutdown a subsystem manually (or
         * call SDL_Quit() to force shutdown). If a subsystem is already loaded then
         * this call will increase the ref-count and return.</p>
         *
         * @param flags subsystem initialization flags
         * @return 0 on success or a negative error code on failure; call
         *          SDL_GetError() for more information.
         *
         * @see #SDL_InitSubSystem
         * @see #SDL_Quit
         * @see #SDL_SetMainReady
         * @see #SDL_WasInit
         */
        public int SDL_Init(int flags);

        /**
         * <p>Get a mask of the specified subsystems which are currently initialized.</p>
         *
         * @param flags any of the flags used by SDL_Init(); see SDL_Init for details.
         * @return <p>a mask of all initialized subsystems if {@code flags} is 0, otherwise it
         *         returns the initialization status of the specified subsystems.</p>
         *
         *         <p>The return value does not include SDL_INIT_NOPARACHUTE.</p>
         *
         * @see #SDL_Init
         * @see #SDL_InitSubSystem
         */
        public int SDL_WasInit(int flags);

        /**
         * <p>Compatibility function to initialize the SDL library.</p>
         *
         * <p>In SDL2, this function and SDL_Init() are interchangeable.</p>
         *
         * @param flags any of the flags used by SDL_Init(); see SDL_Init for details.
         * @return 0 on success or a negative error code on failure; call
         *         SDL_GetError() for more information.
         *
         * @see #SDL_Init
         * @see #SDL_Quit
         * @see #SDL_QuitSubSystem
         */
        public int SDL_InitSubSystem(int flags);

        /**
         * <p>Shut down specific SDL subsystems.
         *
         * <p>If you start a subsystem using a call to that subsystem's init function
         * (for example SDL_VideoInit()) instead of SDL_Init() or SDL_InitSubSystem(),
         * SDL_QuitSubSystem() and SDL_WasInit() will not work. You will need to use
         * that subsystem's quit function (SDL_VideoQuit()) directly instead. But
         * generally, you should not be using those functions directly anyhow; use
         * SDL_Init() instead.</p>
         *
         * <p>You still need to call SDL_Quit() even if you close all open subsystems
         * with SDL_QuitSubSystem().</p>
         *
         * @param flags any of the flags used by SDL_Init(); see SDL_Init for details.
         *
         * @see #SDL_InitSubSystem
         * @see #SDL_Quit
         */
        public void SDL_QuitSubSystem(int flags);

        /**
         * <p>Clean up all initialized subsystems.</p>
         *
         * <p>You should call this function even if you have already shutdown each
         * initialized subsystem with SDL_QuitSubSystem(). It is safe to call this
         * function even in the case of errors in initialization.</p>
         *
         * <p>If you start a subsystem using a call to that subsystem's init function
         * (for example SDL_VideoInit()) instead of SDL_Init() or SDL_InitSubSystem(),
         * then you must use that subsystem's quit function (SDL_VideoQuit()) to shut
         * it down before calling SDL_Quit(). But generally, you should not be using
         * those functions directly anyhow; use SDL_Init() instead.</p>
         *
         * <p>You can use this function with atexit() to ensure that it is run when your
         * application is shutdown, but it is not wise to do this from a library or
         * other dynamically loaded code.</p>
         *
         * @see #SDL_Init
         * @see #SDL_QuitSubSystem
         */
        public void SDL_Quit();
        //#endregion

        //#region SDL_error.h
        /**
         * <p>Retrieve a message about the last error that occurred on the current
         * thread.</p>
         *
         * <p>It is possible for multiple errors to occur before calling SDL_GetError().
         * Only the last error is returned.</p>
         *
         * <p>The message is only applicable when an SDL function has signaled an error.
         * You must check the return values of SDL function calls to determine when to
         * appropriately call SDL_GetError(). You should <i>not</i> use the results of
         * SDL_GetError() to decide if an error has occurred! Sometimes SDL will set
         * an error string even when reporting success.</p>
         *
         * <p>SDL will <i>>not</i> clear the error string for successful API calls. You <i>must</i>
         * check return values for failure cases before you can assume the error
         * string applies.</p>
         *
         * <p>Error strings are set per-thread, so an error set in a different thread
         * will not interfere with the current thread's operation.</p>
         *
         * <p>The returned string is internally allocated and must not be freed by the
         * application.</p>
         *
         * @return a message with information about the specific error that occurred,
         *         or an empty string if there hasn't been an error message set since
         *         the last call to SDL_ClearError(). The message is only applicable
         *         when an SDL function has signaled an error. You must check the
         *         return values of SDL function calls to determine when to
         *         appropriately call SDL_GetError().
         *
         * @see #SDL_ClearError()
         * @see #SDL_SetError(String, Object...)
         */
        public String SDL_GetError();
        //#endregion

        //#region SDL_events.h
        public static final byte SDL_RELEASED = 0;
        public static final byte SDL_PRESSED = 1;

        /**
         * Fields shared by every event
         */
        @FieldOrder({ "type", "timestamp" })
        public static class SDL_CommonEvent extends Structure {
            public int type;
            /** In milliseconds, populated using SDL_GetTicks() */
            public int timestamp;
        }

        /**
         *  Display state change event data (event.display.*)
         */
        @FieldOrder({
            "display",
            "event",
            "padding1",
            "padding2",
            "padding3",
            "data1"
        })
        public static class SDL_DisplayEvent extends SDL_CommonEvent {
            /** The associated display index */
            public int display;
            public byte event;
            public int padding1, padding2, padding3;
            /** event dependent data */
            public int data1;
        }

        /**
         * Window state change event data (event.window.*)
         */
        @FieldOrder({
            "windowID",
            "event",
            "padding1",
            "padding2",
            "padding3",
            "data1",
            "data2"
        })
        public static class SDL_WindowEvent extends SDL_CommonEvent {
            /** The associated window */
            public int windowID;
            public byte event;
            public int padding1, padding2, padding3;
            /** event dependent data */
            public int data1, data2;
        }

        /**
         * Keyboard button event structure (event.key.*)
         */
        @FieldOrder({
            "windowID",
            "state",
            "repeat",
            "padding2",
            "padding3",
            "keysym"
        })
        public static class SDL_KeyboardEvent extends SDL_CommonEvent {
            /** The window with keyboard focus, if any */
            public int windowID;
            public byte state;
            /** Non-zero if this is a key repeat */
            public byte repeat;
            public byte padding2, padding3;
            /** The key that was pressed or released */
            public SDL_Keysym keysym;
        }

        /**
         * Keyboard text editing event structure (event.edit.*)
         */
        @FieldOrder({
            "windowID",
            "text",
            "start",
            "length"
        })
        public static class SDL_TextEditingEvent extends SDL_CommonEvent {
            public static final int TEXT_SIZE = 32;

            /** The window with keyboard focus, if any */
            public int windowID;
            /** The editing text */
            public byte[] text = new byte[TEXT_SIZE];
            /** The start cursor of selected editing text */
            public int start;
            /** The length of selected editing text */
            public int length;
        }
        /**
         * Keyboard text input event structure (event.text.*)
         */
        @FieldOrder({
            "windowID",
            "text"
        })
        public static class SDL_TextInputEvent extends SDL_CommonEvent {
            public static final int TEXT_SIZE = 32;

            /** The window with keyboard focus, if any */
            public int windowID;
            /** The editing text */
            public byte[] text = new byte[TEXT_SIZE];
        }

        /**
         * Mouse motion event structure (event.motion.*)
         */
        @FieldOrder({
            "windowID",
            "which",
            "state",
            "x",
            "y",
            "xrel",
            "yrel"
        })
        public static class SDL_MouseMotionEvent extends SDL_CommonEvent {
            /** The window with mouse focus, if any */
            public int windowID;
            /** The mouse instance id, or SDL_TOUCH_MOUSEID */
            public int which;
            /** The current button state */
            public int state;
            /** X coordinate, relative to window */
            public int x;
            /** Y coordinate, relative to window */
            public int y;
            /** The relative motion in the X direction */
            public int xrel;
            /** The relative motion in the Y direction */
            public int yrel;
        }

        /**
         * Mouse button event structure (event.button.*)
         */
        @FieldOrder({
            "windowID",
            "which",
            "button",
            "state",
            "clicks",
            "padding1",
            "x",
            "y"
        })
        public static class SDL_MouseButtonEvent extends SDL_CommonEvent {
            /** The window with mouse focus, if any */
            public int windowID;
            /** The mouse instance id, or SDL_TOUCH_MOUSEID */
            public int which;
            /** The mouse button index */
            public byte button;
            /** ::SDL_PRESSED or ::SDL_RELEASED */
            public byte state;
            /** 1 for single-click, 2 for double-click, etc. */
            public byte clicks;
            public byte padding1;
            /** X coordinate, relative to window */
            public int x;
            /** Y coordinate, relative to window */
            public int y;
        }

        /**
         * Mouse wheel event structure (event.wheel.*)
         */
        @FieldOrder({
            "windowID",
            "which",
            "x",
            "y",
            "direction"
        })
        public static class SDL_MouseWheelEvent extends SDL_CommonEvent {
            /** The window with mouse focus, if any */
            public int windowID;
            /** The mouse instance id, or SDL_TOUCH_MOUSEID */
            public int which;
            /** The amount scrolled horizontally, positive to the right and negative to the left */
            public int x;
            /** The amount scrolled vertically, positive away from the user and negative toward the user */
            public int y;
            /** Set to one of the SDL_MOUSEWHEEL_* defines. When FLIPPED the values in X and Y will be opposite. Multiply by -1 to change them back */
            public int direction;
        }

        /**
         * Joystick axis motion event structure (event.jaxis.*)
         */
        @FieldOrder({
            "which",
            "axis",
            "padding1",
            "padding2",
            "padding3",
            "value"
        })
        public static class SDL_JoyAxisEvent extends SDL_CommonEvent {
            /** The joystick instance id */
            public int which;
            /** The joystick axis index */
            public byte axis;
            public byte padding1, padding2, padding3;
            /** The axis value (range: -32768 to 32767) */
            public short value;
        }

        /**
         * Joystick trackball motion event structure (event.jball.*)
         */
        @FieldOrder({
            "which",
            "ball",
            "padding1",
            "padding2",
            "padding3",
            "xrel",
            "yrel"
        })
        public static class SDL_JoyBallEvent extends SDL_CommonEvent {
            /** The joystick instance id */
            public int which;
            /** The joystick trackball index */
            public byte ball;
            public byte padding1, padding2, padding3;
            /** The relative motion in the X direction */
            public short xrel;
            /** The relative motion in the Y direction */
            public short yrel;
        }

        /**
         * Joystick hat position change event structure (event.jhat.*)
         */
        @FieldOrder({
            "which",
            "hat",
            "value",
            "padding1",
            "padding2"
        })
        public static class SDL_JoyHatEvent extends SDL_CommonEvent {
            /** The joystick instance id */
            public int which;
            /**< The joystick hat index */
            public byte hat;
            /**
             * The hat position value.
             * @apiNote Note that zero means the POV is centered.
             */
            public byte value;
            public byte padding1, padding2;
        }

        /**
         * Joystick button event structure (event.jbutton.*)
         */
        @FieldOrder({
            "which",
            "button",
            "state",
            "padding1",
            "padding2"
        })
        public static class SDL_JoyButtonEvent extends SDL_CommonEvent {
            /** The joystick instance id */
            public int which;
            /**< The joystick button index */
            public byte button;
            /** ::SDL_PRESSED or ::SDL_RELEASED */
            public byte state;
            public byte padding1, padding2;
        }

        /**
         * Joystick device event structure (event.jdevice.*)
         */
        @FieldOrder({
            "which"
        })
        public static class SDL_JoyDeviceEvent extends SDL_CommonEvent {
            /** The joystick device index for the ADDED event, instance id for the REMOVED event */
            public int which;
        }

        /**
         * Game controller axis motion event structure (event.caxis.*)
         */
        @FieldOrder({
            "which",
            "axis",
            "padding1",
            "padding2",
            "padding3",
            "value",
            "padding4"
        })
        public static class SDL_ControllerAxisEvent extends SDL_CommonEvent {
            /** The joystick instance id */
            public int which;
            /** The controller axis (SDL_GameControllerAxis) */
            public byte axis;
            public byte padding1, padding2, padding3;
            /** The axis value (range: -32768 to 32767) */
            public short value;
            public short padding4;
        }

        /**
         * Game controller button event structure (event.cbutton.*)
         */
        @FieldOrder({
            "which",
            "button",
            "state",
            "padding1",
            "padding2"
        })
        public static class SDL_ControllerButtonEvent extends SDL_CommonEvent {
            /** The joystick instance id */
            public int which;
            /** The controller button (SDL_GameControllerButton) */
            public byte button;
            /** ::SDL_PRESSED or ::SDL_RELEASED */
            public byte state;
            public byte padding1, padding2;
        }

        /**
         * Controller device event structure (event.cdevice.*)
         */
        @FieldOrder({
            "which"
        })
        public static class SDL_ControllerDeviceEvent extends SDL_CommonEvent {
            /** The joystick device index for the ADDED event, instance id for the REMOVED or REMAPPED event */
            public int which;
        }

        /**
         * Game controller touchpad event structure (event.ctouchpad.*)
         */
        @FieldOrder({
            "which",
            "touchpad",
            "finger",
            "x",
            "y",
            "pressure"
        })
        public static class SDL_ControllerTouchpadEvent extends SDL_CommonEvent {
            /** The joystick instance id */
            public int which;
            /** The index of the touchpad */
            public int touchpad;
            /** The index of the finger on the touchpad */
            public int finger;
            /** Normalized in the range 0...1 with 0 being on the left */
            public float x;
            /** Normalized in the range 0...1 with 0 being at the top */
            public float y;
            /** Normalized in the range 0...1 */
            public float pressure;
        }

        /**
         * Game controller sensor event structure (event.csensor.*)
         */
        @FieldOrder({
            "which",
            "sensor",
            "data"
        })
        public static class SDL_ControllerSensorEvent extends SDL_CommonEvent {
            /** The joystick instance id */
            public int which;
            /** The type of the sensor, one of the values of ::SDL_SensorType */
            public int sensor;
            /** Up to 3 values from the sensor, as defined in SDL_sensor.h */
            public float[] data = new float[3];
        }

        /**
         * Audio device event structure (event.adevice.*)
         */
        @FieldOrder({
            "which",
            "iscapture",
            "padding1",
            "padding2",
            "padding3"
        })
        public static class SDL_AudioDeviceEvent extends SDL_CommonEvent {
            /** The audio device index for the ADDED event (valid until next SDL_GetNumAudioDevices() call), SDL_AudioDeviceID for the REMOVED event */
            public int which;
            /** zero if an output device, non-zero if a capture device. */
            public byte iscapture;
            public byte padding1, padding2, padding3;
        }

        /**
         * Touch finger event structure (event.tfinger.*)
         */
        @FieldOrder({
            "touchId",
            "fingerId",
            "x",
            "y",
            "dx",
            "dy",
            "pressure",
            "windowID"
        })
        public static class SDL_TouchFingerEvent extends SDL_CommonEvent {
            /** The touch device id */
            public long touchId;
            public long fingerId;
            /** Normalized in the range 0...1 */
            public float x, y;
            /** Normalized in the range -1...1 */
            public float dx, dy;
            /** Normalized in the range 0...1 */
            public float pressure;
            /** The window underneath the finger, if any */
            public int windowID;
        }

        /**
         * Multiple Finger Gesture Event (event.mgesture.*)
         */
        @FieldOrder({
            "touchId",
            "dTheta",
            "dDist",
            "x",
            "y",
            "numFingers",
            "padding"
        })
        public static class SDL_MultiGestureEvent extends SDL_CommonEvent {
            /** The touch device id */
            public long touchId;
            public float dTheta, dDist;
            public float x, y;
            public short numFingers;
            public short padding;
        }

        /**
         * Dollar Gesture Event (event.dgesture.*)
         */
        @FieldOrder({
            "touchId",
            "gestureId",
            "numFingers",
            "error",
            "x",
            "y"
        })
        public static class SDL_DollarGestureEvent extends SDL_CommonEvent {
            /** The touch device id */
            public long touchId;
            public long gestureId;
            public int numFingers;
            public float error;
            /** Normalized center of gesture */
            public float x, y;
        }

        /**
         * An event used to request a file open by the system (event.drop.*)
         * This event is enabled by default, you can disable it with SDL_EventState().
         *
         * @apiNote If this event is enabled, you must free the filename in the event.
         */
        @FieldOrder({
            "file",
            "windowID"
        })
        public static class SDL_DropEvent extends SDL_CommonEvent {
            /** The file name, which should be freed with SDL_free(), is NULL on begin/complete */
            public String file;
            /** The window that was dropped on, if any */
            public int windowID;
        }

        /**
         * Sensor event structure (event.sensor.*)
         */
        @FieldOrder({
            "which",
            "data"
        })
        public static class SDL_SensorEvent extends SDL_CommonEvent {
            /** The instance ID of the sensor */
            public int which;
            /** Up to 6 values from the sensor - additional values can be queried using SDL_SensorGetData() */
            public float[] data = new float[6];
        }

        /**
         * The "quit requested" event
         */
        @FieldOrder({
        })
        public static class SDL_QuitEvent extends SDL_CommonEvent {
        }

        /**
         * A user-defined event type (event.user.*)
         */
        @FieldOrder({
            "windowID",
            "code",
            "data1",
            "data2"
        })
        public static class SDL_UserEvent extends SDL_CommonEvent {
            /** The associated window if any */
            public int windowID;
            /** User defined event code */
            public int code;
            /** User defined event code */
            public Pointer data1, data2;
        }

        /**
         *  A video driver dependent system event (event.syswm.*)
         *  This event is disabled by default, you can enable it with SDL_EventState()
         *
         *  @apiNote If you want to use this event, you should include SDL_syswm.h.
         */
        @FieldOrder({
            "windowID",
            "code",
            "data1",
            "data2"
        })
        public static class SDL_SysWMEvent extends SDL_CommonEvent {
            /** User defined event code */
            public Pointer msg;
        }

        /**
         * General event structure
         */
        public static class SDL_Event extends Union {
            int type;
            /** Common event data */
            SDL_CommonEvent common;
            /** Display event data */
            SDL_DisplayEvent display;
            /** Window event data */
            SDL_WindowEvent window;
            /** Keyboard event data */
            SDL_KeyboardEvent key;
            /** Text editing event data */
            SDL_TextEditingEvent edit;
            /** Text input event data */
            SDL_TextInputEvent text;
            /** Mouse motion event data */
            SDL_MouseMotionEvent motion;
            /** Mouse button event data */
            SDL_MouseButtonEvent button;
            /** Mouse wheel event data */
            SDL_MouseWheelEvent wheel;
            /** Joystick axis event data */
            SDL_JoyAxisEvent jaxis;
            /** Joystick ball event data */
            SDL_JoyBallEvent jball;
            /** Joystick hat event data */
            SDL_JoyHatEvent jhat;
            /** Joystick button event data */
            SDL_JoyButtonEvent jbutton;
            /** Joystick device change event data */
            SDL_JoyDeviceEvent jdevice;
            /** Game Controller axis event data */
            SDL_ControllerAxisEvent caxis;
            /** Game Controller button event data */
            SDL_ControllerButtonEvent cbutton;
            /** Game Controller device event data */
            SDL_ControllerDeviceEvent cdevice;
            /** Game Controller touchpad event data */
            SDL_ControllerTouchpadEvent ctouchpad;
            /** Game Controller sensor event data */
            SDL_ControllerSensorEvent csensor;
            /** Audio device event data */
            SDL_AudioDeviceEvent adevice;
            /** Sensor event data */
            SDL_SensorEvent sensor;
            /** Quit request event data */
            SDL_QuitEvent quit;
            /** Custom event data */
            SDL_UserEvent user;
            /** System dependent window event data */
            SDL_SysWMEvent syswm;
            /** Touch finger event data */
            SDL_TouchFingerEvent tfinger;
            /** Gesture event data */
            SDL_MultiGestureEvent mgesture;
            /** Gesture event data */
            SDL_DollarGestureEvent dgesture;
            /** Drag and drop event data */
            SDL_DropEvent drop;

            public int getType() {
                return (int)getTypedValue(int.class);
            }

            public void setTypeFromID(int type) {
                setType(getClassFromType(type));
            }

            public void setProperType() {
                setTypeFromID(getType());
            }

            @SuppressWarnings("unchecked")
            public <T> T getProperValue(Class<T> clazz) {
                setProperType();
                return (T)getTypedValue(clazz);
            }

            @SuppressWarnings("unchecked")
            public <T extends SDL_CommonEvent, V> V readTypedField(Class<T> clazz, String field) {
                return (V)((T)getProperValue(clazz)).readField(field);
            }

            public static Class<? extends SDL_CommonEvent> getClassFromType(int type) {
                switch (type) {
                    case SDL_EventType.DISPLAYEVENT: return SDL_DisplayEvent.class;
                    case SDL_EventType.WINDOWEVENT: return SDL_WindowEvent.class;
                    case SDL_EventType.KEYDOWN:
                    case SDL_EventType.KEYUP: return SDL_KeyboardEvent.class;
                    case SDL_EventType.TEXTEDITING: return SDL_TextEditingEvent.class;
                    case SDL_EventType.TEXTINPUT: return SDL_TextInputEvent.class;
                    case SDL_EventType.MOUSEMOTION: return SDL_MouseMotionEvent.class;
                    case SDL_EventType.MOUSEBUTTONDOWN:
                    case SDL_EventType.MOUSEBUTTONUP: return SDL_MouseButtonEvent.class;
                    case SDL_EventType.MOUSEWHEEL: return SDL_MouseWheelEvent.class;
                    case SDL_EventType.JOYAXISMOTION: return SDL_JoyAxisEvent.class;
                    case SDL_EventType.JOYBALLMOTION: return SDL_JoyBallEvent.class;
                    case SDL_EventType.JOYHATMOTION: return SDL_JoyHatEvent.class;
                    case SDL_EventType.JOYBUTTONDOWN:
                    case SDL_EventType.JOYBUTTONUP: return SDL_JoyButtonEvent.class;
                    case SDL_EventType.JOYDEVICEADDED:
                    case SDL_EventType.JOYDEVICEREMOVED: return SDL_JoyDeviceEvent.class;
                    case SDL_EventType.CONTROLLERAXISMOTION: return SDL_ControllerAxisEvent.class;
                    case SDL_EventType.CONTROLLERBUTTONDOWN:
                    case SDL_EventType.CONTROLLERBUTTONUP: return SDL_ControllerButtonEvent.class;
                    case SDL_EventType.CONTROLLERDEVICEADDED:
                    case SDL_EventType.CONTROLLERDEVICEREMOVED:
                    case SDL_EventType.CONTROLLERDEVICEREMAPPED: return SDL_ControllerDeviceEvent.class;
                    case SDL_EventType.CONTROLLERTOUCHPADDOWN:
                    case SDL_EventType.CONTROLLERTOUCHPADMOTION:
                    case SDL_EventType.CONTROLLERTOUCHPADUP: return SDL_ControllerTouchpadEvent.class;
                    case SDL_EventType.CONTROLLERSENSORUPDATE: return SDL_ControllerSensorEvent.class;
                    case SDL_EventType.AUDIODEVICEADDED:
                    case SDL_EventType.AUDIODEVICEREMOVED: return SDL_AudioDeviceEvent.class;
                    case SDL_EventType.FINGERMOTION:
                    case SDL_EventType.FINGERDOWN:
                    case SDL_EventType.FINGERUP: return SDL_TouchFingerEvent.class;
                    case SDL_EventType.MULTIGESTURE: return SDL_MultiGestureEvent.class;
                    case SDL_EventType.DOLLARGESTURE:
                    case SDL_EventType.DOLLARRECORD: return SDL_DollarGestureEvent.class;
                    case SDL_EventType.DROPBEGIN:
                    case SDL_EventType.DROPFILE:
                    case SDL_EventType.DROPTEXT:
                    case SDL_EventType.DROPCOMPLETE: return SDL_DropEvent.class;
                    case SDL_EventType.SENSORUPDATE: return SDL_SensorEvent.class;
                    case SDL_EventType.QUIT: return SDL_QuitEvent.class;
                    case SDL_EventType.USEREVENT:
                    case SDL_EventType.LASTEVENT: return SDL_UserEvent.class;
                    case SDL_EventType.SYSWMEVENT: return SDL_SysWMEvent.class;
                }
                if (type > SDL_EventType.USEREVENT && type < SDL_EventType.LASTEVENT) return SDL_UserEvent.class;
                return null;
            }
        }

        /**
         * <p>Pump the event loop, gathering events from the input devices.</p>
         *
         * <p>This function updates the event queue and internal input device state.</p>
         *
         * <p><b>WARNING</b>: This should only be run in the thread that initialized the
         * video subsystem, and for extra safety, you should consider only doing those
         * things on the main thread in any case.</p>
         *
         * <p>SDL_PumpEvents() gathers all the pending input information from devices and
         * places it in the event queue. Without calls to SDL_PumpEvents() no events
         * would ever be placed on the queue. Often the need for calls to
         * SDL_PumpEvents() is hidden from the user since SDL_PollEvent() and
         * SDL_WaitEvent() implicitly call SDL_PumpEvents(). However, if you are not
         * polling or waiting for events (e.g. you are filtering them), then you must
         * call SDL_PumpEvents() to force an event queue update.</p>
         *
         * @see #SDL_PollEvent
         * @see #SDL_WaitEvent
         */
        public void SDL_PumpEvents();

        /**
         * A function pointer used for callbacks that watch the event queue.
         *
         * @param userdata what was passed as {@code userdata} to SDL_SetEventFilter()
         *                 or SDL_AddEventWatch, etc
         * @param event the event that triggered the callback
         * @return 1 to permit event to be added to the queue, and 0 to disallow
         *         it. When used with SDL_AddEventWatch, the return value is ignored.
         *
         * @see #SDL_SetEventFilter
         * @see #SDL_AddEventWatch
         */
        @FunctionalInterface
        public static interface SDL_EventFilter extends Callback {
            public boolean filter(Pointer userdata, SDL_Event event);
        }

        /**
         * <p>Set up a filter to process all events before they change internal state and
         * are posted to the internal event queue.</p>
         *
         * <p>If the filter function returns 1 when called, then the event will be added
         * to the internal queue. If it returns 0, then the event will be dropped from
         * the queue, but the internal state will still be updated. This allows
         * selective filtering of dynamically arriving events.</p>
         *
         * <p><b>WARNING</b>: Be very careful of what you do in the event filter function,
         * as it may run in a different thread!</p>
         *
         * <p>On platforms that support it, if the quit event is generated by an
         * interrupt signal (e.g. pressing Ctrl-C), it will be delivered to the
         * application at the next event poll.</p>
         *
         * <p>There is one caveat when dealing with the ::SDL_QuitEvent event type. The
         * event filter is only called when the window manager desires to close the
         * application window. If the event filter returns 1, then the window will be
         * closed, otherwise the window will remain open if possible.</p>
         *
         * <p>Note: Disabled events never make it to the event filter function; see
         * SDL_EventState().</p>
         *
         * <p>Note: If you just want to inspect events without filtering, you should use
         * SDL_AddEventWatch() instead.</p>
         *
         * <p>Note: Events pushed onto the queue with SDL_PushEvent() get passed through
         * the event filter, but events pushed onto the queue with SDL_PeepEvents() do
         * not.</p>
         *
         * @param filter An SDL_EventFilter function to call when an event happens
         * @param userdata a pointer that is passed to {@code filter}
         *
         * @see #SDL_AddEventWatch
         * @see #SDL_EventState
         * @see #SDL_GetEventFilter
         * @see #SDL_PeepEvents
         * @see #SDL_PushEvent
         */
        public void SDL_SetEventFilter(SDL_EventFilter filter, Pointer userdata);

        /**
         * <p>Run a specific filter function on the current event queue, removing any
         * events for which the filter returns 0.</p>
         *
         * <p>See SDL_SetEventFilter() for more information. Unlike SDL_SetEventFilter(),
         * this function does not change the filter permanently, it only uses the
         * supplied filter until this function returns.</p>
         *
         * @param filter the SDL_EventFilter function to call when an event happens
         * @param userdata a pointer that is passed to {@code filter}
         *
         * @see #SDL_GetEventFilter
         * @see #SDL_SetEventFilter
         */
        public void SDL_FilterEvents(SDL_EventFilter filter, Pointer userdata);

        /**
         * <p>Add an event to the event queue.</p>
         *
         * <p>The event queue can actually be used as a two way communication channel.
         * Not only can events be read from the queue, but the user can also push
         * their own events onto it. {@code event} is a pointer to the event structure you
         * wish to push onto the queue. The event is copied into the queue, and the
         * caller may dispose of the memory pointed to after SDL_PushEvent() returns.</p>
         *
         * <p>Note: Pushing device input events onto the queue doesn't modify the state
         * of the device within SDL.</p>
         *
         * <p>This function is thread-safe, and can be called from other threads safely.</p>
         *
         * <p>Note: Events pushed onto the queue with SDL_PushEvent() get passed through
         * the event filter but events added with SDL_PeepEvents() do not.</p>
         *
         * <p>For pushing application-specific events, please use SDL_RegisterEvents() to
         * get an event type that does not conflict with other code that also wants
         * its own custom event types.</p>
         *
         * @param event the SDL_Event to be added to the queue
         * @return 1 on success, 0 if the event was filtered, or a negative error
         *         code on failure; call SDL_GetError() for more information. A
         *         common reason for error is the event queue being full.
         *
         * @see #SDL_PeepEvents
         * @see #SDL_PollEvent
         * @see #SDL_RegisterEvents
         */
        public boolean SDL_PushEvent(SDL_Event event);

        public static final int SDL_QUERY = -1;
        public static final int SDL_IGNORE = 0;
        public static final int SDL_DISABLE = 0;
        public static final int SDL_ENABLE = 1;

        /**
         * <p>Set the state of processing events by type.</p>
         *
         * <p>{@code state} may be any of the following:</p>
         *
         * <ul>
         *   <li>{@code SDL_QUERY}: returns the current processing state of the specified event</li>
         *   <li>{@code SDL_IGNORE} (aka {@code SDL_DISABLE}): the event will automatically be dropped</li>
         *       from the event queue and will not be filtered
         *   <li>{@code SDL_ENABLE}: the event will be processed normally</li>
         * </ul>
         *
         * @param type the type of event; see SDL_EventType for details
         * @param state how to process the event
         * @return {@code SDL_DISABLE} or {@code SDL_ENABLE}, representing the processing state
         *          of the event before this function makes any changes to it.
         *
         * @see #SDL_GetEventState
         */
        public boolean SDL_EventState(int type, int state);

        /**
         * <p>Add a callback to be triggered when an event is added to the event queue.</p>
         *
         * <p>{@code filter} will be called when an event happens, and its return value is
         * ignored.</p>
         *
         * <b>WARNING</b>: Be very careful of what you do in the event filter function,
         * as it may run in a different thread!
         *
         * <p>If the quit event is generated by a signal (e.g. SIGINT), it will bypass
         * the internal queue and be delivered to the watch callback immediately, and
         * arrive at the next event poll.</p>
         *
         * <p>Note: the callback is called for events posted by the user through
         * SDL_PushEvent(), but not for disabled events, nor for events by a filter
         * callback set with SDL_SetEventFilter(), nor for events posted by the user
         * through SDL_PeepEvents().</p>
         *
         * @param filter an SDL_EventFilter function to call when an event happens.
         * @param userdata a pointer that is passed to {@code filter}
         *
         * @see #SDL_DelEventWatch
         * @see #SDL_SetEventFilter
         */
        public void SDL_AddEventWatch(SDL_EventFilter filter, Pointer userdata);

        /**
         * <p>Remove an event watch callback added with SDL_AddEventWatch().</p>
         *
         * <p>This function takes the same input as SDL_AddEventWatch() to identify and
         * delete the corresponding callback.</p>
         *
         * @param filter the function originally passed to SDL_AddEventWatch()
         * @param userdata the pointer originally passed to SDL_AddEventWatch()
         *
         * @see #SDL_AddEventWatch
         */
        public void SDL_DelEventWatch(SDL_EventFilter filter, Pointer userdata);
        //#endregion

        //#region SDL_joystick.h
        /**
         * <p>Enable/disable joystick event polling.</p>
         *
         * <p>If joystick events are disabled, you must call SDL_JoystickUpdate()
         * yourself and manually check the state of the joystick when you want
         * joystick information.</p>
         *
         * <p>It is recommended that you leave joystick event handling enabled.</p>
         *
         * <p><b>WARNING</b>: Calling this function may delete all events currently in SDL's
         * event queue.</p>
         *
         * @param state can be one of {@code SDL_QUERY}, {@code SDL_IGNORE}, or {@code SDL_ENABLE}
         * @return 1 if enabled, 0 if disabled, or a negative error code on failure;
         *         call SDL_GetError() for more information.
         *
         *         If {@code state} is {@code SDL_QUERY} then the current state is returned,
         *         otherwise the new processing state is returned.
         *
         * @see #SDL_GameControllerEventState
         */
        public int SDL_JoystickEventState(int state);
        //#endregion

        //#region SDL_hints.h
        /**
         *  <p>A variable controlling the scaling quality</p>
         *
         *  <p>This variable can be set to the following values:<br>
         *    <blockquote>"0" or "nearest" - Nearest pixel sampling</blockquote>
         *    <blockquote>"1" or "linear"  - Linear filtering (supported by OpenGL and Direct3D)</blockquote>
         *    <blockquote>"2" or "best"    - Currently this is the same as "linear"</blockquote></p>
         *
         *  <p>By default nearest pixel sampling is used</p>
         */
        public static final String SDL_HINT_RENDER_SCALE_QUALITY = "SDL_RENDER_SCALE_QUALITY";

        /**
         * <p>Set a hint with a specific priority.</p>
         *
         * <p>The priority controls the behavior when setting a hint that already has a
         * value. Hints will replace existing hints of their priority and lower.
         * Environment variables are considered to have override priority.</p>
         *
         * @param name the hint to set
         * @param value the value of the hint variable
         * @param priority the SDL_HintPriority level for the hint
         * @return SDL_TRUE if the hint was set, SDL_FALSE otherwise.
         *
         * @see #SDL_GetHint
         * @see #SDL_SetHint
         */
        public boolean SDL_SetHintWithPriority(String name, String value, int priority);

        /**
         * Get the boolean value of a hint variable.
         *
         * @param name the name of the hint to get the boolean value from
         * @param default_value the value to return if the hint does not exist
         * @return the boolean value of a hint or the provided default value if the
         *         hint does not exist.
         *
         * @since This function is available since SDL 2.0.5.
         *
         * @see #SDL_GetHint
         * @see #SDL_SetHint
         */
        public boolean SDL_GetHintBoolean(String name, boolean defaultValue);
        //#endregion

        //#region SDL_keyboard.h
        /**
         * The SDL keysym structure, used in key events.
         *
         * @apiNote If you are looking for translated character input, see the ::SDL_TEXTINPUT event.
         */
        @FieldOrder({
            "scancode",
            "sym",
            "mod",
            "unused"
        })
        public static class SDL_Keysym extends Structure {
            public int scancode;
            public int sym;
            public short mod;
            public int unused;
        }
        //#endregion

        //#region SDL_mouse.h
        public static final int SDL_MOUSEWHEEL_NORMAL = 0;
        public static final int SDL_MOUSEWHEEL_FLIPPED = 1;

        /**
         * <p>Get the current state of the mouse in relation to the desktop.</p>
         *
         * <p>This works similarly to SDL_GetMouseState(), but the coordinates will be
         * reported relative to the top-left of the desktop. This can be useful if you
         * need to track the mouse outside of a specific window and SDL_CaptureMouse()
         * doesn't fit your needs. For example, it could be useful if you need to
         * track the mouse while dragging a window, where coordinates relative to a
         * window might not be in sync at all times.</p>
         *
         * <p>Note: SDL_GetMouseState() returns the mouse position as SDL understands it
         * from the last pump of the event queue. This function, however, queries the
         * OS for the current mouse position, and as such, might be a slightly less
         * efficient function. Unless you know what you're doing and have a good
         * reason to use this function, you probably want SDL_GetMouseState() instead.</p>
         *
         * @param x filled in with the current X coord relative to the desktop; can be
         *          NULL
         * @param y filled in with the current Y coord relative to the desktop; can be
         *          NULL
         * @return the current button state as a bitmask which can be tested using
         *         the SDL_BUTTON(X) macros.
         *
         * @since This function is available since SDL 2.0.4.
         *
         * @see #SDL_CaptureMouse
         */
        public int SDL_GetGlobalMouseState(IntByReference x, IntByReference y);
        //#endregion

        //#region SDL_mutex.h
        /** The SDL mutex structure, defined in SDL_sysmutex.c */
        public static final class SDL_mutex extends Structure {}

        /**
         * <p>Create a new mutex.</p>
         *
         * <p>All newly-created mutexes begin in the <i>unlocked</i> state.</p>
         *
         * <p>Calls to SDL_LockMutex() will not return while the mutex is locked by
         * another thread. See SDL_TryLockMutex() to attempt to lock without blocking.</p>
         *
         * <p>SDL mutexes are reentrant.</p>
         *
         * @return the initialized and unlocked mutex or NULL on failure; call
         *         SDL_GetError() for more information.
         *
         * @see #SDL_DestroyMutex
         * @see #SDL_LockMutex
         * @see #SDL_TryLockMutex
         * @see #SDL_UnlockMutex
         */
        public SDL_mutex SDL_CreateMutex();

        /**
         * <p>Lock the mutex.</p>
         *
         * <p>This will block until the mutex is available, which is to say it is in the
         * unlocked state and the OS has chosen the caller as the next thread to lock
         * it. Of all threads waiting to lock the mutex, only one may do so at a time.</p>
         *
         * <p>It is legal for the owning thread to lock an already-locked mutex. It must
         * unlock it the same number of times before it is actually made available for
         * other threads in the system (this is known as a "recursive mutex").</p>
         *
         * @param mutex the mutex to lock
         * @return 0, or -1 on error.
         */
        public int SDL_LockMutex(SDL_mutex mutex);

        /**
         * <p>Unlock the mutex.</p>
         *
         * <p>It is legal for the owning thread to lock an already-locked mutex. It must
         * unlock it the same number of times before it is actually made available for
         * other threads in the system (this is known as a "recursive mutex").</p>
         *
         * <p>It is an error to unlock a mutex that has not been locked by the current
         * thread, and doing so results in undefined behavior.</p>
         *
         * <p>It is also an error to unlock a mutex that isn't locked at all.</p>
         *
         * @param mutex the mutex to unlock.
         * @return 0, or -1 on error.
         */
        public int SDL_UnlockMutex(SDL_mutex mutex);

        /**
         * <p>Destroy a mutex created with SDL_CreateMutex().</p>
         *
         * <p>This function must be called on any mutex that is no longer needed. Failure
         * to destroy a mutex will result in a system memory or resource leak. While
         * it is safe to destroy a mutex that is <i>unlocked</i>, it is not safe to attempt
         * to destroy a locked mutex, and may result in undefined behavior depending
         * on the platform.</p>
         *
         * @param mutex the mutex to destroy
         *
         * @see #SDL_CreateMutex
         * @see #SDL_LockMutex
         * @see #SDL_TryLockMutex
         * @see #SDL_UnlockMutex
         */
        public void SDL_DestroyMutex(SDL_mutex mutex);
        //#endregion

        //#region SDL_stdinc.h
        public static int SDL_FOURCC(int a, int b, int c, int d) {
            return (((int)((byte)a)) << 0) |
                   (((int)((byte)b)) << 8) |
                   (((int)((byte)c)) << 16) |
                   (((int)((byte)d)) << 24);
        }

        public String SDL_getenv(String name);
        public int SDL_setenv(String name, String value, boolean overwrite);
        //#endregion

        //#region SDL_pixels.h
        public static int SDL_DEFINE_PIXELFOURCC(int a, int b, int c, int d) {
            return SDL_FOURCC(a, b, c, d);
        }

        public static int SDL_DEFINE_PIXELFORMAT(int type, int order, int layout, int bits, int bytes) {
            return ((1 << 28) | ((type) << 24) | ((order) << 20) | ((layout) << 16) | ((bits) << 8) | ((bytes) << 0));
        }

        @FieldOrder({
            "r",
            "g",
            "b",
            "a"
        })
        public static class SDL_Color extends Structure {
            public static class ByReference extends SDL_Color implements Structure.ByReference {
                public ByReference() { }
                public ByReference(Pointer p) { super(p); }
            }

            public SDL_Color() { }
            public SDL_Color(Pointer p) { super(p); read(); }

            public byte r;
            public byte g;
            public byte b;
            public byte a;
        }

        @FieldOrder({
            "ncolors",
            "colors",
            "version",
            "refcount"
        })
        public static class SDL_Palette extends Structure {
            public static class ByReference extends SDL_Palette implements Structure.ByReference {
                public ByReference() { }
                public ByReference(Pointer p) { super(p); }
            }

            public SDL_Palette() { }
            public SDL_Palette(Pointer p) { super(p); read(); }

            public int ncolors;
            public SDL_Color.ByReference colors;
            public int version;
            public int refcount;
        }

        /**
         * @apiNote Everything in the pixel format structure is read-only.
         */
        @FieldOrder({
            "format",
            "palette",
            "BitsPerPixel",
            "BytesPerPixel",
            "padding",
            "Rmask",
            "Gmask",
            "Bmask",
            "Amask",
            "Rloss",
            "Gloss",
            "Bloss",
            "Aloss",
            "Rshift",
            "Gshift",
            "Bshift",
            "Ashift",
            "refcount",
            "next"
        })
        public static class SDL_PixelFormat extends Structure {
            public static class ByReference extends SDL_PixelFormat implements Structure.ByReference {
                public ByReference() { }
                public ByReference(Pointer p) { super(p); }
            }

            public SDL_PixelFormat() { }
            public SDL_PixelFormat(Pointer p) { super(p); read(); }

            public int format;
            public SDL_Palette.ByReference palette;
            public byte BitsPerPixel;
            public byte BytesPerPixel;
            public byte[] padding = new byte[2];
            public int Rmask;
            public int Gmask;
            public int Bmask;
            public int Amask;
            public byte Rloss;
            public byte Gloss;
            public byte Bloss;
            public byte Aloss;
            public byte Rshift;
            public byte Gshift;
            public byte Bshift;
            public byte Ashift;
            public int refcount;
            public SDL_PixelFormat.ByReference next;
        }
        //#endregion

        //#region SDL_rect.h
        /**
         * The structure that defines a point (integer)
         *
         * @see #SDL_EnclosePoints
         * @see #SDL_PointInRect
         */
        @FieldOrder({
            "x",
            "y"
        })
        public static class SDL_Point extends Structure {
            public int x;
            public int y;
        }

        /**
         * A rectangle, with the origin at the upper left (integer).
         *
         * @see #SDL_RectEmpty
         * @see #SDL_RectEquals
         * @see #SDL_HasIntersection
         * @see #SDL_IntersectRect
         * @see #SDL_UnionRect
         * @see #SDL_EnclosePoints
         */
        @FieldOrder({
            "x", "y",
            "w", "h"
        })
        public static class SDL_Rect extends Structure {
            public int x, y;
            public int w, h;
        }

        /**
         * Returns true if point resides inside a rectangle.
         */
        default public boolean SDL_PointInRect(final SDL_Point p, final SDL_Rect r) {
            return p.x >= r.x && p.x < r.x + r.w && p.y >= r.y && p.y < r.y + r.h;
        }
        //#endregion

        //#region SDL_render.h
        /**
         * A structure representing rendering state
         */
        public static final class SDL_Renderer extends PointerType {}

        /**
         * An efficient driver-specific representation of pixel data
         */
        public static final class SDL_Texture extends PointerType {}

        /**
         * Create a 2D rendering context for a window.
         *
         * @param window the window where rendering is displayed
         * @param index the index of the rendering driver to initialize, or -1 to
         *              initialize the first one supporting the requested flags
         * @param flags 0, or one or more SDL_RendererFlags OR'd together
         * @return a valid rendering context or NULL if there was an error; call
         *         SDL_GetError() for more information.
         *
         * @see #SDL_CreateSoftwareRenderer
         * @see #SDL_DestroyRenderer
         * @see #SDL_GetNumRenderDrivers
         * @see #SDL_GetRendererInfo
         */
        public SDL_Renderer SDL_CreateRenderer(SDL_Window window, int index, int flags);

        /**
         * <p>Create a texture for a rendering context.</p>
         *
         * <p>You can set the texture scaling method by setting
         * {@code SDL_HINT_RENDER_SCALE_QUALITY} before creating the texture.</p>
         *
         * @param renderer the rendering context
         * @param format one of the enumerated values in SDL_PixelFormatEnum
         * @param access one of the enumerated values in SDL_TextureAccess
         * @param w the width of the texture in pixels
         * @param h the height of the texture in pixels
         * @return a pointer to the created texture or NULL if no rendering context
         *         was active, the format was unsupported, or the width or height
         *         were out of range; call SDL_GetError() for more information.
         *
         * @see #SDL_CreateTextureFromSurface
         * @see #SDL_DestroyTexture
         * @see #SDL_QueryTexture
         * @see #SDL_UpdateTexture
         */
        public SDL_Texture SDL_CreateTexture(SDL_Renderer renderer, int format, int access, int w, int h);

        /**
         * <p>Update the given texture rectangle with new pixel data.</p>
         *
         * <p>The pixel data must be in the pixel format of the texture. Use
         * SDL_QueryTexture() to query the pixel format of the texture.</p>
         *
         * <p>This is a fairly slow function, intended for use with static textures that
         * do not change often.</p>
         *
         * <p>If the texture is intended to be updated often, it is preferred to create
         * the texture as streaming and use the locking functions referenced below.
         * While this function will work with streaming textures, for optimization
         * reasons you may not get the pixels back if you lock the texture afterward.</p>
         *
         * @param texture the texture to update
         * @param rect an SDL_Rect structure representing the area to update, or NULL
         *             to update the entire texture
         * @param pixels the raw pixel data in the format of the texture
         * @param pitch the number of bytes in a row of pixel data, including padding
         *              between lines
         * @return 0 on success or a negative error code on failure; call
         *         SDL_GetError() for more information.
         *
         * @see #SDL_CreateTexture
         * @see #SDL_LockTexture
         * @see #SDL_UnlockTexture
         */
        public int SDL_UpdateTexture(SDL_Texture texture, final SDL_Rect rect, final Pointer pixels, int pitch);

        /**
         * <p>Clear the current rendering target with the drawing color.</p>
         *
         * <p>This function clears the entire rendering target, ignoring the viewport and
         * the clip rectangle.</p>
         *
         * @param renderer the rendering context
         * @return 0 on success or a negative error code on failure; call
         *         SDL_GetError() for more information.
         *
         * @since This function is available since SDL 2.0.0.
         *
         * @see #SDL_SetRenderDrawColor
         */
        public int SDL_RenderClear(SDL_Renderer renderer);

        /**
         * <p>Copy a portion of the texture to the current rendering target.</p>
         *
         * <p>The texture is blended with the destination based on its blend mode set
         * with SDL_SetTextureBlendMode().</p>
         *
         * <p>The texture color is affected based on its color modulation set by
         * SDL_SetTextureColorMod().</p>
         *
         * <p>The texture alpha is affected based on its alpha modulation set by
         * SDL_SetTextureAlphaMod().</p>
         *
         * @param renderer the rendering context
         * @param texture the source texture
         * @param srcrect the source SDL_Rect structure or NULL for the entire texture
         * @param dstrect the destination SDL_Rect structure or NULL for the entire
         *                rendering target; the texture will be stretched to fill the
         *                given rectangle
         * @return 0 on success or a negative error code on failure; call
         *         SDL_GetError() for more information.
         *
         * @see #SDL_RenderCopyEx
         * @see #SDL_SetTextureAlphaMod
         * @see #SDL_SetTextureBlendMode
         * @see #SDL_SetTextureColorMod
         */
        public int SDL_RenderCopy(SDL_Renderer renderer, SDL_Texture texture, final SDL_Rect srcrect, final SDL_Rect dstrect);

        /**
         * <p>Update the screen with any rendering performed since the previous call.</p>
         *
         * <p>SDL's rendering functions operate on a backbuffer; that is, calling a
         * rendering function such as SDL_RenderDrawLine() does not directly put a
         * line on the screen, but rather updates the backbuffer. As such, you compose
         * your entire scene and *present* the composed backbuffer to the screen as a
         * complete picture.</p>
         *
         * <p>Therefore, when using SDL's rendering API, one does all drawing intended
         * for the frame, and then calls this function once per frame to present the
         * final drawing to the user.</p>
         *
         * <p>The backbuffer should be considered invalidated after each present; do not
         * assume that previous contents will exist between frames. You are strongly
         * encouraged to call SDL_RenderClear() to initialize the backbuffer before
         * starting each new frame's drawing, even if you plan to overwrite every
         * pixel.</p>
         *
         * @param renderer the rendering context
         *
         * @see #SDL_RenderClear
         * @see #SDL_RenderDrawLine
         * @see #SDL_RenderDrawLines
         * @see #SDL_RenderDrawPoint
         * @see #SDL_RenderDrawPoints
         * @see #SDL_RenderDrawRect
         * @see #SDL_RenderDrawRects
         * @see #SDL_RenderFillRect
         * @see #SDL_RenderFillRects
         * @see #SDL_SetRenderDrawBlendMode
         * @see #SDL_SetRenderDrawColor
         */
        public void SDL_RenderPresent(SDL_Renderer renderer);

        /**
         * <p>Destroy the specified texture.</p>
         *
         * <p>Passing NULL or an otherwise invalid texture will set the SDL error message
         * to "Invalid texture".</p>
         *
         * @param texture the texture to destroy
         *
         * @see #SDL_CreateTexture
         * @see #SDL_CreateTextureFromSurface
         */
        public void SDL_DestroyTexture(SDL_Texture texture);

        /**
         * Destroy the rendering context for a window and free associated textures.
         *
         * @param renderer the rendering context
         *
         * @see #SDL_CreateRenderer
         */
        public void SDL_DestroyRenderer(SDL_Renderer renderer);

        /**
         * <p>Set a device independent resolution for rendering.</p>
         *
         * <p>This function uses the viewport and scaling functionality to allow a fixed
         * logical resolution for rendering, regardless of the actual output
         * resolution. If the actual output resolution doesn't have the same aspect
         * ratio the output rendering will be centered within the output display.</p>
         *
         * <p>If the output display is a window, mouse and touch events in the window
         * will be filtered and scaled so they seem to arrive within the logical
         * resolution. The SDL_HINT_MOUSE_RELATIVE_SCALING hint controls whether
         * relative motion events are also scaled.</p>
         *
         * <p>If this function results in scaling or subpixel drawing by the rendering
         * backend, it will be handled using the appropriate quality hints.</p>
         *
         * @param renderer the renderer for which resolution should be set
         * @param w the width of the logical resolution
         * @param h the height of the logical resolution
         * @return 0 on success or a negative error code on failure; call
         *         SDL_GetError() for more information.
         *
         * @since This function is available since SDL 2.0.0.
         *
         * @see #SDL_RenderGetLogicalSize
         */
        public int SDL_RenderSetLogicalSize(SDL_Renderer renderer, int w, int h);

        /**
         * <p>Set whether to force integer scales for resolution-independent rendering.</p>
         *
         * <p>This function restricts the logical viewport to integer values - that is,
         * when a resolution is between two multiples of a logical size, the viewport
         * size is rounded down to the lower multiple.</p>
         *
         * @param renderer the renderer for which integer scaling should be set
         * @param enable enable or disable the integer scaling for rendering
         * @return 0 on success or a negative error code on failure; call
         *         SDL_GetError() for more information.
         *
         * @since This function is available since SDL 2.0.5.
         *
         * @see #SDL_RenderGetIntegerScale
         * @see #SDL_RenderSetLogicalSize
         */
        public int SDL_RenderSetIntegerScale(SDL_Renderer renderer, boolean enable);
        //#endregion

        //#region SDL_surface.h
        /** Just here for compatibility */
        public static final int SDL_SWSURFACE = 0;

        /**
         * A collection of pixels used in software blitting.
         *
         * @apiNote This structure should be treated as read-only, except for {@code pixels},
         *          which, if not NULL, contains the raw pixel data for the surface.
         */
        @FieldOrder({
            "flags",
            "format",
            "w", "h",
            "pitch",
            "pixels",
            "userdata",
            "locked",
            "list_blitmap",
            "clip_rect",
            "map",
            "refcount"
        })
        public static class SDL_Surface extends Structure {
            /** Read-only */
            public int flags;
            /** Read-only */
            public SDL_PixelFormat.ByReference format;
            /** Read-only */
            public int w, h;
            /** Read-only */
            public int pitch;
            /** Read-write */
            public Pointer pixels;

            /**
             * <p>Application data associated with the surface</p>
             *
             * <p>Read-write</p>
             */
            public Pointer userdata;

            /**
             * <p>information needed for surfaces requiring locks</p>
             *
             * <p>Read-only</p>
             */
            public int locked;

            /**
             * <p>list of BlitMap that hold a reference to this surface</p>
             *
             * <p>Private</p>
             */
            public Pointer list_blitmap;

            /**
             * <p>clipping information</p>
             *
             * <p>Read-only</p>
             */
            public SDL_Rect clip_rect;

            /**
             * <p>info for fast blit mapping to other surfaces</p>
             *
             * <p>Private</p>
             */
            public Pointer map;

            /**
             * <p>Reference count -- used when freeing surface</p>
             *
             * <p>Read-mostly</p>
             */
            public int refcount;
        }

        /**
         * <p>Allocate a new RGB surface.</p>
         *
         * <p>If {@code depth} is 4 or 8 bits, an empty palette is allocated for the surface.
         * If {@code depth} is greater than 8 bits, the pixel format is set using the
         * [RGBA]mask parameters.</p>
         *
         * <p>The [RGBA]mask parameters are the bitmasks used to extract that color from
         * a pixel. For instance, {@code Rmask} being 0xFF000000 means the red data is
         * stored in the most significant byte. Using zeros for the RGB masks sets a
         * default value, based on the depth. For example:</p>
         *
         * <pre>
         * SDL_CreateRGBSurface(0,w,h,32,0,0,0,0);
         * </pre>
         *
         * <p>However, using zero for the Amask results in an Amask of 0.</p>
         *
         * <p>By default surfaces with an alpha mask are set up for blending as with:</p>
         *
         * <pre>
         * SDL_SetSurfaceBlendMode(surface, SDL_BLENDMODE_BLEND)
         * </pre>
         *
         * <p>You can change this by calling SDL_SetSurfaceBlendMode() and selecting a
         * different {@code blendMode}.</p>
         *
         * @param flags the flags are unused and should be set to 0
         * @param width the width of the surface
         * @param height the height of the surface
         * @param depth the depth of the surface in bits
         * @param Rmask the red mask for the pixels
         * @param Gmask the green mask for the pixels
         * @param Bmask the blue mask for the pixels
         * @param Amask the alpha mask for the pixels
         * @return the new SDL_Surface structure that is created or NULL if it fails;
         *         call SDL_GetError() for more information.
         *
         * @see #SDL_CreateRGBSurfaceFrom
         * @see #SDL_CreateRGBSurfaceWithFormat
         * @see #SDL_FreeSurface
         */
        public SDL_Surface SDL_CreateRGBSurface(int flags, int width, int height, int depth, int Rmask, int Gmask, int Bmask, int Amask);

        /**
         * <p>Free an RGB surface.</p>
         *
         * <p>It is safe to pass NULL to this function.</p>
         *
         * @param surface the SDL_Surface to free.
         *
         * @see #SDL_CreateRGBSurface
         * @see #SDL_CreateRGBSurfaceFrom
         * @see #SDL_LoadBMP
         * @see #SDL_LoadBMP_RW
         */
        public void SDL_FreeSurface(SDL_Surface surface);
        //#endregion

        //#region SDL_timer.h
        /**
         * <p>Function prototype for the timer callback function.</p>
         *
         * <p>The callback function is passed the current timer interval and returns
         * the next timer interval. If the returned value is the same as the one
         * passed in, the periodic alarm continues, otherwise a new alarm is
         * scheduled. If the callback returns 0, the periodic alarm is cancelled.</p>
         */
        @FunctionalInterface
        public static interface SDL_TimerCallback extends Callback {
            public int execute(int interval, Pointer param);
        }

        /**
         * <p>Call a callback function at a future time.</p>
         *
         * <p>If you use this function, you must pass {@code SDL_INIT_TIMER} to SDL_Init().</p>
         *
         * <p>The callback function is passed the current timer interval and the user
         * supplied parameter from the SDL_AddTimer() call and should return the next
         * timer interval. If the value returned from the callback is 0, the timer is
         * canceled.</p>
         *
         * <p>The callback is run on a separate thread.</p>
         *
         * <p>Timers take into account the amount of time it took to execute the
         * callback. For example, if the callback took 250 ms to execute and returned
         * 1000 (ms), the timer would only wait another 750 ms before its next
         * iteration.</p>
         *
         * <p>Timing may be inexact due to OS scheduling. Be sure to note the current
         * time with SDL_GetTicks() or SDL_GetPerformanceCounter() in case your
         * callback needs to adjust for variances.</p>
         *
         * @param interval the timer delay, in milliseconds, passed to {@code callback}
         * @param callback the SDL_TimerCallback function to call when the specified
         *                 {@code interval} elapses
         * @param param a pointer that is passed to {@code callback}
         * @return a timer ID or 0 if an error occurs; call SDL_GetError() for more
         *         information.
         *
         * @see #SDL_RemoveTimer
         */
        public int SDL_AddTimer(int interval, SDL_TimerCallback callback, Pointer param);

        /**
         * Remove a timer created with SDL_AddTimer().
         *
         * @param id the ID of the timer to remove
         * @return SDL_TRUE if the timer is removed or SDL_FALSE if the timer wasn't
         *         found.
         *
         * @see #SDL_AddTimer
         */
        public boolean SDL_RemoveTimer(int id);
        //#endregion

        //#region SDL_video.h
        /**
         * The type used to identify a window
         *
         * @see #SDL_CreateWindow
         * @see #SDL_CreateWindowFrom
         * @see #SDL_DestroyWindow
         * @see #SDL_FlashWindow
         * @see #SDL_GetWindowData
         * @see #SDL_GetWindowFlags
         * @see #SDL_GetWindowGrab
         * @see #SDL_GetWindowKeyboardGrab
         * @see #SDL_GetWindowMouseGrab
         * @see #SDL_GetWindowPosition
         * @see #SDL_GetWindowSize
         * @see #SDL_GetWindowTitle
         * @see #SDL_HideWindow
         * @see #SDL_MaximizeWindow
         * @see #SDL_MinimizeWindow
         * @see #SDL_RaiseWindow
         * @see #SDL_RestoreWindow
         * @see #SDL_SetWindowData
         * @see #SDL_SetWindowFullscreen
         * @see #SDL_SetWindowGrab
         * @see #SDL_SetWindowKeyboardGrab
         * @see #SDL_SetWindowMouseGrab
         * @see #SDL_SetWindowIcon
         * @see #SDL_SetWindowPosition
         * @see #SDL_SetWindowSize
         * @see #SDL_SetWindowBordered
         * @see #SDL_SetWindowResizable
         * @see #SDL_SetWindowTitle
         * @see #SDL_ShowWindow
         */
        public static final class SDL_Window extends PointerType {}

        /**
         * Used to indicate that you don't care what the window position is.
         */
        public static final int SDL_WINDOWPOS_UNDEFINED_MASK = 0x1FFF0000;
        public static int SDL_WINDOWPOS_UNDEFINED_DISPLAY(int x) {
            return SDL_WINDOWPOS_UNDEFINED_MASK | x;
        }

        /**
         * Used to indicate that the window position should be centered.
         */
        public static final int SDL_WINDOWPOS_CENTERED_MASK = 0x2FFF0000;
        public static int SDL_WINDOWPOS_CENTERED_DISPLAY(int x) {
            return SDL_WINDOWPOS_CENTERED_MASK | x;
        }

        @FieldOrder({
            "format",
            "w",
            "h",
            "refresh_rate",
            "driverdata"
        })
        public static class SDL_DisplayMode extends Structure {
            /** pixel format */
            public int format;
            /** width, in screen coordinates */
            public int w;
            /** height, in screen coordinates */
            public int h;
            /** refresh rate (or zero for unspecified) */
            public int refresh_rate;
            /** driver-specific data, initialize to 0 */
            public Pointer driverdata;
        }

        /**
         * <p>Get the desktop area represented by a display.</p>
         *
         * <p>The primary display ({@code displayIndex} zero) is always located at 0,0.</p>
         *
         * @param displayIndex the index of the display to query
         * @param rect the SDL_Rect structure filled in with the display bounds
         * @return 0 on success or a negative error code on failure; call
         *         SDL_GetError() for more information.
         *
         * @see #SDL_GetNumVideoDisplays
         */
        public int SDL_GetDisplayBounds(int displayIndex, SDL_Rect rect);

        /**
         * <p>Get the usable desktop area represented by a display.</p>
         *
         * <p>The primary display ({@code displayIndex} zero) is always located at 0,0.</p>
         *
         * <p>This is the same area as SDL_GetDisplayBounds() reports, but with portions
         * reserved by the system removed. For example, on Apple's macOS, this
         * subtracts the area occupied by the menu bar and dock.</p>
         *
         * <p>Setting a window to be fullscreen generally bypasses these unusable areas,
         * so these are good guidelines for the maximum space available to a
         * non-fullscreen window.</p>
         *
         * <p>The parameter {@code rect} is ignored if it is NULL.
         *
         * <p>This function also returns -1 if the parameter {@code displayIndex} is out of
         * range.</p>
         *
         * @param displayIndex the index of the display to query the usable bounds
         *                     from
         * @param rect the SDL_Rect structure filled in with the display bounds
         * @return 0 on success or a negative error code on failure; call
         *         SDL_GetError() for more information.
         *
         * @since This function is available since SDL 2.0.5.
         *
         * @see #SDL_GetDisplayBounds
         * @see #SDL_GetNumVideoDisplays
         */
        public int SDL_GetDisplayUsableBounds(int displayIndex, SDL_Rect rect);

        /**
         * Get the number of available video displays.
         *
         * @return a number >= 1 or a negative error code on failure; call
         *         SDL_GetError() for more information.
         *
         * @since This function is available since SDL 2.0.0.
         *
         * @see #SDL_GetDisplayBounds
         */
        public int SDL_GetNumVideoDisplays();

        /**
         * Get the index of the display associated with a window.
         *
         * @param window the window to query
         * @return the index of the display containing the center of the window on
         *         success or a negative error code on failure; call SDL_GetError()
         *         for more information.
         *
         * @see #SDL_GetDisplayBounds
         * @see #SDL_GetNumVideoDisplays
         */
        public int SDL_GetWindowDisplayIndex(SDL_Window window);

        /**
         * <p>Create a window with the specified position, dimensions, and flags.</p>
         *
         * <p>{@code flags} may be any of the following OR'd together:</p>
         *
         * <ul>
         *   <li>{@code SDL_WINDOW_FULLSCREEN}: fullscreen window</li>
         *   <li>{@code SDL_WINDOW_FULLSCREEN_DESKTOP}: fullscreen window at desktop resolution</li>
         *   <li>{@code SDL_WINDOW_OPENGL}: window usable with an OpenGL context</li>
         *   <li>{@code SDL_WINDOW_VULKAN}: window usable with a Vulkan instance</li>
         *   <li>{@code SDL_WINDOW_METAL}: window usable with a Metal instance</li>
         *   <li>{@code SDL_WINDOW_HIDDEN}: window is not visible</li>
         *   <li>{@code SDL_WINDOW_BORDERLESS}: no window decoration</li>
         *   <li>{@code SDL_WINDOW_RESIZABLE}: window can be resized</li>
         *   <li>{@code SDL_WINDOW_MINIMIZED}: window is minimized</li>
         *   <li>{@code SDL_WINDOW_MAXIMIZED}: window is maximized</li>
         *   <li>{@code SDL_WINDOW_INPUT_GRABBED}: window has grabbed input focus</li>
         *   <li>{@code SDL_WINDOW_ALLOW_HIGHDPI}: window should be created in high-DPI mode if
         *       supported (>= SDL 2.0.1)</li>
         * </ul>
         *
         * <p>{@code SDL_WINDOW_SHOWN} is ignored by SDL_CreateWindow(). The SDL_Window is
         * implicitly shown if SDL_WINDOW_HIDDEN is not set. {@code SDL_WINDOW_SHOWN} may be
         * queried later using SDL_GetWindowFlags().</p>
         *
         * <p>On Apple's macOS, you <b>must</b> set the NSHighResolutionCapable Info.plist
         * property to YES, otherwise you will not receive a High-DPI OpenGL canvas.</p>
         *
         * <p>If the window is created with the {@code SDL_WINDOW_ALLOW_HIGHDPI} flag, its size
         * in pixels may differ from its size in screen coordinates on platforms with
         * high-DPI support (e.g. iOS and macOS). Use SDL_GetWindowSize() to query the
         * client area's size in screen coordinates, and SDL_GL_GetDrawableSize() or
         * SDL_GetRendererOutputSize() to query the drawable size in pixels.</p>
         *
         * <p>If the window is set fullscreen, the width and height parameters {@code w} and
         * {@code h} will not be used. However, invalid size parameters (e.g. too large) may
         * still fail. Window size is actually limited to 16384 x 16384 for all
         * platforms at window creation.</p>
         *
         * <p>If the window is created with any of the SDL_WINDOW_OPENGL or
         * SDL_WINDOW_VULKAN flags, then the corresponding LoadLibrary function
         * (SDL_GL_LoadLibrary or SDL_Vulkan_LoadLibrary) is called and the
         * corresponding UnloadLibrary function is called by SDL_DestroyWindow().</p>
         *
         * <p>If SDL_WINDOW_VULKAN is specified and there isn't a working Vulkan driver,
         * SDL_CreateWindow() will fail because SDL_Vulkan_LoadLibrary() will fail.</p>
         *
         * <p>If SDL_WINDOW_METAL is specified on an OS that does not support Metal,
         * SDL_CreateWindow() will fail.</p>
         *
         * <p>On non-Apple devices, SDL requires you to either not link to the Vulkan
         * loader or link to a dynamic library version. This limitation may be removed
         * in a future version of SDL.</p>
         *
         * @param title the title of the window, in UTF-8 encoding
         * @param x the x position of the window, {@code SDL_WINDOWPOS_CENTERED}, or
         *          {@code SDL_WINDOWPOS_UNDEFINED}
         * @param y the y position of the window, {@code SDL_WINDOWPOS_CENTERED}, or
         *          {@code SDL_WINDOWPOS_UNDEFINED}
         * @param w the width of the window, in screen coordinates
         * @param h the height of the window, in screen coordinates
         * @param flags 0, or one or more SDL_WindowFlags OR'd together
         * @return the window that was created or NULL on failure; call
         *         SDL_GetError() for more information.
         *
         * @since This function is available since SDL 2.0.0.
         *
         * @see #SDL_CreateWindowFrom
         * @see #SDL_DestroyWindow
         */
        public SDL_Window SDL_CreateWindow(final byte[] title, int x, int y, int w, int h, int flags);

        /**
         * Like {@link #SDL_CreateWindow(byte[], int, int, int, int, int)}, but handles UTF-8 encoding for you.
         *
         * @see #SDL_CreateWindow(byte[], int, int, int, int, int)
         */
        default public SDL_Window SDL_CreateWindow(final String title, int x, int y, int w, int h, int flags) {
            return SDL_CreateWindow(Util.toByteArray(title), x, y, w, h, flags);
        }

        /**
         * <p>Get a window from a stored ID.</p>
         *
         * <p>The numeric ID is what SDL_WindowEvent references, and is necessary to map
         * these events to specific SDL_Window objects.</p>
         *
         * @param id the ID of the window
         * @return the window associated with {@code id} or NULL if it doesn't exist; call
         *         SDL_GetError() for more information.
         *
         * @see #SDL_GetWindowID
         */
        public SDL_Window SDL_GetWindowFromID(int id);

        /**
         * Get the window flags.
         *
         * @param window the window to query
         * @return a mask of the SDL_WindowFlags associated with {@code window}
         *
         * @see #SDL_CreateWindow
         * @see #SDL_HideWindow
         * @see #SDL_MaximizeWindow
         * @see #SDL_MinimizeWindow
         * @see #SDL_SetWindowFullscreen
         * @see #SDL_SetWindowGrab
         * @see #SDL_ShowWindow
         */
        public int SDL_GetWindowFlags(SDL_Window window);

        /**
         * <p>Set the title of a window.</p>
         *
         * <p>This string is expected to be in UTF-8 encoding.</p>
         *
         * @param window the window to change
         * @param title the desired window title in UTF-8 format
         *
         * @see #SDL_GetWindowTitle
         */
        public void SDL_SetWindowTitle(SDL_Window window, byte[] title);

        /**
         * Like {@link #SDL_SetWindowTitle(SDL_Window, byte[])}, but handles UTF-8 encoding for you.
         *
         * @see #SDL_SetWindowTitle(SDL_Window, byte[])
         */
        default public void SDL_SetWindowTitle(SDL_Window window, String title) {
            SDL_SetWindowTitle(window, Util.toByteArray(title));
        }

        /**
         * <p>Set the icon for a window.</p>
         *
         * @param window the window to change
         * @param icon an SDL_Surface structure containing the icon for the window
         */
        public void SDL_SetWindowIcon(SDL_Window window, SDL_Surface icon);

        /**
         * <p>Set the position of a window.</p>
         *
         * <p>The window coordinate origin is the upper left of the display.</p>
         *
         * @param window the window to reposition
         * @param x the x coordinate of the window in screen coordinates, or
         *          {@code SDL_WINDOWPOS_CENTERED} or {@code SDL_WINDOWPOS_UNDEFINED}
         * @param y the y coordinate of the window in screen coordinates, or
         *          {@code SDL_WINDOWPOS_CENTERED} or {@code SDL_WINDOWPOS_UNDEFINED}
         *
         * @see #SDL_GetWindowPosition
         */
        public void SDL_SetWindowPosition(SDL_Window window, int x, int y);

        /**
         * <p>Set a window's fullscreen state.</p>
         *
         * <p>{@code flags} may be {@code SDL_WINDOW_FULLSCREEN}, for "real" fullscreen with a
         * videomode change; {@code SDL_WINDOW_FULLSCREEN_DESKTOP} for "fake" fullscreen
         * that takes the size of the desktop; and 0 for windowed mode.</p>
         *
         * @param window the window to change
         * @param flags {@code SDL_WINDOW_FULLSCREEN}, {@code SDL_WINDOW_FULLSCREEN_DESKTOP} or 0
         * @return 0 on success or a negative error code on failure; call
         *         SDL_GetError() for more information.
         *
         * @since This function is available since SDL 2.0.0.
         *
         * @see #SDL_GetWindowDisplayMode
         * @see #SDL_SetWindowDisplayMode
         */
        public int SDL_SetWindowFullscreen(SDL_Window window, int flags);

        /**
         * <p>Get the SDL surface associated with the window.</p>
         *
         * <p>A new surface will be created with the optimal format for the window, if
         * necessary. This surface will be freed when the window is destroyed. Do not
         * free this surface.</p>
         *
         * <p>This surface will be invalidated if the window is resized. After resizing a
         * window this function must be called again to return a valid surface.</p>
         *
         * <p>You may not combine this with 3D or the rendering API on this window.</p>
         *
         * <p>This function is affected by {@code SDL_HINT_FRAMEBUFFER_ACCELERATION}.</p>
         *
         * @param window the window to query
         * @return the surface associated with the window, or NULL on failure; call
         *         SDL_GetError() for more information.
         *
         * @see #SDL_UpdateWindowSurface
         * @see #SDL_UpdateWindowSurfaceRects
         */
        public SDL_Surface SDL_GetWindowSurface(SDL_Window window);

        /**
         * <p>Copy the window surface to the screen.</p>
         *
         * <p>This is the function you use to reflect any changes to the surface on the
         * screen.</p>
         *
         * <p>This function is equivalent to the SDL 1.2 API SDL_Flip().</p>
         *
         * @param window the window to update
         * @return 0 on success or a negative error code on failure; call
         *         SDL_GetError() for more information.
         *
         * @see #SDL_GetWindowSurface
         * @see #SDL_UpdateWindowSurfaceRects
         */
        public int SDL_UpdateWindowSurface(SDL_Window window);

        /**
         * <p>Get the position of a window.</p>
         *
         * <p>If you do not need the value for one of the positions a NULL may be passed
         * in the {@code x} or {@code y} parameter.</p>
         *
         * @param window the window to query
         * @param x a pointer filled in with the x position of the window, in screen
         *          coordinates, may be NULL
         * @param y a pointer filled in with the y position of the window, in screen
         *          coordinates, may be NULL
         *
         * @see #SDL_SetWindowPosition
         */
        public void SDL_GetWindowPosition(SDL_Window window, IntByReference x, IntByReference y);

        /**
         * <p>Set the size of a window's client area.</p>
         *
         * <p>The window size in screen coordinates may differ from the size in pixels,
         * if the window was created with {@code SDL_WINDOW_ALLOW_HIGHDPI} on a platform
         * with high-dpi support (e.g. iOS or macOS). Use SDL_GL_GetDrawableSize() or
         * SDL_GetRendererOutputSize() to get the real client area size in pixels.</p>
         *
         * <p>Fullscreen windows automatically match the size of the display mode, and
         * you should use SDL_SetWindowDisplayMode() to change their size.</p>
         *
         * @param window the window to change
         * @param w the width of the window in pixels, in screen coordinates, must be
         *          > 0
         * @param h the height of the window in pixels, in screen coordinates, must be
         *          > 0
         *
         * @see #SDL_GetWindowSize
         * @see #SDL_SetWindowDisplayMode
         */
        public void SDL_SetWindowSize(SDL_Window window, int w, int h);

        /**
         * <p>Destroy a window.</p>
         *
         * <p>If {@code window} is NULL, this function will return immediately after setting
         * the SDL error message to "Invalid window". See SDL_GetError().</p>
         *
         * @param window the window to destroy
         *
         * @see #SDL_CreateWindow
         * @see #SDL_CreateWindowFrom
         */
        public void SDL_DestroyWindow(SDL_Window window);

        /**
         * Set the minimum size of a window's client area.
         *
         * @param window the window to change
         * @param min_w the minimum width of the window in pixels
         * @param min_h the minimum height of the window in pixels
         *
         * @see #SDL_GetWindowMinimumSize
         * @see #SDL_SetWindowMaximumSize
         */
        public void SDL_SetWindowMinimumSize(SDL_Window window, int min_w, int min_h);

        /**
         * <p>Set the border state of a window.</p>
         *
         * <p>This will add or remove the window's {@code SDL_WINDOW_BORDERLESS} flag and add
         * or remove the border from the actual window. This is a no-op if the
         * window's border already matches the requested state.</p>
         *
         * <p>You can't change the border state of a fullscreen window.</p>
         *
         * @param window the window of which to change the border state
         * @param bordered SDL_FALSE to remove border, SDL_TRUE to add border
         *
         * @since This function is available since SDL 2.0.0.
         *
         * @see #SDL_GetWindowFlags
         */
        public void SDL_SetWindowBordered(SDL_Window window, boolean bordered);

        /**
         * <p>Set the user-resizable state of a window.</p>
         *
         * <p>This will add or remove the window's {@code SDL_WINDOW_RESIZABLE} flag and
         * allow/disallow user resizing of the window. This is a no-op if the window's
         * resizable state already matches the requested state.</p>
         *
         * <p>You can't change the resizable state of a fullscreen window.</p>
         *
         * @param window the window of which to change the resizable state
         * @param resizable SDL_TRUE to allow resizing, SDL_FALSE to disallow
         *
         * @since This function is available since SDL 2.0.5.
         *
         * @see #SDL_GetWindowFlags
         */
        public void SDL_SetWindowResizable(SDL_Window window, boolean resizable);

        /**
         * Show a window.
         *
         * @param window the window to show
         *
         * @see #SDL_HideWindow
         * @see #SDL_RaiseWindow
         */
        public void SDL_ShowWindow(SDL_Window window);

        /**
         * Hide a window.
         *
         * @param window the window to hide
         *
         * @see #SDL_ShowWindow
         */
        public void SDL_HideWindow(SDL_Window window);

        /**
         * <p>Get information about the desktop's display mode.</p>
         *
         * <p>There's a difference between this function and SDL_GetCurrentDisplayMode()
         * when SDL runs fullscreen and has changed the resolution. In that case this
         * function will return the previous native display mode, and not the current
         * display mode.</p>
         *
         * @param displayIndex the index of the display to query
         * @param mode an SDL_DisplayMode structure filled in with the current display
         *             mode
         * @return 0 on success or a negative error code on failure; call
         *         SDL_GetError() for more information.
         *
         * @see #SDL_GetCurrentDisplayMode
         * @see #SDL_GetDisplayMode
         * @see #SDL_SetWindowDisplayMode
         */
        public int SDL_GetDesktopDisplayMode(int displayIndex, SDL_DisplayMode mode);

        /**
         * <p>Set the gamma ramp for the display that owns a given window.</p>
         *
         * <p>Set the gamma translation table for the red, green, and blue channels of
         * the video hardware. Each table is an array of 256 16-bit quantities,
         * representing a mapping between the input and output for that channel. The
         * input is the index into the array, and the output is the 16-bit gamma value
         * at that index, scaled to the output color precision.</p>
         *
         * <p>Despite the name and signature, this method sets the gamma ramp of the
         * entire display, not an individual window. A window is considered to be
         * owned by the display that contains the window's center pixel. (The index of
         * this display can be retrieved using SDL_GetWindowDisplayIndex().) The gamma
         * ramp set will not follow the window if it is moved to another display.</p>
         *
         * @param window the window used to select the display whose gamma ramp will
         *               be changed
         * @param red a 256 element array of 16-bit quantities representing the
         *            translation table for the red channel, or NULL
         * @param green a 256 element array of 16-bit quantities representing the
         *              translation table for the green channel, or NULL
         * @param blue a 256 element array of 16-bit quantities representing the
         *             translation table for the blue channel, or NULL
         * @return 0 on success or a negative error code on failure; call
         *         SDL_GetError() for more information.
         *
         * @see #SDL_GetWindowGammaRamp
         */
        public int SDL_SetWindowGammaRamp(SDL_Window window, Pointer red, Pointer green, Pointer blue);

        /**
         * <p>Get an OpenGL function by name.</p>
         *
         * <p>If the GL library is loaded at runtime with SDL_GL_LoadLibrary(), then all
         * GL functions must be retrieved this way. Usually this is used to retrieve
         * function pointers to OpenGL extensions.</p>
         *
         * <p>There are some quirks to looking up OpenGL functions that require some
         * extra care from the application. If you code carefully, you can handle
         * these quirks without any platform-specific code, though:</p>
         * <ul>
         *   <li>On Windows, function pointers are specific to the current GL context;
         *   this means you need to have created a GL context and made it current
         *   before calling SDL_GL_GetProcAddress(). If you recreate your context or
         *   create a second context, you should assume that any existing function
         *   pointers aren't valid to use with it. This is (currently) a
         *   Windows-specific limitation, and in practice lots of drivers don't suffer
         *   this limitation, but it is still the way the wgl API is documented to
         *   work and you should expect crashes if you don't respect it. Store a copy
         *   of the function pointers that comes and goes with context lifespan.</li>
         *   <li>On X11, function pointers returned by this function are valid for any
         *   context, and can even be looked up before a context is created at all.
         *   This means that, for at least some common OpenGL implementations, if you
         *   look up a function that doesn't exist, you'll get a non-NULL result that
         *   is <i>NOT</i> safe to call. You must always make sure the function is actually
         *   available for a given GL context before calling it, by checking for the
         *   existence of the appropriate extension with SDL_GL_ExtensionSupported(),
         *   or verifying that the version of OpenGL you're using offers the function
         *   as core functionality.</li>
         *   <li>Some OpenGL drivers, on all platforms, <i>will</i> return NULL if a function
         *   isn't supported, but you can't count on this behavior. Check for
         *   extensions you use, and if you get a NULL anyway, act as if that
         *   extension wasn't available. This is probably a bug in the driver, but you
         *   can code defensively for this scenario anyhow.</li>
         *   <li>Just because you're on Linux/Unix, don't assume you'll be using X11.
         *   Next-gen display servers are waiting to replace it, and may or may not
         *   make the same promises about function pointers.</li>
         *   <li>OpenGL function pointers must be declared {@code APIENTRY} as in the example
         *   code. This will ensure the proper calling convention is followed on
         *   platforms where this matters (Win32) thereby avoiding stack corruption.</li>
         * </ul>
         *
         * @param proc the name of an OpenGL function
         * @return a pointer to the named OpenGL function. The returned pointer
         *         should be cast to the appropriate function signature.
         *
         * @see #SDL_GL_ExtensionSupported
         * @see #SDL_GL_LoadLibrary
         * @see #SDL_GL_UnloadLibrary
         */
        public Pointer SDL_GL_GetProcAddress(final String proc);

        /**
         * <p>Set an OpenGL window attribute before window creation.</p>
         *
         * <p>This function sets the OpenGL attribute {@code attr} to {@code value}. The requested
         * attributes should be set before creating an OpenGL window. You should use
         * SDL_GL_GetAttribute() to check the values after creating the OpenGL
         * context, since the values obtained can differ from the requested ones.</p>
         *
         * @param attr an SDL_GLattr enum value specifying the OpenGL attribute to set
         * @param value the desired value for the attribute
         * @return 0 on success or a negative error code on failure; call
         *         SDL_GetError() for more information.
         *
         * @see #SDL_GL_GetAttribute
         * @see #SDL_GL_ResetAttributes
         */
        public int SDL_GL_SetAttribute(int attr, int value);

        /**
         * <p>Create an OpenGL context for an OpenGL window, and make it current.</p>
         *
         * <p>Windows users new to OpenGL should note that, for historical reasons, GL
         * functions added after OpenGL version 1.1 are not available by default.
         * Those functions must be loaded at run-time, either with an OpenGL
         * extension-handling library or with SDL_GL_GetProcAddress() and its related
         * functions.</p>
         *
         * <p>SDL_GLContext is an alias for {@code void *}. It's opaque to the application.</p>
         *
         * @param window the window to associate with the context
         * @return the OpenGL context associated with {@code window} or NULL on error; call
         *         SDL_GetError() for more details.
         *
         * @see #SDL_GL_DeleteContext
         * @see #SDL_GL_MakeCurrent
         */
        public Pointer SDL_GL_CreateContext(SDL_Window window);

        /**
         * <p>Set up an OpenGL context for rendering into an OpenGL window.</p>
         *
         * <p>The context must have been created with a compatible window.</p>
         *
         * @param window the window to associate with the context
         * @param context the OpenGL context to associate with the window
         * @return 0 on success or a negative error code on failure; call
         *         SDL_GetError() for more information.
         *
         * @see #SDL_GL_CreateContext
         */
        public int SDL_GL_MakeCurrent(SDL_Window window, Pointer context);

        /**
         * Delete an OpenGL context.
         *
         * @param context the OpenGL context to be deleted
         *
         * @see #SDL_GL_CreateContext
         */
        public void SDL_GL_DeleteContext(Pointer context);

        /**
         * <p>Set the swap interval for the current OpenGL context.</p>
         *
         * <p>Some systems allow specifying -1 for the interval, to enable adaptive
         * vsync. Adaptive vsync works the same as vsync, but if you've already missed
         * the vertical retrace for a given frame, it swaps buffers immediately, which
         * might be less jarring for the user during occasional framerate drops. If an
         * application requests adaptive vsync and the system does not support it,
         * this function will fail and return -1. In such a case, you should probably
         * retry the call with 1 for the interval.</p>
         *
         * <p>Adaptive vsync is implemented for some glX drivers with
         * GLX_EXT_swap_control_tear:</p>
         *
         * <p>https://www.opengl.org/registry/specs/EXT/glx_swap_control_tear.txt</p>
         *
         * <p>and for some Windows drivers with WGL_EXT_swap_control_tear:</p>
         *
         * <p>https://www.opengl.org/registry/specs/EXT/wgl_swap_control_tear.txt</p>
         *
         * <p>Read more on the Khronos wiki:
         * https://www.khronos.org/opengl/wiki/Swap_Interval#Adaptive_Vsync</p>
         *
         * @param interval 0 for immediate updates, 1 for updates synchronized with
         *                 the vertical retrace, -1 for adaptive vsync
         * @return 0 on success or -1 if setting the swap interval is not supported;
         *         call SDL_GetError() for more information.
         *
         * @since This function is available since SDL 2.0.0.
         *
         * @see #SDL_GL_GetSwapInterval
         */
        public int SDL_GL_SetSwapInterval(int interval);

        /**
         * <p>Update a window with OpenGL rendering.</p>
         *
         * <p>This is used with double-buffered OpenGL contexts, which are the default.</p>
         *
         * <p>On macOS, make sure you bind 0 to the draw framebuffer before swapping the
         * window, otherwise nothing will happen. If you aren't using
         * glBindFramebuffer(), this is the default and you won't have to do anything
         * extra.</p>
         *
         * @param window the window to change
         */
        public void SDL_GL_SwapWindow(SDL_Window window);
        //#endregion
    }

    public static final class OpenGL {
        @FunctionalInterface
        public static interface GL_glViewport_Func extends Callback {
            public void invoke(int arg0, int arg1, int arg2, int arg3);
        }
    }

    public static final class Util {
        public static <T extends Structure> T copyStructure(Class<T> type, T struct) {
            int size = struct.size();
            Memory mem = new Memory(size);
            byte[] data = new byte[size];
            struct.getPointer().read(0, data, 0, size);
            mem.write(0, data, 0, size);
            return Structure.newInstance(type, mem);
        }

        public static <T extends Structure> void copyStructureInPlace(T from, T to) {
            int size = from.size();
            byte[] data = new byte[size];
            from.getPointer().read(0, data, 0, size);
            to.getPointer().write(0, data, 0, size);
        }

        public static final byte[] toByteArray(String s, Charset charset) {
            byte[] base = s.getBytes(charset);
            byte[] result = Arrays.copyOf(base, base.length + 1);
            result[base.length] = 0;
            return result;
        }

        public static final byte[] toByteArray(String s) {
            return toByteArray(s, StandardCharsets.UTF_8);
        }
    }

    private static final Throwable FAIL_CAUSE;
    private static final SDL2Library SDL2;

    static {
        Throwable failCause = null;
        SDL2Library sdl2 = null;
        try {
            sdl2 = Native.load("SDL2", SDL2Library.class);
        } catch (UnsatisfiedLinkError e) {
            if (Platform.isWindows()) {
                String resource = Platform.is64Bit() ? "SDL2-x64.dll" : "SDL2-x86.dll";
                File destFile = new File("SDL2.dll").getAbsoluteFile();
                try {
                    Files.copy(LowLevel.class.getResourceAsStream(resource), destFile.toPath());
                } catch (IOException e1) {
                    failCause = e1;
                }
                if (failCause == null) { // Success
                    System.setProperty("jna.library.path", destFile.getAbsolutePath());
                    try {
                        sdl2 = Native.load("SDL2", SDL2Library.class);
                    } catch (UnsatisfiedLinkError e2) {
                        failCause = e2;
                    }
                }
            } else {
                failCause = e;
            }
        }
        FAIL_CAUSE = failCause;
        SDL2 = sdl2;
    }

    public static SDL2Library getInstance() {
        if (SDL2 == null) {
            throw new RuntimeException("Couldn't find SDL2 library", FAIL_CAUSE);
        }
        return SDL2;
    }
}
