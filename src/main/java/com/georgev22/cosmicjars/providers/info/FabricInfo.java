package com.georgev22.cosmicjars.providers.info;

/**
 * The FabricInfo class contains nested classes that provide information about
 * different components of the Fabric toolchain.
 */
public class FabricInfo {

    /**
     * FabricVersionInfo holds information about a specific version of Fabric.
     */
    public static class FabricVersionInfo {

        private String version;
        private boolean stable;

        /**
         * Gets the version of Fabric.
         *
         * @return the version of Fabric.
         */
        public String getVersion() {
            return version;
        }

        /**
         * Checks if the Fabric version is stable.
         *
         * @return true if the Fabric version is stable, false otherwise.
         */
        public boolean isStable() {
            return stable;
        }

    }

    /**
     * FabricInstallerInfo holds information about the Fabric installer.
     */
    public static class FabricInstallerInfo {
        private String url;
        private String maven;
        private String version;
        private boolean stable;

        /**
         * Gets the URL of the Fabric installer.
         *
         * @return the URL of the Fabric installer.
         */
        public String getUrl() {
            return url;
        }

        /**
         * Gets the Maven identifier for the Fabric installer.
         *
         * @return the Maven identifier for the Fabric installer.
         */
        public String getMaven() {
            return maven;
        }

        /**
         * Gets the version of the Fabric installer.
         *
         * @return the version of the Fabric installer.
         */
        public String getVersion() {
            return version;
        }

        /**
         * Checks if the Fabric installer version is stable.
         *
         * @return true if the Fabric installer version is stable, false otherwise.
         */
        public boolean isStable() {
            return stable;
        }
    }

    /**
     * FabricLoaderInfo holds information about the Fabric loader.
     */
    public static class FabricLoaderInfo {
        private String separator;
        private int build;
        private String maven;
        private String version;
        private boolean stable;

        /**
         * Gets the separator used in the Fabric loader version.
         *
         * @return the separator used in the Fabric loader version.
         */
        public String getSeparator() {
            return separator;
        }

        /**
         * Gets the build number of the Fabric loader.
         *
         * @return the build number of the Fabric loader.
         */
        public int getBuild() {
            return build;
        }

        /**
         * Gets the Maven identifier for the Fabric loader.
         *
         * @return the Maven identifier for the Fabric loader.
         */
        public String getMaven() {
            return maven;
        }

        /**
         * Gets the version of the Fabric loader.
         *
         * @return the version of the Fabric loader.
         */
        public String getVersion() {
            return version;
        }

        /**
         * Checks if the Fabric loader version is stable.
         *
         * @return true if the Fabric loader version is stable, false otherwise.
         */
        public boolean isStable() {
            return stable;
        }
    }

}
