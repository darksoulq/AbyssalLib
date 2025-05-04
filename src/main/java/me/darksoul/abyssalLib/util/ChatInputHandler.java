package me.darksoul.abyssalLib.util;

import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.event.SubscribeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ChatInputHandler {

    private final Map<UUID, Consumer<String>> inputMap = new ConcurrentHashMap<>();

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