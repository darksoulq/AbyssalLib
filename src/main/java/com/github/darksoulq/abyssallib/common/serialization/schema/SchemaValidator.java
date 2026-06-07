package com.github.darksoulq.abyssallib.common.serialization.schema;

import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.util.*;

/**
 * Validates structured data against a set of schema rules.
 */
public class SchemaValidator {
    private String currentPath = "";
    private final List<Rule> rules = new ArrayList<>();

    /**
     * Represents a validation rule applied to input data.
     */
    public interface Rule {
        /**
         * Validates the supplied input.
         *
         * @param ops operations used to inspect the input
         * @param rootInput root value being validated
         * @param <D> dynamic data type
         * @return validation result
         */
        <D> DataResult<D> check(DynamicOps<D> ops, D rootInput);
    }

    private <D> Optional<D> resolve(DynamicOps<D> ops, D input, String path) {
        return path.isEmpty() ? Optional.of(input) : ops.query(input, path);
    }

    /**
     * Returns all registered validation rules.
     *
     * @return validation rules
     */
    public List<Rule> getRules() {
        return rules;
    }

    /**
     * Targets a structural path for subsequent validation rules.
     *
     * @param path the field path to target
     * @return this validator
     */
    public SchemaValidator field(String path) {
        this.currentPath = path;
        return this;
    }

    /**
     * Restricts a numerical field to a specific range.
     *
     * @param min minimum inclusive value
     * @param max maximum inclusive value
     * @return this validator
     */
    public SchemaValidator range(double min, double max) {
        String p = currentPath;
        rules.add(new Rule() {
            @Override
            public <D> DataResult<D> check(DynamicOps<D> ops, D rootInput) {
                Optional<D> target = resolve(ops, rootInput, p);
                if (target.isEmpty()) return DataResult.success(rootInput);
                D val = target.get();
                if (ops.getDoubleValue(val).isPresent()) {
                    double d = ops.getDoubleValue(val).get();
                    if (d < min || d > max) return DataResult.<D>error(DataError.outOfBounds(d, min, max)).prependPath(p);
                } else if (ops.getIntValue(val).isPresent()) {
                    double d = ops.getIntValue(val).get();
                    if (d < min || d > max) return DataResult.<D>error(DataError.outOfBounds(d, min, max)).prependPath(p);
                }
                return DataResult.success(rootInput);
            }
        });
        return this;
    }

    /**
     * Enforces a minimum length constraint on a string, list, or map.
     *
     * @param length minimum required length
     * @return this validator
     */
    public SchemaValidator minLength(int length) {
        String p = currentPath;
        rules.add(new Rule() {
            @Override
            public <D> DataResult<D> check(DynamicOps<D> ops, D rootInput) {
                Optional<D> target = resolve(ops, rootInput, p);
                if (target.isEmpty()) return DataResult.success(rootInput);
                D val = target.get();
                int len = -1;
                if (ops.getStringValue(val).isPresent()) len = ops.getStringValue(val).get().length();
                else if (ops.getList(val).isPresent()) len = ops.getList(val).get().size();
                else if (ops.getMap(val).isPresent()) len = ops.getMap(val).get().size();

                if (len != -1 && len < length) return DataResult.<D>error(DataError.custom("Length/size is less than minimum " + length)).prependPath(p);
                return DataResult.success(rootInput);
            }
        });
        return this;
    }

    /**
     * Enforces a maximum length constraint on a string, list, or map.
     *
     * @param length maximum allowed length
     * @return this validator
     */
    public SchemaValidator maxLength(int length) {
        String p = currentPath;
        rules.add(new Rule() {
            @Override
            public <D> DataResult<D> check(DynamicOps<D> ops, D rootInput) {
                Optional<D> target = resolve(ops, rootInput, p);
                if (target.isEmpty()) return DataResult.success(rootInput);
                D val = target.get();
                int len = -1;
                if (ops.getStringValue(val).isPresent()) len = ops.getStringValue(val).get().length();
                else if (ops.getList(val).isPresent()) len = ops.getList(val).get().size();
                else if (ops.getMap(val).isPresent()) len = ops.getMap(val).get().size();

                if (len != -1 && len > length) return DataResult.<D>error(DataError.custom("Length/size exceeds maximum " + length)).prependPath(p);
                return DataResult.success(rootInput);
            }
        });
        return this;
    }

    /**
     * Enforces a regular expression match on a string field.
     *
     * @param pattern required regular expression
     * @return this validator
     */
    public SchemaValidator regex(String pattern) {
        String p = currentPath;
        java.util.regex.Pattern pat = java.util.regex.Pattern.compile(pattern);
        rules.add(new Rule() {
            @Override
            public <D> DataResult<D> check(DynamicOps<D> ops, D rootInput) {
                Optional<D> target = resolve(ops, rootInput, p);
                if (target.isEmpty()) return DataResult.success(rootInput);
                if (ops.getStringValue(target.get()).isPresent()) {
                    if (!pat.matcher(ops.getStringValue(target.get()).get()).matches()) {
                        return DataResult.<D>error(DataError.invalidFormat(ops.getStringValue(target.get()).get(), pattern)).prependPath(p);
                    }
                }
                return DataResult.success(rootInput);
            }
        });
        return this;
    }

    /**
     * Requires a numerical field to be greater than zero.
     *
     * @return this validator
     */
    public SchemaValidator positive() {
        String p = currentPath;
        rules.add(new Rule() {
            @Override
            public <D> DataResult<D> check(DynamicOps<D> ops, D rootInput) {
                Optional<D> target = resolve(ops, rootInput, p);
                if (target.isEmpty()) return DataResult.success(rootInput);
                D val = target.get();
                if (ops.getDoubleValue(val).isPresent() && ops.getDoubleValue(val).get() <= 0) {
                    return DataResult.<D>error(DataError.custom("Value must be strictly positive")).prependPath(p);
                } else if (ops.getIntValue(val).isPresent() && ops.getIntValue(val).get() <= 0) {
                    return DataResult.<D>error(DataError.custom("Value must be strictly positive")).prependPath(p);
                }
                return DataResult.success(rootInput);
            }
        });
        return this;
    }

    /**
     * Restricts a field to a predefined set of allowed values.
     *
     * @param values permitted values
     * @return this validator
     */
    public SchemaValidator oneOf(Object... values) {
        String p = currentPath;
        Set<Object> set = new HashSet<>(Arrays.asList(values));
        rules.add(new Rule() {
            @Override
            public <D> DataResult<D> check(DynamicOps<D> ops, D rootInput) {
                Optional<D> target = resolve(ops, rootInput, p);
                if (target.isEmpty()) return DataResult.success(rootInput);
                D val = target.get();
                Object parsed = null;
                if (ops.getStringValue(val).isPresent()) parsed = ops.getStringValue(val).get();
                else if (ops.getDoubleValue(val).isPresent()) parsed = ops.getDoubleValue(val).get();
                else if (ops.getIntValue(val).isPresent()) parsed = ops.getIntValue(val).get();
                else if (ops.getBooleanValue(val).isPresent()) parsed = ops.getBooleanValue(val).get();

                if (parsed != null && !set.contains(parsed)) {
                    boolean found = false;
                    if (parsed instanceof Number num) {
                        for (Object o : set) {
                            if (o instanceof Number n && n.doubleValue() == num.doubleValue()) {
                                found = true; break;
                            }
                        }
                    }
                    if (!found) return DataResult.<D>error(DataError.custom("Value is not one of allowed values: " + set)).prependPath(p);
                }
                return DataResult.success(rootInput);
            }
        });
        return this;
    }
}
