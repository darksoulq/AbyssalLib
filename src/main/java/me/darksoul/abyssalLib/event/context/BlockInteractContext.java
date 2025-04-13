package me.darksoul.abyssalLib.event.context;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BlockInteractContext {
    Player player;
    Block block;
    BlockFace blockFace;
    Location interactionPoint;
    Action action;
    EquipmentSlot hand;
    ItemStack stack;
    PlayerInteractEvent event;

    public BlockInteractContext(Player player, Block block, BlockFace blockFace, Location interactionPoint, Action action, EquipmentSlot hand, ItemStack stack, PlayerInteractEvent event) {
        this.player = player;
        this.block = block;
        this.blockFace = blockFace;
        this.interactionPoint = interactionPoint;
        this.action = action;
        this.hand = hand;
        this.stack = stack;
        this.event = event;
    }

    public Player player() {
        return player;
    }

    public Block block() {
        return block;
    }

    public BlockFace blockFace() {
        return blockFace;
    }

    public Location interactionPoint() {
        return interactionPoint;
    }

    public Action action() {
        return action;
    }

    public EquipmentSlot hand() {
        return hand;
    }

    public ItemStack stack() {
        return stack;
    }

    public PlayerInteractEvent event() {
        return event;
    }
}
