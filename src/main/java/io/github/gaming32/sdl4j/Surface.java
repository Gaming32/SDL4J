package io.github.gaming32.sdl4j;

import java.util.List;

import io.github.gaming32.sdl4j.LowLevel.SDL2Library;
import io.github.gaming32.sdl4j.LowLevel.SDL2Library.SDL_Surface;

public final class Surface {
    protected SDL_Surface surf;
    protected boolean owner;
    protected List<Object> locks;

    protected Surface(SDL_Surface surface) {
        this(surface, true);
    }

    protected Surface(SDL_Surface s, boolean owner) {
        setSurface(s, owner);
    }

    protected void setSurface(SDL_Surface s, boolean owner) {
        if (s == null) {
            SDLException.throwNew();
        }
        if (s.equals(this.surf)) {
            this.owner = owner;
            return;
        }

        cleanup();
        this.surf = s;
        this.owner = owner;
    }

    protected void cleanup() {
        SDL2Library lib = LowLevel.getInstance();
        if (this.surf != null && this.owner) {
            lib.SDL_FreeSurface(this.surf);
            this.surf = null;
        }
        if (this.locks != null) {
            this.locks = null;
        }
        this.owner = false;
    }

    @Override
    protected void finalize() throws Throwable {
        cleanup();
    }
}
