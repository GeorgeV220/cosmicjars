package com.georgev22.cosmicjars.helpers;

import java.util.Locale;

/**
 * The Platform class provides functionality to determine the current operating system (OS).
 * It includes an enumeration {@link OS} representing different operating systems and a static
 * method {@link #getCurrentOS()} to retrieve the current OS.
 */
public class Platform {

    /**
     * Enumeration representing different operating systems.
     */
    public enum OS {
        /**
         * Windows operating system.
         */
        WINDOWS("windows"),

        /**
         * Linux operating system.
         */
        LINUX("linux"),

        /**
         * macOS operating system.
         */
        MACOS("mac"),

        /**
         * Unknown operating system.
         */
        UNKNOWN("unknown");

        private final String os;

        OS(String os) {
            this.os = os;
        }

        /**
         * Get the string representation of the operating system.
         *
         * @return The string representation of the operating system.
         */
        public String getOS() {
            return this.os;
        }
    }

    private static final OS currentOS;

    static {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (osName.contains("win")) {
            currentOS = OS.WINDOWS;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            currentOS = OS.LINUX;
        } else if (osName.contains("mac")) {
            currentOS = OS.MACOS;
        } else {
            currentOS = OS.UNKNOWN;
        }
    }

    /**
     * Get the current operating system.
     *
     * @return The current operating system.
     */
    public static OS getCurrentOS() {
        return currentOS;
    }

}