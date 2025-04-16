package me.darksoul.abyssalLib.block.test;

import me.darksoul.abyssalLib.block.Block;
import me.darksoul.abyssalLib.event.context.BlockInteractContext;
import me.darksoul.abyssalLib.item.Item;
import me.darksoul.abyssalLib.item.test.TestItems;
import me.darksoul.abyssalLib.registry.BuiltinRegistries;
import me.darksoul.abyssalLib.util.ResourceLocation;
import net.kyori.adventure.text.Component;

public class TestBlock extends Block {
    public TestBlock(ResourceLocation id) {
        super(id);
        getData().setInt("data_works", 1);
    }

    @Override
    public void onInteract(BlockInteractContext ctx) {
        if (ctx.action().isRightClick()) {
            ctx.player().sendMessage(Component.text("Clicked a Custom Block!"));
        } else if (ctx.action().isLeftClick()) {
            ctx.player().sendMessage(Component.text(from(ctx.block()).getData().getInt("data_works")));
            int x = from(ctx.block()).getData().getInt("data_works");
            from(ctx.block()).getData().setInt("data_works", ++x);
        }
    }

    @Override
    public int exp() {
        return 10;
    }

    @Override
    public Item blockItem() {
        return BuiltinRegistries.ITEMS.get(TestItems.BLOCK_ITEM.getId());
    }
}
