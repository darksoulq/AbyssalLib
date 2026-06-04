package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.core;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
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
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Entity value, Map<D, D> map) {
        CustomEntity<?> custom = CustomEntity.resolve(value);
        if (custom == null) return DataResult.success(null);

        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        Map<Attribute, Double> attributes = custom.getAttributes();
        Map<Attribute, List<AttributeModifier>> modifiers = custom.getModifiers();

        List<LivingEntityAdapter.AttributeState> customAttrs = new ArrayList<>();
        attributes.forEach((attr, val) -> {
            List<AttributeModifier> mods = modifiers.getOrDefault(attr, new ArrayList<>());
            customAttrs.add(new LivingEntityAdapter.AttributeState(attr, val, mods));
        });

        if (!customAttrs.isEmpty()) {
            ctx.write("attributes", LivingEntityAdapter.ATTRIBUTE_STATE.list(), customAttrs);
        }

        List<DataComponent<?>> components = custom.getComponentMap().getAllComponents();
        if (!components.isEmpty()) {
            ctx.write("custom_components", Codecs.DATA_COMPONENT_MAP, components);
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        CustomEntity<?> custom = CustomEntity.resolve(base);
        if (custom == null) return DataResult.success(null);

        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("custom_components", Codecs.DATA_COMPONENT_MAP, opt -> opt.ifPresent(components -> {
            for (DataComponent<?> component : components) {
                custom.setData(component);
            }
        }));

        ctx.readOptional("attributes", LivingEntityAdapter.ATTRIBUTE_STATE.list(), opt -> opt.ifPresent(states -> {
            for (LivingEntityAdapter.AttributeState state : states) {
                custom.getAttributes().put(state.attribute(), state.baseValue());
                custom.getModifiers().compute(state.attribute(), (k, v) -> new ArrayList<>()).addAll(state.modifiers());
            }
        }));

        return ctx.result();
    }
}