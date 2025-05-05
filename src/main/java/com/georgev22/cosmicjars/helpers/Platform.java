package com.georgev22.cosmicjars.helpers;

import java.util.Locale;

/**
 * The Platform class provides functionality to determine the current operating system (OS)
 * and architecture. It includes an enumeration {@link OS} representing different operating systems,
 * and static methods {@link #getCurrentOS()} to retrieve the current OS, as well as {@link #getCurrentArchitecture()}
 * to retrieve the current architecture.
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
         * IBM Aix operating system.
         */
        AIX("aix"),

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
    private static final String currentArchitecture;

    static {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (osName.contains("win")) {
            currentOS = OS.WINDOWS;
        } else if (osName.contains("nix") || osName.contains("nux")) {
            currentOS = OS.LINUX;
        } else if (osName.contains("aix")) {
            currentOS = OS.AIX;
        }else if (osName.contains("mac")) {
            currentOS = OS.MACOS;
        } else {
            currentOS = OS.UNKNOWN;
        }

        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
        if (arch.contains("x86_64") || arch.contains("amd64")) {
            currentArchitecture = "x64";
        } else if (arch.contains("x86") || arch.contains("i386") || arch.contains("i686")) {
            currentArchitecture = "x86";
        } else if (arch.contains("aarch64") || arch.contains("arm64")) {
            currentArchitecture = "aarch64";
        } else if (arch.contains("arm")) {
            if (arch.contains("v8")) {
                currentArchitecture = "aarch64";
            } else {
                currentArchitecture = "arm";
            }
        } else if (arch.contains("ppc64")) {
            currentArchitecture = arch.contains("le") ? "ppc64le" : "ppc64";
        } else if (arch.contains("riscv64")) {
            currentArchitecture = "riscv64";
        } else if (arch.contains("s390x")) {
            currentArchitecture = "s390x";
        } else if (arch.contains("sparcv9")) {
            currentArchitecture = "sparcv9";
        } else {
            currentArchitecture = "unknown";
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

    /**
     * Get the current architecture.
     *
     * @return The current architecture (e.g., aarch64, arm, ppc64, ppc64le, riscv64, s390x, sparcv9, x64, x86).
     */
    public static String getCurrentArchitecture() {
        return currentArchitecture;
    }
}
