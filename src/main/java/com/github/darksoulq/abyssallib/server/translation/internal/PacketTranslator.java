package com.github.darksoulq.abyssallib.server.translation.internal;

import com.github.darksoulq.abyssallib.server.translation.ServerTranslator;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PacketTranslator {

    public static Packet<?> process(Packet<?> packet, Player player) {
        try {
            if (packet instanceof ClientboundSystemChatPacket p) return handleChat(p, player);
            if (packet instanceof ClientboundSetTitleTextPacket p) return handleTitle(p, player);
            if (packet instanceof ClientboundSetSubtitleTextPacket p) return handleSubtitle(p, player);
            if (packet instanceof ClientboundSetActionBarTextPacket p) return handleActionBar(p, player);
            if (packet instanceof ClientboundOpenScreenPacket p) return handleOpenScreen(p, player);
            if (packet instanceof ClientboundTabListPacket p) return handleTabList(p, player);
            if (packet instanceof ClientboundContainerSetSlotPacket p) return handleSlot(p, player);
            if (packet instanceof ClientboundContainerSetContentPacket p) return handleContent(p, player);
            if (packet instanceof ClientboundSetEntityDataPacket p) return handleEntityData(p, player);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packet;
    }

    private static Packet<?> handleChat(ClientboundSystemChatPacket packet, Player player) {
        return new ClientboundSystemChatPacket((net.minecraft.network.chat.Component) translate(packet.content(), player), packet.overlay());
    }

    private static Packet<?> handleTitle(ClientboundSetTitleTextPacket packet, Player player) {
        return new ClientboundSetTitleTextPacket((net.minecraft.network.chat.Component) translate(packet.text(), player));
    }

    private static Packet<?> handleSubtitle(ClientboundSetSubtitleTextPacket packet, Player player) {
        return new ClientboundSetSubtitleTextPacket((net.minecraft.network.chat.Component) translate(packet.text(), player));
    }

    private static Packet<?> handleActionBar(ClientboundSetActionBarTextPacket packet, Player player) {
        return new ClientboundSetActionBarTextPacket((net.minecraft.network.chat.Component) translate(packet.text(), player));
    }

    private static Packet<?> handleOpenScreen(ClientboundOpenScreenPacket packet, Player player) {
        return new ClientboundOpenScreenPacket(packet.getContainerId(), packet.getType(), (net.minecraft.network.chat.Component) translate(packet.getTitle(), player));
    }

    private static Packet<?> handleTabList(ClientboundTabListPacket packet, Player player) {
        net.minecraft.network.chat.Component header = packet.header();
        net.minecraft.network.chat.Component footer = packet.footer();
        return new ClientboundTabListPacket(
            (net.minecraft.network.chat.Component) translate(header, player),
            (net.minecraft.network.chat.Component) translate(footer, player)
        );
    }

    private static Packet<?> handleSlot(ClientboundContainerSetSlotPacket packet, Player player) {
        ItemStack original = packet.getItem();
        if (original.isEmpty()) return packet;

        ItemStack copy = original.copy();
        translateItem(copy, player);

        return new ClientboundContainerSetSlotPacket(packet.getContainerId(), packet.getStateId(), packet.getSlot(), copy);
    }

    private static Packet<?> handleContent(ClientboundContainerSetContentPacket packet, Player player) {
        List<ItemStack> originalItems = packet.items();
        NonNullList<ItemStack> translatedItems = NonNullList.withSize(originalItems.size(), ItemStack.EMPTY);
        boolean changed = false;

        for (int i = 0; i < originalItems.size(); i++) {
            ItemStack stack = originalItems.get(i);
            if (stack.isEmpty()) {
                translatedItems.set(i, stack);
                continue;
            }
            ItemStack copy = stack.copy();
            translateItem(copy, player);
            translatedItems.set(i, copy);
            changed = true;
        }

        ItemStack carried = packet.carriedItem();
        ItemStack translatedCarried = carried;
        if (!carried.isEmpty()) {
            translatedCarried = carried.copy();
            translateItem(translatedCarried, player);
            changed = true;
        }

        if (!changed) return packet;

        return new ClientboundContainerSetContentPacket(packet.containerId(), packet.stateId(), translatedItems, translatedCarried);
    }

    @SuppressWarnings("unchecked")
    private static Packet<?> handleEntityData(ClientboundSetEntityDataPacket packet, Player player) {
        List<SynchedEntityData.DataValue<?>> packed = packet.packedItems();
        if (packed == null) return packet;

        List<SynchedEntityData.DataValue<?>> newValues = new ArrayList<>(packed.size());
        boolean modified = false;

        for (SynchedEntityData.DataValue<?> entry : packed) {
            if (entry.value() instanceof Optional<?> opt && opt.isPresent() && opt.get() instanceof net.minecraft.network.chat.Component c) {
                net.minecraft.network.chat.Component translated = translateNMS(c, player);
                EntityDataSerializer<Optional<net.minecraft.network.chat.Component>> serializer = (EntityDataSerializer<Optional<net.minecraft.network.chat.Component>>) entry.serializer();
                newValues.add(new SynchedEntityData.DataValue<>(entry.id(), serializer, Optional.of(translated)));
                modified = true;
            } else if (entry.value() instanceof net.minecraft.network.chat.Component c) {
                net.minecraft.network.chat.Component translated = translateNMS(c, player);
                EntityDataSerializer<net.minecraft.network.chat.Component> serializer = (EntityDataSerializer<net.minecraft.network.chat.Component>) entry.serializer();
                newValues.add(new SynchedEntityData.DataValue<>(entry.id(), serializer, translated));
                modified = true;
            } else {
                newValues.add(entry);
            }
        }

        if (!modified) return packet;

        return new ClientboundSetEntityDataPacket(packet.id(), newValues);
    }

    private static void translateItem(ItemStack stack, Player player) {
        net.minecraft.network.chat.Component name = stack.get(DataComponents.CUSTOM_NAME);
        if (name != null) {
            stack.set(DataComponents.CUSTOM_NAME, translateNMS(name, player));
        }

        net.minecraft.network.chat.Component itemName = stack.get(DataComponents.ITEM_NAME);
        if (itemName != null) {
            stack.set(DataComponents.ITEM_NAME, translateNMS(itemName, player));
        }

        ItemLore lore = stack.get(DataComponents.LORE);
        if (lore != null) {
            List<net.minecraft.network.chat.Component> lines = new ArrayList<>();
            for (net.minecraft.network.chat.Component line : lore.lines()) {
                lines.add(translateNMS(line, player));
            }
            stack.set(DataComponents.LORE, new ItemLore(lines));
        }
    }

    private static Object translate(Object vanillaComponent, Player player) {
        if (vanillaComponent instanceof net.minecraft.network.chat.Component c) {
            return translateNMS(c, player);
        }
        return vanillaComponent;
    }

    private static net.minecraft.network.chat.Component translateNMS(net.minecraft.network.chat.Component vanilla, Player player) {
        Component adventure = PaperAdventure.asAdventure(vanilla);
        Component translated = ServerTranslator.translate(adventure, player);
        return PaperAdventure.asVanilla(translated);
    }
}