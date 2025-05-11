package me.darksoul.abyssallib.util;

import me.darksoul.abyssallib.AbyssalLib;
import me.darksoul.abyssallib.event.SubscribeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * A utility class that allows capturing a player's next chat message as input.
 *
 * <p>Once a player is registered via {@link #await(Player, Consumer)}, their next message
 * will be intercepted, the event will be cancelled, and the message will be passed to
 * the provided handler on the main thread.</p>
 */
public class ChatInputHandler {

    private final Map<UUID, Consumer<String>> inputMap = new ConcurrentHashMap<>();

    /**
     * Registers a player to capture their next chat message.
     *
     * @param player        the player whose input should be awaited
     * @param inputHandler  a consumer to handle the player's input
     */
    public void await(Player player, Consumer<String> inputHandler) {
        inputMap.put(player.getUniqueId(), inputHandler);
        player.sendMessage("§7[§bAbyssal§7] §fPlease type your input in chat.");
    }

    @SubscribeEvent
    public void onChat(AsyncPlayerChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Consumer<String> handler = inputMap.remove(uuid);
        if (handler != null) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(AbyssalLib.getInstance(), () -> {
                handler.accept(event.getMessage());
            });
        }
    }
}