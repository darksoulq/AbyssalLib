package me.darksoul.abyssalLib.gui.test;

import me.darksoul.abyssalLib.gui.AbyssalGui;
import me.darksoul.abyssalLib.gui.slot.AnimatedSlot;
import me.darksoul.abyssalLib.gui.slot.ButtonSlot;
import me.darksoul.abyssalLib.gui.slot.StaticSlot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExampleGui extends AbyssalGui {

    public ExampleGui(Player viewer) {
        super(viewer, "Example GUI", 3);
    }

    @Override
    public void init() {
        slot(new StaticSlot(0, new ItemStack(Material.STONE)));

        slot(new ButtonSlot(1, new ItemStack(Material.EMERALD), ctx -> {
            ctx.player().sendMessage("You clicked the emerald!");
        }));

        slot(new AnimatedSlot(2, () -> {
            Material[] mats = { Material.RED_WOOL, Material.GREEN_WOOL, Material.BLUE_WOOL };
            int index = (int)(System.currentTimeMillis() / 500 % mats.length);
            return new ItemStack(mats[index]);
        }));
    }

    @Override
    public boolean shouldTick() {
        return true;
    }
}
