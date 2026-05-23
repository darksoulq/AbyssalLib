package com.github.darksoulq.abyssallib.server.util;

import java.util.regex.Pattern;

public class Version implements Comparable<Version> {
    private static final Pattern SPLIT = Pattern.compile("[^a-zA-Z0-9]+");
    private final String[] parts;

    public Version(String version) {
        this.parts = SPLIT.split(version);
    }

    @Override
    public int compareTo(Version other) {
        int length = Math.max(this.parts.length, other.parts.length);
        for (int i = 0; i < length; i++) {
            String p1 = i < this.parts.length ? this.parts[i] : "";
            String p2 = i < other.parts.length ? other.parts[i] : "";

            boolean p1Numeric = p1.matches("\\d+");
            boolean p2Numeric = p2.matches("\\d+");

            if (p1Numeric && p2Numeric) {
                int cmp = Integer.compare(Integer.parseInt(p1), Integer.parseInt(p2));
                if (cmp != 0) return cmp;
            } else if (!p1Numeric && !p2Numeric) {
                int cmp = p1.compareToIgnoreCase(p2);
                if (cmp != 0) return cmp;
            } else {
                return p1Numeric ? 1 : -1;
            }
        }
        return 0;
    }
}