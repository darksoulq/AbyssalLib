package com.github.darksoulq.abyssallib.server.permission;

import com.github.darksoulq.abyssallib.server.registry.Registries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for calculating the effective permissions of a holder based on inheritance.
 */
public class PermissionCalculator {

    /**
     * Calculates the full effective permission map for a holder.
     * <p>
     * Logic flow:
     * 1. Recursively collect all inherited groups.
     * 2. Sort groups by weight (low to high).
     * 3. Apply permissions from groups (later overwrites earlier).
     * 4. Apply direct permissions from the holder (final priority).
     *
     * @param holder The {@link PermissionHolder} to calculate for.
     * @return An unmodifiable map of effective permission nodes.
     */
    public static Map<String, Boolean> calculateEffective(PermissionHolder holder) {
        Map<String, PermissionGroup> collectedGroups = new HashMap<>();

        for (Node parentNode : holder.getParentNodes()) {
            if (parentNode.hasExpired()) continue;
            PermissionGroup group = Registries.PERMISSION_GROUPS.get(parentNode.getKey());
            if (group != null) {
                collectGroups(group, collectedGroups);
            }
        }

        List<PermissionGroup> sortedGroups = new ArrayList<>(collectedGroups.values());
        sortedGroups.sort(Comparator.comparingInt(PermissionGroup::getWeight));

        Map<String, Boolean> effective = new LinkedHashMap<>();

        for (PermissionGroup group : sortedGroups) {
            for (Node node : group.getNodes()) {
                if (!node.hasExpired()) {
                    effective.put(node.getKey(), node.getValue());
                }
            }
        }

        for (Node node : holder.getNodes()) {
            if (!node.hasExpired()) {
                effective.put(node.getKey(), node.getValue());
            }
        }

        return Collections.unmodifiableMap(effective);
    }

    /**
     * Recursively collects all groups in an inheritance tree.
     *
     * @param group     The current group to process.
     * @param collected The map of already collected groups to prevent cycles.
     */
    private static void collectGroups(PermissionGroup group, Map<String, PermissionGroup> collected) {
        if (collected.containsKey(group.getId())) return;
        collected.put(group.getId(), group);

        for (Node parentNode : group.getParentNodes()) {
            if (parentNode.hasExpired()) continue;
            PermissionGroup parent = Registries.PERMISSION_GROUPS.get(parentNode.getKey());
            if (parent != null) {
                collectGroups(parent, collected);
            }
        }
    }
}