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
    public static void set(AItem item, int timeToEat, int nut, int sat, boolean canAlwaysEat) {
        // Make item consumable
        Consumable.Builder consumableProp = Consumable.consumable().consumeSeconds(timeToEat);
        item.getItem().setData(DataComponentTypes.CONSUMABLE, consumableProp);

        // Set Food Properties
        FoodProperties.Builder foodProps = FoodProperties.food().canAlwaysEat(canAlwaysEat)
                .nutrition(nut).saturation(sat);
        item.getItem().setData(DataComponentTypes.FOOD, foodProps);
    }

    public static void setEffects(AItem item, List<ConsumeEffect> effects) {
        Consumable.Builder consumableProps = item.getItem().getData(DataComponentTypes.CONSUMABLE)
                .toBuilder();
        consumableProps.addEffects(effects);
        item.getItem().setData(DataComponentTypes.CONSUMABLE, consumableProps);
    }

    public static void setParticles(AItem item, boolean v) {
        Consumable.Builder consumableProps = item.getItem().getData(DataComponentTypes.CONSUMABLE)
                .toBuilder();
        consumableProps.hasConsumeParticles(v);
        item.getItem().setData(DataComponentTypes.CONSUMABLE, consumableProps);
    }

    public static void setAnimation(AItem item, ItemUseAnimation anim) {
        Consumable.Builder consumableProps = item.getItem().getData(DataComponentTypes.CONSUMABLE)
                .toBuilder();
        consumableProps.animation(anim);
        item.getItem().setData(DataComponentTypes.CONSUMABLE, consumableProps);
    }

    public static void setSound(AItem item, Key key) {
        Consumable.Builder consumableProps = item.getItem().getData(DataComponentTypes.CONSUMABLE)
                .toBuilder();
        consumableProps.sound(key);
        item.getItem().setData(DataComponentTypes.CONSUMABLE, consumableProps);
    }

    public static void setCooldown(AItem item, float v) {
        ItemMeta meta = item.getItem().getItemMeta();
        UseCooldownComponent cooldownProps = meta.getUseCooldown();
        cooldownProps.setCooldownSeconds(v);
        meta.setUseCooldown(cooldownProps);
        item.getItem().setItemMeta(meta);
    }

    public static void setUseRemainder(AItem item, ItemStack remainder) {
        ItemMeta meta = item.getItem().getItemMeta();
        meta.setUseRemainder(remainder);
        item.getItem().setItemMeta(meta);
    }

    public static void setUseRemainder(AItem item, AItem remainder) {
        ItemMeta meta = item.getItem().getItemMeta();
        meta.setUseRemainder(remainder.getItem());
        item.getItem().setItemMeta(meta);
    }
}