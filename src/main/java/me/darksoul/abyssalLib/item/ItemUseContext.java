package me.darksoul.abyssalLib.item;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ItemUseContext {
    private final Player player;
    private final Item item;
    private final EquipmentSlot hand;
    private final Location clickLocation;
    private final Block clickedBlock;
    private final Entity targetEntity;
    private final Event underlyingEvent;

    public ItemUseContext(Player player, Item item, EquipmentSlot hand, Location clickLocation, Block clickedBlock, Entity targetEntity, Event event) {
        this.player = player;
        this.item = item;
        this.hand = hand;
        this.clickLocation = clickLocation;
        this.clickedBlock = clickedBlock;
        this.targetEntity = targetEntity;
        this.underlyingEvent = event;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return item;
    }

    public EquipmentSlot getHand() {
        return hand;
    }

    public Optional<Location> getClickLocation() {
        return Optional.ofNullable(clickLocation);
    }

    public Optional<Block> getClickedBlock() {
        return Optional.ofNullable(clickedBlock);
    }

    public Optional<Entity> getTargetEntity() {
        return Optional.ofNullable(targetEntity);
    }

    public Optional<Event> getUnderlyingEvent() {
        return Optional.ofNullable(underlyingEvent);
    }
}
