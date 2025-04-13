package me.darksoul.abyssalLib.event;

import me.darksoul.abyssalLib.event.context.AnvilContext;
import me.darksoul.abyssalLib.event.context.ItemUseContext;
import me.darksoul.abyssalLib.item.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemEvents {
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItem(event.getHand());
        Item item = Item.from(stack);
        if (item != null) {
            ItemUseContext ctx = new ItemUseContext(
                    player,
                    item,
                    event.getHand(),
                    event.getInteractionPoint(),
                    event.getClickedBlock(),
                    null,
                    event
            );
            item.onInteract(ctx);
        }
    }

    @SubscribeEvent
    public void onUseEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItem(event.getHand());
        Item item = Item.from(stack);
        if (item != null) {
            ItemUseContext ctx = new ItemUseContext(
                    player,
                    item,
                    event.getHand(),
                    null,
                    null,
                    event.getRightClicked(),
                    event
            );
            item.onUseEntity(ctx);
        }
    }

    @SubscribeEvent
    public void onAnvilCombine(PrepareAnvilEvent event) {
        Player player = (Player) event.getView().getPlayer();

        ItemStack[] stacks = event.getInventory().getContents();
        for (ItemStack stack : stacks) {
            Item item = Item.from(stack);
            if (item != null) {
                AnvilContext ctx = new AnvilContext(
                        player,
                        event.getView().getTopInventory().getFirstItem(),
                        event.getView().getTopInventory().getSecondItem(),
                        event.getView().getTopInventory().getResult(),
                        event.getView().getRenameText(),
                        event.getView().getRepairCost(),
                        event
                );
                item.onAnvilPrepare(ctx);
                break;
            }
        }
    }
}
