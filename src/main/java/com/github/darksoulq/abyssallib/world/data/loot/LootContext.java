package com.github.darksoulq.abyssallib.world.data.loot;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Immutable record containing the environment and entity data for loot generation.
 * @param location The {@link Location} where loot is being generated.
 * @param looter   The {@link Entity} (usually a Player) receiving the loot.
 * @param killer   The {@link Entity} that caused a death (if applicable).
 * @param victim   The {@link Entity} that died (if applicable).
 * @param tool     The {@link ItemStack} used to obtain the loot.
 * @param luck     The calculated luck value used for bonus rolls and quality.
 * @param random   The {@link Random} instance used for selections.
 */
public record LootContext(Location location, @Nullable Entity looter, @Nullable Entity killer, @Nullable Entity victim, @Nullable ItemStack tool, float luck, Random random) {
    /**
     * Initializes a builder for a LootContext.
     *
     * @param loc The world location.
     * @return A new {@link Builder} instance.
     */
    public static Builder builder(Location loc) {
        return new Builder(loc);
    }

    /** Builder class for constructing {@link LootContext} instances. */
    public static class Builder {
        private final Location location;
        private Entity looter;
        private Entity killer;
        private Entity victim;
        private ItemStack tool;
        private float luck;
        private Random random;

        /** @param location The world location. */
        public Builder(Location location) {
            this.location = location;
            this.random = new Random();
        }

        /** @param looter The entity collecting loot. @return This builder. */
        public Builder looter(Entity looter) {
            this.looter = looter;
            if (looter instanceof Player p) {
                this.luck = (float) p.getAttribute(Attribute.LUCK).getValue();
            }
            return this;
        }

        /** @param killer The attacker entity. @return This builder. */
        public Builder killer(Entity killer) {
            this.killer = killer;
            return this;
        }

        /** @param victim The slain entity. @return This builder. */
        public Builder victim(Entity victim) {
            this.victim = victim;
            return this;
        }

        /** @param tool The tool stack. @return This builder. */
        public Builder tool(ItemStack tool) {
            this.tool = tool;
            return this;
        }

        /** @param luck Manual luck value. @return This builder. */
        public Builder luck(float luck) {
            this.luck = luck;
            return this;
        }

        /** @param random Custom random source. @return This builder. */
        public Builder random(Random random) {
            this.random = random;
            return this;
        }

        /** @return A new {@link LootContext} instance. */
        public LootContext build() {
            return new LootContext(location, looter, killer, victim, tool, luck, random);
        }
    }
}