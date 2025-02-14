package me.darksoul.abyssalLib.item.component;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import me.darksoul.abyssalLib.item.AItem;
import org.bukkit.inventory.EquipmentSlot;

public class AEquippable {
    public static void set(AItem item, EquipmentSlot slot) {
        Equippable.Builder equipProps = Equippable.equippable(slot);
        item.getItem().setData(DataComponentTypes.EQUIPPABLE, equipProps);
    }
}
