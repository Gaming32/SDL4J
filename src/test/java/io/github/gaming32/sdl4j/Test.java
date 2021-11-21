package io.github.gaming32.sdl4j;

import io.github.gaming32.sdl4j.math.Vector2;
import io.github.gaming32.sdl4j.sdl_enums.SDL_EventType;

public class Test {
    public static void main(String[] args) {
        SDL4J.init();
        try {
            Surface screen = Display.setMode(new Vector2(640, 480));
            boolean running = true;
            while (running) {
                for (Event event : Event.get()) {
                    System.out.println(event);
                    if (event.type == SDL_EventType.QUIT) {
                        running = false;
                    }
                }
            }
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        } finally {
            SDL4J.quit();
        }
    }
}
