package com.github.darksoulq.abyssallib.world.item;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Condition;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.item.component.ComponentMap;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Predicate;

/**
 * A highly configurable {@link Predicate} for matching {@link ItemStack}s.
 * <p>
 * This class supports checking for specific Minecraft {@link Material}s, the presence
 * or absence of {@link DataComponent}s, specific values within components, and
 * recursive nested predicates using boolean logic (Conditions).
 */
public class ItemPredicate implements Predicate<ItemStack> {

    /**
     * Internal codec for decoding individual data component entries within a predicate.
     */
    private static final Codec<DataComponent<?>> COMPONENT_ENTRY_CODEC = new Codec<>() {
        @Override
        public <D> DataComponent<?> decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected Map for component entry"));
            if (map.isEmpty()) throw new CodecException("Empty component map");
            Map.Entry<D, D> entry = map.entrySet().iterator().next();
            String key = ops.getStringValue(entry.getKey()).orElseThrow(() -> new CodecException("Key must be string"));

            DataComponentType<?> type = Registries.DATA_COMPONENT_TYPES.get(key);
            if (type == null) throw new CodecException("Unknown component type: " + key);

            return type.codec().decode(ops, entry.getValue());
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, DataComponent<?> value) throws CodecException {
            String id = Registries.DATA_COMPONENT_TYPES.getId(value.getType());
            if (id == null) throw new CodecException("Unregistered component type: " + value.getType());

            return ops.createMap(Map.of(
                ops.createString(id),
                ComponentMap.encodeComponent(value, ops)
            ));
        }
    };

    /**
     * The primary codec for serializing and deserializing {@link ItemPredicate} instances.
     * <p>
     * Can decode from a simple string (looking up registered predicates) or a complex
     * map object defining custom filter logic.
     */
    public static final Codec<ItemPredicate> CODEC = new Codec<>() {
        @Override
        public <D> ItemPredicate decode(DynamicOps<D> ops, D input) throws CodecException {
            if (ops.getStringValue(input).isPresent()) {
                String id = ops.getStringValue(input).get();
                ItemPredicate registered = Registries.PREDICATES.get(id);
                if (registered == null) throw new CodecException("Unknown predicate: " + id);
                return registered;
            }

            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected Map"));

            List<Condition<Identifier>> without = new ArrayList<>();
            List<Condition<Identifier>> with = new ArrayList<>();
            List<Condition<DataComponent<?>>> valued = new ArrayList<>();
            List<Condition<ItemPredicate>> predicates = new ArrayList<>();
            Material material = null;

            if (map.containsKey(ops.createString("type"))) {
                material = Codec.enumCodec(Material.class).decode(ops, map.get(ops.createString("type")));
            }
            if (map.containsKey(ops.createString("without"))) {
                without = Condition.codec(Codecs.IDENTIFIER).list().decode(ops, map.get(ops.createString("without")));
            }
            if (map.containsKey(ops.createString("with"))) {
                with = Condition.codec(Codecs.IDENTIFIER).list().decode(ops, map.get(ops.createString("with")));
            }
            if (map.containsKey(ops.createString("components"))) {
                valued = Condition.codec(COMPONENT_ENTRY_CODEC).list().decode(ops, map.get(ops.createString("components")));
            }
            if (map.containsKey(ops.createString("predicates"))) {
                predicates = Condition.codec(this).list().decode(ops, map.get(ops.createString("predicates")));
            }

            return new ItemPredicate(without, with, valued, predicates, material);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, ItemPredicate value) throws CodecException {
            if (Registries.PREDICATES.getAll().containsValue(value)) {
                return ops.createString(Registries.PREDICATES.getId(value));
            }

            Map<D, D> map = new HashMap<>();
            if (value.material != null) {
                map.put(ops.createString("type"), Codec.enumCodec(Material.class).encode(ops, value.material));
            }
            if (!value.without.isEmpty()) {
                map.put(ops.createString("without"), Condition.codec(Codecs.IDENTIFIER).list().encode(ops, value.without));
            }
            if (!value.with.isEmpty()) {
                map.put(ops.createString("with"), Condition.codec(Codecs.IDENTIFIER).list().encode(ops, value.with));
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

    /** Conditions defining what component identifiers MUST NOT be present on the item. */
    private final List<Condition<Identifier>> without;
    /** Conditions defining what component identifiers MUST be present on the item. */
    private final List<Condition<Identifier>> with;
    /** Conditions defining specific components and values that MUST match on the item. */
    private final List<Condition<DataComponent<?>>> valued;
    /** Conditions defining nested sub-predicates that MUST evaluate to true. */
    private final List<Condition<ItemPredicate>> predicates;
    /** The specific material type to match, or null for any material. */
    private final Material material;

    /**
     * Internal constructor for building a predicate.
     *
     * @param without    List of exclusion conditions.
     * @param with       List of inclusion conditions.
     * @param valued     List of component value conditions.
     * @param predicates List of sub-predicate conditions.
     * @param material   The required {@link Material}.
     */
    public ItemPredicate(List<Condition<Identifier>> without,
                         List<Condition<Identifier>> with,
                         List<Condition<DataComponent<?>>> valued,
                         List<Condition<ItemPredicate>> predicates,
                         Material material) {
        this.without = without;
        this.with = with;
        this.valued = valued;
        this.predicates = predicates;
        this.material = material;
    }

    /**
     * @return A new builder instance for creating an ItemPredicate.
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemPredicate that)) return false;
        return Objects.equals(material, that.material) &&
            Objects.equals(without, that.without) &&
            Objects.equals(with, that.with) &&
            Objects.equals(valued, that.valued) &&
            Objects.equals(predicates, that.predicates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(without, with, valued, predicates, material);
    }

    /**
     * Evaluates whether the provided {@link ItemStack} satisfies all conditions of this predicate.
     *
     * @param stack The item stack to test.
     * @return {@code true} if the stack matches all filters; {@code false} otherwise.
     */
    @Override
    public boolean test(ItemStack stack) {
        Item item = Item.resolve(stack);
        if (item == null) item = new Item(stack);
        final Item finalItem = item;

        if (material != null && !material.equals(stack.getType())) return false;

        for (Condition<Identifier> condition : without) {
            if (condition.test(id -> {
                DataComponentType<?> type = Registries.DATA_COMPONENT_TYPES.get(id.toString());
                return type != null && finalItem.hasData(type);
            })) return false;
        }

        for (Condition<Identifier> condition : with) {
            if (!condition.test(id -> {
                DataComponentType<?> type = Registries.DATA_COMPONENT_TYPES.get(id.toString());
                return type != null && finalItem.hasData(type);
            })) return false;
        }

        for (Condition<DataComponent<?>> condition : valued) {
            if (!condition.test(comp -> {
                DataComponentType<?> type = comp.getType();
                if (!finalItem.hasData(type)) return false;
                DataComponent<?> other = finalItem.getData(type);
                return Objects.equals(comp.getValue(), other.getValue());
            })) {
                return false;
            }
        }

        for (Condition<ItemPredicate> condition : predicates) {
            if (!condition.test(sub -> sub.test(stack))) return false;
        }

        return true;
    }

    /**
     * A fluent builder for constructing complex {@link ItemPredicate} instances.
     */
    public static class Builder {
        private final List<Condition<Identifier>> without = new ArrayList<>();
        private final List<Condition<Identifier>> with = new ArrayList<>();
        private final List<Condition<DataComponent<?>>> valued = new ArrayList<>();
        private final List<Condition<ItemPredicate>> predicates = new ArrayList<>();
        private Material material = null;

        /**
         * Sets the required material type.
         * @param material The {@link Material}.
         * @return This builder.
         */
        public Builder material(Material material) {
            this.material = material;
            return this;
        }

        /**
         * Adds a condition that must fail (the item must NOT have these components).
         * @param condition The exclusion condition.
         * @return This builder.
         */
        public Builder without(Condition<Identifier> condition) {
            this.without.add(condition);
            return this;
        }

        /**
         * Adds a condition that must succeed (the item must have these components).
         * @param condition The inclusion condition.
         * @return This builder.
         */
        public Builder with(Condition<Identifier> condition) {
            this.with.add(condition);
            return this;
        }

        /**
         * Adds a condition that checks specific component values.
         * @param condition The value-matching condition.
         * @return This builder.
         */
        public Builder value(Condition<DataComponent<?>> condition) {
            this.valued.add(condition);
            return this;
        }

        /**
         * Adds a sub-predicate condition for nested logic.
         * @param condition The sub-predicate condition.
         * @return This builder.
         */
        public Builder check(Condition<ItemPredicate> condition) {
            this.predicates.add(condition);
            return this;
        }

        /**
         * Adds an exclusion for a specific component ID.
         * @param identifier The component ID.
         * @return This builder.
         */
        public Builder without(Identifier identifier) {
            return without(Condition.one(identifier));
        }

        /**
         * Adds a requirement for a specific component ID.
         * @param identifier The component ID.
         * @return This builder.
         */
        public Builder with(Identifier identifier) {
            return with(Condition.one(identifier));
        }

        /**
         * Adds a requirement for a component with a specific value.
         * @param component The component instance with value.
         * @param <T> The value type.
         * @return This builder.
         */
        @SuppressWarnings("unchecked")
        public <T> Builder value(DataComponent<T> component) {
            return value((Condition<DataComponent<?>>) (Condition<?>) Condition.one(component));
        }

        /**
         * Adds a requirement for a specific sub-predicate to pass.
         * @param predicate The predicate.
         * @return This builder.
         */
        public Builder check(ItemPredicate predicate) {
            return check(Condition.one(predicate));
        }

        /**
         * Adds a condition requiring ANY of the provided identifiers to be present.
         * @param identifiers Varargs of identifiers.
         * @return This builder.
         */
        public Builder withAny(Identifier... identifiers) {
            return with(Condition.anyOf(Arrays.stream(identifiers).map(Condition::one).toList()));
        }

        /**
         * Adds a condition requiring ANY of the provided identifiers to be present.
         * @param identifiers Collection of identifiers.
         * @return This builder.
         */
        public Builder withAny(Collection<Identifier> identifiers) {
            return with(Condition.anyOf(identifiers.stream().map(Condition::one).toList()));
        }

        /**
         * Adds a condition requiring ANY of the provided component values to match.
         * @param components Varargs of data components.
         * @return This builder.
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
         * Adds a condition requiring ANY of the provided sub-predicates to pass.
         * @param predicates Varargs of predicates.
         * @return This builder.
         */
        public Builder checkAny(ItemPredicate... predicates) {
            return check(Condition.anyOf(Arrays.stream(predicates).map(Condition::one).toList()));
        }

        /**
         * Builds the {@link ItemPredicate} instance.
         * @return The resulting predicate.
         */
        public ItemPredicate build() {
            return new ItemPredicate(without, with, valued, predicates, material);
        }
    }
}