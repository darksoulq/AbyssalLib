package com.github.darksoulq.abyssallib.world.item;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Condition;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.item.component.ComponentMap;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Predicate;

/**
 * A sophisticated predicate used to evaluate and filter {@link ItemStack} instances.
 * This class allows for complex conditional checks against an item's identity,
 * the presence or absence of specific data components, exact component values,
 * and evaluation of nested predicates.
 */
public class ItemPredicate implements Predicate<ItemStack> {

    /**
     * Internal codec responsible for serializing and deserializing individual
     * data component entries used within the predicate's value conditions.
     */
    private static final Codec<DataComponent<?>> COMPONENT_ENTRY_CODEC = new Codec<>() {
        /**
         * Decodes a data component entry from a serialized map format.
         *
         * @param <D>
         * The dynamic data type.
         * @param ops
         * The dynamic operations logic provider.
         * @param input
         * The raw serialized input data.
         * @return
         * The decoded {@link DataComponent} instance.
         * @throws CodecException
         * If the map is malformed, empty, or the component type is unregistered.
         */
        @Override
        public <D> DataComponent<?> decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected Map for component entry"));
            if (map.isEmpty()) {
                throw new CodecException("Empty component map");
            }
            Map.Entry<D, D> entry = map.entrySet().iterator().next();
            String key = ops.getStringValue(entry.getKey()).orElseThrow(() -> new CodecException("Key must be string"));

            DataComponentType<?> type = Registries.DATA_COMPONENT_TYPES.get(key);
            if (type == null) {
                throw new CodecException("Unknown component type: " + key);
            }

            return type.codec().decode(ops, entry.getValue());
        }

        /**
         * Encodes a data component entry into a serialized map format.
         *
         * @param <D>
         * The dynamic data type.
         * @param ops
         * The dynamic operations logic provider.
         * @param value
         * The {@link DataComponent} to serialize.
         * @return
         * The encoded data map.
         * @throws CodecException
         * If the component type is not registered.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, DataComponent<?> value) throws CodecException {
            String id = Registries.DATA_COMPONENT_TYPES.getId(value.getType());
            if (id == null) {
                throw new CodecException("Unregistered component type: " + value.getType());
            }

            return ops.createMap(Map.of(
                ops.createString(id),
                ComponentMap.encodeComponent(value, ops)
            ));
        }
    };

    /**
     * The primary codec used to serialize and deserialize entire {@link ItemPredicate} instances.
     * Supports resolving registered predicates by their String ID or parsing full nested objects.
     */
    public static final Codec<ItemPredicate> CODEC = new Codec<>() {
        /**
         * Decodes an ItemPredicate from a serialized format.
         *
         * @param <D>
         * The dynamic data type.
         * @param ops
         * The dynamic operations logic provider.
         * @param input
         * The raw serialized input data.
         * @return
         * The decoded {@link ItemPredicate} instance.
         * @throws CodecException
         * If the payload is malformed or references unknown registered predicates.
         */
        @Override
        public <D> ItemPredicate decode(DynamicOps<D> ops, D input) throws CodecException {
            if (ops.getStringValue(input).isPresent()) {
                String idStr = ops.getStringValue(input).get();
                ItemPredicate registered = Registries.ITEM_PREDICATES.get(idStr);
                if (registered == null) {
                    throw new CodecException("Unknown predicate: " + idStr);
                }
                return registered;
            }

            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected Map"));

            List<Condition<Key>> without = new ArrayList<>();
            List<Condition<Key>> with = new ArrayList<>();
            List<Condition<DataComponent<?>>> valued = new ArrayList<>();
            List<Condition<ItemPredicate>> predicates = new ArrayList<>();
            Key id = null;

            if (map.containsKey(ops.createString("id"))) {
                id = Codecs.KEY.decode(ops, map.get(ops.createString("id")));
            }
            if (map.containsKey(ops.createString("without"))) {
                without = Condition.codec(Codecs.KEY).list().decode(ops, map.get(ops.createString("without")));
            }
            if (map.containsKey(ops.createString("with"))) {
                with = Condition.codec(Codecs.KEY).list().decode(ops, map.get(ops.createString("with")));
            }
            if (map.containsKey(ops.createString("components"))) {
                valued = Condition.codec(COMPONENT_ENTRY_CODEC).list().decode(ops, map.get(ops.createString("components")));
            }
            if (map.containsKey(ops.createString("predicates"))) {
                predicates = Condition.codec(this).list().decode(ops, map.get(ops.createString("predicates")));
            }

            return new ItemPredicate(without, with, valued, predicates, id);
        }

        /**
         * Encodes an ItemPredicate into a serialized format.
         *
         * @param <D>
         * The dynamic data type.
         * @param ops
         * The dynamic operations logic provider.
         * @param value
         * The {@link ItemPredicate} to serialize.
         * @return
         * The encoded data, either as a reference string or a complete definition map.
         * @throws CodecException
         * If internal encoding operations fail.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, ItemPredicate value) throws CodecException {
            if (Registries.ITEM_PREDICATES.getAll().containsValue(value)) {
                return ops.createString(Registries.ITEM_PREDICATES.getId(value));
            }

            Map<D, D> map = new HashMap<>();
            if (value.id != null) {
                map.put(ops.createString("id"), Codecs.KEY.encode(ops, value.id));
            }
            if (!value.without.isEmpty()) {
                map.put(ops.createString("without"), Condition.codec(Codecs.KEY).list().encode(ops, value.without));
            }
            if (!value.with.isEmpty()) {
                map.put(ops.createString("with"), Condition.codec(Codecs.KEY).list().encode(ops, value.with));
            }
            if (!value.valued.isEmpty()) {
                map.put(ops.createString("components"), Condition.codec(COMPONENT_ENTRY_CODEC).list().encode(ops, value.valued));
            }
            if (!value.predicates.isEmpty()) {
                map.put(ops.createString("predicates"), Condition.codec(this).list().encode(ops, value.predicates));
            }
            return ops.createMap(map);
        }
    };

    /** A list of conditions checking for the strict absence of specific component Keys. */
    private final List<Condition<Key>> without;

    /** A list of conditions checking for the strict presence of specific component Keys. */
    private final List<Condition<Key>> with;

    /** A list of conditions checking for exact data matches on specific components. */
    private final List<Condition<DataComponent<?>>> valued;

    /** A list of nested predicate conditions to evaluate against the item. */
    private final List<Condition<ItemPredicate>> predicates;

    /** The specific base identity Key the item must match, if any. */
    private final Key id;

    /**
     * Constructs a new ItemPredicate with the specified conditional rules.
     *
     * @param without
     * The list of exclusionary component conditions.
     * @param with
     * The list of inclusionary component conditions.
     * @param valued
     * The list of specific value-matching component conditions.
     * @param predicates
     * The list of nested predicate conditions.
     * @param id
     * The base item Key that must match (nullable for any item).
     */
    public ItemPredicate(List<Condition<Key>> without, List<Condition<Key>> with, List<Condition<DataComponent<?>>> valued, List<Condition<ItemPredicate>> predicates, Key id) {
        this.without = without;
        this.with = with;
        this.valued = valued;
        this.predicates = predicates;
        this.id = id;
    }

    /**
     * Creates a new fluent builder instance for constructing an ItemPredicate.
     *
     * @return
     * A new {@link Builder} instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Compares this predicate against another object for logical equivalence.
     *
     * @param o
     * The object to compare.
     * @return
     * True if the object is an identical ItemPredicate.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemPredicate that)) {
            return false;
        }
        return Objects.equals(id, that.id) &&
            Objects.equals(without, that.without) &&
            Objects.equals(with, that.with) &&
            Objects.equals(valued, that.valued) &&
            Objects.equals(predicates, that.predicates);
    }

    /**
     * Generates a hash code based on the predicate's underlying conditions.
     *
     * @return
     * The integer hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(without, with, valued, predicates, id);
    }

    /**
     * Evaluates an ItemStack against all configured conditions within this predicate.
     *
     * @param stack
     * The {@link ItemStack} to test.
     * @return
     * True if the item satisfies all rules; false if it fails any rule or is null/air.
     */
    @Override
    public boolean test(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            return false;
        }

        Item item = Item.resolve(stack);
        if (item == null) {
            item = new Item(stack);
        }
        final Item finalItem = item;

        if (id != null && !id.equals(finalItem.getId())) {
            return false;
        }

        for (Condition<Key> condition : without) {
            if (condition.test(compId -> {
                DataComponentType<?> type = Registries.DATA_COMPONENT_TYPES.get(compId.toString());
                return type != null && finalItem.hasData(type);
            })) {
                return false;
            }
        }

        for (Condition<Key> condition : with) {
            if (!condition.test(compId -> {
                DataComponentType<?> type = Registries.DATA_COMPONENT_TYPES.get(compId.toString());
                return type != null && finalItem.hasData(type);
            })) {
                return false;
            }
        }

        for (Condition<DataComponent<?>> condition : valued) {
            if (!condition.test(comp -> {
                DataComponentType<?> type = comp.getType();
                if (!finalItem.hasData(type)) {
                    return false;
                }
                DataComponent<?> other = finalItem.getData(type);
                return Objects.equals(comp.getValue(), other.getValue());
            })) {
                return false;
            }
        }

        for (Condition<ItemPredicate> condition : predicates) {
            if (!condition.test(sub -> sub.test(stack))) {
                return false;
            }
        }

        return true;
    }

    /**
     * A fluent builder pattern class designed to easily construct {@link ItemPredicate} instances.
     */
    public static class Builder {
        private final List<Condition<Key>> without = new ArrayList<>();
        private final List<Condition<Key>> with = new ArrayList<>();
        private final List<Condition<DataComponent<?>>> valued = new ArrayList<>();
        private final List<Condition<ItemPredicate>> predicates = new ArrayList<>();
        private Key id = null;

        /**
         * Mandates that the item matches a specific base Key.
         *
         * @param id
         * The {@link Key} to match.
         * @return
         * This builder instance.
         */
        public Builder id(Key id) {
            this.id = id;
            return this;
        }

        /**
         * Mandates that the item matches a specific vanilla material.
         *
         * @param material
         * The {@link Material} to match.
         * @return
         * This builder instance.
         */
        public Builder material(Material material) {
            this.id = Key.key(Key.MINECRAFT_NAMESPACE, material.name().toLowerCase(Locale.ROOT));
            return this;
        }

        /**
         * Adds an exclusionary condition requiring the absence of specific components.
         *
         * @param condition
         * The {@link Condition} wrapping a component Key.
         * @return
         * This builder instance.
         */
        public Builder without(Condition<Key> condition) {
            this.without.add(condition);
            return this;
        }

        /**
         * Adds an inclusionary condition requiring the presence of specific components.
         *
         * @param condition
         * The {@link Condition} wrapping a component Key.
         * @return
         * This builder instance.
         */
        public Builder with(Condition<Key> condition) {
            this.with.add(condition);
            return this;
        }

        /**
         * Adds a value-matching condition requiring exact component equivalence.
         *
         * @param condition
         * The {@link Condition} wrapping a data component instance.
         * @return
         * This builder instance.
         */
        public Builder value(Condition<DataComponent<?>> condition) {
            this.valued.add(condition);
            return this;
        }

        /**
         * Adds a nested predicate condition.
         *
         * @param condition
         * The {@link Condition} wrapping a nested predicate.
         * @return
         * This builder instance.
         */
        public Builder check(Condition<ItemPredicate> condition) {
            this.predicates.add(condition);
            return this;
        }

        /**
         * Requires the strict absence of a specific component Key.
         *
         * @param identifier
         * The {@link Key} of the component that must be absent.
         * @return
         * This builder instance.
         */
        public Builder without(Key identifier) {
            return without(Condition.one(identifier));
        }

        /**
         * Requires the strict presence of a specific component Key.
         *
         * @param identifier
         * The {@link Key} of the component that must be present.
         * @return
         * This builder instance.
         */
        public Builder with(Key identifier) {
            return with(Condition.one(identifier));
        }

        /**
         * Requires an exact value match against a provided data component.
         *
         * @param <T>
         * The data type of the component.
         * @param component
         * The {@link DataComponent} to match against.
         * @return
         * This builder instance.
         */
        @SuppressWarnings("unchecked")
        public <T> Builder value(DataComponent<T> component) {
            return value((Condition<DataComponent<?>>) (Condition<?>) Condition.one(component));
        }

        /**
         * Evaluates a sub-predicate against the item.
         *
         * @param predicate
         * The nested {@link ItemPredicate} to test.
         * @return
         * This builder instance.
         */
        public Builder check(ItemPredicate predicate) {
            return check(Condition.one(predicate));
        }

        /**
         * Requires the presence of at least one of the provided component Keys.
         *
         * @param identifiers
         * The varargs array of component {@link Key}s to check.
         * @return
         * This builder instance.
         */
        public Builder withAny(Key... identifiers) {
            return with(Condition.anyOf(Arrays.stream(identifiers).map(Condition::one).toList()));
        }

        /**
         * Requires the presence of at least one of the provided component Keys.
         *
         * @param identifiers
         * The collection of component {@link Key}s to check.
         * @return
         * This builder instance.
         */
        public Builder withAny(Collection<Key> identifiers) {
            return with(Condition.anyOf(identifiers.stream().map(Condition::one).toList()));
        }

        /**
         * Requires an exact value match against at least one of the provided components.
         *
         * @param components
         * The varargs array of {@link DataComponent} instances to check.
         * @return
         * This builder instance.
         */
        @SuppressWarnings("unchecked")
        public Builder valueAny(DataComponent<?>... components) {
            List<Condition<DataComponent<?>>> list = new ArrayList<>();
            for (DataComponent<?> c : components) {
                list.add((Condition<DataComponent<?>>) (Condition<?>) Condition.one(c));
            }
            return value(Condition.anyOf(list));
        }

        /**
         * Requires the item to satisfy at least one of the provided sub-predicates.
         *
         * @param predicates
         * The varargs array of nested {@link ItemPredicate}s to check.
         * @return
         * This builder instance.
         */
        public Builder checkAny(ItemPredicate... predicates) {
            return check(Condition.anyOf(Arrays.stream(predicates).map(Condition::one).toList()));
        }

        /**
         * Finalizes construction and returns the built ItemPredicate.
         *
         * @return
         * The configured {@link ItemPredicate} instance.
         */
        public ItemPredicate build() {
            return new ItemPredicate(without, with, valued, predicates, id);
        }
    }
}