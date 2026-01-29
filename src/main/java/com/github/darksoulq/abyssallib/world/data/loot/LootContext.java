package com.github.darksoulq.abyssallib.world.data.loot;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Random;

public record LootContext(
    Location location,
    @Nullable Entity looter,
    @Nullable Entity killer,
    @Nullable Entity victim,
    @Nullable ItemStack tool,
    float luck,
    Random random
) {
    public static Builder builder(Location loc) {
        return new Builder(loc);
    }

    public static class Builder {
        private final Location location;
        private Entity looter;
        private Entity killer;
        private Entity victim;
        private ItemStack tool;
        private float luck;
        private Random random;

        public Builder(Location location) {
            this.location = location;
            this.random = new Random();
        }

        public Builder looter(Entity looter) {
            this.looter = looter;
            if (looter instanceof Player p) {
                this.luck = (float) p.getAttribute(Attribute.LUCK).getValue();
            }
            return this;
        }

        public Builder killer(Entity killer) {
            this.killer = killer;
            return this;
        }

        public Builder victim(Entity victim) {
            this.victim = victim;
            return this;
        }

        public Builder tool(ItemStack tool) {
            this.tool = tool;
            return this;
        }

        public Builder luck(float luck) {
            this.luck = luck;
            return this;
        }

        public Builder random(Random random) {
            this.random = random;
            return this;
        }

        public LootContext build() {
            return new LootContext(location, looter, killer, victim, tool, luck, random);
        }
    }
}