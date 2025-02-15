package me.darksoul.abyssalLib.item.component;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import me.darksoul.abyssalLib.item.AItem;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class AEquippable {
    private final ItemStack item;
    private final Equippable.Builder equipProps;

    public AEquippable(AItem aItem, EquipmentSlot slot) {
        item = aItem.getItem();
        equipProps = Equippable.equippable(slot);
    }

    public AEquippable sound(Key soundID) {
        equipProps.equipSound(soundID);
        return this;
    }
    public AEquippable model(NamespacedKey modelID) {
        equipProps.assetId(modelID);
        return this;
    }
    public AEquippable swappable(boolean v) {
        equipProps.swappable(v);
        return this;
    }
    public AEquippable dispensable(boolean v) {
        equipProps.dispensable(v);
        return this;
    }

    public void build() {
        item.setData(DataComponentTypes.EQUIPPABLE, equipProps);
    }
}
