package io.github.gaming32.sdl4j;

import io.github.gaming32.sdl4j.math.Vector2;

public class Test {
    public static void main(String[] args) {
        SDL4J.init();
        Surface screen = Display.setMode(new Vector2(640, 480));
        boolean running = true;
        while (running) {
            running = false;
        }
        SDL4J.quit();
    }
}
