package com.github.darksoulq.abyssallib.server.packet;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.custom.server.PacketReceiveEvent;
import com.github.darksoulq.abyssallib.server.event.custom.server.PacketSendEvent;
import io.netty.channel.*;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.WeakHashMap;

/**
 * Utility class that injects and removes a Netty packet interceptor into a player's
 * network pipeline to allow interception of both incoming and outgoing Minecraft packets.
 * This interceptor triggers Bukkit {@link PacketReceiveEvent} and {@link PacketSendEvent}
 * to allow plugin-based packet handling and cancellation.
 */
public final class PacketInterceptor {

    /**
     * The name used for the Netty handler in the pipeline.
     */
    private static final @NotNull String HANDLER = "abyssal_packet_listener";

    /**
     * Tracks injected pipelines to avoid double injection.
     */
    private static final @NotNull WeakHashMap<ChannelPipeline, Boolean> injected = new WeakHashMap<>();

    /**
     * Injects the interceptor into the specified player's pipeline.
     * Triggers {@link PacketReceiveEvent} for incoming packets and
     * {@link PacketSendEvent} for outgoing packets.
     *
     * @param player the player to inject
     */
    public static void inject(@NotNull Player player) {
        ServerPlayer nms = ((CraftPlayer) player).getHandle();
        Connection connection = nms.connection.connection;
        ChannelPipeline pipeline = connection.channel.pipeline();

        if (injected.containsKey(pipeline) || pipeline.get(HANDLER) != null) return;

        ChannelDuplexHandler handler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof Packet<?> packet) {
                    PacketReceiveEvent event = new PacketReceiveEvent(player, packet, true);
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) return;
                }

                super.channelRead(ctx, msg);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                if (msg instanceof Packet<?> packet) {
                    PacketSendEvent event = new PacketSendEvent(player, packet, true);
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        promise.setSuccess();
                        return;
                    }
                }

                super.write(ctx, msg, promise);
            }
        };

        try {
            pipeline.addBefore("packet_handler", HANDLER, handler);
            injected.put(pipeline, true);
        } catch (Throwable t) {
            AbyssalLib.getInstance().getLogger().severe("Failed to inject packet handler: " + t.getMessage());
        }
    }

    /**
     * Removes the interceptor from the specified player's pipeline if injected.
     *
     * @param player the player to uninject
     */
    public static void uninject(@NotNull Player player) {
        ServerPlayer nms = ((CraftPlayer) player).getHandle();
        Connection connection = nms.connection.connection;
        ChannelPipeline pipeline = connection.channel.pipeline();

        try {
            if (pipeline.get(HANDLER) != null) {
                pipeline.remove(HANDLER);
                injected.remove(pipeline);
            }
        } catch (Throwable t) {
            AbyssalLib.getInstance().getLogger().severe("Failed to uninject packet handler: " + t.getMessage());
        }
    }
}
