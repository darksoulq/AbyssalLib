package com.github.darksoulq.abyssallib.server.config.internal.format;

import com.github.darksoulq.abyssallib.server.config.annotation.*;
import com.github.darksoulq.abyssallib.server.config.serializer.Serializer;
import com.github.darksoulq.abyssallib.server.config.serializer.SerializerRegistry;

import java.lang.reflect.*;
import java.util.*;
import java.io.*;

public final class JsonWriter {

    private final StringBuilder sb = new StringBuilder();
    private int indentLevel = 0;

    public void writeConfig(Class<?> clazz, File file) throws IOException {
        sb.setLength(0);
        indentLevel = 0;
        writeClass(clazz);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(sb.toString());
        }
    }

    private void writeClass(Class<?> clazz) {
        sb.append("{\n");
        indentLevel++;

        List<Field> fields = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) fields.add(f);
        }

        for (int i = 0; i < fields.size(); i++) {
            Field f = fields.get(i);
            writeField(f);
            if (i < fields.size() - 1 || clazz.getDeclaredClasses().length > 0) sb.append(",");
            sb.append("\n");
        }

        Class<?>[] nestedClasses = clazz.getDeclaredClasses();
        for (int i = 0; i < nestedClasses.length; i++) {
            Class<?> nested = nestedClasses[i];
            if (!nested.isAnnotationPresent(Nest.class)) continue;

            Nest n = nested.getAnnotation(Nest.class);
            String key = n.value().isEmpty() ? nested.getSimpleName() : n.value();
            Comment cmt = nested.getAnnotation(Comment.class);
            if (cmt != null && !cmt.inline()) for (String s : cmt.value()) writeComment(s);

            sb.append(indent()).append("\"").append(key).append("\": ");
            writeClass(nested);

            if (i < nestedClasses.length - 1) sb.append(",");
            sb.append("\n");
        }

        indentLevel--;
        sb.append(indent()).append("}");
    }

    @SuppressWarnings("unchecked")
    private void writeField(Field f) {
        ConfigProperty cp = f.getAnnotation(ConfigProperty.class);
        String key = (cp != null && !cp.value().isEmpty()) ? cp.value() : f.getName();

        Comment cmt = f.getAnnotation(Comment.class);
        Object value;
        try {
            f.setAccessible(true);
            value = f.get(null);
            if (SerializerRegistry.has(f.getType()))
                value = ((Serializer<Object>) SerializerRegistry.get(f.getType())).serialize(value);
        } catch (Exception e) { return; }

        if (cmt != null && !cmt.inline()) for (String s : cmt.value()) writeComment(s);

        sb.append(indent()).append("\"").append(key).append("\": ").append(formatValue(value));

        if (cmt != null && cmt.inline()) sb.append(" // ").append(String.join(" ", cmt.value()));
    }

    private void writeComment(String comment) {
        sb.append(indent()).append("// ").append(comment).append("\n");
    }

    private String indent() { return "  ".repeat(Math.max(0, indentLevel)); }

    private String formatValue(Object value) {
        if (value == null) return "null";
        if (value instanceof String) return "\"" + ((String) value).replace("\"", "\\\"") + "\"";
        if (value instanceof Number || value instanceof Boolean) return value.toString();
        if (value instanceof List<?> list) {
            StringBuilder b = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                b.append(formatValue(list.get(i)));
                if (i < list.size() - 1) b.append(", ");
            }
            b.append("]");
            return b.toString();
        }
        if (value instanceof Map<?, ?> map) {
            StringBuilder b = new StringBuilder("{");
            int i = 0;
            for (Map.Entry<?, ?> e : map.entrySet()) {
                b.append("\"").append(e.getKey()).append("\": ").append(formatValue(e.getValue()));
                if (i++ < map.size() - 1) b.append(", ");
            }
            b.append("}");
            return b.toString();
        }
        return "\"" + value + "\"";
    }
}
