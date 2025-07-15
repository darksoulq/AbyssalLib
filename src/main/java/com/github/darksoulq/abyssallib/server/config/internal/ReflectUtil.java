package com.github.darksoulq.abyssallib.server.config.internal;

import com.github.darksoulq.abyssallib.server.config.annotation.Comment;
import com.github.darksoulq.abyssallib.server.config.annotation.ConfigProperty;
import com.github.darksoulq.abyssallib.server.config.annotation.Nest;

import java.lang.reflect.*;
import java.util.*;

public class ReflectUtil {
    public static void collectFields(Class<?> cls, String prefix,
                                     List<String> keys, List<Field> fields, List<String[]> comments) {

        for (Field f : cls.getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers())) continue;
            f.setAccessible(true);
            ConfigProperty cp = f.getAnnotation(ConfigProperty.class);
            if (cp != null) {
                keys.add(prefix + (cp.name().isEmpty() ? f.getName() : cp.name()));
                fields.add(f);
                Comment c = f.getAnnotation(Comment.class);
                comments.add(c != null ? c.comments() : null);
            }
        }
        for (Class<?> inner : cls.getDeclaredClasses()) {
            Nest n = inner.getAnnotation(Nest.class);
            if (n != null) {
                String newPrefix = prefix + n.name() + ".";
                collectFields(inner, newPrefix, keys, fields, comments);
            }
        }
    }
}
