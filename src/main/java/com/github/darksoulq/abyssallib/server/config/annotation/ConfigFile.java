package com.github.darksoulq.abyssallib.server.config.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigFile {
    String pluginId();
    String folder() default "";
}
