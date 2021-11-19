package io.github.gaming32.sdl4j.math;

import java.util.Arrays;

public class Vector2 extends Vector {
    public static final Vector2 IDENTITY = new Vector2();

    public final double x, y;

    protected Vector2() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2(double xy) {
        this.x = this.y = xy;
    }

    public Vector2(double[] coords) {
        if (coords.length < 2) {
            throw new IllegalArgumentException("Not enough coords");
        }
        this.x = coords[0];
        this.y = coords[1];
    }

    public Vector2(Vector vec) {
        this(vec.getCoords());
    }

    public Vector2(Vector2 vec) {
        this.x = vec.x;
        this.y = vec.y;
    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Vector2{x=" + x + ", y=" + y + "}";
    }

    @Override
    protected int getDim() {
        return 2;
    }

    @Override
    protected double[] getCoords() {
        return new double[] {x, y};
    }

    protected static boolean check(Vector x) {
        return x instanceof Vector2;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double cross(Vector other) {
        if (this.equals(other)) return 0.0;
        if (!Vector.compatibleCheck(other, 2)) {
            throw new IllegalArgumentException("Vector must be Vector2 or greater");
        }
        double[] otherCoords = Arrays.copyOf(other.getCoords(), 2);
        return (x * otherCoords[1]) - (y * otherCoords[0]);
    }

    @Override
    public Vector2 normalize() {
        double length = length();
        if (length == 0) {
            throw new IllegalStateException("Can't normalize a Vector of length zero");
        }
        return new Vector2(x / length, y / length);
    }

    @Override
    public Vector2 scaleToLength(double length) {
        double oldLength = length();
        if (oldLength < EPSILON) {
            throw new IllegalStateException("Can't scale a Vector of length zero");
        }
        double fraction = length / oldLength;
        return new Vector2(x * fraction, y * fraction);
    }

    @Override
    public Vector2 reflect(Vector normal) {
        return new Vector2(reflectHelper(normal, 2));
    }

    @Override
    public Vector2 lerp(Vector other, double t) {
        return new Vector2(lerpHelper(other, t));
    }

    @Override
    public Vector2 slerp(Vector other, double t) {
        return new Vector2(slerpHelper(other, t));
    }

    @Override
    public Vector2 rotate(double angle) {
        double resultX, resultY;

        angle = angle % TWO_PI;
        if (angle < 0) {
            angle += TWO_PI;
        }

        if ((angle + EPSILON) % PI_2 < 2 * EPSILON) {
            switch ((int)((angle + EPSILON) / PI_2)) {
                case 0: // 0 degrees
                case 4: // 360 degrees
                    resultX = x;
                    resultY = y;
                    break;
                case 1: // 90 degrees
                    resultX = -y;
                    resultY = x;
                    break;
                case 2: // 180 degrees
                    resultX = -x;
                    resultY = -y;
                    break;
                case 3: // 270 degrees
                    resultX = y;
                    resultY = -x;
                    break;
                default:
                    throw new InternalError("This code should never be reached.");
            }
        } else {
            double sinValue = Math.sin(angle);
            double cosValue = Math.cos(angle);

            resultX = cosValue * x - sinValue * y;
            resultY = sinValue * x - cosValue * y;
        }
        return new Vector2(resultX, resultY);
    }

    @Override
    public Vector2 rotateDeg(double angle) {
        return rotate(Math.toRadians(angle));
    }

    @Override
    public double angleTo(Vector other) {
        if (!Vector.compatibleCheck(other, 2)) {
            throw new IllegalArgumentException("Argument must be Vector2 or greater");
        }
        double[] otherCoords = other.getCoords();
        return Math.atan2(otherCoords[1], otherCoords[0]) - Math.atan2(y, x);
    }

    @Override
    public Vector2 project(Vector other) {
        return new Vector2(projectOnto(other));
    }
}
