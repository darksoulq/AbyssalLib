package com.github.darksoulq.abyssallib.server.translation.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.translation.ItemTranslationContext;
import com.github.darksoulq.abyssallib.server.translation.ServerTranslator;
import com.mojang.datafixers.util.Pair;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
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

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PacketTranslator {
    private static final Map<UUID, Map<Integer, String>> TRANSLATION_STATES = new ConcurrentHashMap<>();
    private static final GsonComponentSerializer GSON = GsonComponentSerializer.gson();
    private static final Base64.Encoder B64_ENCODER = Base64.getEncoder();
    private static final Base64.Decoder B64_DECODER = Base64.getDecoder();

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
                        ItemStack translated = original.copy();
                        translateItem(translated, player);

                        String currentState = getTranslationState(translated);
                        String lastState = states.get(i);

                        if (!currentState.equals(lastState)) {
                            states.put(i, currentState);
                            sp.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.getStateId(), i, translated));
                        }
                    } else {
                        states.remove(i);
                    }
                }

                ItemStack carried = menu.getCarried();
                if (!carried.isEmpty()) {
                    ItemStack translatedCarried = carried.copy();
                    translateItem(translatedCarried, player);

                    String currentState = getTranslationState(translatedCarried);
                    String lastState = states.get(-1);

                    if (!currentState.equals(lastState)) {
                        states.put(-1, currentState);
                        sp.connection.send(new ClientboundContainerSetSlotPacket(-1, menu.getStateId(), -1, translatedCarried));
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
            return handleCreativeSlot(p);
        }
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
            for (java.lang.reflect.Field field : packet.getClass().getDeclaredFields()) {
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
                        for (java.lang.reflect.Field innerField : innerObj.getClass().getDeclaredFields()) {
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
                    if (!stack.isEmpty()) {
                        ItemStack copy = stack.copy();
                        translateItem(copy, player);
                        EntityDataSerializer<ItemStack> serializer = (EntityDataSerializer<ItemStack>) entry.serializer();
                        newValues.add(new SynchedEntityData.DataValue<>(entry.id(), serializer, copy));
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

    private static Packet<?> handleCreativeSlot(ServerboundSetCreativeModeSlotPacket packet) {
        ItemStack item = packet.itemStack();
        if (!item.isEmpty()) {
            untranslateItem(item);
        }
        return packet;
    }

    private static void translateItem(ItemStack stack, Player player) {
        untranslateItem(stack);

        CustomData existing = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag rootTag = existing.copyTag();

        Optional<CompoundTag> customDataOpt = rootTag.getCompound("CustomData");

        CompoundTag translationCache = new CompoundTag();
        boolean hasOg = false;

        net.minecraft.network.chat.Component customName = stack.get(DataComponents.CUSTOM_NAME);
        if (customName != null) {
            String json = GSON.serialize(PaperAdventure.asAdventure(customName));
            translationCache.putString("cn", B64_ENCODER.encodeToString(json.getBytes(StandardCharsets.UTF_8)));
            stack.set(DataComponents.CUSTOM_NAME, translateItemNMS(customName, player, stack, ItemTranslationContext.CUSTOM_NAME));
            hasOg = true;
        }

        net.minecraft.network.chat.Component itemName = stack.get(DataComponents.ITEM_NAME);
        if (itemName != null) {
            String json = GSON.serialize(PaperAdventure.asAdventure(itemName));
            translationCache.putString("in", B64_ENCODER.encodeToString(json.getBytes(StandardCharsets.UTF_8)));
            stack.set(DataComponents.ITEM_NAME, translateItemNMS(itemName, player, stack, ItemTranslationContext.NAME));
            hasOg = true;
        }

        ItemLore lore = stack.get(DataComponents.LORE);
        if (lore != null) {
            ListTag loreOg = new ListTag();
            List<net.minecraft.network.chat.Component> translatedLines = new ArrayList<>();

            for (net.minecraft.network.chat.Component line : lore.lines()) {
                String json = GSON.serialize(PaperAdventure.asAdventure(line));
                loreOg.add(StringTag.valueOf(B64_ENCODER.encodeToString(json.getBytes(StandardCharsets.UTF_8))));
                translatedLines.add(translateItemNMS(line, player, stack, ItemTranslationContext.LORE));
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

    private static Component preProcessTags(Component component) {
        return component
            .replaceText(b -> b.match(java.util.regex.Pattern.compile("<(?:lang|tr|translate):([^>]+)>"))
                .replacement((match, builder) -> Component.translatable(match.group(1))))
            .replaceText(b -> b.match(java.util.regex.Pattern.compile("<(?:lang_or|tr_or|translate_or):([^:]+):([^>]+)>"))
                .replacement((match, builder) -> Component.translatable(match.group(1)).fallback(match.group(2))));
    }

    private static net.minecraft.network.chat.Component translateItemNMS(net.minecraft.network.chat.Component vanilla, Player player, ItemStack stack, ItemTranslationContext context) {
        Component adventure = preProcessTags(PaperAdventure.asAdventure(vanilla));
        org.bukkit.inventory.ItemStack bukkitStack = CraftItemStack.asCraftMirror(stack);
        Component translated = ServerTranslator.translateItemComponent(adventure, player, bukkitStack, context);
        return PaperAdventure.asVanilla(translated);
    }

    private static boolean untranslateItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return false;

        CompoundTag rootTag = customData.copyTag();
        Optional<CompoundTag> customDataOpt = rootTag.getCompound("CustomData");
        if (customDataOpt.isEmpty()) return false;

        CompoundTag customDataKeyTag = customDataOpt.get();
        Optional<CompoundTag> cacheOpt = customDataKeyTag.getCompound("TranslationCache");
        if (cacheOpt.isEmpty()) return false;

        CompoundTag translationCache = cacheOpt.get();

        try {
            translationCache.getString("cn").ifPresent(b64 -> {
                try {
                    String cleanB64 = b64.replaceAll("^\"|\"$", "");
                    String json = new String(B64_DECODER.decode(cleanB64), StandardCharsets.UTF_8);
                    stack.set(DataComponents.CUSTOM_NAME, PaperAdventure.asVanilla(GSON.deserialize(json)));
                } catch (Exception ignored) {}
            });

            translationCache.getString("in").ifPresent(b64 -> {
                try {
                    String cleanB64 = b64.replaceAll("^\"|\"$", "");
                    String json = new String(B64_DECODER.decode(cleanB64), StandardCharsets.UTF_8);
                    stack.set(DataComponents.ITEM_NAME, PaperAdventure.asVanilla(GSON.deserialize(json)));
                } catch (Exception ignored) {}
            });

            translationCache.getList("lr").ifPresent(loreOg -> {
                try {
                    List<net.minecraft.network.chat.Component> restoredLines = new ArrayList<>();
                    for (Tag tag : loreOg) {
                        tag.asString().ifPresent(b64 -> {
                            try {
                                String cleanB64 = b64.replaceAll("^\"|\"$", "");
                                String json = new String(B64_DECODER.decode(cleanB64), StandardCharsets.UTF_8);
                                restoredLines.add(PaperAdventure.asVanilla(GSON.deserialize(json)));
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

    private static net.minecraft.network.chat.Component translateNMS(net.minecraft.network.chat.Component vanilla, Player player) {
        Component adventure = preProcessTags(PaperAdventure.asAdventure(vanilla));
        Component translated = ServerTranslator.translate(adventure, player);
        return PaperAdventure.asVanilla(translated);
    }
}