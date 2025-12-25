package com.github.darksoulq.abyssallib.server.chat;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.util.TaskUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Handles chat input for players, allowing you to prompt and capture their next chat message.
 * <p>
 * Input is captured only once per registration unless re-registered. Optional prompt messages
 * and timeouts are supported. Input can also be cancelled programmatically.
 */
public class ChatInputHandler {

    /**
     * Map of player UUIDs to their pending chat input handlers.
     */
    private static final Map<UUID, Consumer<String>> INPUT_MAP = new ConcurrentHashMap<>();

    /**
     * Prompts the player and captures their next chat message with a default prompt and no timeout.
     *
     * @param player       the player to listen to
     * @param inputHandler the function that handles their next message
     */
    public static void await(Player player, Consumer<String> inputHandler) {
        registerAwait(player, inputHandler, MiniMessage.miniMessage().deserialize("<gray>[<bold>AbyssalLib</bold></gray>] Please type your input in chat."), -1);
    }

    /**
     * Prompts the player with a custom message and captures their next chat message, no timeout.
     *
     * @param player       the player to listen to
     * @param inputHandler the function that handles their next message
     * @param prompt       the message to send as a prompt
     */
    public static void await(Player player, Consumer<String> inputHandler, Component prompt) {
        registerAwait(player, inputHandler, prompt, -1);
    }

    /**
     * Prompts the player with a default message and captures their next chat message with timeout.
     *
     * @param player       the player to listen to
     * @param inputHandler the function that handles their next message
     * @param timeoutTicks number of ticks before the input expires; -1 for no timeout
     */
    public static void await(Player player, Consumer<String> inputHandler, long timeoutTicks) {
        registerAwait(player, inputHandler, MiniMessage.miniMessage().deserialize("<gray>[<bold>AbyssalLib</bold></gray>] Please type your input in chat."), timeoutTicks);
    }

    /**
     * Prompts the player with a custom message and captures their next chat message with timeout.
     *
     * @param player       the player to listen to
     * @param inputHandler the function that handles their next message
     * @param prompt       the message to send as a prompt
     * @param timeoutTicks number of ticks before the input expires; -1 for no timeout
     */
    public static void await(Player player, Consumer<String> inputHandler, Component prompt, long timeoutTicks) {
        registerAwait(player, inputHandler, prompt, timeoutTicks);
    }

    /**
     * Cancels input awaiting the given player, if any.
     *
     * @param player the player whose input is to be cancelled
     * @return true if there was an active input to cancel, false otherwise
     */
    public static boolean cancel(Player player) {
        return INPUT_MAP.remove(player.getUniqueId()) != null;
    }

    /**
     * Internal method that registers the player and schedules expiration (if any).
     */
    private static void registerAwait(Player player, Consumer<String> inputHandler, Component prompt, long timeoutTicks) {
        UUID uuid = player.getUniqueId();
        INPUT_MAP.put(uuid, inputHandler);

        if (prompt != null) {
            player.sendMessage(prompt);
        }

        if (timeoutTicks > 0) {
            TaskUtil.delayedTask(AbyssalLib.getInstance(), (int) timeoutTicks, () -> {
                Consumer<String> removed = INPUT_MAP.remove(uuid);
                if (removed != null) {
                    player.sendMessage("§7[§cTimeout§7] §fInput request expired.");
                }
            });
        }
    }

    /**
     * Handles incoming player chat messages and dispatches to handlers if registered.
     */
    @SubscribeEvent(ignoreCancelled = false)
    public void onChat(AsyncPlayerChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Consumer<String> handler = INPUT_MAP.remove(uuid);

        if (handler != null) {
            event.setCancelled(true);
            String message = event.getMessage();

            new BukkitRunnable() {
                @Override
                public void run() {
                    handler.accept(message);
                }
            }.runTask(AbyssalLib.getInstance());
        }
    }
}
