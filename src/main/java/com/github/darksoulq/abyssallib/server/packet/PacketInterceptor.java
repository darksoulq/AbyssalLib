package com.github.darksoulq.abyssallib.server.packet;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.server.PacketReceiveEvent;
import com.github.darksoulq.abyssallib.server.event.custom.server.PacketSendEvent;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Manages the injection of Netty handlers into the player's network pipeline.
 * <p>
 * By injecting a {@link ChannelDuplexHandler}, the system can intercept raw
 * packets and fire {@link PacketReceiveEvent} or {@link PacketSendEvent}.
 */
public final class PacketInterceptor {

    /** The unique identifier for the abyssal packet listener in the Netty pipeline. */
    private static final @NotNull String HANDLER = "abyssal_packet_listener";

    /**
     * Injects a custom packet listener into the player's Netty pipeline.
     * <p>
     * The handler is placed before "packet_handler" to ensure it intercepts
     * packets before they are processed by the vanilla server logic.
     *
     * @param player The player whose connection should be intercepted.
     */
    public static void inject(@NotNull Player player) {
        ServerPlayer nms = ((CraftPlayer) player).getHandle();
        Connection connection = nms.connection.connection;
        ChannelPipeline pipeline = connection.channel.pipeline();

        if (pipeline.get(HANDLER) != null) return;

        ChannelDuplexHandler handler = new ChannelDuplexHandler() {
            /**
             * Intercepts Inbound packets (Client -> Server).
             */
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof Packet<?> packet) {
                    PacketReceiveEvent event = EventBus.post(new PacketReceiveEvent(player, packet, true));
                    if (event.isCancelled()) return;

                    super.channelRead(ctx, event.getPacket());
                    return;
                }
                super.channelRead(ctx, msg);
            }

            /**
             * Intercepts Outbound packets (Server -> Client).
             */
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                if (msg instanceof Packet<?> packet) {
                    PacketSendEvent event = EventBus.post(new PacketSendEvent(player, packet, true));
                    if (event.isCancelled()) {
                        promise.setSuccess();
                        return;
                    }

                    Packet<?> result = event.getPacket();
                    if (result == null) {
                        connection.disconnect(PaperAdventure.asVanilla(TextUtil.parse("<red>Invalid packet modification</red>")));
                        return;
                    }
                    super.write(ctx, result, promise);
                    return;
                }
                super.write(ctx, msg, promise);
            }
        };

        try {
            pipeline.addBefore("packet_handler", HANDLER, handler);
        } catch (Throwable t) {
            AbyssalLib.getInstance().getLogger().severe("Failed to inject packet handler: " + t.getMessage());
        }
    }

    /**
     * Removes the custom packet listener from the player's Netty pipeline.
     *
     * @param player The player to uninject.
     */
    public static void uninject(@NotNull Player player) {
        ServerPlayer nms = ((CraftPlayer) player).getHandle();
        Connection connection = nms.connection.connection;
        ChannelPipeline pipeline = connection.channel.pipeline();

        try {
            if (pipeline.get(HANDLER) != null) {
                pipeline.remove(HANDLER);
            }
        } catch (Throwable t) {
            AbyssalLib.getInstance().getLogger().severe("Failed to uninject packet handler: " + t.getMessage());
        }
    }
}