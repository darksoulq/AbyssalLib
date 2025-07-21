package com.github.darksoulq.abyssallib.server.config.internal.format;

import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class YamlFormat implements ConfigFormat {

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> parse(String t) {
        return new Yaml().loadAs(t, Map.class);
    }

    @Override
    public String dump(Map<String, Object> flatData, Map<String, String[]> comments) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> nested = expand(flatData);
        writeYaml(sb, nested, comments, "", "");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private void writeYaml(StringBuilder sb, Map<String, Object> map, Map<String, String[]> comments, String path, String indent) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            String fullPath = path.isEmpty() ? key : path + "." + key;

            if (comments.containsKey(fullPath)) {
                for (String line : comments.get(fullPath)) {
                    sb.append(indent).append("# ").append(line).append("\n");
                }
            }

            sb.append(indent).append(key).append(":");

            if (val instanceof Map<?, ?> nestedMap) {
                sb.append("\n");
                writeYaml(sb, (Map<String, Object>) nestedMap, comments, fullPath, indent + "  ");

            } else if (val instanceof List<?> list) {
                if (list.isEmpty()) {
                    sb.append(" []\n");
                } else {
                    sb.append("\n");
                    for (Object item : list) {
                        if (item instanceof Map<?, ?> innerMap) {
                            sb.append(indent).append("  -\n");
                            writeYaml(sb, (Map<String, Object>) innerMap, comments, fullPath, indent + "    ");
                        } else {
                            sb.append(indent).append("  - ").append(serializeYamlValue(item)).append("\n");
                        }
                    }
                }
            } else {
                sb.append(" ").append(serializeYamlValue(val)).append("\n");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> expand(Map<String, Object> flatMap) {
        Map<String, Object> nestedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : flatMap.entrySet()) {
            String[] parts = entry.getKey().split("\\.");
            Map<String, Object> current = nestedMap;
            for (int i = 0; i < parts.length - 1; i++) {
                current = (Map<String, Object>) current.computeIfAbsent(parts[i], k -> new LinkedHashMap<>());
            }
            current.put(parts[parts.length - 1], entry.getValue());
        }
        return nestedMap;
    }

    private String serializeYamlValue(Object val) {
        if (val == null) return "null";
        if (val instanceof String s) {
            if (s.matches("^[a-zA-Z0-9_\\-\\.]+$")) return s;
            return "\"" + s.replace("\"", "\\\"") + "\"";
        }
        return val.toString();
    }
}
