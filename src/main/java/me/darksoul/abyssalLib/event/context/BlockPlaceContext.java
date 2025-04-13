package me.darksoul.abyssalLib.event.context;

import me.darksoul.abyssalLib.item.Item;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BlockPlaceContext {
    private final Block block;
    private final Player player;
    private final Item item;
    private final EquipmentSlot hand;
    private final BlockPlaceEvent event;

    public BlockPlaceContext(Block block, Player player, Item item, EquipmentSlot hand, BlockPlaceEvent event) {
        this.block = block;
        this.player = player;
        this.item = item;
        this.hand = hand;
        this.event = event;
    }

    public Block block() {
        return block;
    }
    public Player player() {
        return player;
    }
    public Item item() {
        return item;
    }
    public EquipmentSlot hand() {
        return hand;
    }
    public BlockPlaceEvent event() {
        return event;
    }
}
