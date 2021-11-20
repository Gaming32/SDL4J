package io.github.gaming32.sdl4j;

import io.github.gaming32.sdl4j.math.Vector2;

public class Test {
    public static void main(String[] args) {
        SDL4J.init();
        try {
            Surface screen = Display.setMode(new Vector2(640, 480));
            Thread.sleep(2500);
            // boolean running = true;
            // while (running) {
            //     running = false;
            // }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            SDL4J.quit();
        }
    }
}
