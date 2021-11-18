package io.github.gaming32.sdl4j.math;

import java.util.Arrays;

public abstract class Vector {
    protected static final double EPSILON = 1e-6;
    protected static final double TWO_PI = Math.PI * 2;
    protected static final double PI_2 = Math.PI / 2;

    protected abstract int getDim();
    protected abstract double[] getCoords();

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Vector)) return false;
        Vector other = (Vector)o;
        return Arrays.equals(getCoords(), other.getCoords());
    }

    @Override
    public int hashCode() {
        double[] coords = getCoords();
        int dim = getDim();
        int result = Double.hashCode(coords[0]);
        for (int i = 1; i < dim; i++) {
            result ^= Double.hashCode(coords[i]);
        }
        return result;
    }

    public double dot(Vector other) {
        return scalarProduct(getCoords(), other.getCoords(), 2);
    }

    public double length() {
        double[] coords = getCoords();
        return Math.sqrt(scalarProduct(coords, coords, getDim()));
    }

    public double lengthSquared() {
        double[] coords = getCoords();
        return scalarProduct(coords, coords, getDim());
    }

    public double magnitude() {
        return length();
    }

    public double magnitudeSquared() {
        return lengthSquared();
    }

    public abstract Vector normalize();

    public boolean isNormalized() {
        double lengthSquared = lengthSquared();
        if (Math.abs(lengthSquared - 1) < EPSILON) {
            return true;
        }
        return false;
    }

    public abstract Vector scaleToLength(double length);

    public abstract Vector reflect(Vector normal);

    public abstract Vector lerp(Vector other, double t);

    public abstract Vector slerp(Vector other, double t);

    public abstract Vector rotate(double angle);

    public abstract Vector rotateDeg(double angle);

    public double distanceTo(Vector other) {
        return Math.sqrt(distanceSquaredTo(other));
    }

    public double distanceSquaredTo(Vector other) {
        if (getDim() > other.getDim()) {
            throw new IllegalArgumentException("Not enough coords");
        }
        double[] thisCoords = getCoords();
        double[] otherCoords = other.getCoords();
        double distanceSquared = 0;
        for (int i = 0; i < getDim(); i++) {
            double dist = otherCoords[i] - thisCoords[i];
            distanceSquared += dist * dist;
        }
        return distanceSquared;
    }

    public abstract double angleTo(Vector other);

    public double angleToDeg(Vector other) {
        return Math.toDegrees(angleTo(other));
    }

    public abstract Vector project(Vector other);

    //#region Helper methods
    protected static double scalarProduct(final double[] coords1, final double[] coords2, int size) {
        double product = 0;
        for (int i = 0; i < size; i++) {
            product += coords1[i] * coords2[i];
        }
        return product;
    }

    protected static boolean check(Vector x) {
        return Vector2.check(x);
    }

    protected static boolean compatibleCheck(Vector obj, int dim) {
        switch (dim) {
            case 2:
                if (Vector2.check(obj)) {
                    return true;
                }
                break;
            /*
            case 3:
                if (Vector3.check(obj)) {
                    return true;
                }
                break;
            */
            default:
                throw new IllegalArgumentException("Wrong internal call to Vector.compatibleCheck");
        }

        double[] coords = obj.getCoords();
        if (coords.length < dim) {
            return false;
        }

        for (int i = 0; i < dim; i++) {
            double tmp = coords[i];
            if (Double.isNaN(tmp)) {
                return false;
            }
        }
        return true;
    }

    protected double[] reflectHelper(Vector normal, int dim) {
        double[] normCoords = normal.getCoords();
        if (normCoords.length < dim) {
            throw new IllegalArgumentException("Not enough coords");
        }

        double normLength = scalarProduct(normCoords, normCoords, dim);
        if (normLength < EPSILON) {
            throw new IllegalArgumentException("Normal must not be of length zero");
        }
        if (normLength != 1) {
            normLength = Math.sqrt(normLength);
            for (int i = 0; i < dim; i++) {
                normCoords[i] /= normLength;
            }
        }

        double[] result = new double[dim];
        double[] srcCoords = getCoords();
        double dotProduct = scalarProduct(result, srcCoords, dim);

        for (int i = 0; i < dim; i++) {
            result[i] = srcCoords[i] - 2 * normCoords[i] * dotProduct;
        }
        return result;
    }

    protected double[] lerpHelper(Vector other, double t) {
        int dim = getDim();
        if (dim > other.getDim()) {
            throw new IllegalArgumentException("Not enough coords");
        }
        if (t < 0 || t > 1) {
            throw new IllegalArgumentException("t must be in range [0, 1]");
        }

        double[] thisCoords = getCoords();
        double[] otherCoords = other.getCoords();
        double[] result = new double[dim];

        for (int i = 0; i < dim; i++) {
            result[i] = thisCoords[i] * (1 - t) + otherCoords[i] * t;
        }
        return result;
    }

    protected double[] slerpHelper(Vector other, double t) {
        int dim = getDim();
        if (dim > other.getDim()) {
            throw new IllegalArgumentException("Not enough coords");
        }
        if (t < 0 || t > 1) {
            throw new IllegalArgumentException("t must be in range [-1, 1]");
        }

        double[] thisCoords = getCoords();
        double[] otherCoords = other.getCoords();

        double length1 = Math.sqrt(scalarProduct(thisCoords, thisCoords, dim));
        double length2 = Math.sqrt(scalarProduct(otherCoords, otherCoords, dim));
        if (length1 < EPSILON || length2 < EPSILON) {
            throw new IllegalArgumentException("Can't slerp with a vector of length zero");
        }
        double tmp = scalarProduct(thisCoords, otherCoords, dim) / (length1 * length2);
        tmp = tmp < -1 ? -1 : (tmp > 1 ? 1 : tmp);
        double angle = Math.acos(tmp);

        if (t < 0) {
            angle -= 2 * Math.PI;
            t = -t;
        }
        if (thisCoords[0] * otherCoords[1] < thisCoords[1] * otherCoords[0]) {
            angle *= -1;
        }

        double[] result = new double[dim];

        if (Math.abs(angle) < EPSILON || Math.abs(Math.abs(angle) - 2 * Math.PI) < EPSILON) {
            for (int i = 0; i < dim; i++) {
                result[i] = thisCoords[i] * (1 - t) + otherCoords[i] * t;
            }
        } else if (Math.abs(Math.abs(angle) - Math.PI) < EPSILON) {
            throw new IllegalArgumentException("SLERP with 180 degrees is undefined");
        } else {
            double f0 = ((length2 - length1) * t + length1) / Math.sin(angle);
            double f1 = Math.sin(angle * (1 - t)) / length1;
            double f2 = Math.sin(angle * t) / length2;
            for (int i = 0; i < dim; i++) {
                result[i] = (thisCoords[i] * f1 + otherCoords[i] * f2) * f0;
            }
        }
        return result;
    }

    protected double[] projectOnto(Vector other) {
        int dim = getDim();
        if (dim > other.getDim()) {
            throw new IllegalArgumentException("Not enough coords");
        }

        double[] otherCoords = other.getCoords();

        double bDotB = scalarProduct(otherCoords, otherCoords, dim);

        if (bDotB < EPSILON) {
            throw new IllegalArgumentException("Cannot project onto a vector with zero length");
        }

        double[] result = new double[dim];

        double aDotB = scalarProduct(getCoords(), otherCoords, dim);
        double factor = aDotB / bDotB;

        for (int i = 0; i < dim; i++) {
            result[i] = otherCoords[i] * factor;
        }
        return result;
    }
    //#endregion
}
