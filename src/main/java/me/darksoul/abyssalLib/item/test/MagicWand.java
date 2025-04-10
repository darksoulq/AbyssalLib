package me.darksoul.abyssalLib.item.test;

import me.darksoul.abyssalLib.item.Item;
import me.darksoul.abyssalLib.item.ItemUseContext;
import me.darksoul.abyssalLib.util.ResourceLocation;
import org.bukkit.Material;

public class MagicWand extends Item {
    public MagicWand(ResourceLocation id) {
        super(id, Material.STICK);
    }

    @Override
    public void onRightClick(ItemUseContext ctx) {
        System.out.println("Works");
        ctx.getPlayer().sendMessage("✨ Whoosh!");
    }

    @Override
    public void onUseEntity(ItemUseContext ctx) {
        if (ctx.getTargetEntity().isPresent()) {
            ctx.getTargetEntity().get().setGlowing(true);
            ctx.getPlayer().sendMessage("§aGlowing Target!");
        }
    }
}
