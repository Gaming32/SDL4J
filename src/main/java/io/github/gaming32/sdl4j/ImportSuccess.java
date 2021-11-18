package io.github.gaming32.sdl4j;

public final class ImportSuccess {
    public final int success, fail;

    public ImportSuccess(int success, int fail) {
        this.success = success;
        this.fail = fail;
    }

    public String toString() {
        return "ImportSuccess{success=" + this.success + ", fail=" + this.fail + "}";
    }
}
