package com.georgev22.cosmicjars.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Annotation to indicate a required library for a class.
 */
@Documented
@Repeatable(MavenLibraries.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MavenLibrary {

    @NotNull
    String value() default "";

    /**
     * The group id of the library
     *
     * @return the group id of the library
     */
    @NotNull
    String groupId() default "";

    /**
     * The artifact id of the library
     *
     * @return the artifact id of the library
     */
    @NotNull
    String artifactId() default "";

    /**
     * The version of the library
     *
     * @return the version of the library
     */
    @NotNull
    String version() default "";

    /**
     * The repo where the library can be obtained from
     *
     * @return the repo where the library can be obtained from
     */
    @NotNull
    Repository repo() default @Repository(repoName = "central", repoURL = "https://repo1.maven.org/maven2");

}