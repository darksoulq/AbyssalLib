package com.github.darksoulq.abyssallib.common.serialization.fixer;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import net.kyori.adventure.key.Key;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class DataFixerRegistry {
    private final int targetVersion;
    private final Map<Key, TreeMap<Integer, DataFixer>> fixers = new HashMap<>();

    public DataFixerRegistry(int targetVersion) {
        this.targetVersion = targetVersion;
    }

    public int getTargetVersion() {
        return this.targetVersion;
    }

    public Key registerFixer(Key id, int fromVersion, DataFixer fixer) {
        this.fixers.computeIfAbsent(id, k -> new TreeMap<>()).put(fromVersion, fixer);
        return id;
    }

    public <D> D update(DynamicOps<D> ops, Key id, int currentVersion, D input) {
        NavigableMap<Integer, DataFixer> migrations = fixers.get(id);
        if (migrations == null) {
            return input;
        }

        D currentData = input;
        NavigableMap<Integer, DataFixer> tailMap = migrations.tailMap(currentVersion, true);

        for (Map.Entry<Integer, DataFixer> entry : tailMap.entrySet()) {
            if (entry.getKey() >= targetVersion) {
                break;
            }
            currentData = entry.getValue().fix(ops, currentData);
        }

        return currentData;
    }
}