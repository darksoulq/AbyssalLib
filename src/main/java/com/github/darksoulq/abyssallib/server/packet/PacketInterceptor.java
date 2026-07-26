package com.github.darksoulq.abyssallib.server.packet;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.server.PacketReceiveEvent;
import com.github.darksoulq.abyssallib.server.event.custom.server.PacketSendEvent;
import com.github.darksoulq.abyssallib.server.translation.internal.ItemPacketModifier;
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

    /**
     * The unique identifier for the abyssal packet listener in the Netty pipeline.
     */
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

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof Packet<?> packet) {
                    try {
                        Packet<?> processed = ItemPacketModifier.processReceive(packet, player);
                        Packet<?> result = processed;

                        if (PacketReceiveEvent.getHandlerList().getRegisteredListeners().length > 0) {
                            PacketReceiveEvent event = EventBus.post(new PacketReceiveEvent(player, processed, true));
                            if (event.isCancelled()) return;
                            result = event.getPacket();
                        }
                        super.channelRead(ctx, result);
                    } catch (Exception e) {
                        AbyssalLib.getInstance().getLogger().warning("Error processing incoming packet: " + e.getMessage());
                        super.channelRead(ctx, msg);
                    }
                    return;
                }
                super.channelRead(ctx, msg);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                if (msg instanceof Packet<?> packet) {
                    try {
                        Packet<?> processed = ItemPacketModifier.processSend(packet, player);
                        Packet<?> result = processed;

                        if (PacketSendEvent.getHandlerList().getRegisteredListeners().length > 0) {
                            PacketSendEvent event = EventBus.post(new PacketSendEvent(player, processed, true));
                            if (event.isCancelled()) {
                                promise.setSuccess();
                                return;
                            }
                            result = event.getPacket();
                        }

                        if (result == null) {
                            connection.disconnect(PaperAdventure.asVanilla(TextUtil.parse("<red>Invalid packet modification</red>")));
                            return;
                        }
                        super.write(ctx, result, promise);
                    } catch (Exception e) {
                        AbyssalLib.getInstance().getLogger().warning("Error processing outgoing packet: " + e.getMessage());
                        super.write(ctx, msg, promise);
                    }
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