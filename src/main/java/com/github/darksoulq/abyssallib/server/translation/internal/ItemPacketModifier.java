package com.github.darksoulq.abyssallib.server.translation.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.translation.ClientItemModifier;
import com.github.darksoulq.abyssallib.server.translation.ItemTranslationContext;
import com.github.darksoulq.abyssallib.server.translation.ServerTranslator;
import com.mojang.datafixers.util.Pair;
import io.papermc.paper.advancement.AdvancementDisplay;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.dialog.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.scores.Objective;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ItemPacketModifier {
    private static final Map<UUID, Map<Integer, String>> TRANSLATION_STATES = new ConcurrentHashMap<>();
    private static final GsonComponentSerializer GSON = GsonComponentSerializer.gson();
    private static final Pattern LANG_PATTERN = Pattern.compile("<(?:lang|tr|translate):([^>]+)>");
    private static final Pattern LANG_OR_PATTERN = Pattern.compile("<(?:lang_or|tr_or|translate_or):([^:]+):([^>]+)>");
    private static final List<ClientItemModifier> MODIFIERS = new ArrayList<>();

    public static void registerModifier(ClientItemModifier modifier) {
        MODIFIERS.add(modifier);
    }

    public static void startUpdater() {
        Bukkit.getScheduler().runTaskTimer(AbyssalLib.getInstance(), () -> {
            TRANSLATION_STATES.keySet().removeIf(uuid -> Bukkit.getPlayer(uuid) == null);

            for (Player player : Bukkit.getOnlinePlayers()) {
                ServerPlayer sp = ((CraftPlayer) player).getHandle();
                AbstractContainerMenu menu = sp.containerMenu;
                Map<Integer, String> states = TRANSLATION_STATES.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>());
                int selectedSlot = sp.getInventory().getSelectedSlot();

                for (int i = 0; i < menu.slots.size(); i++) {
                    Slot slot = menu.slots.get(i);

                    if (slot.container == sp.getInventory() && slot.getContainerSlot() == selectedSlot) {
                        continue;
                    }

                    if (slot.hasItem()) {
                        ItemStack original = slot.getItem();
                        ItemStack processed = processItemSend(original, player);

                        String currentState = getTranslationState(processed);
                        String lastState = states.get(i);

                        if (!currentState.equals(lastState)) {
                            states.put(i, currentState);
                            sp.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.getStateId(), i, processed));
                        }
                    } else {
                        states.remove(i);
                    }
                }

                ItemStack carried = menu.getCarried();
                if (!carried.isEmpty()) {
                    ItemStack processedCarried = processItemSend(carried, player);

                    String currentState = getTranslationState(processedCarried);
                    String lastState = states.get(-1);

                    if (!currentState.equals(lastState)) {
                        states.put(-1, currentState);
                        sp.connection.send(new ClientboundSetCursorItemPacket(processedCarried));
                    }
                } else {
                    states.remove(-1);
                }
            }
        }, 5L, 5L);
    }

    private static String getTranslationState(ItemStack stack) {
        StringBuilder state = new StringBuilder();
        net.minecraft.network.chat.Component customName = stack.get(DataComponents.CUSTOM_NAME);
        if (customName != null) {
            state.append(GSON.serialize(PaperAdventure.asAdventure(customName)));
        }
        net.minecraft.network.chat.Component itemName = stack.get(DataComponents.ITEM_NAME);
        if (itemName != null) {
            state.append(GSON.serialize(PaperAdventure.asAdventure(itemName)));
        }
        ItemLore lore = stack.get(DataComponents.LORE);
        if (lore != null) {
            for (net.minecraft.network.chat.Component line : lore.lines()) {
                state.append(GSON.serialize(PaperAdventure.asAdventure(line)));
            }
        }
        return state.toString();
    }

    public static Packet<?> processSend(Packet<?> packet, Player player) {
        return switch (packet) {
            case ClientboundBundlePacket p -> handleBundle(p, player);
            case ClientboundContainerSetSlotPacket p -> handleSlot(p, player);
            case ClientboundContainerSetContentPacket p -> handleContent(p, player);
            case ClientboundSetCursorItemPacket p -> handleCursorItem(p, player);
            case ClientboundSetEntityDataPacket p -> handleEntityData(p, player);
            case ClientboundSetEquipmentPacket p -> handleEquipment(p, player);
            case ClientboundMerchantOffersPacket p -> handleMerchantOffers(p, player);
            case ClientboundShowDialogPacket p -> handleShowDialog(p, player);
            case ClientboundSystemChatPacket p -> handleSystemChat(p, player);
            case ClientboundPlayerChatPacket p -> handlePlayerChat(p, player);
            case ClientboundDisguisedChatPacket p -> handleDisguisedChat(p, player);
            case ClientboundSetActionBarTextPacket p -> handleActionBar(p, player);
            case ClientboundSetTitleTextPacket p -> handleTitle(p, player);
            case ClientboundSetSubtitleTextPacket p -> handleSubtitle(p, player);
            case ClientboundTabListPacket p -> handleTabList(p, player);
            case ClientboundOpenScreenPacket p -> handleOpenScreen(p, player);
            case ClientboundSetObjectivePacket p -> handleObjective(p, player);
            case ClientboundBossEventPacket p -> handleBossEvent(p, player);
            default -> packet;
        };
    }

    public static Packet<?> processReceive(Packet<?> packet, Player player) {
        if (packet instanceof ServerboundSetCreativeModeSlotPacket p) {
            return handleCreativeSlot(p, player);
        }
        return packet;
    }

    private static Packet<?> handleCursorItem(ClientboundSetCursorItemPacket packet, Player player) {
        ItemStack processed = processItemSend(packet.contents(), player);
        if (processed != packet.contents()) {
            return new ClientboundSetCursorItemPacket(processed);
        }
        return packet;
    }

    @SuppressWarnings("unchecked")
    private static Packet<?> handleBundle(ClientboundBundlePacket packet, Player player) {
        List<Packet<? super ClientGamePacketListener>> subPackets = new ArrayList<>();
        boolean changed = false;

        List<Packet<? super ClientGamePacketListener>> safeIter = new ArrayList<>();
        try {
            packet.subPackets().forEach(safeIter::add);
        } catch (ConcurrentModificationException e) {
            return packet;
        }

        for (Packet<? super ClientGamePacketListener> sub : safeIter) {
            Packet<?> processed = processSend(sub, player);
            subPackets.add((Packet<? super ClientGamePacketListener>) processed);
            if (processed != sub) changed = true;
        }

        if (!changed) return packet;
        return new ClientboundBundlePacket(subPackets);
    }

    private static Packet<?> handleSystemChat(ClientboundSystemChatPacket packet, Player player) {
        return new ClientboundSystemChatPacket(translateNMS(packet.content(), player), packet.overlay());
    }

    private static Packet<?> handlePlayerChat(ClientboundPlayerChatPacket packet, Player player) {
        net.minecraft.network.chat.Component content = packet.unsignedContent();
        if (content == null) {
            content = net.minecraft.network.chat.Component.literal(packet.body().content());
        }

        ChatType.Bound chatType = packet.chatType();
        ChatType.Bound translatedChatType = new ChatType.Bound(
            chatType.chatType(),
            translateNMS(chatType.name(), player),
            chatType.targetName().map(c -> translateNMS(c, player))
        );

        return new ClientboundPlayerChatPacket(
            packet.globalIndex(),
            packet.sender(),
            packet.index(),
            packet.signature(),
            packet.body(),
            translateNMS(content, player),
            packet.filterMask(),
            translatedChatType
        );
    }

    private static Packet<?> handleDisguisedChat(ClientboundDisguisedChatPacket packet, Player player) {
        ChatType.Bound chatType = packet.chatType();
        ChatType.Bound translatedChatType = new ChatType.Bound(
            chatType.chatType(),
            translateNMS(chatType.name(), player),
            chatType.targetName().map(c -> translateNMS(c, player))
        );

        return new ClientboundDisguisedChatPacket(
            translateNMS(packet.message(), player),
            translatedChatType
        );
    }

    private static Packet<?> handleActionBar(ClientboundSetActionBarTextPacket packet, Player player) {
        return new ClientboundSetActionBarTextPacket(translateNMS(packet.text(), player));
    }

    private static Packet<?> handleTitle(ClientboundSetTitleTextPacket packet, Player player) {
        return new ClientboundSetTitleTextPacket(translateNMS(packet.text(), player));
    }

    private static Packet<?> handleSubtitle(ClientboundSetSubtitleTextPacket packet, Player player) {
        return new ClientboundSetSubtitleTextPacket(translateNMS(packet.text(), player));
    }

    private static Packet<?> handleTabList(ClientboundTabListPacket packet, Player player) {
        return new ClientboundTabListPacket(translateNMS(packet.header(), player), translateNMS(packet.footer(), player));
    }

    private static Packet<?> handleOpenScreen(ClientboundOpenScreenPacket packet, Player player) {
        return new ClientboundOpenScreenPacket(packet.getContainerId(), packet.getType(), translateNMS(packet.getTitle(), player));
    }

    private static Packet<?> handleObjective(ClientboundSetObjectivePacket packet, Player player) {
        Objective dummy = new Objective(
            null,
            packet.getObjectiveName(),
            null,
            translateNMS(packet.getDisplayName(), player),
            packet.getRenderType(),
            false,
            packet.getNumberFormat().orElse(null)
        );
        return new ClientboundSetObjectivePacket(dummy, packet.getMethod());
    }

    private static Packet<?> handleBossEvent(ClientboundBossEventPacket packet, Player player) {
        try {
            for (Field field : packet.getClass().getDeclaredFields()) {
                if (net.minecraft.network.chat.Component.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    net.minecraft.network.chat.Component comp = (net.minecraft.network.chat.Component) field.get(packet);
                    if (comp != null) {
                        field.set(packet, translateNMS(comp, player));
                    }
                } else if (!field.getType().isPrimitive() && !field.getType().getName().startsWith("java.lang.")) {
                    field.setAccessible(true);
                    Object innerObj = field.get(packet);
                    if (innerObj != null) {
                        for (Field innerField : innerObj.getClass().getDeclaredFields()) {
                            if (net.minecraft.network.chat.Component.class.isAssignableFrom(innerField.getType())) {
                                innerField.setAccessible(true);
                                net.minecraft.network.chat.Component innerComp = (net.minecraft.network.chat.Component) innerField.get(innerObj);
                                if (innerComp != null) {
                                    innerField.set(innerObj, translateNMS(innerComp, player));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return packet;
    }

    private static Packet<?> handleShowDialog(ClientboundShowDialogPacket packet, Player player) {
        Dialog original = packet.dialog().value();
        Dialog translated = translateDialog(original, player);
        if (original == translated) return packet;
        return new ClientboundShowDialogPacket(Holder.direct(translated));
    }

    private static Dialog translateDialog(Dialog dialog, Player player) {
        if (dialog instanceof NoticeDialog(CommonDialogData common, ActionButton action)) {
            return new NoticeDialog(translateCommonDialogData(common, player), translateActionButton(action, player));
        } else if (dialog instanceof ConfirmationDialog(CommonDialogData common, ActionButton yesButton, ActionButton noButton)) {
            return new ConfirmationDialog(translateCommonDialogData(common, player), translateActionButton(yesButton, player), translateActionButton(noButton, player));
        } else if (dialog instanceof DialogListDialog(CommonDialogData common, HolderSet<Dialog> dialogs, Optional<ActionButton> exitAction, int columns, int buttonWidth)) {
            List<Holder<Dialog>> newDialogs = new ArrayList<>();
            for (Holder<Dialog> h : dialogs) {
                newDialogs.add(Holder.direct(translateDialog(h.value(), player)));
            }
            return new DialogListDialog(translateCommonDialogData(common, player), HolderSet.direct(newDialogs), exitAction.map(a -> translateActionButton(a, player)), columns, buttonWidth);
        } else if (dialog instanceof MultiActionDialog(CommonDialogData common, List<ActionButton> actions, Optional<ActionButton> exitAction, int columns)) {
            List<ActionButton> newActions = actions.stream().map(a -> translateActionButton(a, player)).toList();
            return new MultiActionDialog(translateCommonDialogData(common, player), newActions, exitAction.map(a -> translateActionButton(a, player)), columns);
        } else if (dialog instanceof ServerLinksDialog(CommonDialogData common, Optional<ActionButton> exitAction, int columns, int buttonWidth)) {
            return new ServerLinksDialog(translateCommonDialogData(common, player), exitAction.map(a -> translateActionButton(a, player)), columns, buttonWidth);
        }
        return dialog;
    }

    private static CommonDialogData translateCommonDialogData(CommonDialogData data, Player player) {
        return new CommonDialogData(
            translateNMS(data.title(), player),
            data.externalTitle().map(c -> translateNMS(c, player)),
            data.canCloseWithEscape(),
            data.pause(),
            data.afterAction(),
            data.body(),
            data.inputs()
        );
    }

    private static CommonButtonData translateCommonButtonData(CommonButtonData data, Player player) {
        return new CommonButtonData(
            translateNMS(data.label(), player),
            data.width()
        );
    }

    private static ActionButton translateActionButton(ActionButton button, Player player) {
        return new ActionButton(translateCommonButtonData(button.button(), player), button.action());
    }

    private static Packet<?> handleSlot(ClientboundContainerSetSlotPacket packet, Player player) {
        ItemStack processed = processItemSend(packet.getItem(), player);
        if (processed != packet.getItem()) {
            return new ClientboundContainerSetSlotPacket(packet.getContainerId(), packet.getStateId(), packet.getSlot(), processed);
        }
        return packet;
    }

    private static Packet<?> handleContent(ClientboundContainerSetContentPacket packet, Player player) {
        List<ItemStack> originalItems = packet.items();
        NonNullList<ItemStack> translatedItems = NonNullList.withSize(originalItems.size(), ItemStack.EMPTY);
        boolean changed = false;

        for (int i = 0; i < originalItems.size(); i++) {
            ItemStack stack = originalItems.get(i);
            ItemStack processed = processItemSend(stack, player);
            if (processed != stack) changed = true;
            translatedItems.set(i, processed);
        }

        ItemStack carried = packet.carriedItem();
        ItemStack processedCarried = processItemSend(carried, player);
        if (processedCarried != carried) changed = true;

        if (!changed) return packet;
        return new ClientboundContainerSetContentPacket(packet.containerId(), packet.stateId(), translatedItems, processedCarried);
    }

    @SuppressWarnings("unchecked")
    private static Packet<?> handleEntityData(ClientboundSetEntityDataPacket packet, Player player) {
        List<SynchedEntityData.DataValue<?>> packed = packet.packedItems();
        List<SynchedEntityData.DataValue<?>> newValues = new ArrayList<>(packed.size());
        boolean modified = false;

        for (SynchedEntityData.DataValue<?> entry : packed) {
            Object val = entry.value();
            switch (val) {
                case Optional<?> opt when opt.isPresent() && opt.get() instanceof net.minecraft.network.chat.Component c -> {
                    net.minecraft.network.chat.Component translated = translateNMS(c, player);
                    EntityDataSerializer<Optional<net.minecraft.network.chat.Component>> serializer = (EntityDataSerializer<Optional<net.minecraft.network.chat.Component>>) entry.serializer();
                    newValues.add(new SynchedEntityData.DataValue<>(entry.id(), serializer, Optional.of(translated)));
                    modified = true;
                }
                case net.minecraft.network.chat.Component c -> {
                    net.minecraft.network.chat.Component translated = translateNMS(c, player);
                    EntityDataSerializer<net.minecraft.network.chat.Component> serializer = (EntityDataSerializer<net.minecraft.network.chat.Component>) entry.serializer();
                    newValues.add(new SynchedEntityData.DataValue<>(entry.id(), serializer, translated));
                    modified = true;
                }
                case ItemStack stack -> {
                    ItemStack processed = processItemSend(stack, player);
                    if (processed != stack) {
                        EntityDataSerializer<ItemStack> serializer = (EntityDataSerializer<ItemStack>) entry.serializer();
                        newValues.add(new SynchedEntityData.DataValue<>(entry.id(), serializer, processed));
                        modified = true;
                    } else {
                        newValues.add(entry);
                    }
                }
                default -> newValues.add(entry);
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
            ItemStack processed = processItemSend(pair.getSecond(), player);
            if (processed != pair.getSecond()) changed = true;
            newSlots.add(Pair.of(pair.getFirst(), processed));
        }

        if (!changed) return packet;
        return new ClientboundSetEquipmentPacket(packet.getEntity(), newSlots);
    }

    private static Packet<?> handleMerchantOffers(ClientboundMerchantOffersPacket packet, Player player) {
        MerchantOffers original = packet.getOffers();
        MerchantOffers copy = new MerchantOffers();
        boolean changed = false;

        for (MerchantOffer offer : original) {
            ItemStack result = processItemSend(offer.getResult(), player);

            if (result != offer.getResult()) changed = true;

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

    private static Packet<?> handleCreativeSlot(ServerboundSetCreativeModeSlotPacket packet, Player player) {
        ItemStack item = packet.itemStack();
        ItemStack untranslated = untranslateItem(item, player);
        if (untranslated != item) {
            return new ServerboundSetCreativeModeSlotPacket(packet.slotNum(), untranslated);
        }
        return packet;
    }

    private static ItemStack processItemSend(ItemStack stack, Player player) {
        if (stack == null || stack.isEmpty()) return stack;

        ItemStack untranslated = untranslateItem(stack, player);
        boolean wasUntranslated = (untranslated != stack);

        ItemStack workingCopy = untranslated.copy();
        org.bukkit.inventory.ItemStack bukkitStack = CraftItemStack.asCraftMirror(workingCopy);

        boolean modified = false;
        for (ClientItemModifier modifier : MODIFIERS) {
            if (modifier.modify(bukkitStack, player)) {
                modified = true;
            }
        }

        boolean needsTranslation = false;
        net.minecraft.network.chat.Component customName = workingCopy.get(DataComponents.CUSTOM_NAME);
        if (customName != null) {
            workingCopy.set(DataComponents.CUSTOM_NAME, translateItemNMS(customName, player, workingCopy, ItemTranslationContext.CUSTOM_NAME));
            needsTranslation = true;
        }

        net.minecraft.network.chat.Component itemName = workingCopy.get(DataComponents.ITEM_NAME);
        if (itemName != null) {
            workingCopy.set(DataComponents.ITEM_NAME, translateItemNMS(itemName, player, workingCopy, ItemTranslationContext.NAME));
            needsTranslation = true;
        }

        ItemLore lore = workingCopy.get(DataComponents.LORE);
        if (lore != null) {
            List<net.minecraft.network.chat.Component> translatedLines = new ArrayList<>();
            for (net.minecraft.network.chat.Component line : lore.lines()) {
                translatedLines.add(translateItemNMS(line, player, workingCopy, ItemTranslationContext.LORE));
            }
            workingCopy.set(DataComponents.LORE, new ItemLore(translatedLines));
            needsTranslation = true;
        }

        if (modified || needsTranslation) {
            CustomData existing = workingCopy.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag rootTag = existing.copyTag();

            byte[] serialized = CraftItemStack.asCraftMirror(untranslated).serializeAsBytes();
            rootTag.putByteArray("OriginalItem", serialized);

            workingCopy.set(DataComponents.CUSTOM_DATA, CustomData.of(rootTag));
            return workingCopy;
        }

        if (wasUntranslated) {
            return untranslated;
        }

        return stack;
    }

    private static ItemStack untranslateItem(ItemStack stack, Player player) {
        if (stack == null || stack.isEmpty()) return stack;

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return stack;

        CompoundTag rootTag = customData.copyTag();

        if (rootTag.contains("OriginalItem")) {
            Optional<byte[]> optBytes = rootTag.getByteArray("OriginalItem");
            if (optBytes.isPresent() && optBytes.get().length > 0) {
                try {
                    org.bukkit.inventory.ItemStack restoredBukkit = org.bukkit.inventory.ItemStack.deserializeBytes(optBytes.get());
                    return CraftItemStack.asNMSCopy(restoredBukkit);
                } catch (Exception ignored) {}
            }
        }

        return stack;
    }

    private static Component preProcessTags(Component component) {
        return component
            .replaceText(b -> b.match(LANG_PATTERN)
                .replacement((match, _) -> Component.translatable(match.group(1))))
            .replaceText(b -> b.match(LANG_OR_PATTERN)
                .replacement((match, _) -> Component.translatable(match.group(1)).fallback(match.group(2))));
    }

    public static net.minecraft.network.chat.Component translateItemNMS(net.minecraft.network.chat.Component vanilla, Player player, ItemStack stack, ItemTranslationContext context) {
        Component adventure = preProcessTags(PaperAdventure.asAdventure(vanilla));
        org.bukkit.inventory.ItemStack bukkitStack = CraftItemStack.asCraftMirror(stack);
        Component translated = ServerTranslator.translateItemComponent(adventure, player, bukkitStack, context);
        return PaperAdventure.asVanilla(translated);
    }

    public static net.minecraft.network.chat.Component translateNMS(net.minecraft.network.chat.Component vanilla, Player player) {
        Component adventure = preProcessTags(PaperAdventure.asAdventure(vanilla));
        Component translated = ServerTranslator.translate(adventure, player);
        return PaperAdventure.asVanilla(translated);
    }
}