package com.matyrobbrt.gradle.pngoptimiser.util;

import java.util.Locale;

public enum Os {
    LINUX("linux"),
    WINDOWS("win"),
    MAC("mac");

    private final String name;

    Os(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Os getOs() {
        final var osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        for (final var type : Os.values())
            if (osName.contains(type.name))
                return type;
        throw new UnsupportedOperationException("Unknown OS: " + osName);
    }
}
