package com.github.darksoulq.abyssallib.server.config.internal.format;

import com.github.darksoulq.abyssallib.server.config.annotation.*;
import com.github.darksoulq.abyssallib.server.config.serializer.Serializer;
import com.github.darksoulq.abyssallib.server.config.serializer.SerializerRegistry;

import java.lang.reflect.*;
import java.util.*;
import java.io.*;

public final class YamlWriter {

    private final StringBuilder sb = new StringBuilder();

    public void writeConfig(Class<?> clazz, File file) throws IOException {
        sb.setLength(0);
        writeClass(clazz, 0);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(sb.toString());
        }
    }

    @SuppressWarnings("unchecked")
    private void writeClass(Class<?> clazz, int indent) {
        Comment classComment = clazz.getAnnotation(Comment.class);
        if (classComment != null && !classComment.inline()) {
            for (String c : classComment.value()) writeComment(c, indent);
        }

        for (Field f : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers())) continue;
            ConfigProperty cp = f.getAnnotation(ConfigProperty.class);
            String key = (cp != null && !cp.value().isEmpty()) ? cp.value() : f.getName();

            Object value;
            try {
                f.setAccessible(true);
                value = f.get(null);
                if (SerializerRegistry.has(f.getType())) {
                    value = ((Serializer<Object>) SerializerRegistry.get(f.getType())).serialize(value);
                }
            } catch (Exception e) { continue; }

            Comment cmt = f.getAnnotation(Comment.class);
            if (cmt != null && !cmt.inline()) {
                for (String c : cmt.value()) writeComment(c, indent);
            }

            String valStr = formatValue(value, indent + 2);

            if (cmt != null && cmt.inline()) {
                sb.append(spaces(indent)).append(key).append(": ").append(valStr)
                        .append(" # ").append(String.join(" ", cmt.value())).append("\n");
            } else {
                sb.append(spaces(indent)).append(key).append(": ").append(valStr).append("\n");
            }
        }

        for (Class<?> nested : clazz.getDeclaredClasses()) {
            if (!nested.isAnnotationPresent(Nest.class)) continue;
            Nest n = nested.getAnnotation(Nest.class);
            String key = n.value().isEmpty() ? nested.getSimpleName() : n.value();

            Comment cmt = nested.getAnnotation(Comment.class);
            if (cmt != null && !cmt.inline()) {
                for (String c : cmt.value()) writeComment(c, indent);
            }

            sb.append(spaces(indent)).append(key).append(":\n");
            writeClass(nested, indent + 2);
        }
    }

    private void writeComment(String comment, int indent) {
        sb.append(spaces(indent)).append("# ").append(comment).append("\n");
    }

    private String spaces(int count) {
        return " ".repeat(Math.max(0, count));
    }

    private String formatValue(Object value, int indent) {
        if (value == null) return "null";
        if (value instanceof String) return "\"" + ((String) value).replace("\"", "\\\"") + "\"";
        if (value instanceof Number || value instanceof Boolean) return value.toString();
        if (value instanceof List<?> list) {
            if (list.isEmpty()) return "[]";
            StringBuilder b = new StringBuilder("\n");
            for (Object o : list) b.append(spaces(indent)).append("- ").append(formatValue(o, indent + 2)).append("\n");
            return b.toString();
        }
        if (value instanceof Map<?, ?> map) {
            if (map.isEmpty()) return "{}";
            StringBuilder b = new StringBuilder("\n");
            for (Map.Entry<?, ?> e : map.entrySet()) {
                Object val = e.getValue();
                String formatted = formatValue(val, indent + 2);
                Comment inline = null;
                if (val instanceof Field) inline = ((Field) val).getAnnotation(Comment.class);
                if (inline != null && inline.inline()) {
                    b.append(spaces(indent)).append(e.getKey()).append(": ").append(formatted)
                            .append(" # ").append(String.join(" ", inline.value())).append("\n");
                } else {
                    b.append(spaces(indent)).append(e.getKey()).append(": ").append(formatted).append("\n");
                }
            }
            return b.toString();
        }
        return "\"" + value + "\"";
    }
}
