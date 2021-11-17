package io.github.gaming32.sdl4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public final class LowLevel {
    public static interface SDL2Library extends Library {
        // SDL_platform.h
        /**
         * <p>Get the name of the platform.</p>
         *
         * <p>Here are the names returned for some (but not all) supported platforms:</p>
         *
         * <ul>
         *   <li>{@code "Windows"}</li>
         *   <li>{@code "Mac OS X"}</li>
         *   <li>{@code "Linux"}</li>
         *   <li>{@code "iOS"}</li>
         *   <li>{@code "Android"}</li>
         * </ul>
         *
         * @return the name of the platform. If the correct platform name is not
         *         available, returns a string beginning with the text "Unknown".
         */
        public String SDL_GetPlatform();

        // SDL_stdinc.h
        public Pointer SDL_malloc(long size);
        public Pointer SDL_calloc(long nmemb, long size);
        public Pointer SDL_realloc(Pointer mem, long size);
        public void SDL_free(Pointer mem);

        @FunctionalInterface
        public static interface SDL_malloc_func extends Callback {
            public Pointer malloc(long size);
        }
        @FunctionalInterface
        public static interface SDL_calloc_func extends Callback {
            public Pointer calloc(long nmemb, long size);
        }
        @FunctionalInterface
        public static interface SDL_realloc_func extends Callback {
            public Pointer realloc(Pointer mem, long size);
        }
        @FunctionalInterface
        public static interface SDL_free_func extends Callback {
            public void free(Pointer mem);
        }

        /**
         * Get the current set of SDL memory functions
         */
        public void SDL_GetMemoryFunctions(PointerByReference malloc_func, PointerByReference calloc_func, PointerByReference realloc_func, PointerByReference free_func);

        /**
         * Replace SDL's memory allocation functions with a custom set
         */
        public void SDL_SetMemoryFunctions(SDL_malloc_func malloc_func, SDL_calloc_func calloc_func, SDL_realloc_func realloc_func, SDL_free_func free_func);

        /**
         * Get the number of outstanding (unfreed) allocations
         */
        public int SDL_GetNumAllocations();

        // SDL_main.h
        /**
         * <p>Circumvent failure of SDL_Init() when not using SDL_main() as an entry
         * point.</p>
         *
         * <p>This function is defined in SDL_main.h, along with the preprocessor rule to
         * redefine main() as SDL_main(). Thus to ensure that your main() function
         * will not be changed it is necessary to define SDL_MAIN_HANDLED before
         * including SDL.h.</p>
         *
         * @see LowLevel.SDL2Library#SDL_Init(int)
         */
        public void SDL_SetMainReady();

        /**
         * This can be called to set the application class at startup
         */
        public int SDL_RegisterApp(String name, int style, Pointer hInst);
        public void SDL_UnregisterApp();

        // SDL_assert.h
        /**
         * <p>Try to lock a spin lock by setting it to a non-zero value.</p>
         *
         * <p>***Please note that spinlocks are dangerous if you don't know what you're
         * doing. Please be careful using any sort of spinlock!***</p>
         *
         * @param lock a pointer to a lock variable
         * @return SDL_TRUE if the lock succeeded, SDL_FALSE if the lock is already
         *         held.
         *
         * @see LowLevel.SDL2Library#SDL_AtomicLock(IntByReference)
         * @see LowLevel.SDL2Library#SDL_AtomicUnlock(IntByReference)
         */
        public boolean SDL_AtomicTryLock(IntByReference lock);

        /**
         * <p>Lock a spin lock by setting it to a non-zero value.</p>
         *
         * <p>***Please note that spinlocks are dangerous if you don't know what you're
         * doing. Please be careful using any sort of spinlock!***</p>
         *
         * @param lock a pointer to a lock variable
         *
         * @see LowLevel.SDL2Library#SDL_AtomicTryLock(IntByReference)
         * @see LowLevel.SDL2Library#SDL_AtomicUnlock(IntByReference)
         */
        public void SDL_AtomicLock(IntByReference lock);

        /**
         * <p>Unlock a spin lock by setting it to 0.</p>
         *
         * <p>Always returns immediately.</p>
         *
         * <p>***Please note that spinlocks are dangerous if you don't know what you're
         * doing. Please be careful using any sort of spinlock!***</p>
         *
         * @param lock a pointer to a lock variable
         *
         * @since This function is available since SDL 2.0.0.
         *
         * @see LowLevel.SDL2Library#SDL_AtomicLock(IntByReference)
         * @see LowLevel.SDL2Library#SDL_AtomicTryLock(IntByReference)
         */
        public void SDL_AtomicUnlock(IntByReference lock);

        /**
         * <p>Memory barriers are designed to prevent reads and writes from being
         * reordered by the compiler and being seen out of order on multi-core CPUs.</p>
         *
         * <p>A typical pattern would be for thread A to write some data and a flag, and
         * for thread B to read the flag and get the data. In this case you would
         * insert a release barrier between writing the data and the flag,
         * guaranteeing that the data write completes no later than the flag is
         * written, and you would insert an acquire barrier between reading the flag
         * and reading the data, to ensure that all the reads associated with the flag
         * have completed.</p>
         *
         * <p>In this pattern you should always see a release barrier paired with an
         * acquire barrier and you should gate the data reads/writes with a single
         * flag variable.</p>
         *
         * <p>For more information on these semantics, take a look at the blog post:
         * http://preshing.com/20120913/acquire-and-release-semantics</p>
         */
        public void SDL_MemoryBarrierReleaseFunction();
        /**
         * @see LowLevel.SDL2Library#SDL_MemoryBarrierReleaseFunction()
         */
        public void SDL_MemoryBarrierAcquireFunction();

        /**
         * A type representing an atomic integer value.  It is a struct
         * so people don't accidentally use numeric operations on it.
         */
        @Structure.FieldOrder({ "value" })
        public static class SDL_atomic_t extends Structure {
            public int value;
        }

        /**
         * <p>Set an atomic variable to a new value if it is currently an old value.</p>
         *
         * <p>***Note: If you don't know what this function is for, you shouldn't use
         * it!***</p>
         *
         * @param a a pointer to an SDL_atomic_t variable to be modified
         * @param oldval the old value
         * @param newval the new value
         * @return SDL_TRUE if the atomic variable was set, SDL_FALSE otherwise.
         *
         * @since This function is available since SDL 2.0.0.
         *
         * @see LowLevel.SDL2Library#SDL_AtomicCASPtr(PointerByReference, Pointer, Pointer)
         * @see LowLevel.SDL2Library#SDL_AtomicGet(SDL_atomic_t)
         * @see LowLevel.SDL2Library#SDL_AtomicSet(SDL_atomic_t, int)
         */
        public boolean SDL_AtomicCAS(SDL_atomic_t a, int oldval, int newval);

        /**
         * <p>Set an atomic variable to a value.</p>
         *
         * <p>This function also acts as a full memory barrier.</p>
         *
         * <p>***Note: If you don't know what this function is for, you shouldn't use
         * it!***</p>
         *
         * @param a a pointer to an SDL_atomic_t variable to be modified
         * @param v the desired value
         * @return the previous value of the atomic variable.
         *
         * @see LowLevel.SDL2Library#SDL_AtomicGet(SDL_atomic_t)
         */
        public int SDL_AtomicSet(SDL_atomic_t a, int v);

        /**
         * <p>Get the value of an atomic variable.</p>
         *
         * <p>***Note: If you don't know what this function is for, you shouldn't use
         * it!***</p>
         *
         * @param a a pointer to an SDL_atomic_t variable
         * @return the current value of an atomic variable.
         *
         * @see LowLevel.SDL2Library#SDL_AtomicSet(SDL_atomic_t, int)
         */
        public int SDL_AtomicGet(SDL_atomic_t a);

        /**
         * <p>Add to an atomic variable.</p>
         *
         * <p>This function also acts as a full memory barrier.</p>
         *
         * <p>***Note: If you don't know what this function is for, you shouldn't use
         * it!***</p>
         *
         * @param a a pointer to an SDL_atomic_t variable to be modified
         * @param v the desired value to add
         * @return the previous value of the atomic variable.
         */
        public int SDL_AtomicAdd(SDL_atomic_t a, int v);

        /**
         * <p>Set a pointer to a new value if it is currently an old value.</p>
         *
         * <p>***Note: If you don't know what this function is for, you shouldn't use
         * it!***</p>
         *
         * @param a a pointer to a pointer
         * @param oldval the old pointer value
         * @param newval the new pointer value
         * @return SDL_TRUE if the pointer was set, SDL_FALSE otherwise.
         *
         * @since This function is available since SDL 2.0.0.
         *
         * @see LowLevel.SDL2Library#SDL_AtomicCAS(SDL_atomic_t, int, int)
         * @see LowLevel.SDL2Library#SDL_AtomicGetPtr(PointerByReference)
         * @see LowLevel.SDL2Library#SDL_AtomicSetPtr(PointerByReference, Pointer)
         */
        public boolean SDL_AtomicCASPtr(PointerByReference a, Pointer oldval, Pointer newval);

        /**
         * <p>Set a pointer to a value atomically.</p>
         *
         * <p>***Note: If you don't know what this function is for, you shouldn't use
         * it!***</p>
         *
         * @param a a pointer to a pointer
         * @param v the desired pointer value
         * @return the previous value of the pointer.
         *
         * @see LowLevel.SDL2Library#SDL_AtomicCASPtr(PointerByReference, Pointer, Pointer)
         * @see LowLevel.SDL2Library#SDL_AtomicGetPtr(PointerByReference)
         */
        public Pointer SDL_AtomicSetPtr(PointerByReference a, Pointer v);

        /**
         * <p>Get the value of a pointer atomically.</p>
         *
         * <p>***Note: If you don't know what this function is for, you shouldn't use
         * it!***</p>
         *
         * @param a a pointer to a pointer
         * @return the current value of a pointer.
         *
         * @see LowLevel.SDL2Library#SDL_AtomicCASPtr(PointerByReference, Pointer, Pointer)
         * @see LowLevel.SDL2Library#SDL_AtomicSetPtr(PointerByReference, Pointer)
         */
        public Pointer SDL_AtomicGetPtr(PointerByReference a);

        // SDL_error.h
        /**
         * <p>Set the SDL error message for the current thread.</p>
         *
         * <p>Calling this function will replace any previous error message that was set.</p>
         *
         * <p>This function always returns -1, since SDL frequently uses -1 to signify an
         * failing result, leading to this idiom:</p>
         *
         * <pre>
         * if (error_code) {
         *     return SDL_SetError("This operation has failed: %d", error_code);
         * }
         * </pre>
         *
         * @param fmt a printf()-style message format string
         * @param args additional parameters matching % tokens in the `fmt` string, if
         *             any
         * @return always -1.
         *
         * @see LowLevel.SDL2Library#SDL_ClearError()
         * @see LowLevel.SDL2Library#SDL_GetError()
         */
        public int SDL_SetError(String fmt, Object... args);

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

        /**
         * <p>Get the last error message that was set for the current thread.</p>
         *
         * <p>This allows the caller to copy the error string into a provided buffer, but
         * otherwise operates exactly the same as SDL_GetError().</p>
         *
         * @param errstr A buffer to fill with the last error message that was set for
         *               the current thread
         * @param maxlen The size of the buffer pointed to by the errstr parameter
         * @return the pointer passed in as the `errstr` parameter.
         *
         * @see LowLevel.SDL2Library#SDL_GetError()
         */
        public byte[] SDL_GetErrorMsg(byte[] errstr, int maxlen);

        /**
         * <p>Clear any previous error message for this thread.</p>
         *
         * @see LowLevel.SDL2Library#SDL_GetError()
         * @see LowLevel.SDL2Library#SDL_SetError(String, Object...)
         */
        public void SDL_ClearError();

        public static final class SDL_errorcode {
            public static final int SDL_ENOMEM = 0;
            public static final int SDL_EFREAD = 1;
            public static final int SDL_EFWRITE = 2;
            public static final int SDL_EFSEEK = 3;
            public static final int SDL_UNSUPPORTED = 4;
            public static final int SDL_LASTERROR = 5;
        }
        public int SDL_Error(int code);
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
