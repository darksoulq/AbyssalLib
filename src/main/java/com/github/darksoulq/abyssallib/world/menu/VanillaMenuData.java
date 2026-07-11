package com.github.darksoulq.abyssallib.world.menu;

public final class VanillaMenuData {

    public static class Furnace extends ContainerData {
        public Furnace() {
            super(4);
        }

        public int getBurnTime() {
            return this.get(0);
        }

        public void setBurnTime(int value) {
            this.set(0, value);
        }

        public int getMaxBurnTime() {
            return this.get(1);
        }

        public void setMaxBurnTime(int value) {
            this.set(1, value);
        }

        public int getProgress() {
            return this.get(2);
        }

        public void setProgress(int value) {
            this.set(2, value);
        }

        public int getMaxProgress() {
            return this.get(3);
        }

        public void setMaxProgress(int value) {
            this.set(3, value);
        }
    }

    public static class BrewingStand extends ContainerData {
        public BrewingStand() {
            super(2);
        }

        public int getBrewTime() {
            return this.get(0);
        }

        public void setBrewTime(int value) {
            this.set(0, value);
        }

        public int getFuel() {
            return this.get(1);
        }

        public void setFuel(int value) {
            this.set(1, value);
        }
    }

    public static class Enchantment extends ContainerData {
        public Enchantment() {
            super(10);
        }

        public int getLevelRequirement(int slot) {
            return this.get(slot);
        }

        public void setLevelRequirement(int slot, int level) {
            this.set(slot, level);
        }

        public int getEnchantmentSeed() {
            return this.get(3);
        }

        public void setEnchantmentSeed(int seed) {
            this.set(3, seed);
        }

        public int getEnchantmentId(int slot) {
            return this.get(4 + slot);
        }

        public void setEnchantmentId(int slot, int id) {
            this.set(4 + slot, id);
        }

        public int getEnchantmentLevel(int slot) {
            return this.get(7 + slot);
        }

        public void setEnchantmentLevel(int slot, int level) {
            this.set(7 + slot, level);
        }
    }

    public static class Beacon extends ContainerData {
        public Beacon() {
            super(3);
        }

        public int getPowerLevel() {
            return this.get(0);
        }

        public void setPowerLevel(int level) {
            this.set(0, level);
        }

        public int getPrimaryEffect() {
            return this.get(1);
        }

        public void setPrimaryEffect(int effectId) {
            this.set(1, effectId);
        }

        public int getSecondaryEffect() {
            return this.get(2);
        }

        public void setSecondaryEffect(int effectId) {
            this.set(2, effectId);
        }
    }

    public static class Anvil extends ContainerData {
        public Anvil() {
            super(1);
        }

        public int getRepairCost() {
            return this.get(0);
        }

        public void setRepairCost(int cost) {
            this.set(0, cost);
        }
    }

    public static class Loom extends ContainerData {
        public Loom() {
            super(1);
        }

        public int getSelectedPattern() {
            return this.get(0);
        }

        public void setSelectedPattern(int index) {
            this.set(0, index);
        }
    }

    public static class Stonecutter extends ContainerData {
        public Stonecutter() {
            super(1);
        }

        public int getSelectedRecipe() {
            return this.get(0);
        }

        public void setSelectedRecipe(int index) {
            this.set(0, index);
        }
    }

    public static class Lectern extends ContainerData {
        public Lectern() {
            super(1);
        }

        public int getPage() {
            return this.get(0);
        }

        public void setPage(int page) {
            this.set(0, page);
        }
    }
}