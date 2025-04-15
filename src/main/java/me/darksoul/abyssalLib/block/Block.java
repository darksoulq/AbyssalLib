package me.darksoul.abyssalLib.block;

import me.darksoul.abyssalLib.event.context.BlockBreakContext;
import me.darksoul.abyssalLib.event.context.BlockInteractContext;
import me.darksoul.abyssalLib.event.context.BlockPlaceContext;
import me.darksoul.abyssalLib.item.Item;
import me.darksoul.abyssalLib.loot.LootTable;
import me.darksoul.abyssalLib.util.ResourceLocation;

public class Block implements Cloneable {
    protected final ResourceLocation id;
    private BlockData data = new BlockData();

    public Block(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation id() {
        return id;
    }

    public Item blockItem() { return null; }

    public void place(BlockPlaceContext ctx) {
        BlockManager.INSTANCE.setBlockAt(ctx.block().getLocation(), this);
        onPlace(ctx);
    }

    public LootTable lootTable() {
        return null;
    }
    public int exp() {
        return 0;
    }
    public void onPlace(BlockPlaceContext ctx) {}
    public void onBreak(BlockBreakContext ctx) {}
    public void onInteract(BlockInteractContext ctx) {}

    public BlockData getData() {
        return data;
    }
    public void setData(BlockData data) {
        this.data = data;
    }

    public static Block from(org.bukkit.block.Block bukkitBlock) {
        return BlockManager.INSTANCE.getBlockAt(bukkitBlock.getLocation());
    }

    @Override
    public Block clone() {
        try {
            return (Block) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
