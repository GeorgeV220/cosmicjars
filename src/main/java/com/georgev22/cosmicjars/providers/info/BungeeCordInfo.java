package com.georgev22.cosmicjars.providers.info;

import java.util.List;

/**
 * Represents information about BungeeCord builds.
 */
public class BungeeCordInfo {

    private List<Build> builds;

    /**
     * Get the list of builds.
     *
     * @return The list of builds.
     */
    public List<Build> getBuilds() {
        return builds;
    }

    /**
     * Set the list of builds.
     *
     * @param builds The list of builds to set.
     */
    public void setBuilds(List<Build> builds) {
        this.builds = builds;
    }

    /**
     * Get the latest build.
     *
     * @return The latest build.
     */
    public Build getLatestBuild() {
        Build latestBuild = null;
        int maxNumber = 0;

        for (Build build : builds) {
            if (build.getNumber() > maxNumber) {
                maxNumber = build.getNumber();
                latestBuild = build;
            }
        }

        return latestBuild;
    }

    /**
     * Represents a build of BungeeCord.
     */
    public static class Build {

        private int number;
        private String url;

        /**
         * Get the build number.
         *
         * @return The build number.
         */
        public int getNumber() {
            return number;
        }

        /**
         * Set the build number.
         *
         * @param number The build number to set.
         */
        public void setNumber(int number) {
            this.number = number;
        }

        /**
         * Get the URL for the build.
         *
         * @return The URL for the build.
         */
        public String getUrl() {
            return url;
        }

        /**
         * Set the URL for the build.
         *
         * @param url The URL for the build to set.
         */
        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return String.valueOf(number);
        }
    }
}