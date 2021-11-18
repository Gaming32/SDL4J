package io.github.gaming32.sdl4j;

import io.github.gaming32.sdl4j.LowLevel.SDL2Library;

public class Test {
    public static void main(String[] args) {
        SDL2Library lib = LowLevel.getInstance();
        ImportSuccess success = SDL4J.init();
        System.out.println(success);
        SDL4J.quit();
    }
}
