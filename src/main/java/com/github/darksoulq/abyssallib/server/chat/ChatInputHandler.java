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

public class ChatInputHandler {

    private static final Map<UUID, Consumer<String>> INPUT_MAP = new ConcurrentHashMap<>();

    public static void await(Player player, Consumer<String> inputHandler) {
        registerAwait(player, inputHandler, TextUtil.parse("<gray>[<bold>AbyssalLib</bold></gray>] Please type your input in chat."), -1);
    }

    public static void await(Player player, Consumer<String> inputHandler, Component prompt) {
        registerAwait(player, inputHandler, prompt, -1);
    }

    public static void await(Player player, Consumer<String> inputHandler, long timeoutTicks) {
        registerAwait(player, inputHandler, TextUtil.parse("<gray>[<bold>AbyssalLib</bold></gray>] Please type your input in chat."), timeoutTicks);
    }

    public static void await(Player player, Consumer<String> inputHandler, Component prompt, long timeoutTicks) {
        registerAwait(player, inputHandler, prompt, timeoutTicks);
    }

    public static boolean cancel(Player player) {
        return INPUT_MAP.remove(player.getUniqueId()) != null;
    }

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