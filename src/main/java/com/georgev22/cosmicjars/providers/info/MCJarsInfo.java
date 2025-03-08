package com.georgev22.cosmicjars.providers.info;

import java.util.Map;

/**
 * Represents the response data structure from the MCJars API.
 */
public class MCJarsInfo {

    private boolean success;
    private Map<String, BuildInfo> builds;

    /**
     * Checks if the API request was successful.
     *
     * @return true if the request was successful, false otherwise.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Retrieves the builds available for different versions.
     *
     * @return A map where the key is the version and the value is the corresponding {@link BuildInfo}.
     */
    public Map<String, BuildInfo> getBuilds() {
        return builds;
    }

    /**
     * Represents build information for a specific version.
     */
    public static class BuildInfo {
        private String type;
        private boolean supported;
        private int java;
        private String created;
        private int builds;
        private LatestBuild latest;

        /**
         * Gets the type of the server (e.g., Paper, Purpur).
         *
         * @return The type of the server.
         */
        public String getType() {
            return type;
        }

        /**
         * Checks if the build is supported.
         *
         * @return true if supported, false otherwise.
         */
        public boolean isSupported() {
            return supported;
        }

        /**
         * Gets the required Java version for this build.
         *
         * @return The Java version required.
         */
        public int getJava() {
            return java;
        }

        /**
         * Gets the creation date of this build.
         *
         * @return The creation date as a String.
         */
        public String getCreated() {
            return created;
        }

        /**
         * Gets the number of builds available for this version.
         *
         * @return The number of builds.
         */
        public int getBuilds() {
            return builds;
        }

        /**
         * Gets the latest build information.
         *
         * @return The {@link LatestBuild} details.
         */
        public LatestBuild getLatest() {
            return latest;
        }
    }

    /**
     * Represents details of the latest build.
     */
    public static class LatestBuild {
        private int id;
        private String type;
        private String versionId;
        private Integer projectVersionId;
        private int buildNumber;
        private String name;
        private boolean experimental;
        private String jarUrl;
        private long jarSize;

        /**
         * Gets the unique ID of the build.
         *
         * @return The build ID.
         */
        public int getId() {
            return id;
        }

        /**
         * Gets the type of the build.
         *
         * @return The type of the build.
         */
        public String getType() {
            return type;
        }

        /**
         * Gets the version ID of this build.
         *
         * @return The version ID.
         */
        public String getVersionId() {
            return versionId;
        }

        /**
         * Gets the project version ID if available.
         *
         * @return The project version ID, or null if not applicable.
         */
        public Integer getProjectVersionId() {
            return projectVersionId;
        }

        /**
         * Gets the build number.
         *
         * @return The build number.
         */
        public int getBuildNumber() {
            return buildNumber;
        }

        /**
         * Gets the name of the build.
         *
         * @return The name of the build.
         */
        public String getName() {
            return name;
        }

        /**
         * Checks if the build is marked as experimental.
         *
         * @return true if the build is experimental, false otherwise.
         */
        public boolean isExperimental() {
            return experimental;
        }

        /**
         * Gets the URL to download the JAR file for this build.
         *
         * @return The JAR file URL.
         */
        public String getJarUrl() {
            return jarUrl;
        }

        /**
         * Gets the size of the JAR file in bytes.
         *
         * @return The size of the JAR file.
         */
        public long getJarSize() {
            return jarSize;
        }
    }
}
