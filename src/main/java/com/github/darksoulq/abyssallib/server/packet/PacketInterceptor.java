package com.github.darksoulq.abyssallib.server.packet;

import com.github.darksoulq.abyssallib.AbyssalLib;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.WeakHashMap;

/**
 * Injects a Netty duplex handler into a player's network pipeline to intercept
 * incoming and outgoing Minecraft packets using the {@link PacketEvents} system.
 */
public final class PacketInterceptor {

    /**
     * The name of the injected Netty handler in the pipeline.
     */
    private static final @NotNull String HANDLER = "abyssal_packet_listener";

    /**
     * Tracks which pipelines have already been injected to avoid duplicate handlers.
     */
    private static final @NotNull WeakHashMap<ChannelPipeline, Boolean> injected = new WeakHashMap<>();

    private PacketInterceptor() {}

    /**
     * Injects the packet interceptor into the given player's network pipeline if not already injected.
     *
     * @param player the Bukkit player whose packets should be intercepted
     */
    public static void inject(@NotNull Player player) {
        ServerPlayer nms = ((CraftPlayer) player).getHandle();
        Connection connection = nms.connection.connection;
        ChannelPipeline pipeline = connection.channel.pipeline();

        if (injected.containsKey(pipeline) || pipeline.get(HANDLER) != null) return;

        ChannelDuplexHandler handler = new ChannelDuplexHandler() {

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                try {
                    if (msg instanceof Packet<?> packet) {
                        boolean cancel = PacketEvents.dispatchIncoming(nms, packet);
                        if (cancel) return;
                    }
                } catch (Throwable t) {
                    AbyssalLib.getInstance().getLogger().severe("Error while reading packet: " + t.getMessage());
                }

                super.channelRead(ctx, msg);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                try {
                    if (msg instanceof Packet<?> packet) {
                        boolean cancel = PacketEvents.dispatchOutgoing(nms, packet);
                        if (cancel) {
                            promise.setSuccess();
                            return;
                        }
                    }
                } catch (Throwable t) {
                    System.err.println("Error while writing packet: " + t.getMessage());
                    promise.setSuccess();
                    return;
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
     * Removes the interceptor from the given player's network pipeline.
     *
     * @param player the Bukkit player to uninject
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
