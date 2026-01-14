package com.github.darksoulq.abyssallib.world.item;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Condition;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.item.component.ComponentMap;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;

public class ItemPredicate implements Predicate<ItemStack> {

    private static final Codec<DataComponent<?>> COMPONENT_ENTRY_CODEC = new Codec<>() {
        @Override
        public <D> DataComponent<?> decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected Map for component entry"));
            if (map.isEmpty()) throw new CodecException("Empty component map");
            Map.Entry<D, D> entry = map.entrySet().iterator().next();
            String key = ops.getStringValue(entry.getKey()).orElseThrow(() -> new CodecException("Key must be string"));

            Class<? extends DataComponent<?>> cls = Registries.DATA_COMPONENTS.get(key);
            if (cls == null) throw new CodecException("Unknown component: " + key);

            Codec<?> codec = ComponentMap.COMPONENT_CODEC_CACHE.computeIfAbsent(cls, k -> Try.of(() -> {
                java.lang.reflect.Field codecField = k.getDeclaredField("CODEC");
                if (!Modifier.isStatic(codecField.getModifiers())) throw new NoSuchFieldException("Field CODEC must be static");
                codecField.setAccessible(true);
                return (Codec<?>) codecField.get(null);
            }).orElse(null));

            if (codec == null) throw new CodecException("No Codec found for " + key);
            return (DataComponent<?>) codec.decode(ops, entry.getValue());
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, DataComponent<?> value) {
            return ops.createMap(Map.of(
                ops.createString(value.getId().toString()),
                ComponentMap.encodeComponent(value, ops)
            ));
        }
    };

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

    private final List<Condition<Identifier>> without;
    private final List<Condition<Identifier>> with;
    private final List<Condition<DataComponent<?>>> valued;
    private final List<Condition<ItemPredicate>> predicates;
    private final Material material;

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

    @Override
    public boolean test(ItemStack stack) {
        Item item = Item.resolve(stack);
        if (item == null) item = new Item(stack);
        final Item finalItem = item;

        if (material != null && !material.equals(stack.getType())) return false;

        for (Condition<Identifier> condition : without) {
            if (condition.test(finalItem::hasData)) return false;
        }
        for (Condition<Identifier> condition : with) {
            if (!condition.test(finalItem::hasData)) return false;
        }
        for (Condition<DataComponent<?>> condition : valued) {
            if (!condition.test(comp -> {
                DataComponent<?> other = finalItem.getData(comp.getId());
                return other != null && Objects.equals(comp.value, other.value);
            })) {
                return false;
            }
        }
        for (Condition<ItemPredicate> condition : predicates) {
            if (!condition.test(sub -> sub.test(stack))) return false;
        }

        return true;
    }

    public static class Builder {
        private final List<Condition<Identifier>> without = new ArrayList<>();
        private final List<Condition<Identifier>> with = new ArrayList<>();
        private final List<Condition<DataComponent<?>>> valued = new ArrayList<>();
        private final List<Condition<ItemPredicate>> predicates = new ArrayList<>();
        private Material material = null;

        public Builder material(Material material) {
            this.material = material;
            return this;
        }

        public Builder without(Condition<Identifier> condition) {
            this.without.add(condition);
            return this;
        }

        public Builder with(Condition<Identifier> condition) {
            this.with.add(condition);
            return this;
        }

        public Builder value(Condition<DataComponent<?>> condition) {
            this.valued.add(condition);
            return this;
        }

        public Builder check(Condition<ItemPredicate> condition) {
            this.predicates.add(condition);
            return this;
        }

        public Builder without(Identifier identifier) {
            return without(Condition.one(identifier));
        }

        public Builder with(Identifier identifier) {
            return with(Condition.one(identifier));
        }

        @SuppressWarnings("unchecked")
        public <T> Builder value(DataComponent<T> component) {
            return value((Condition<DataComponent<?>>) (Condition<?>) Condition.one(component));
        }

        public Builder check(ItemPredicate predicate) {
            return check(Condition.one(predicate));
        }

        public Builder withAny(Identifier... identifiers) {
            return with(Condition.anyOf(Arrays.stream(identifiers).map(Condition::one).toList()));
        }

        public Builder withAny(Collection<Identifier> identifiers) {
            return with(Condition.anyOf(identifiers.stream().map(Condition::one).toList()));
        }

        @SuppressWarnings("unchecked")
        public Builder valueAny(DataComponent<?>... components) {
            List<Condition<DataComponent<?>>> list = new ArrayList<>();
            for (DataComponent<?> c : components) {
                list.add((Condition<DataComponent<?>>) (Condition<?>) Condition.one(c));
            }
            return value(Condition.anyOf(list));
        }

        public Builder checkAny(ItemPredicate... predicates) {
            return check(Condition.anyOf(Arrays.stream(predicates).map(Condition::one).toList()));
        }

        public ItemPredicate build() {
            return new ItemPredicate(without, with, valued, predicates, material);
        }
    }
}