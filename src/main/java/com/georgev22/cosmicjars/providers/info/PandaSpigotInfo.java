package com.georgev22.cosmicjars.providers.info;

/**
 * Represents the metadata information for a PandaSpigot build.
 */
public class PandaSpigotInfo {

    /**
     * The build number of the PandaSpigot version.
     */
    private int build;

    /**
     * The downloads information associated with this build.
     */
    private Downloads downloads;

    /**
     * Gets the build number.
     *
     * @return the build number
     */
    public int getBuild() {
        return build;
    }

    /**
     * Gets the downloads information.
     *
     * @return the downloads information
     */
    public Downloads getDownloads() {
        return downloads;
    }

    /**
     * Represents download-related metadata.
     */
    public static class Downloads {

        /**
         * The paperclip jar download information.
         */
        Paperclip paperclip;

        /**
         * Gets the paperclip download metadata.
         *
         * @return the paperclip metadata
         */
        public Paperclip getPaperclip() {
            return paperclip;
        }

        /**
         * Represents the details of the paperclip jar download.
         */
        public static class Paperclip {

            /**
             * The name of the jar file.
             */
            String name;

            /**
             * The SHA-256 checksum of the jar file.
             */
            String sha256;

            /**
             * Gets the name of the jar file.
             *
             * @return the jar file name
             */
            public String getName() {
                return name;
            }

            /**
             * Gets the SHA-256 checksum of the jar file.
             *
             * @return the SHA-256 checksum
             */
            public String getSha256() {
                return sha256;
            }
        }
    }
}
