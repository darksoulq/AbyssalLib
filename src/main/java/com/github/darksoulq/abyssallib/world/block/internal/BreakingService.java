package com.github.darksoulq.abyssallib.world.block.internal;

import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.component.builtin.CustomToolType;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BreakingService {

    private static final BreakingService INSTANCE = new BreakingService();
    private static final int INACTIVE_DELAY_TICKS = 100;

    private final HashMap<UUID, Integer> activePlayers = new HashMap<>();
    private final HashMap<UUID, LastState> lastStates = new HashMap<>();
    private final HashMap<UUID, Double> previousBaseSpeeds = new HashMap<>();

    public static BreakingService getInstance() {
        return INSTANCE;
    }

    public void updateBreakSpeeds() {
        updateActivePlayers();
        if (activePlayers.isEmpty()) return;

        for (UUID uuid : activePlayers.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                lastStates.remove(uuid);
                resetToInitialValues(player);
                continue;
            }

            double range = Objects.requireNonNull(player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE)).getValue();
            Block block = player.getTargetBlockExact((int) Math.ceil(range), FluidCollisionMode.NEVER);

            if (block == null) {
                lastStates.remove(uuid);
                resetToInitialValues(player);
                continue;
            }

            Location blockLocation = block.getLocation();
            ItemStack handItem = player.getInventory().getItemInMainHand();
            boolean onGround = player.isOnGround();
            boolean underWater = player.isUnderWater();

            if (!lastStateChanged(uuid, blockLocation, handItem, onGround, underWater)) continue;
            updateLastState(uuid, blockLocation, handItem, onGround, underWater);

            CustomToolType.ToolData toolData = null;
            if (handItem.getType() != Material.AIR) {
                Item item = Item.resolve(handItem);
                if (item != null && item.hasData(CustomToolType.TYPE)) {
                    toolData = item.getData(CustomToolType.TYPE).getValue();
                }
            }

            CustomBlock customBlock = BlockManager.get(blockLocation);
            calcBlockBreakSpeed(player, toolData, block, customBlock, onGround, underWater);
        }
    }

    public void storeInitialSpeed(Player player) {
        previousBaseSpeeds.put(player.getUniqueId(), Objects.requireNonNull(player.getAttribute(Attribute.BLOCK_BREAK_SPEED)).getBaseValue());
    }

    public void wasActive(Player player) {
        activePlayers.put(player.getUniqueId(), 0);
    }
    
    public void forceReset(Player player) {
        UUID uuid = player.getUniqueId();
        lastStates.remove(uuid);
        resetToInitialValues(player);
    }

    public void removeTrackedPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        activePlayers.remove(uuid);
        lastStates.remove(uuid);
        resetToInitialValues(player);
        previousBaseSpeeds.remove(uuid);
    }

    private void updateActivePlayers() {
        if (activePlayers.isEmpty()) return;

        Iterator<Map.Entry<UUID, Integer>> it = activePlayers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Integer> entry = it.next();
            if (entry.getValue() > INACTIVE_DELAY_TICKS) {
                lastStates.remove(entry.getKey());
                resetToInitialValues(entry.getKey());
                it.remove();
                continue;
            }
            entry.setValue(entry.getValue() + 1);
        }
    }

    private void resetToInitialValues(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        resetToInitialValues(player);
    }

    private void resetToInitialValues(Player player) {
        Double baseSpeed = previousBaseSpeeds.get(player.getUniqueId());
        if (baseSpeed != null) {
            Objects.requireNonNull(player.getAttribute(Attribute.BLOCK_BREAK_SPEED)).setBaseValue(baseSpeed);
        }
    }

    private void calcBlockBreakSpeed(Player player, CustomToolType.ToolData tool, Block block, CustomBlock blockType, boolean onGround, boolean underWater) {
        float vanillaBlockHardness = block.getType().getHardness();
        if (vanillaBlockHardness <= 0) return;

        double breakSpeed = blockType != null ? 1f / (blockType.properties.hardness / vanillaBlockHardness) : 1;

        if (tool != null) {
            if (tool.isSuitable(block, blockType)) {
                breakSpeed *= tool.speed();
            }

            if (!tool.affectedByFloating() && !onGround) {
                breakSpeed *= 5;
            }
            if (!tool.affectedByUnderwater() && underWater) {
                breakSpeed *= 5;
            }
        }

        Objects.requireNonNull(player.getAttribute(Attribute.BLOCK_BREAK_SPEED)).setBaseValue(breakSpeed);
    }

    private boolean lastStateChanged(UUID uuid, Location lookingAt, ItemStack handItem, boolean onGround, boolean underwater) {
        if (!lastStates.containsKey(uuid)) return true;
        LastState state = lastStates.get(uuid);
        return !state.lastLookedAt().equals(lookingAt)
                || !state.lastHeldItem().isSimilar(handItem)
                || state.lastOnGround() != onGround
                || state.lastUnderwater() != underwater;
    }

    private void updateLastState(UUID uuid, Location lookingAt, ItemStack handItem, boolean onGround, boolean underwater) {
        if (lastStates.containsKey(uuid)) {
            LastState state = lastStates.get(uuid);
            state.lastLookedAt(lookingAt);
            state.lastHeldItem(handItem);
            state.lastOnGround(onGround);
            state.lastUnderwater(underwater);
        } else {
            lastStates.put(uuid, new LastState(lookingAt, handItem, onGround, underwater));
        }
    }

    private static class LastState {
        private Location lastLookedAt;
        private ItemStack lastHeldItem;
        private boolean lastOnGround;
        private boolean lastUnderwater;

        public LastState(Location lastLookedAt, ItemStack lastHeldItem, boolean lastOnGround, boolean lastUnderwater) {
            this.lastLookedAt = lastLookedAt.clone();
            this.lastHeldItem = lastHeldItem.clone();
            this.lastOnGround = lastOnGround;
            this.lastUnderwater = lastUnderwater;
        }

        public Location lastLookedAt() { return lastLookedAt; }
        public void lastLookedAt(Location lastLookedAt) { this.lastLookedAt = lastLookedAt.clone(); }
        public ItemStack lastHeldItem() { return lastHeldItem; }
        public void lastHeldItem(ItemStack lastHeldItem) { this.lastHeldItem = lastHeldItem.clone(); }
        public boolean lastOnGround() { return lastOnGround; }
        public void lastOnGround(boolean lastOnGround) { this.lastOnGround = lastOnGround; }
        public boolean lastUnderwater() { return lastUnderwater; }
        public void lastUnderwater(boolean lastUnderwater) { this.lastUnderwater = lastUnderwater; }
    }
}