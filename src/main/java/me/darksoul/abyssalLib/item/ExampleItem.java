package me.darksoul.abyssalLib.item;

import me.darksoul.abyssalLib.item.component.AEquippable;
import me.darksoul.abyssalLib.item.component.AFood;
import me.darksoul.abyssalLib.item.component.ATool;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;

public class ExampleItem extends AItem {

    public ExampleItem() {
        super(Material.PAPER, new NamespacedKey("abyssallib", "core"));
    }

    @Override
    public void setComponents() {
        new AFood(this)
                .canAlwaysEat(true)
                .nutrition(5)
                .saturation(5)
                .timeToEat(5)
                .build();
        new AEquippable(this, EquipmentSlot.FEET)
                .build();
        new ATool(this, ATool.ToolType.PICKAXE, 6)
                .build();
        setStackSize(1);
    }
}
