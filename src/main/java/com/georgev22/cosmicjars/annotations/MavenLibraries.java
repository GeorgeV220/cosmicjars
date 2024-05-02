package com.georgev22.cosmicjars.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Annotation to indicate the required libraries for a class.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MavenLibraries {

    @NotNull
    MavenLibrary[] value() default {};

}