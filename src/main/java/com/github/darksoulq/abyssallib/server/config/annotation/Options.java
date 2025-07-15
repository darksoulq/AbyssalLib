package com.github.darksoulq.abyssallib.server.config.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Options {
    String[] options();
}
