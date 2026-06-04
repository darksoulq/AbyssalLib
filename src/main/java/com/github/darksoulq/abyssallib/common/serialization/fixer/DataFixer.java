package com.github.darksoulq.abyssallib.common.serialization.fixer;

import com.github.darksoulq.abyssallib.common.config.DataPath;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Defines a function for mutating and upgrading serialized structures.
 */
@FunctionalInterface
public interface DataFixer {

    /**
     * Applies the required data transformation logic utilizing dynamic operations.
     *
     * @param ops   The dynamic operations logic used to navigate data.
     * @param input The current data payload to mutate.
     * @param <D>   The target serialization format type.
     * @return The mutated, upgraded serialized data.
     */
    <D> D fix(DynamicOps<D> ops, D input);

    /**
     * Chains sequential data fixers together in execution order.
     *
     * @param fixers Varargs array of fixers to run in sequence.
     * @return A consolidated DataFixer handling the entire chain.
     */
    static DataFixer compose(DataFixer... fixers) {
        return new DataFixer() {
            @Override
            public <D> D fix(DynamicOps<D> ops, D input) {
                D current = input;
                for (DataFixer fixer : fixers) {
                    current = fixer.fix(ops, current);
                }
                return current;
            }
        };
    }

    /**
     * Executes a structural logic transformation on a data node representing a map.
     *
     * @param transformer The logic defining map modifications.
     * @return A DataFixer encapsulating the map transform task.
     */
    static DataFixer mapTransform(MapTransformer transformer) {
        return new DataFixer() {
            @Override
            public <D> D fix(DynamicOps<D> ops, D input) {
                Optional<Map<D, D>> mapOpt = ops.getMap(input);
                if (mapOpt.isEmpty()) {
                    return input;
                }
                Map<D, D> mutableMap = new HashMap<>(mapOpt.get());
                transformer.transform(ops, mutableMap);
                return ops.createMap(mutableMap);
            }
        };
    }

    /**
     * Executes a structural logic transformation on a data node representing a list.
     *
     * @param transformer The logic defining list modifications.
     * @return A DataFixer encapsulating the list transform task.
     */
    static DataFixer listTransform(ListTransformer transformer) {
        return new DataFixer() {
            @Override
            public <D> D fix(DynamicOps<D> ops, D input) {
                Optional<List<D>> listOpt = ops.getList(input);
                if (listOpt.isEmpty()) {
                    return input;
                }
                List<D> mutableList = new ArrayList<>(listOpt.get());
                transformer.transform(ops, mutableList);
                return ops.createList(mutableList);
            }
        };
    }

    /**
     * Translates a nested path expression into a sequence of modifications down the tree structure.
     *
     * @param path  The literal target nested segment using the compiled DataPath syntax.
     * @param fixer The isolated logic running at the resolved path destination.
     * @return A DataFixer managing nested traversal constraints.
     */
    static DataFixer path(String path, DataFixer fixer) {
        DataPath compiledPath = DataPath.of(path);
        return new DataFixer() {
            @Override
            public <D> D fix(DynamicOps<D> ops, D input) {
                return processPath(ops, input, compiledPath.segments(), 0, fixer);
            }

            private <D> D processPath(DynamicOps<D> ops, D current, List<DataPath.Segment> segments, int index, DataFixer fixer) {
                if (index >= segments.size()) return fixer.fix(ops, current);

                DataPath.Segment segment = segments.get(index);

                if (segment instanceof DataPath.Key(String value1)) {
                    Optional<Map<D, D>> mapOpt = ops.getMap(current);
                    if (mapOpt.isPresent()) {
                        Map<D, D> map = new LinkedHashMap<>(mapOpt.get());
                        D keyObj = ops.createString(value1);
                        if (map.containsKey(keyObj)) {
                            map.put(keyObj, processPath(ops, map.get(keyObj), segments, index + 1, fixer));
                            return ops.createMap(map);
                        }
                    }
                } else if (segment instanceof DataPath.Index(int value)) {
                    Optional<List<D>> listOpt = ops.getList(current);
                    if (listOpt.isPresent()) {
                        List<D> list = new ArrayList<>(listOpt.get());
                        if (value >= 0 && value < list.size()) {
                            list.set(value, processPath(ops, list.get(value), segments, index + 1, fixer));
                            return ops.createList(list);
                        }
                    }
                }

                return current;
            }
        };
    }

    /**
     * Replaces a specific map key with a new identifier.
     *
     * @param oldKey The current identifier.
     * @param newKey The target identifier to use moving forward.
     * @return A DataFixer executing the rename map transform.
     */
    static DataFixer renameKey(String oldKey, String newKey) {
        return mapTransform(new MapTransformer() {
            @Override
            public <D> void transform(DynamicOps<D> ops, Map<D, D> map) {
                D oldD = ops.createString(oldKey);
                if (map.containsKey(oldD)) {
                    map.put(ops.createString(newKey), map.remove(oldD));
                }
            }
        });
    }

    /**
     * Deletes a specific key entirely from the map structure.
     *
     * @param key The identifier to remove.
     * @return A DataFixer executing the removal map transform.
     */
    static DataFixer removeKey(String key) {
        return mapTransform(new MapTransformer() {
            @Override
            public <D> void transform(DynamicOps<D> ops, Map<D, D> map) {
                map.remove(ops.createString(key));
            }
        });
    }

    /**
     * Recursively passes a specific map entry's value to a secondary fixer logic.
     *
     * @param key        The map key housing the nested structure to fix.
     * @param valueFixer The sub-fixer to execute against the nested data.
     * @return A DataFixer executing the conditional map transform.
     */
    static DataFixer transformValue(String key, DataFixer valueFixer) {
        return mapTransform(new MapTransformer() {
            @Override
            public <D> void transform(DynamicOps<D> ops, Map<D, D> map) {
                D targetKey = ops.createString(key);
                if (map.containsKey(targetKey)) {
                    map.put(targetKey, valueFixer.fix(ops, map.get(targetKey)));
                }
            }
        });
    }

    /**
     * Elevates a deeply nested map key up to the parent map execution level.
     *
     * @param nestedMapKey The parent housing the current key.
     * @param keyToHoist   The key to elevate.
     * @return A DataFixer handling the map transform.
     */
    static DataFixer hoistKey(String nestedMapKey, String keyToHoist) {
        return mapTransform(new MapTransformer() {
            @Override
            public <D> void transform(DynamicOps<D> ops, Map<D, D> map) {
                D parentKey = ops.createString(nestedMapKey);
                D targetKey = ops.createString(keyToHoist);
                if (map.containsKey(parentKey)) {
                    ops.getMap(map.get(parentKey)).ifPresent(innerMap -> {
                        if (innerMap.containsKey(targetKey)) {
                            map.put(targetKey, innerMap.get(targetKey));
                        }
                    });
                }
            }
        });
    }

    /**
     * Flattens and shifts explicitly defined sibling keys into a shared child nested map.
     *
     * @param newMapKey  The new identifier for the overarching map wrapper.
     * @param keysToNest Variadic target array of explicit keys to condense.
     * @return A DataFixer executing the shift map transform.
     */
    static DataFixer nestKeys(String newMapKey, String... keysToNest) {
        return mapTransform(new MapTransformer() {
            @Override
            public <D> void transform(DynamicOps<D> ops, Map<D, D> map) {
                Map<D, D> nestedMap = new HashMap<>();
                for (String key : keysToNest) {
                    D targetKey = ops.createString(key);
                    if (map.containsKey(targetKey)) {
                        nestedMap.put(targetKey, map.remove(targetKey));
                    }
                }
                if (!nestedMap.isEmpty()) {
                    map.put(ops.createString(newMapKey), ops.createMap(nestedMap));
                }
            }
        });
    }

    /**
     * Injects a predefined default primitive value if a key node does not physically exist.
     *
     * @param key   The key target for enforcement.
     * @param value The primitive payload fallback.
     * @return A DataFixer ensuring structure conformity.
     */
    static DataFixer addDefault(String key, Object value) {
        return mapTransform(new MapTransformer() {
            @Override
            public <D> void transform(DynamicOps<D> ops, Map<D, D> map) {
                D targetKey = ops.createString(key);
                if (!map.containsKey(targetKey)) {
                    D wrappedValue = switch (value) {
                        case String s -> ops.createString(s);
                        case Integer i -> ops.createInt(i);
                        case Long l -> ops.createLong(l);
                        case Float f -> ops.createFloat(f);
                        case Double d -> ops.createDouble(d);
                        case Boolean b -> ops.createBoolean(b);
                        case null, default -> ops.empty();
                    };

                    map.put(targetKey, wrappedValue);
                }
            }
        });
    }

    /**
     * Reorders keys within a map structure based on advanced pattern matching algorithms.
     * <p>
     * Supported Pattern Syntax:
     * <ul>
     * <li><b>Exact Match:</b> {@code "key_name"}</li>
     * <li><b>Prefix Match:</b> {@code "prefix_*"}</li>
     * <li><b>Suffix Match:</b> {@code "*_suffix"}</li>
     * <li><b>Contains Match:</b> {@code "*contains*"}</li>
     * <li><b>Regex Match:</b> {@code "~^regex.*$"} (Prefixed with tilde)</li>
     * <li><b>Remainder/Dump:</b> {@code "*"} (Captures any keys not matched by explicit rules)</li>
     * </ul>
     *
     * @param patterns Sequential string rules prioritizing and organizing keys.
     * @return A DataFixer executing structural sorting logic on the current node.
     */
    static DataFixer reorderKeys(String... patterns) {
        return mapTransform(new MapTransformer() {
            @Override
            public <D> void transform(DynamicOps<D> ops, Map<D, D> map) {
                Map<String, List<Map.Entry<D, D>>> buckets = new LinkedHashMap<>();
                Map<String, Pattern> regexes = new HashMap<>();

                for (String p : patterns) {
                    buckets.put(p, new ArrayList<>());
                    if (p.startsWith("~")) {
                        regexes.put(p, Pattern.compile(p.substring(1)));
                    }
                }

                List<Map.Entry<D, D>> remainder = new ArrayList<>();

                for (Map.Entry<D, D> entry : map.entrySet()) {
                    String key = ops.getStringValue(entry.getKey()).orElse("");
                    boolean matched = false;

                    for (String pattern : patterns) {
                        if (pattern.equals("*")) continue;

                        boolean match = false;
                        if (pattern.startsWith("~")) {
                            match = regexes.get(pattern).matcher(key).matches();
                        } else if (pattern.startsWith("*") && pattern.endsWith("*") && pattern.length() > 2) {
                            match = key.contains(pattern.substring(1, pattern.length() - 1));
                        } else if (pattern.startsWith("*")) {
                            match = key.endsWith(pattern.substring(1));
                        } else if (pattern.endsWith("*")) {
                            match = key.startsWith(pattern.substring(0, pattern.length() - 1));
                        } else {
                            match = key.equals(pattern);
                        }

                        if (match) {
                            buckets.get(pattern).add(entry);
                            matched = true;
                            break;
                        }
                    }
                    if (!matched) remainder.add(entry);
                }

                Map<D, D> ordered = new LinkedHashMap<>();
                boolean wildcardUsed = false;

                for (String pattern : patterns) {
                    if (pattern.equals("*")) {
                        for (Map.Entry<D, D> e : remainder) ordered.put(e.getKey(), e.getValue());
                        wildcardUsed = true;
                    } else {
                        for (Map.Entry<D, D> e : buckets.get(pattern)) ordered.put(e.getKey(), e.getValue());
                    }
                }

                if (!wildcardUsed) {
                    for (Map.Entry<D, D> e : remainder) ordered.put(e.getKey(), e.getValue());
                }

                map.clear();
                map.putAll(ordered);
            }
        });
    }

    /**
     * Redirects internal data handling dependent on an explicitly provided logical predicate.
     *
     * @param predicate The verification condition encapsulating the current environment dynamic ops.
     * @param thenFixer Evaluated pipeline branch when the predicate returns physically true.
     * @param elseFixer Evaluated pipeline branch when the predicate returns physically false.
     * @return A DataFixer executing contextual branch resolution.
     */
    static DataFixer conditional(DataPredicate predicate, DataFixer thenFixer, DataFixer elseFixer) {
        return new DataFixer() {
            @Override
            public <D> D fix(DynamicOps<D> ops, D input) {
                if (predicate.test(ops, input)) {
                    return thenFixer.fix(ops, input);
                }
                return elseFixer.fix(ops, input);
            }
        };
    }

    /**
     * Triggers cyclic recursive transformation operations universally executing across all array node elements.
     *
     * @param elementFixer Target internal sub-fixer iteration logic block.
     * @return A DataFixer sequentially passing node list data to the provided execution target.
     */
    static DataFixer updateElements(DataFixer elementFixer) {
        return listTransform(new ListTransformer() {
            @Override
            public <D> void transform(DynamicOps<D> ops, List<D> list) {
                list.replaceAll(input -> elementFixer.fix(ops, input));
            }
        });
    }

    /**
     * Blueprint for physical node map interception functions.
     */
    @FunctionalInterface
    interface MapTransformer {
        <D> void transform(DynamicOps<D> ops, Map<D, D> map);
    }

    /**
     * Blueprint for physical node array interception functions.
     */
    @FunctionalInterface
    interface ListTransformer {
        <D> void transform(DynamicOps<D> ops, List<D> list);
    }

    /**
     * Validation condition format wrapper ensuring safety checking bypasses compiler wildcard strictness overrides.
     */
    @FunctionalInterface
    interface DataPredicate {
        <D> boolean test(DynamicOps<D> ops, D input);
    }
}