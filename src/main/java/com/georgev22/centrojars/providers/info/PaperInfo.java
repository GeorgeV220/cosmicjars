package com.georgev22.centrojars.providers.info;

/**
 * Class representing information about a PaperMC project version.
 */
public class PaperInfo {
    /**
     * Project ID.
     */
    private final String project_id;

    /**
     * Project name.
     */
    private final String project_name;

    /**
     * Version of the project.
     */
    private final String version;

    /**
     * Array of builds.
     */
    private final int[] builds;

    /**
     * Constructs a new PaperInfo object with the specified parameters.
     *
     * @param project_id    Project ID.
     * @param project_name  Project name.
     * @param version       Version of the project.
     * @param builds        Array of builds.
     */
    public PaperInfo(String project_id, String project_name, String version, int[] builds) {
        this.project_id = project_id;
        this.project_name = project_name;
        this.version = version;
        this.builds = builds;
    }

    /**
     * Retrieves the project ID.
     *
     * @return The project ID.
     */
    public String getProjectId() {
        return project_id;
    }

    /**
     * Retrieves the project name.
     *
     * @return The project name.
     */
    public String getProjectName() {
        return project_name;
    }

    /**
     * Retrieves the version of the project.
     *
     * @return The version of the project.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Retrieves the array of builds.
     *
     * @return The array of builds.
     */
    public int[] getBuilds() {
        return builds;
    }

    /**
     * Retrieves the latest build.
     *
     * @return The latest build, or -1 if no builds are available.
     */
    public int getLatestBuild() {
        if (builds != null && builds.length > 0) {
            return builds[builds.length - 1];
        } else {
            return -1;
        }
    }
}