package com.github.darksoulq.abyssallib.server.chat;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.util.TaskUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Utility class for capturing asynchronous player chat input.
 * <p>
 * This handler allows for "waiting" on a player's next chat message to use as
 * functional input, supporting custom prompts, timeouts, and automatic cancellation.
 * </p>
 */
public class ChatInputHandler {

    /**
     * Internal map storing active input listeners indexed by player {@link UUID}.
     */
    private static final Map<UUID, Consumer<String>> INPUT_MAP = new ConcurrentHashMap<>();

    /**
     * Awaits a chat input from the specified player using a default prompt and no timeout.
     *
     * @param player       The {@link Player} to await input from.
     * @param inputHandler The {@link Consumer} to execute when input is received.
     */
    public static void await(Player player, Consumer<String> inputHandler) {
        registerAwait(player, inputHandler, TextUtil.parse("<gray>[<bold>AbyssalLib</bold></gray>] Please type your input in chat."), -1);
    }

    /**
     * Awaits a chat input from the specified player with a custom prompt and no timeout.
     *
     * @param player       The {@link Player} to await input from.
     * @param inputHandler The {@link Consumer} to execute when input is received.
     * @param prompt       The {@link Component} message to send as a prompt.
     */
    public static void await(Player player, Consumer<String> inputHandler, Component prompt) {
        registerAwait(player, inputHandler, prompt, -1);
    }

    /**
     * Awaits a chat input from the specified player with a default prompt and a timeout.
     *
     * @param player       The {@link Player} to await input from.
     * @param inputHandler The {@link Consumer} to execute when input is received.
     * @param timeoutTicks The duration in server ticks before the request expires.
     */
    public static void await(Player player, Consumer<String> inputHandler, long timeoutTicks) {
        registerAwait(player, inputHandler, TextUtil.parse("<gray>[<bold>AbyssalLib</bold></gray>] Please type your input in chat."), timeoutTicks);
    }

    /**
     * Awaits a chat input from the specified player with a custom prompt and a timeout.
     *
     * @param player       The {@link Player} to await input from.
     * @param inputHandler The {@link Consumer} to execute when input is received.
     * @param prompt       The {@link Component} message to send as a prompt.
     * @param timeoutTicks The duration in server ticks before the request expires.
     */
    public static void await(Player player, Consumer<String> inputHandler, Component prompt, long timeoutTicks) {
        registerAwait(player, inputHandler, prompt, timeoutTicks);
    }

    /**
     * Cancels any pending input request for the specified player.
     *
     * @param player The {@link Player} whose input request should be removed.
     * @return {@code true} if a request was active and successfully removed.
     */
    public static boolean cancel(Player player) {
        return INPUT_MAP.remove(player.getUniqueId()) != null;
    }

    /**
     * Internal helper to register the player into the input map and handle prompt/timeout logic.
     *
     * @param player       The player.
     * @param inputHandler The logic to run on success.
     * @param prompt       The message to display.
     * @param timeoutTicks The expiration time.
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
                    player.sendMessage(TextUtil.parse("<gray>[<red>Timeout</red>] <white>Input request expired.</white>"));
                }
            });
        }
    }

    /**
     * Listens for the Paper {@link AsyncChatEvent} to intercept player messages.
     * <p>
     * If the player has a pending input request, the event is cancelled and
     * the message is passed to the registered handler on the primary server thread.
     * </p>
     *
     * @param event The chat event.
     */
    @SubscribeEvent
    public void onChat(AsyncChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Consumer<String> handler = INPUT_MAP.remove(uuid);

        if (handler != null) {
            event.setCancelled(true);
            String plainMessage = TextUtil.MM.serialize(event.message());

            new BukkitRunnable() {
                @Override
                public void run() {
                    handler.accept(plainMessage);
                }
            }.runTask(AbyssalLib.getInstance());
        }
    }
}