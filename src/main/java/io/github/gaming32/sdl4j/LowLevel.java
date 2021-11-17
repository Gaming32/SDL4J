package io.github.gaming32.sdl4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.Union;

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
         * @see LowLevel.SDL2Library#SDL_InitSubSystem
         * @see LowLevel.SDL2Library#SDL_Quit
         * @see LowLevel.SDL2Library#SDL_SetMainReady
         * @see LowLevel.SDL2Library#SDL_WasInit
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
         * @see LowLevel.SDL2Library#SDL_Init
         * @see LowLevel.SDL2Library#SDL_InitSubSystem
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
         * @see LowLevel.SDL2Library#SDL_Init
         * @see LowLevel.SDL2Library#SDL_Quit
         * @see LowLevel.SDL2Library#SDL_QuitSubSystem
         */
        public int SDL_InitSubSystem(int flags);
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
         * @see LowLevel.SDL2Library#SDL_ClearError()
         * @see LowLevel.SDL2Library#SDL_SetError(String, Object...)
         */
        public String SDL_GetError();
        //#endregion

        //#region SDL_events.h
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
            @SuppressWarnings("unused")
            private int padding1, padding2, padding3;
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
            @SuppressWarnings("unused")
            private int padding1, padding2, padding3;
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
            @SuppressWarnings("unused")
            private byte padding2, padding3;
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
            @SuppressWarnings("unused")
            private byte padding1;
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
            @SuppressWarnings("unused")
            private byte padding1, padding2, padding3;
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
            @SuppressWarnings("unused")
            private byte padding1, padding2, padding3;
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
            @SuppressWarnings("unused")
            private byte padding1, padding2;
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
            @SuppressWarnings("unused")
            private byte padding1, padding2;
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
            @SuppressWarnings("unused")
            private byte padding1, padding2, padding3;
            /** The axis value (range: -32768 to 32767) */
            public short value;
            @SuppressWarnings("unused")
            private short padding4;
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
            @SuppressWarnings("unused")
            private byte padding1, padding2;
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
            @SuppressWarnings("unused")
            private byte padding1, padding2, padding3;
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
            @SuppressWarnings("unused")
            private short padding;
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

            public void setTypeFromID(int type) {
                setType(getClassFromType(type));
            }

            public void setProperType() {
                setTypeFromID((int)this.getTypedValue(int.class));
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
         * A function pointer used for callbacks that watch the event queue.
         *
         * @param userdata what was passed as {@code userdata} to SDL_SetEventFilter()
         *                 or SDL_AddEventWatch, etc
         * @param event the event that triggered the callback
         * @return 1 to permit event to be added to the queue, and 0 to disallow
         *         it. When used with SDL_AddEventWatch, the return value is ignored.
         *
         * @see LowLevel.SDL2Library#SDL_SetEventFilter
         * @see LowLevel.SDL2Library#SDL_AddEventWatch
         */
        @FunctionalInterface
        public static interface SDL_EventFilter {
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
         * @see LowLevel.SDL2Library#SDL_AddEventWatch
         * @see LowLevel.SDL2Library#SDL_EventState
         * @see LowLevel.SDL2Library#SDL_GetEventFilter
         * @see LowLevel.SDL2Library#SDL_PeepEvents
         * @see LowLevel.SDL2Library#SDL_PushEvent
         */
        public void SDL_SetEventFilter(SDL_EventFilter filter, Pointer userdata);
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
            int scancode;
            int sym;
            short mod;
            int unused;
        }
        //#endregion

        //#region SDL_mouse.h
        public static final int SDL_MOUSEWHEEL_NORMAL = 0;
        public static final int SDL_MOUSEWHEEL_FLIPPED = 1;
        //#endregion

        //#region SDL_mutex.h
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
         * @see LowLevel.SDL2Library#SDL_DestroyMutex
         * @see LowLevel.SDL2Library#SDL_LockMutex
         * @see LowLevel.SDL2Library#SDL_TryLockMutex
         * @see LowLevel.SDL2Library#SDL_UnlockMutex
         */
        public Pointer SDL_CreateMutex();
        //#endregion

        //#region SDL_stdinc.h
        public String SDL_getenv(String name);
        public int SDL_setenv(String name, String value, boolean overwrite);
        //#endregion
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
