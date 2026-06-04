package com.github.darksoulq.abyssallib.common.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a parsed hierarchical data path for querying and mutating nested structures.
 * <p>
 * Supported Path Syntaxes:
 * <ul>
 * <li><b>Standard Key:</b> {@code "inventory"}</li>
 * <li><b>Nested Nodes:</b> {@code "inventory.weapon.damage"}</li>
 * <li><b>List Indices:</b> {@code "players[3]"}</li>
 * <li><b>Root List Index:</b> {@code "[0]"}</li>
 * <li><b>Complex Chains:</b> {@code "players[3].inventory.weapon[0].damage"}</li>
 * </ul>
 */
public class DataPath {

    private final List<Segment> segments;

    private DataPath(List<Segment> segments) {
        this.segments = Collections.unmodifiableList(segments);
    }

    /**
     * Parses a string representation of a data path into structural segments.
     *
     * @param path The literal path string.
     * @return A compiled DataPath instance.
     */
    public static DataPath of(String path) {
        List<Segment> segments = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        boolean inIndex = false;

        for (char c : path.toCharArray()) {
            if (c == '.') {
                if (!buffer.isEmpty()) {
                    segments.add(new Key(buffer.toString()));
                    buffer.setLength(0);
                }
            } else if (c == '[') {
                if (!buffer.isEmpty()) {
                    segments.add(new Key(buffer.toString()));
                    buffer.setLength(0);
                }
                inIndex = true;
            } else if (c == ']') {
                if (inIndex && !buffer.isEmpty()) {
                    try {
                        segments.add(new Index(Integer.parseInt(buffer.toString())));
                    } catch (NumberFormatException e) {
                        segments.add(new Key(buffer.toString()));
                    }
                    buffer.setLength(0);
                    inIndex = false;
                }
            } else {
                buffer.append(c);
            }
        }
        if (!buffer.isEmpty()) {
            segments.add(new Key(buffer.toString()));
        }
        return new DataPath(segments);
    }

    /**
     * @return An immutable list of the parsed segments.
     */
    public List<Segment> segments() {
        return segments;
    }

    /**
     * @return True if the path contains zero segments.
     */
    public boolean isEmpty() {
        return segments.isEmpty();
    }

    /**
     * Reconstructs the string representation of the parsed path.
     *
     * @return The canonical path string.
     */
    public String asString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segments.size(); i++) {
            Segment seg = segments.get(i);
            if (seg instanceof Key(String value)) {
                if (i > 0 && !(segments.get(i - 1) instanceof Index)) {
                    sb.append(".");
                } else if (i > 0 && segments.get(i - 1) instanceof Index) {
                    sb.append(".");
                }
                sb.append(value);
            } else if (seg instanceof Index idx) {
                sb.append("[").append(idx.value()).append("]");
            }
        }
        return sb.toString();
    }

    /**
     * Represents a singular conceptual step within a hierarchical data structure.
     */
    public sealed interface Segment permits Key, Index {
        /**
         * @return True if this segment targets a list position, false if it targets a map key.
         */
        boolean isIndex();
    }

    /**
     * Represents a dictionary or map key segment.
     *
     * @param value The exact string key.
     */
    public record Key(String value) implements Segment {
        @Override
        public boolean isIndex() {
            return false;
        }
    }

    /**
     * Represents an array or list positional index.
     *
     * @param value The zero-based integer index.
     */
    public record Index(int value) implements Segment {
        @Override
        public boolean isIndex() {
            return true;
        }
    }
}