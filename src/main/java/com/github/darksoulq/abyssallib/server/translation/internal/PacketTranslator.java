package com.github.darksoulq.abyssallib.server.translation.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.translation.ServerTranslator;
import com.github.darksoulq.abyssallib.server.util.TaskUtil;
import com.mojang.datafixers.util.Pair;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PacketTranslator {

    private static final Map<UUID, Map<Integer, Integer>> translationHashes = new ConcurrentHashMap<>();

    public static void startUpdater() {
        Bukkit.getScheduler().runTaskTimer(AbyssalLib.getInstance(), () -> {
            translationHashes.keySet().removeIf(uuid -> Bukkit.getPlayer(uuid) == null);

            for (Player player : Bukkit.getOnlinePlayers()) {
                ServerPlayer sp = ((CraftPlayer) player).getHandle();
                AbstractContainerMenu menu = sp.containerMenu;
                Map<Integer, Integer> hashes = translationHashes.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>());

                for (int i = 0; i < menu.slots.size(); i++) {
                    Slot slot = menu.slots.get(i);
                    if (slot != null && slot.hasItem()) {
                        ItemStack original = slot.getItem();
                        ItemStack translated = original.copy();
                        translateItem(translated, player);

                        int currentHash = translated.hashCode();
                        Integer lastHash = hashes.get(i);

                        if (lastHash == null || lastHash != currentHash) {
                            hashes.put(i, currentHash);
                            sp.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.getStateId(), i, translated));
                        }
                    } else {
                        hashes.remove(i);
                    }
                }

                ItemStack carried = menu.getCarried();
                if (!carried.isEmpty()) {
                    ItemStack translatedCarried = carried.copy();
                    translateItem(translatedCarried, player);

                    int carriedHash = translatedCarried.hashCode();
                    Integer lastCarried = hashes.get(-1);

                    if (lastCarried == null || lastCarried != carriedHash) {
                        hashes.put(-1, carriedHash);
                        sp.connection.send(new ClientboundContainerSetSlotPacket(-1, menu.getStateId(), -1, translatedCarried));
                    }
                } else {
                    hashes.remove(-1);
                }
            }
        }, 20L, 20L);
    }

    public static Packet<?> processSend(Packet<?> packet, Player player) {
        if (packet instanceof ClientboundBundlePacket p) return handleBundle(p, player);
        if (packet instanceof ClientboundSystemChatPacket p) return handleChat(p, player);
        if (packet instanceof ClientboundSetTitleTextPacket p) return handleTitle(p, player);
        if (packet instanceof ClientboundSetSubtitleTextPacket p) return handleSubtitle(p, player);
        if (packet instanceof ClientboundSetActionBarTextPacket p) return handleActionBar(p, player);
        if (packet instanceof ClientboundOpenScreenPacket p) return handleOpenScreen(p, player);
        if (packet instanceof ClientboundTabListPacket p) return handleTabList(p, player);
        if (packet instanceof ClientboundContainerSetSlotPacket p) return handleSlot(p, player);
        if (packet instanceof ClientboundContainerSetContentPacket p) return handleContent(p, player);
        if (packet instanceof ClientboundSetEntityDataPacket p) return handleEntityData(p, player);
        if (packet instanceof ClientboundSetEquipmentPacket p) return handleEquipment(p, player);
        if (packet instanceof ClientboundMerchantOffersPacket p) return handleMerchantOffers(p, player);
        return packet;
    }

    public static Packet<?> processReceive(Packet<?> packet, Player player) {
        if (packet instanceof ServerboundContainerClickPacket p) return handleSlotChange(p, player);
        if (packet instanceof ServerboundSetCreativeModeSlotPacket p) return handleCreativeSlot(p);
        if (packet instanceof ServerboundPlayerActionPacket p) return handleSlotChange(p, player);
        if (packet instanceof ServerboundClientInformationPacket p) return handleClientInfo(p, player);
        return packet;
    }

    @SuppressWarnings("unchecked")
    private static Packet<?> handleBundle(ClientboundBundlePacket packet, Player player) {
        List<Packet<? super ClientGamePacketListener>> subPackets = new ArrayList<>();
        boolean changed = false;

        for (Packet<? super ClientGamePacketListener> sub : packet.subPackets()) {
            Packet<?> processed = processSend(sub, player);
            subPackets.add((Packet<? super ClientGamePacketListener>) processed);
            if (processed != sub) changed = true;
        }

        if (!changed) return packet;
        return new ClientboundBundlePacket(subPackets);
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
        return new ClientboundTabListPacket(
            (net.minecraft.network.chat.Component) translate(packet.header(), player),
            (net.minecraft.network.chat.Component) translate(packet.footer(), player)
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
        List<SynchedEntityData.DataValue<?>> newValues = new ArrayList<>(packed.size());
        boolean modified = false;

        for (SynchedEntityData.DataValue<?> entry : packed) {
            Object val = entry.value();
            if (val instanceof Optional<?> opt && opt.isPresent() && opt.get() instanceof net.minecraft.network.chat.Component c) {
                net.minecraft.network.chat.Component translated = translateNMS(c, player);
                EntityDataSerializer<Optional<net.minecraft.network.chat.Component>> serializer = (EntityDataSerializer<Optional<net.minecraft.network.chat.Component>>) entry.serializer();
                newValues.add(new SynchedEntityData.DataValue<>(entry.id(), serializer, Optional.of(translated)));
                modified = true;
            } else if (val instanceof net.minecraft.network.chat.Component c) {
                net.minecraft.network.chat.Component translated = translateNMS(c, player);
                EntityDataSerializer<net.minecraft.network.chat.Component> serializer = (EntityDataSerializer<net.minecraft.network.chat.Component>) entry.serializer();
                newValues.add(new SynchedEntityData.DataValue<>(entry.id(), serializer, translated));
                modified = true;
            } else if (val instanceof ItemStack stack) {
                if (!stack.isEmpty()) {
                    ItemStack copy = stack.copy();
                    translateItem(copy, player);
                    EntityDataSerializer<ItemStack> serializer = (EntityDataSerializer<ItemStack>) entry.serializer();
                    newValues.add(new SynchedEntityData.DataValue<>(entry.id(), serializer, copy));
                    modified = true;
                } else {
                    newValues.add(entry);
                }
            } else {
                newValues.add(entry);
            }
        }

        if (!modified) return packet;
        return new ClientboundSetEntityDataPacket(packet.id(), newValues);
    }

    private static Packet<?> handleEquipment(ClientboundSetEquipmentPacket packet, Player player) {
        List<Pair<EquipmentSlot, ItemStack>> slots = packet.getSlots();
        List<Pair<EquipmentSlot, ItemStack>> newSlots = new ArrayList<>(slots.size());
        boolean changed = false;

        for (var pair : slots) {
            ItemStack original = pair.getSecond();
            if (original.isEmpty()) {
                newSlots.add(pair);
                continue;
            }
            ItemStack copy = original.copy();
            translateItem(copy, player);
            newSlots.add(Pair.of(pair.getFirst(), copy));
            changed = true;
        }

        if (!changed) return packet;
        return new ClientboundSetEquipmentPacket(packet.getEntity(), newSlots);
    }

    private static Packet<?> handleMerchantOffers(ClientboundMerchantOffersPacket packet, Player player) {
        MerchantOffers original = packet.getOffers();
        MerchantOffers copy = new MerchantOffers();
        boolean changed = false;

        for (MerchantOffer offer : original) {
            ItemStack result = offer.getResult().copy();
            ItemStack costA = offer.getCostA().copy();
            ItemStack costB = offer.getCostB().copy();
            if (!result.isEmpty()) {
                translateItem(result, player);
                changed = true;
            }
            if (!costA.isEmpty()) {
                translateItem(costA, player);
                changed = true;
            }
            if (!costB.isEmpty()) {
                translateItem(costB, player);
                changed = true;
            }
            MerchantOffer newOffer = new MerchantOffer(
                offer.getItemCostA(), offer.getItemCostB(), result,
                offer.getUses(), offer.getMaxUses(), offer.getXp(),
                offer.getSpecialPriceDiff(), offer.getDemand(), offer.ignoreDiscounts, offer.asBukkit()
            );
            newOffer.priceMultiplier = offer.priceMultiplier;
            newOffer.rewardExp = offer.rewardExp;
            copy.add(newOffer);
        }

        if (!changed) return packet;
        return new ClientboundMerchantOffersPacket(packet.getContainerId(), copy, packet.getVillagerLevel(), packet.getVillagerXp(), packet.showProgress(), packet.canRestock());
    }

    private static Packet<?> handleSlotChange(Packet<?> packet, Player player) {
        TaskUtil.delayedTask(AbyssalLib.getInstance(), 1, () -> cleanPlayerInventory(player));
        return packet;
    }

    private static Packet<?> handleCreativeSlot(ServerboundSetCreativeModeSlotPacket packet) {
        ItemStack item = packet.itemStack();
        if (!item.isEmpty()) {
            untranslateItem(item);
        }
        return packet;
    }

    private static Packet<?> handleClientInfo(ServerboundClientInformationPacket packet, Player player) {
        TaskUtil.delayedTask(AbyssalLib.getInstance(), 10, () -> cleanPlayerInventory(player));
        return packet;
    }

    private static void cleanPlayerInventory(Player player) {
        ServerPlayer sp = ((CraftPlayer) player).getHandle();
        AbstractContainerMenu menu = sp.containerMenu;
        Map<Integer, Integer> hashes = translationHashes.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>());

        untranslateItem(menu.getCarried());
        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.slots.get(i);
            if (slot.hasItem()) {
                untranslateItem(slot.getItem());
            }
        }

        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.slots.get(i);
            if (slot.hasItem()) {
                ItemStack translated = slot.getItem().copy();
                translateItem(translated, player);
                int hash = translated.hashCode();
                if (!hashes.containsKey(i) || hashes.get(i) != hash) {
                    hashes.put(i, hash);
                    sp.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.getStateId(), i, translated));
                }
            } else {
                hashes.remove(i);
            }
        }

        ItemStack carried = menu.getCarried();
        if (!carried.isEmpty()) {
            ItemStack translatedCarried = carried.copy();
            translateItem(translatedCarried, player);
            int hash = translatedCarried.hashCode();
            if (!hashes.containsKey(-1) || hashes.get(-1) != hash) {
                hashes.put(-1, hash);
                sp.connection.send(new ClientboundContainerSetSlotPacket(-1, menu.getStateId(), -1, translatedCarried));
            }
        } else {
            hashes.remove(-1);
        }
    }

    private static void translateItem(ItemStack stack, Player player) {
        untranslateItem(stack);

        CustomData existing = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag rootTag = existing.copyTag();

        Optional<CompoundTag> customDataOpt = rootTag.getCompound("CustomData");

        CompoundTag translationCache = new CompoundTag();
        boolean hasOg = false;
        GsonComponentSerializer gson = GsonComponentSerializer.gson();

        net.minecraft.network.chat.Component customName = stack.get(DataComponents.CUSTOM_NAME);
        if (customName != null) {
            String json = gson.serialize(PaperAdventure.asAdventure(customName));
            translationCache.putString("cn", Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8)));
            stack.set(DataComponents.CUSTOM_NAME, translateNMS(customName, player));
            hasOg = true;
        }

        net.minecraft.network.chat.Component itemName = stack.get(DataComponents.ITEM_NAME);
        if (itemName != null) {
            String json = gson.serialize(PaperAdventure.asAdventure(itemName));
            translationCache.putString("in", Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8)));
            stack.set(DataComponents.ITEM_NAME, translateNMS(itemName, player));
            hasOg = true;
        }

        ItemLore lore = stack.get(DataComponents.LORE);
        if (lore != null) {
            ListTag loreOg = new ListTag();
            List<net.minecraft.network.chat.Component> translatedLines = new ArrayList<>();

            for (net.minecraft.network.chat.Component line : lore.lines()) {
                String json = gson.serialize(PaperAdventure.asAdventure(line));
                loreOg.add(StringTag.valueOf(Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8))));
                translatedLines.add(translateNMS(line, player));
            }
            translationCache.put("lr", loreOg);
            stack.set(DataComponents.LORE, new ItemLore(translatedLines));
            hasOg = true;
        }

        if (hasOg) {
            CompoundTag customDataKeyTag = customDataOpt.orElseGet(CompoundTag::new);
            customDataKeyTag.put("TranslationCache", translationCache);
            rootTag.put("CustomData", customDataKeyTag);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(rootTag));
        }
    }

    private static boolean untranslateItem(ItemStack stack) {
        if (stack.isEmpty()) return false;

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return false;

        CompoundTag rootTag = customData.copyTag();
        Optional<CompoundTag> customDataOpt = rootTag.getCompound("CustomData");
        if (customDataOpt.isEmpty()) return false;

        CompoundTag customDataKeyTag = customDataOpt.get();
        Optional<CompoundTag> cacheOpt = customDataKeyTag.getCompound("TranslationCache");
        if (cacheOpt.isEmpty()) return false;

        CompoundTag translationCache = cacheOpt.get();
        GsonComponentSerializer gson = GsonComponentSerializer.gson();

        try {
            translationCache.getString("cn").ifPresent(b64 -> {
                try {
                    String cleanB64 = b64.replaceAll("^\"|\"$", "");
                    String json = new String(Base64.getDecoder().decode(cleanB64), StandardCharsets.UTF_8);
                    stack.set(DataComponents.CUSTOM_NAME, PaperAdventure.asVanilla(gson.deserialize(json)));
                } catch (Exception ignored) {}
            });

            translationCache.getString("in").ifPresent(b64 -> {
                try {
                    String cleanB64 = b64.replaceAll("^\"|\"$", "");
                    String json = new String(Base64.getDecoder().decode(cleanB64), StandardCharsets.UTF_8);
                    stack.set(DataComponents.ITEM_NAME, PaperAdventure.asVanilla(gson.deserialize(json)));
                } catch (Exception ignored) {}
            });

            translationCache.getList("lr").ifPresent(loreOg -> {
                try {
                    List<net.minecraft.network.chat.Component> restoredLines = new ArrayList<>();
                    for (Tag tag : loreOg) {
                        tag.asString().ifPresent(b64 -> {
                            try {
                                String cleanB64 = b64.replaceAll("^\"|\"$", "");
                                String json = new String(Base64.getDecoder().decode(cleanB64), StandardCharsets.UTF_8);
                                restoredLines.add(PaperAdventure.asVanilla(gson.deserialize(json)));
                            } catch (Exception ignored) {}
                        });
                    }
                    stack.set(DataComponents.LORE, new ItemLore(restoredLines));
                } catch (Exception ignored) {}
            });
        } finally {
            customDataKeyTag.remove("TranslationCache");
            if (customDataKeyTag.isEmpty()) {
                rootTag.remove("CustomData");
            } else {
                rootTag.put("CustomData", customDataKeyTag);
            }

            if (rootTag.isEmpty()) {
                stack.remove(DataComponents.CUSTOM_DATA);
            } else {
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(rootTag));
            }
        }
        return true;
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