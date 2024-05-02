package com.georgev22.cosmicjars.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Represents an annotations' repository.
 */
@Documented
@Repeatable(Repositories.class)
@Target({ElementType.TYPE, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Repository {

    @NotNull
    String repoName();

    @NotNull
    String type() default "default";

    /**
     * Gets the base url of the repository.
     *
     * @return the base url of the repository
     */
    @NotNull
    String repoURL();

}