package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.core;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordCodecBuilder;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LivingEntityAdapter extends EntityAdapter<LivingEntity> {

    public record AttributeState(Attribute attribute, double baseValue, List<AttributeModifier> modifiers) {
        public void apply(LivingEntity entity) {
            AttributeInstance instance = entity.getAttribute(attribute);
            if (instance != null) {
                instance.setBaseValue(baseValue);
                for (AttributeModifier mod : instance.getModifiers()) instance.removeModifier(mod);
                for (AttributeModifier mod : modifiers) instance.addModifier(mod);
            }
        }
    }

    public static final Codec<AttributeState> ATTRIBUTE_STATE = RecordCodecBuilder.create(
            ExtraCodecs.ATTRIBUTE.fieldOf("attribute", AttributeState::attribute),
            Codecs.DOUBLE.fieldOf("base_value", AttributeState::baseValue),
            ExtraCodecs.ATTRIBUTE_MODIFIER.list().fieldOf("modifiers", AttributeState::modifiers),
            AttributeState::new
    );

    @Override
    public boolean doesApply(Entity entity) { return entity instanceof LivingEntity; }

    @Override
    public <D> void serialize(DynamicOps<D> ops, LivingEntity value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("health"), Codecs.DOUBLE.encode(ops, value.getHealth()));
        map.put(ops.createString("absorption"), Codecs.DOUBLE.encode(ops, value.getAbsorptionAmount()));
        map.put(ops.createString("ai"), Codecs.BOOLEAN.encode(ops, value.hasAI()));
        map.put(ops.createString("collidable"), Codecs.BOOLEAN.encode(ops, value.isCollidable()));
        map.put(ops.createString("gliding"), Codecs.BOOLEAN.encode(ops, value.isGliding()));
        map.put(ops.createString("swimming"), Codecs.BOOLEAN.encode(ops, value.isSwimming()));
        map.put(ops.createString("max_no_damage_ticks"), Codecs.INT.encode(ops, value.getMaximumNoDamageTicks()));

        List<AttributeState> attrs = new ArrayList<>();
        for (Attribute attr : RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE)) {
            AttributeInstance inst = value.getAttribute(attr);
            if (inst != null) attrs.add(new AttributeState(inst.getAttribute(), inst.getBaseValue(), new ArrayList<>(inst.getModifiers())));
        }
        map.put(ops.createString("attributes"), ATTRIBUTE_STATE.list().encode(ops, attrs));
        map.put(ops.createString("potion_effects"), ExtraCodecs.POTION_EFFECT.list().encode(ops, new ArrayList<>(value.getActivePotionEffects())));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof LivingEntity living)) return;

        D attrs = map.get(ops.createString("attributes"));
        if (attrs != null) {
            Try.of(() -> ATTRIBUTE_STATE.list().decode(ops, attrs)).onSuccess(list -> list.forEach(a -> a.apply(living)));
        }

        Try.of(() -> Codecs.DOUBLE.decode(ops, map.get(ops.createString("health")))).onSuccess(h -> {
            AttributeInstance maxHealthAttr = living.getAttribute(Attribute.MAX_HEALTH);
            double maxHealth = maxHealthAttr != null ? maxHealthAttr.getValue() : h;
            living.setHealth(Math.min(h, maxHealth));
        });

        Try.of(() -> Codecs.DOUBLE.decode(ops, map.get(ops.createString("absorption")))).onSuccess(living::setAbsorptionAmount);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("ai")))).onSuccess(living::setAI);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("collidable")))).onSuccess(living::setCollidable);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("gliding")))).onSuccess(living::setGliding);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("swimming")))).onSuccess(living::setSwimming);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("max_no_damage_ticks")))).onSuccess(living::setMaximumNoDamageTicks);

        D potions = map.get(ops.createString("potion_effects"));
        if (potions != null) {
            Try.of(() -> ExtraCodecs.POTION_EFFECT.list().decode(ops, potions)).onSuccess(list -> {
                for (PotionEffect effect : living.getActivePotionEffects()) living.removePotionEffect(effect.getType());
                living.addPotionEffects(list);
            });
        }
    }
}