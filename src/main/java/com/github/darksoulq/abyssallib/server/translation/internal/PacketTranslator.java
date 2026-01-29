package com.github.darksoulq.abyssallib.server.translation.internal;

import com.github.darksoulq.abyssallib.server.translation.ServerTranslator;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class PacketTranslator {

    public static void process(Packet<?> packet, Player player) {
        try {
            if (packet instanceof ClientboundSystemChatPacket p) handleChat(p, player);
            else if (packet instanceof ClientboundSetTitleTextPacket p) handleTitle(p, player);
            else if (packet instanceof ClientboundSetSubtitleTextPacket p) handleSubtitle(p, player);
            else if (packet instanceof ClientboundSetActionBarTextPacket p) handleActionBar(p, player);
            else if (packet instanceof ClientboundOpenScreenPacket p) handleOpenScreen(p, player);
            else if (packet instanceof ClientboundTabListPacket p) handleTabList(p, player);
            else if (packet instanceof ClientboundContainerSetSlotPacket p) handleSlot(p, player);
            else if (packet instanceof ClientboundContainerSetContentPacket p) handleContent(p, player);
            else if (packet instanceof ClientboundSetEntityDataPacket p) handleEntityData(p, player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleChat(ClientboundSystemChatPacket packet, Player player) throws Exception {
        modify(packet, "content", val -> translate(val, player));
    }

    private static void handleTitle(ClientboundSetTitleTextPacket packet, Player player) throws Exception {
        modify(packet, "text", val -> translate(val, player));
    }

    private static void handleSubtitle(ClientboundSetSubtitleTextPacket packet, Player player) throws Exception {
        modify(packet, "text", val -> translate(val, player));
    }

    private static void handleActionBar(ClientboundSetActionBarTextPacket packet, Player player) throws Exception {
        modify(packet, "text", val -> translate(val, player));
    }

    private static void handleOpenScreen(ClientboundOpenScreenPacket packet, Player player) throws Exception {
        modify(packet, "title", val -> translate(val, player));
    }

    private static void handleTabList(ClientboundTabListPacket packet, Player player) throws Exception {
        modify(packet, "header", val -> translate(val, player));
        modify(packet, "footer", val -> translate(val, player));
    }

    private static void handleSlot(ClientboundContainerSetSlotPacket packet, Player player) throws Exception {
        ItemStack original = packet.getItem();
        if (original.isEmpty()) return;

        ItemStack copy = original.copy();
        translateItem(copy, player);

        setField(packet, "itemStack", copy);
    }

    private static void handleContent(ClientboundContainerSetContentPacket packet, Player player) throws Exception {
        List<ItemStack> originalItems = packet.items();
        List<ItemStack> translatedItems = new ArrayList<>(originalItems.size());
        boolean changed = false;

        for (ItemStack stack : originalItems) {
            if (stack.isEmpty()) {
                translatedItems.add(stack);
                continue;
            }
            ItemStack copy = stack.copy();
            translateItem(copy, player);
            translatedItems.add(copy);
            changed = true;
        }

        ItemStack carried = packet.carriedItem();
        ItemStack translatedCarried = carried;
        if (!carried.isEmpty()) {
            translatedCarried = carried.copy();
            translateItem(translatedCarried, player);
            changed = true;
        }

        if (changed) {
            setField(packet, "items", translatedItems);
            setField(packet, "carriedItem", translatedCarried);
        }
    }

    @SuppressWarnings("unchecked")
    private static void handleEntityData(ClientboundSetEntityDataPacket packet, Player player) throws Exception {
        List<SynchedEntityData.DataValue<?>> packed = packet.packedItems();

        List<SynchedEntityData.DataValue<?>> newValues = new ArrayList<>();
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

        if (modified) {
            setField(packet, "packedItems", newValues);
        }
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

    private static void modify(Packet<?> packet, String fieldName, Function<Object, Object> transformer) throws Exception {
        Field field = packet.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Object value = field.get(packet);
        if (value != null) {
            field.set(packet, transformer.apply(value));
        }
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}