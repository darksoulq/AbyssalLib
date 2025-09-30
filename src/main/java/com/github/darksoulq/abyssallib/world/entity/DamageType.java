package com.github.darksoulq.abyssallib.world.entity;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.DamageTypeRegistryEntry;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.keys.DamageTypeKeys;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.damage.DamageEffect;
import org.bukkit.damage.DamageScaling;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DeathMessageType;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a custom damage type to be registered into the Paper {@code DamageType} registry.
 *
 * <p>Each {@code DamageType} can have custom audio effects, exhaustion, and scaling behavior.
 * Instances are registered automatically via a {@code DeferredRegistry}
 * in plugin bootstrap.</p>
 */
@ApiStatus.Experimental
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

    public DamageSource withCause(Entity cause) {
        org.bukkit.damage.DamageType dmg = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE)
                .getOrThrow(id.asNamespacedKey());
        return DamageSource.builder(dmg).withCausingEntity(cause).build();
    }
    public DamageSource withDirect(Entity direct) {
        org.bukkit.damage.DamageType dmg = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE)
                .getOrThrow(id.asNamespacedKey());
        return DamageSource.builder(dmg).withDirectEntity(direct).build();
    }
    public DamageSource withLocation(Location loc) {
        org.bukkit.damage.DamageType dmg = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE)
                .getOrThrow(id.asNamespacedKey());
        return DamageSource.builder(dmg).withDamageLocation(loc).build();
    }

    public static Builder builder(Identifier id) {
        return new Builder(id);
    }

    protected static class Builder {
        private Identifier id;
        private DamageEffect effect = DamageEffect.HURT;
        private DamageScaling scaling = DamageScaling.NEVER;
        private DeathMessageType messageType = DeathMessageType.DEFAULT;
        private float exhaustion = 0;

        /**
         * Creates a new custom {@code DamageType} with the given identifier.
         *
         * @param id Unique namespaced ID for this damage type.
         */
        public Builder(Identifier id) {
            this.id = id;
        }

        /**
         * Sets the visual/audio effect shown when this damage is applied.
         *
         * @param effect The {@link DamageEffect} to use.
         */
        public Builder damageEffect(DamageEffect effect) {
            this.effect = effect;
            return this;
        }

        /**
         * Sets the damage scaling behavior for this damage type.
         *
         * @param scaling The {@link DamageScaling} strategy to apply.
         */
        public Builder damageScaling(DamageScaling scaling) {
            this.scaling = scaling;
            return this;
        }

        /**
         * Sets the hunger exhaustion value applied when this damage is dealt.
         *
         * @param exhaustion The exhaustion amount (e.g., for sprinting penalty).
         */
        public Builder exhaustion(float exhaustion) {
            this.exhaustion = exhaustion;
            return this;
        }

        /**
         * Sets the Death Message Type of this DamageType
         *
         * @param messageType the Death Message Type.
         */
        public Builder deathMessageType(DeathMessageType messageType) {
            this.messageType = messageType;
            return this;
        }

        public DamageType build() {
            DamageType dmg = new DamageType(id);
            dmg.scaling = scaling;
            dmg.effect = effect;
            dmg.exhaustion = exhaustion;
            return dmg;
        }
    }

    /**
     * Internal class responsible for registering {@link DamageType} instances to the Paper registry.
     *
     * <p><strong>This class is not intended to be constructed or used directly by plugin developers.</strong>
     * It is automatically invoked when a {@code DeferredRegistry<DamageType>} is applied via
     * {@code #apply} in {@code PluginBootstrap}.</p>
     */
    @ApiStatus.Internal
    public static class Registrar {

        private RegistryComposeEvent<org.bukkit.damage.DamageType, DamageTypeRegistryEntry.Builder> event;

        /**
         * Internal constructor used during registry freeze to bind and register custom damage types.
         *
         * @param event The registry freeze event triggered by Paper.
         */
        public Registrar(RegistryComposeEvent<org.bukkit.damage.DamageType,
                        DamageTypeRegistryEntry.@NotNull Builder> event) {
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
                        DamageTypeKeys.create(type.id.asNamespacedKey()),
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
