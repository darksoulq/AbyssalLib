package com.github.darksoulq.abyssallib.world.level.entity;

import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import io.papermc.paper.registry.data.DamageTypeRegistryEntry;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.keys.DamageTypeKeys;
import org.bukkit.damage.DamageEffect;
import org.bukkit.damage.DamageScaling;
import org.bukkit.damage.DeathMessageType;

import java.util.List;

/**
 * Represents a custom damage type to be registered into the Paper {@code DamageType} registry.
 *
 * <p>Each {@code DamageType} can have custom audio effects, exhaustion, and scaling behavior.
 * Instances are registered automatically via a {@code DeferredRegistry}
 * in plugin bootstrap.</p>
 */
public class DamageType {

    /** Unique identifier of the damage type. */
    private Identifier id;

    /** Visual/audio effect applied when this damage is dealt. */
    private DamageEffect effect = DamageEffect.HURT;

    /** Scaling behavior for the damage amount. */
    private DamageScaling scaling = DamageScaling.NEVER;

    /** The type of death message displayed if this damage causes death. */
    private DeathMessageType messageType = DeathMessageType.DEFAULT;

    /** The amount of hunger exhaustion this damage produces. */
    private float exhaustion = 0;

    /**
     * Creates a new custom {@code DamageType} with the given identifier.
     *
     * @param id Unique namespaced ID for this damage type.
     */
    public DamageType(Identifier id) {
        this.id = id;
    }

    /**
     * Sets the visual/audio effect shown when this damage is applied.
     *
     * @param effect The {@link DamageEffect} to use.
     */
    public void damageEffect(DamageEffect effect) {
        this.effect = effect;
    }

    /**
     * Sets the damage scaling behavior for this damage type.
     *
     * @param scaling The {@link DamageScaling} strategy to apply.
     */
    public void damageScaling(DamageScaling scaling) {
        this.scaling = scaling;
    }

    /**
     * Sets the hunger exhaustion value applied when this damage is dealt.
     *
     * @param exhaustion The exhaustion amount (e.g., for sprinting penalty).
     */
    public void exhaustion(float exhaustion) {
        this.exhaustion = exhaustion;
    }

    /**
     * Internal class responsible for registering {@link DamageType} instances to the Paper registry.
     *
     * <p><strong>This class is not intended to be constructed or used directly by plugin developers.</strong>
     * It is automatically invoked when a {@code DeferredRegistry<DamageType>} is applied via
     * {@code #apply} in {@code PluginBootstrap}.</p>
     */
    public static class Registrar {

        private RegistryFreezeEvent<org.bukkit.damage.DamageType, DamageTypeRegistryEntry.Builder> event;

        /**
         * Internal constructor used during registry freeze to bind and register custom damage types.
         *
         * @param event The registry freeze event triggered by Paper.
         */
        public Registrar(RegistryFreezeEvent<org.bukkit.damage.DamageType,
                DamageTypeRegistryEntry.@org.jetbrains.annotations.NotNull Builder> event) {
            this.event = event;
        }

        /**
         * Registers all provided {@link DamageType} instances into the Paper damage registry.
         *
         * <p>This method is called internally when {@code DeferredRegistry#apply()} is used.</p>
         *
         * @param types List of {@link DamageType} entries to register.
         */
        public void register(List<DamageType> types) {
            types.forEach(type -> {
                event.registry().register(
                        DamageTypeKeys.create(type.id.toKey()),
                        (builder) -> {
                            builder.damageEffect(type.effect);
                            builder.damageScaling(type.scaling);
                            builder.deathMessageType(type.messageType);
                            builder.messageId(type.id.toString().replace(':', '.'));
                            builder.exhaustion(type.exhaustion);
                        }
                );
            });
        }
    }
}
