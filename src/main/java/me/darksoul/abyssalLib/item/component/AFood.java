package me.darksoul.abyssalLib.item.component;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import me.darksoul.abyssalLib.item.AItem;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.UseCooldownComponent;

import java.util.List;

public class AFood {

    private final ItemStack item;
    private final Consumable.Builder consumableProps;
    private final FoodProperties.Builder foodProps;

    public AFood(AItem aItem) {
        item = aItem.getItem();
        consumableProps = Consumable.consumable();
        foodProps = FoodProperties.food();
    }

    public AFood timeToEat(int time) {
        consumableProps.consumeSeconds(time);
        return this;
    }
    public AFood nutrition(int amount) {
        foodProps.nutrition(amount);
        return this;
    }
    public AFood saturation(int amount) {
        foodProps.saturation(amount);
        return this;
    }
    public AFood canAlwaysEat(boolean v) {
        foodProps.canAlwaysEat(v);
        return this;
    }
    public AFood effects(List<ConsumeEffect> effects) {
        consumableProps.addEffects(effects);
        return this;
    }
    public AFood particles(boolean v) {
        consumableProps.hasConsumeParticles(v);
        return this;
    }
    public AFood animation(ItemUseAnimation anim) {
        consumableProps.animation(anim);
        return this;
    }
    public AFood sound(Key soundID) {
        consumableProps.sound(soundID);
        return this;
    }
    public AFood cooldown(float time) {
        ItemMeta meta = item.getItemMeta();
        UseCooldownComponent cooldownProps = meta.getUseCooldown();
        cooldownProps.setCooldownSeconds(time);
        meta.setUseCooldown(cooldownProps);
        item.setItemMeta(meta);
        return this;
    }

    public void build() {
        item.setData(DataComponentTypes.CONSUMABLE, consumableProps);
        item.setData(DataComponentTypes.FOOD, foodProps);
    }
}