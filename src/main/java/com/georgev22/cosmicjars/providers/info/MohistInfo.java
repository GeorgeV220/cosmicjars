package com.georgev22.cosmicjars.providers.info;

import java.util.Comparator;
import java.util.List;

/**
 * Represents information about the Mohist project and its builds.
 */
public class MohistInfo {
    private final String projectName;
    private final String projectVersion;
    private final List<MohistBuild> builds;

    /**
     * Constructs a MohistInfo object.
     *
     * @param projectName    The name of the project.
     * @param projectVersion The version of the project.
     * @param builds         The list of builds associated with the project.
     */
    public MohistInfo(String projectName, String projectVersion, List<MohistBuild> builds) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.builds = builds;
    }

    /**
     * Retrieves the name of the project.
     *
     * @return The project name.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Retrieves the version of the project.
     *
     * @return The project version.
     */
    public String getProjectVersion() {
        return projectVersion;
    }

    /**
     * Retrieves the list of builds associated with the project.
     *
     * @return The list of builds.
     */
    public List<MohistBuild> getBuilds() {
        return builds;
    }

    /**
     * Retrieves the latest build information from the list of builds.
     *
     * @return The latest build, or null if the list is empty.
     */
    public MohistBuild getLatestBuild() {
        return builds.stream()
                .max(Comparator.comparingLong(MohistBuild::getCreatedAt))
                .orElse(null);
    }


    /**
     * Represents build information for the Mohist project.
     */
    public static class MohistBuild {
        private final int number;
        private final String gitSha;
        private final String forgeVersion;
        private final String fileMd5;
        private final String originUrl;
        private final String url;
        private final long createdAt;

        /**
         * Constructs a MohistBuild object.
         *
         * @param number       The build number.
         * @param gitSha       The Git SHA of the build.
         * @param forgeVersion The Forge version.
         * @param fileMd5      The MD5 hash of the file.
         * @param originUrl    The origin URL of the build.
         * @param url          The URL of the build.
         * @param createdAt    The timestamp when the build was created.
         */
        MohistBuild(int number, String gitSha, String forgeVersion, String fileMd5, String originUrl, String url, long createdAt) {
            this.number = number;
            this.gitSha = gitSha;
            this.forgeVersion = forgeVersion;
            this.fileMd5 = fileMd5;
            this.originUrl = originUrl;
            this.url = url;
            this.createdAt = createdAt;
        }

        /**
         * Get the build number.
         *
         * @return The build number.
         */
        public int getNumber() {
            return number;
        }

        /**
         * Get the Git SHA of the build.
         *
         * @return The Git SHA.
         */
        public String getGitSha() {
            return gitSha;
        }

        /**
         * Get the Forge version.
         *
         * @return The Forge version.
         */
        public String getForgeVersion() {
            return forgeVersion;
        }

        /**
         * Get the MD5 hash of the file.
         *
         * @return The MD5 hash.
         */
        public String getFileMd5() {
            return fileMd5;
        }

        /**
         * Get the origin URL of the build.
         *
         * @return The origin URL.
         */
        public String getOriginUrl() {
            return originUrl;
        }

        /**
         * Get the URL of the build.
         *
         * @return The URL.
         */
        public String getUrl() {
            return url;
        }

        /**
         * Get the timestamp when the build was created.
         *
         * @return The creation timestamp.
         */
        public long getCreatedAt() {
            return createdAt;
        }
    }
}
