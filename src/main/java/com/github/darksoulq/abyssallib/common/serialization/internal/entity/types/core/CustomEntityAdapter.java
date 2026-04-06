package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.core;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomEntityAdapter extends EntityAdapter<Entity> {

    @Override
    public boolean doesApply(Entity entity) {
        return CustomEntity.resolve(entity) != null;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Entity value, Map<D, D> map) throws Codec.CodecException {
        CustomEntity<?> custom = CustomEntity.resolve(value);
        if (custom == null) return;
        Map<Attribute, Double> attributes = custom.getAttributes();
        Map<Attribute, List<AttributeModifier>> modifiers = custom.getModifiers();

        List<LivingEntityAdapter.AttributeState> customAttrs = new ArrayList<>();
        attributes.forEach((attr, val) -> {
            List<AttributeModifier> mods = modifiers.getOrDefault(attr, new ArrayList<>());
            customAttrs.add(new LivingEntityAdapter.AttributeState(attr, val, mods));
        });

        if (!customAttrs.isEmpty()) {
            map.put(ops.createString("attributes"), LivingEntityAdapter.ATTRIBUTE_STATE.list().encode(ops, customAttrs));
        }

        List<DataComponent<?>> components = custom.getComponentMap().getAllComponents();
        if (!components.isEmpty()) {
            map.put(ops.createString("custom_components"), Codecs.DATA_COMPONENT_MAP.encode(ops, components));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        CustomEntity<?> custom = CustomEntity.resolve(base);
        if (custom == null) return;

        D componentsData = map.get(ops.createString("custom_components"));
        D customAttrs = map.get(ops.createString("attributes"));
        if (componentsData != null) {
            Try.of(() -> Codecs.DATA_COMPONENT_MAP.decode(ops, componentsData)).onSuccess(list -> {
                for (DataComponent<?> component : list) custom.setData(component);
            });
        }
        if (customAttrs != null) {
            Try.of(() -> LivingEntityAdapter.ATTRIBUTE_STATE.list().decode(ops, customAttrs)).onSuccess(list -> {
                for (LivingEntityAdapter.AttributeState state : list) {
                    custom.getAttributes().put(state.attribute(), state.baseValue());
                    custom.getModifiers().compute(state.attribute(), (k, v) -> new ArrayList<>()).addAll(state.modifiers());
                }
            });
        }
    }
}