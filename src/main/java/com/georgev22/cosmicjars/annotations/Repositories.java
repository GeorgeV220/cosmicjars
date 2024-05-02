package com.georgev22.cosmicjars.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Annotation to indicate the required repositories for dependencies.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Repositories {

    @NotNull Repository[] value() default {};

}
