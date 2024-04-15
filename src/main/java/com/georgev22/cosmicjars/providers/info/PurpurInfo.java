package com.georgev22.cosmicjars.providers.info;

import java.util.List;
import java.util.Objects;

/**
 * Class representing information about a PurpurMC project.
 */
public final class PurpurInfo {
    /**
     * Project name.
     */
    private final String project;

    /**
     * Version of the project.
     */
    private final String version;

    /**
     * Information about PurpurMC builds.
     */
    private final PurpurBuildsInfo builds;

    /**
     * Constructs a new PurpurInfo object with the specified parameters.
     *
     * @param project Project name.
     * @param version Version of the project.
     * @param builds  Information about PurpurMC builds.
     */
    public PurpurInfo(String project, String version, PurpurBuildsInfo builds) {
        this.project = project;
        this.version = version;
        this.builds = builds;
    }

    /**
     * Retrieves the project name.
     *
     * @return The project name.
     */
    public String project() {
        return project;
    }

    /**
     * Retrieves the version of the project.
     *
     * @return The version of the project.
     */
    public String version() {
        return version;
    }

    /**
     * Retrieves information about PurpurMC builds.
     *
     * @return Information about PurpurMC builds.
     */
    public PurpurBuildsInfo builds() {
        return builds;
    }

    /**
     * Returns a string representation of the PurpurInfo object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "PurpurInfo[" +
                "project=" + project + ", " +
                "version=" + version + ", " +
                "builds=" + builds + ']';
    }

    /**
     * Checks whether this PurpurInfo object is equal to another object.
     *
     * @param obj The object to compare to.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PurpurInfo) obj;
        return Objects.equals(this.project, that.project) &&
                Objects.equals(this.version, that.version) &&
                Objects.equals(this.builds, that.builds);
    }

    /**
     * Returns the hash code value for this PurpurInfo object.
     *
     * @return The hash code value for the object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(project, version, builds);
    }

    /**
     * Class representing information about PurpurMC builds.
     */
    public static final class PurpurBuildsInfo {
        /**
         * Latest build.
         */
        private final String latest;

        /**
         * List of all builds.
         */
        private final List<String> all;

        /**
         * Constructs a new PurpurBuildsInfo object with the specified parameters.
         *
         * @param latest Latest build.
         * @param all    List of all builds.
         */
        public PurpurBuildsInfo(String latest, List<String> all) {
            this.latest = latest;
            this.all = all;
        }

        /**
         * Retrieves the latest build.
         *
         * @return The latest build.
         */
        public String latest() {
            return latest;
        }

        /**
         * Retrieves the list of all builds.
         *
         * @return The list of all builds.
         */
        public List<String> all() {
            return all;
        }

        /**
         * Returns a string representation of the PurpurBuildsInfo object.
         *
         * @return A string representation of the object.
         */
        @Override
        public String toString() {
            return "PurpurBuildsInfo[" +
                    "latest=" + latest + ", " +
                    "all=" + all + ']';
        }

        /**
         * Checks whether this PurpurBuildsInfo object is equal to another object.
         *
         * @param obj The object to compare to.
         * @return True if the objects are equal, false otherwise.
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (PurpurBuildsInfo) obj;
            return Objects.equals(this.latest, that.latest) &&
                    Objects.equals(this.all, that.all);
        }

        /**
         * Returns the hash code value for this PurpurBuildsInfo object.
         *
         * @return The hash code value for the object.
         */
        @Override
        public int hashCode() {
            return Objects.hash(latest, all);
        }
    }
}