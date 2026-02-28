package com.github.darksoulq.abyssallib.world.entity;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.DamageTypeRegistryEntry;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.DamageTypeKeys;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
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
    private Key id;

    /** Visual/audio effect applied when this damage is dealt. */
    private DamageEffect effect = DamageEffect.HURT;

    /** Scaling behavior for the damage amount. */
    private DamageScaling scaling = DamageScaling.NEVER;

    /** The type of death message displayed if this damage causes death. */
    private DeathMessageType messageType = DeathMessageType.DEFAULT;

    /**
     * The ID of the Death Message (e.g death.attack.<message_id>). Only effective if DeathMessageType is DEFAULT
     */
    private String messageId = "";

    /** The amount of hunger exhaustion this damage produces. */
    private float exhaustion = 0;

    /**
     * Creates a new custom {@code DamageType} with the given identifier.
     *
     * @param id Unique namespaced ID for this damage type.
     */
    public DamageType(Key id) {
        this.id = id;
    }

    public DamageSource withCause(Entity cause) {
        org.bukkit.damage.DamageType dmg = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).getOrThrow(id);
        return DamageSource.builder(dmg).withCausingEntity(cause).build();
    }
    public DamageSource withDirect(Entity direct) {
        org.bukkit.damage.DamageType dmg = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).getOrThrow(id);
        return DamageSource.builder(dmg).withDirectEntity(direct).build();
    }
    public DamageSource withLocation(Location loc) {
        org.bukkit.damage.DamageType dmg = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).getOrThrow(id);
        return DamageSource.builder(dmg).withDamageLocation(loc).build();
    }

    public void register(BootstrapContext ctx) {
        ctx.getLifecycleManager().registerEventHandler(RegistryEvents.DAMAGE_TYPE.compose(), event -> {
            event.registry().register(
                DamageTypeKeys.create(id),
                builder -> {
                    builder.damageEffect(effect);
                    builder.damageScaling(scaling);
                    builder.deathMessageType(messageType);
                    builder.exhaustion(exhaustion);
                    builder.messageId(messageId);
                }
            );
        });
    }

    public static Builder builder(Key id) {
        return new Builder(id);
    }

    protected static class Builder {
        private Key id;
        private DamageEffect effect = DamageEffect.HURT;
        private DamageScaling scaling = DamageScaling.NEVER;
        private DeathMessageType messageType = DeathMessageType.DEFAULT;
        private String messageId = "";
        private float exhaustion = 0;

        /**
         * Creates a new custom {@code DamageType} with the given identifier.
         *
         * @param id Unique namespaced ID for this damage type.
         */
        public Builder(Key id) {
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

        /**
         * Sets the Message ID of the Death Message (e.g death.attack.<message_id>).
         * Only works if deathMessageType is set to DEFAULT.
         *
         * @param messageId
         * @return This builder
         */
        public Builder messageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public DamageType build() {
            DamageType dmg = new DamageType(id);
            dmg.scaling = scaling;
            dmg.effect = effect;
            dmg.exhaustion = exhaustion;
            dmg.messageId =messageId;
            return dmg;
        }
    }
}
