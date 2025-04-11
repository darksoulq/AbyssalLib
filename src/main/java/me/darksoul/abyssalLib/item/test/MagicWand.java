package me.darksoul.abyssalLib.item.test;

import me.darksoul.abyssalLib.item.Item;
import me.darksoul.abyssalLib.item.ItemUseContext;
import me.darksoul.abyssalLib.resource.glyph.GlyphManager;
import me.darksoul.abyssalLib.util.ResourceLocation;
import org.bukkit.Material;

public class MagicWand extends Item {
    public MagicWand(ResourceLocation id) {
        super(id, Material.STICK);
    }

    @Override
    public void onRightClick(ItemUseContext ctx) {
        ctx.player().sendMessage(GlyphManager.replacePlaceholders("%abyssallib:magic_wand% Whoosh"));
    }

    @Override
    public void onUseEntity(ItemUseContext ctx) {
        if (ctx.targetEntity().isPresent()) {
            ctx.targetEntity().get().setGlowing(true);
            ctx.player().sendMessage("§aGlowing Target!");
        }
    }
}
