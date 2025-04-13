package me.darksoul.abyssalLib.event.context;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class AnvilContext {
    private final Player player;
    private final ItemStack right;
    private final ItemStack left;
    private final ItemStack result;
    private final String renameText;
    private final int repairCost;
    private final PrepareAnvilEvent event;

    public AnvilContext(Player player, ItemStack right, ItemStack left, ItemStack result, String renameText, int repairCost, PrepareAnvilEvent event) {
        this.player = player;
        this.right = right;
        this.left = left;
        this.result = result;
        this.renameText = renameText;
        this.repairCost = repairCost;
        this.event = event;
    }

    public Optional<Player> player() {
        return Optional.of(player);
    }

    public Optional<String> renameText() {
        return Optional.ofNullable(renameText);
    }

    public Optional<ItemStack> left() {
        return Optional.ofNullable(right);
    }

    public Optional<ItemStack> right() {
        return Optional.ofNullable(left);
    }

    public Optional<ItemStack> result() {
        return Optional.ofNullable(result);
    }

    public void result(ItemStack result) {
        event.setResult(result);
    }

    public void repairCost(int levelCost) {
        event.getView().setRepairCost(levelCost);
    }

    public int repairCost() {
        return repairCost;
    }

    public PrepareAnvilEvent event() {
        return event;
    }
}
