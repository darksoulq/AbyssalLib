package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.core;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
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

    public static final Codec<AttributeState> ATTRIBUTE_STATE = RecordBuilder.create(instance -> instance.group(
        ExtraCodecs.ATTRIBUTE.fieldOf("attribute").forGetter(AttributeState::attribute),
        Codecs.DOUBLE.fieldOf("base_value").forGetter(AttributeState::baseValue),
        ExtraCodecs.ATTRIBUTE_MODIFIER.list().fieldOf("modifiers").forGetter(AttributeState::modifiers)
    ).apply(instance, AttributeState::new)).describe("AttributeState");

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof LivingEntity;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, LivingEntity value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("health", Codecs.DOUBLE, value.getHealth())
            .write("absorption", Codecs.DOUBLE, value.getAbsorptionAmount())
            .write("ai", Codecs.BOOLEAN, value.hasAI())
            .write("collidable", Codecs.BOOLEAN, value.isCollidable())
            .write("gliding", Codecs.BOOLEAN, value.isGliding())
            .write("swimming", Codecs.BOOLEAN, value.isSwimming())
            .write("max_no_damage_ticks", Codecs.INT, value.getMaximumNoDamageTicks());

        List<AttributeState> attrs = new ArrayList<>();
        for (Attribute attr : RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE)) {
            AttributeInstance inst = value.getAttribute(attr);
            if (inst != null) {
                attrs.add(new AttributeState(inst.getAttribute(), inst.getBaseValue(), new ArrayList<>(inst.getModifiers())));
            }
        }
        ctx.write("attributes", ATTRIBUTE_STATE.list(), attrs);

        ctx.write("potion_effects", ExtraCodecs.POTION_EFFECT.list(), new ArrayList<>(value.getActivePotionEffects()));

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof LivingEntity living)) return DataResult.success(null);

        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("attributes", ATTRIBUTE_STATE.list(), opt -> opt.ifPresent(states -> states.forEach(a -> a.apply(living))));

        ctx.readOptional("health", Codecs.DOUBLE, opt -> opt.ifPresent(health -> {
            AttributeInstance maxHealthAttr = living.getAttribute(Attribute.MAX_HEALTH);
            double maxHealth = maxHealthAttr != null ? maxHealthAttr.getValue() : health;
            living.setHealth(Math.min(health, maxHealth));
        }));

        ctx.readOptional("absorption", Codecs.DOUBLE, opt -> opt.ifPresent(living::setAbsorptionAmount))
            .readOptional("ai", Codecs.BOOLEAN, opt -> opt.ifPresent(living::setAI))
            .readOptional("collidable", Codecs.BOOLEAN, opt -> opt.ifPresent(living::setCollidable))
            .readOptional("gliding", Codecs.BOOLEAN, opt -> opt.ifPresent(living::setGliding))
            .readOptional("swimming", Codecs.BOOLEAN, opt -> opt.ifPresent(living::setSwimming))
            .readOptional("max_no_damage_ticks", Codecs.INT, opt -> opt.ifPresent(living::setMaximumNoDamageTicks));

        ctx.readOptional("potion_effects", ExtraCodecs.POTION_EFFECT.list(), opt -> opt.ifPresent(effects -> {
            for (PotionEffect effect : living.getActivePotionEffects()) living.removePotionEffect(effect.getType());
            living.addPotionEffects(effects);
        }));

        return ctx.result();
    }
}