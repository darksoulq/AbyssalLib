package me.darksoul.abyssalLib.event;

import me.darksoul.abyssalLib.item.Item;
import me.darksoul.abyssalLib.item.ItemUseContext;
import org.bukkit.entity.Player;
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
}
