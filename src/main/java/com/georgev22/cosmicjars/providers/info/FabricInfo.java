package com.georgev22.cosmicjars.providers.info;

public class FabricInfo {

    public static class FabricVersionInfo {

        private String version;
        private boolean stable;

        public String getVersion() {
            return version;
        }

        public boolean isStable() {
            return stable;
        }

    }

    public static class FabricInstallerInfo {
        private String url;
        private String maven;
        private String version;
        private boolean stable;

        public String getUrl() {
            return url;
        }

        public String getMaven() {
            return maven;
        }

        public String getVersion() {
            return version;
        }

        public boolean isStable() {
            return stable;
        }
    }

    public static class FabricLoaderInfo {
        private String separator;
        private int build;
        private String maven;
        private String version;
        private boolean stable;

        public String getSeparator() {
            return separator;
        }

        public int getBuild() {
            return build;
        }

        public String getMaven() {
            return maven;
        }

        public String getVersion() {
            return version;
        }

        public boolean isStable() {
            return stable;
        }
    }

}
