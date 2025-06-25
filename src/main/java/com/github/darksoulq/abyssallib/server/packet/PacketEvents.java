package com.github.darksoulq.abyssallib.server.packet;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Central registry for registering and dispatching packet handlers.
 * Supports multiple handlers per packet type, with priority ordering and cancellation awareness.
 */
public final class PacketEvents {

    /**
     * Represents a single registered incoming packet handler.
     *
     * @param handler         the functional interface to call
     * @param ignoreCancelled whether this handler should be called even if the packet was already cancelled
     * @param <T>             the type of packet
     */
    private record RegisteredIncomingHandler<T extends Packet<?>>(
            @NotNull IncomingHandler<T> handler,
            boolean ignoreCancelled
    ) {}

    /**
     * Represents a single registered outgoing packet handler.
     *
     * @param handler         the functional interface to call
     * @param ignoreCancelled whether this handler should be called even if the packet was already cancelled
     * @param <T>             the type of packet
     */
    private record RegisteredOutgoingHandler<T extends Packet<?>>(
            @NotNull OutgoingHandler<T> handler,
            boolean ignoreCancelled
    ) {}

    /**
     * Incoming packet handlers, grouped by packet class, then priority.
     */
    private static final @NotNull Map<Class<?>, TreeMap<PacketPriority, List<RegisteredIncomingHandler<?>>>> incomingHandlers = new IdentityHashMap<>();

    /**
     * Outgoing packet handlers, grouped by packet class, then priority.
     */
    private static final @NotNull Map<Class<?>, TreeMap<PacketPriority, List<RegisteredOutgoingHandler<?>>>> outgoingHandlers = new IdentityHashMap<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private PacketEvents() {}

    /**
     * Registers a handler for incoming packets of the given type.
     *
     * @param type            the packet class to listen for
     * @param priority        the execution priority
     * @param handler         the handler to invoke
     * @param ignoreCancelled whether to ignore already-cancelled packets
     * @param <T>             the type of packet
     */
    public static <T extends Packet<?>> void listenIncoming(
            @NotNull Class<T> type,
            @NotNull PacketPriority priority,
            @NotNull IncomingHandler<T> handler,
            boolean ignoreCancelled
    ) {
        incomingHandlers
                .computeIfAbsent(type, k -> new TreeMap<>())
                .computeIfAbsent(priority, k -> new ArrayList<>())
                .add(new RegisteredIncomingHandler<>(handler, ignoreCancelled));
    }

    /**
     * Registers a handler for outgoing packets of the given type.
     *
     * @param type            the packet class to listen for
     * @param priority        the execution priority
     * @param handler         the handler to invoke
     * @param ignoreCancelled whether to ignore already-cancelled packets
     * @param <T>             the type of packet
     */
    public static <T extends Packet<?>> void listenOutgoing(
            @NotNull Class<T> type,
            @NotNull PacketPriority priority,
            @NotNull OutgoingHandler<T> handler,
            boolean ignoreCancelled
    ) {
        outgoingHandlers
                .computeIfAbsent(type, k -> new TreeMap<>())
                .computeIfAbsent(priority, k -> new ArrayList<>())
                .add(new RegisteredOutgoingHandler<>(handler, ignoreCancelled));
    }

    /**
     * Dispatches an incoming packet to all registered handlers.
     *
     * @param player the player who sent the packet
     * @param packet the packet received
     * @return {@code true} if any handler cancelled the packet
     */
    @SuppressWarnings("unchecked")
    public static boolean dispatchIncoming(@NotNull ServerPlayer player, @NotNull Packet<?> packet) {
        TreeMap<PacketPriority, List<RegisteredIncomingHandler<?>>> tree = incomingHandlers.get(packet.getClass());
        if (tree == null) return false;

        boolean cancelled = false;

        for (List<RegisteredIncomingHandler<?>> list : tree.values()) {
            for (RegisteredIncomingHandler<?> entry : list) {
                RegisteredIncomingHandler<Packet<?>> handler = (RegisteredIncomingHandler<Packet<?>>) entry;

                if (cancelled && !handler.ignoreCancelled) continue;

                boolean result = handler.handler().handle(player, packet);
                if (result) cancelled = true;
            }
        }

        return cancelled;
    }

    /**
     * Dispatches an outgoing packet to all registered handlers.
     *
     * @param player the player receiving the packet
     * @param packet the packet to send
     * @return {@code true} if any handler cancelled the packet
     */
    @SuppressWarnings("unchecked")
    public static boolean dispatchOutgoing(@NotNull ServerPlayer player, @NotNull Packet<?> packet) {
        TreeMap<PacketPriority, List<RegisteredOutgoingHandler<?>>> tree = outgoingHandlers.get(packet.getClass());
        if (tree == null) return false;

        boolean cancelled = false;

        for (List<RegisteredOutgoingHandler<?>> list : tree.values()) {
            for (RegisteredOutgoingHandler<?> entry : list) {
                RegisteredOutgoingHandler<Packet<?>> handler = (RegisteredOutgoingHandler<Packet<?>>) entry;

                if (cancelled && !handler.ignoreCancelled) continue;

                boolean result = handler.handler().handle(player, packet);
                if (result) cancelled = true;
            }
        }

        return cancelled;
    }
}
