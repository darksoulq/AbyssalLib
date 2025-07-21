package com.github.darksoulq.abyssallib.server.config.internal.format;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class Json5Format implements ConfigFormat {

    private final ObjectMapper mapper = new ObjectMapper()
            .enable(JsonParser.Feature.ALLOW_COMMENTS)
            .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> parse(String input) {
        try {
            return mapper.readValue(input, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON5", e);
        }
    }

    @Override
    public String dump(Map<String, Object> flatData, Map<String, String[]> comments) {
        StringWriter out = new StringWriter();
        try (JsonGenerator gen = new JsonFactory().createGenerator(out)) {
            gen.useDefaultPrettyPrinter();
            Map<String, Object> nestedData = expand(flatData);
            writeObject(gen, nestedData, comments, "", 0);
            gen.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON5", e);
        }
        return out.toString();
    }

    @SuppressWarnings("unchecked")
    private void writeObject(JsonGenerator gen, Map<String, Object> map, Map<String, String[]> comments, String path, int indentLevel) throws IOException {
        gen.writeRaw("{\n");

        int i = 0;
        int size = map.size();
        String indent = "  ".repeat(indentLevel + 1);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String fullPath = path.isEmpty() ? key : path + "." + key;

            if (comments.containsKey(fullPath)) {
                for (String line : comments.get(fullPath)) {
                    gen.writeRaw(indent + "// " + line + "\n");
                }
            }

            gen.writeRaw(indent + "\"" + key + "\": ");

            if (value instanceof Map<?, ?> nested) {
                writeObject(gen, (Map<String, Object>) nested, comments, fullPath, indentLevel + 1);
            } else {
                mapper.writeValue(gen, value);
            }

            if (++i < size) gen.writeRaw(",");
            gen.writeRaw("\n");
        }

        gen.writeRaw("  ".repeat(indentLevel) + "}");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> expand(Map<String, Object> flatMap) {
        Map<String, Object> nestedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : flatMap.entrySet()) {
            String[] parts = entry.getKey().split("\\.");
            Map<String, Object> current = nestedMap;
            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];
                current = (Map<String, Object>) current.computeIfAbsent(part, k -> new LinkedHashMap<>());
            }
            current.put(parts[parts.length - 1], entry.getValue());
        }
        return nestedMap;
    }
}
