package io.github.gaming32.sdl4j;

import io.github.gaming32.sdl4j.math.Vector2;

public class Test {
    public static void main(String[] args) {
        Vector2 vec = new Vector2(3, 7);
        System.out.println(vec);
        System.out.println(vec.magnitude());
        System.out.println(vec.magnitudeSquared());
        System.out.println(vec.scaleToLength(0));
        System.out.println((new Vector2(1, 0)).angleToDeg(new Vector2(0, 1)));
        System.out.println((new Vector2(5, 5)).lerp(new Vector2(10, 10), 0.2));

        System.out.println();

        Vector2 normalized = vec.normalize();
        System.out.println(normalized);
        System.out.println(vec.isNormalized());
        System.out.println(normalized.isNormalized());

        // SDL4J.init();
        // Surface screen = Display.setMode(new Vector2(640, 480));
        // boolean running = true;
        // while (running) {
        //     running = false;
        // }
        // SDL4J.quit();
    }
}
