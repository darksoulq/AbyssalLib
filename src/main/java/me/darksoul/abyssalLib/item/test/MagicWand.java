package me.darksoul.abyssalLib.item.test;

import me.darksoul.abyssalLib.event.context.ItemUseContext;
import me.darksoul.abyssalLib.item.Item;
import me.darksoul.abyssalLib.util.ResourceLocation;
import org.bukkit.Material;

public class MagicWand extends Item {
    public MagicWand(ResourceLocation id) {
        super(id, Material.OAK_LOG);
    }

    @Override
    public void onUseEntity(ItemUseContext ctx) {
        if (ctx.targetEntity().isPresent()) {
            ctx.targetEntity().get().setGlowing(true);
            ctx.player().sendMessage("§aGlowing Target!");
        }
    }
}
