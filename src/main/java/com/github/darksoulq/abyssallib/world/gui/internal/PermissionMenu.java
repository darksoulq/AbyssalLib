package com.github.darksoulq.abyssallib.world.gui.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.chat.ChatInputHandler;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiClickContext;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiDragContext;
import com.github.darksoulq.abyssallib.server.permission.Node;
import com.github.darksoulq.abyssallib.server.permission.PermissionGroup;
import com.github.darksoulq.abyssallib.server.permission.PermissionHolder;
import com.github.darksoulq.abyssallib.server.permission.PermissionNode;
import com.github.darksoulq.abyssallib.server.permission.PermissionUser;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gui.*;
import com.github.darksoulq.abyssallib.world.gui.element.GuiButton;
import com.github.darksoulq.abyssallib.world.gui.element.GuiItem;
import com.github.darksoulq.abyssallib.world.gui.layer.PagedLayer;
import com.github.darksoulq.abyssallib.world.item.Items;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("UnstableApiUsage")
public class PermissionMenu {
    private static final int[] BOTTOM_SLOTS = {
        9, 10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
        27, 28, 29, 30, 31, 32, 33, 34, 35
    };

    private static final Map<UUID, ItemStack[]> INVENTORY_BACKUPS = new HashMap<>();

    public static final Comparator<String> NODE_COMPARATOR = (s1, s2) -> {
        String[] p1 = s1.split("\\.");
        String[] p2 = s2.split("\\.");
        for (int i = 0; i < Math.min(p1.length, p2.length); i++) {
            int c = p1[i].compareToIgnoreCase(p2[i]);
            if (c != 0) return c;
        }
        return Integer.compare(p1.length, p2.length);
    };

    static void setupBackup(GuiView view) {
        Player player = (Player) view.getInventoryView().getPlayer();
        INVENTORY_BACKUPS.put(player.getUniqueId(), player.getInventory().getContents());
        ItemStack[] bottomContents = view.getBottom().getContents();
        Arrays.fill(bottomContents, ItemStack.empty());
        view.getBottom().setContents(bottomContents);
    }

    static void loadBackup(GuiView view) {
        view.getTop().setItem(0, ItemStack.of(Material.AIR));
        view.getTop().setItem(1, ItemStack.of(Material.AIR));
        view.getTop().setItem(2, ItemStack.of(Material.AIR));
        ItemStack[] bottomContents = view.getBottom().getContents();
        Arrays.fill(bottomContents, ItemStack.empty());
        view.getBottom().setContents(bottomContents);
        Player player = (Player) view.getInventoryView().getPlayer();
        ItemStack[] backup = INVENTORY_BACKUPS.remove(player.getUniqueId());
        if (backup != null) player.getInventory().setContents(backup);
    }

    private static long parseDuration(String input) {
        if (input == null || input.isEmpty() || input.equalsIgnoreCase("permanent") || input.equals("0")) return 0L;
        long total = 0;
        Matcher m = Pattern.compile("(\\d+)\\s*(y|mo|w|d|h|m|s)").matcher(input.toLowerCase(Locale.ROOT));
        while (m.find()) {
            long val = Long.parseLong(m.group(1));
            String unit = m.group(2);
            switch (unit) {
                case "y" -> total += val * 31536000000L;
                case "mo" -> total += val * 2592000000L;
                case "w" -> total += val * 604800000L;
                case "d" -> total += val * 86400000L;
                case "h" -> total += val * 3600000L;
                case "m" -> total += val * 60000L;
                case "s" -> total += val * 1000L;
            }
        }
        return total > 0 ? System.currentTimeMillis() + total : 0L;
    }

    private static String formatExpiry(long expiry) {
        if (expiry == 0) return "Permanent";
        long diff = expiry - System.currentTimeMillis();
        if (diff <= 0) return "Expired";
        long s = diff / 1000;
        if (s < 60) return s + "s";
        long m = s / 60;
        if (m < 60) return m + "m";
        long h = m / 60;
        if (h < 24) return h + "h";
        return (h / 24) + "d";
    }

    private static GuiElement createNodeElement(PermissionHolder holder, Node node, Player player, Runnable reopener) {
        return new GuiElement() {
            @Override
            public ItemStack render(GuiView view, int slot) {
                ItemStack item = new ItemStack(node.getValue() ? Material.LIME_DYE : Material.RED_DYE);
                item.setData(DataComponentTypes.ITEM_NAME, Component.text(node.getKey(), node.getValue() ? NamedTextColor.GREEN : NamedTextColor.RED));

                String desc = "";
                PermissionNode pNode = Registries.PERMISSIONS.get(node.getKey());
                if (pNode != null && pNode.getDescription() != null && !pNode.getDescription().isEmpty()) {
                    desc = pNode.getDescription();
                } else {
                    Permission bkPerm = Bukkit.getPluginManager().getPermission(node.getKey());
                    if (bkPerm != null && !bkPerm.getDescription().isEmpty()) {
                        desc = bkPerm.getDescription();
                    }
                }

                List<Component> lore = new ArrayList<>();
                if (!desc.isEmpty()) lore.add(Component.text(desc, NamedTextColor.GRAY));
                lore.add(Component.text("Expiry: " + formatExpiry(node.getExpiry()), NamedTextColor.GOLD));
                lore.add(Component.text("Left-Click to Toggle Value", NamedTextColor.YELLOW));
                lore.add(Component.text("Shift-Click to Set Expiry", NamedTextColor.YELLOW));
                lore.add(Component.text("Right-Click to Remove", NamedTextColor.RED));
                item.setData(DataComponentTypes.LORE, ItemLore.lore(lore));
                return item;
            }

            @Override
            public ActionResult onClick(GuiClickContext ctx) {
                if (ctx.clickType().isRightClick() && !ctx.clickType().isShiftClick()) {
                    holder.unsetPermission(node.getKey());
                    holder.save();
                    reopener.run();
                } else if (ctx.clickType().isLeftClick() && !ctx.clickType().isShiftClick()) {
                    holder.setPermission(new Node(node.getKey(), !node.getValue(), node.getExpiry()));
                    holder.save();
                    reopener.run();
                } else if (ctx.clickType().isShiftClick()) {
                    GuiManager.close(player);
                    ChatInputHandler.await(player, input -> {
                        long exp = parseDuration(input.trim());
                        holder.setPermission(new Node(node.getKey(), node.getValue(), exp));
                        holder.save();
                        reopener.run();
                    }, TextUtil.parse("<yellow>Enter duration (e.g., 1w 2d 5h, or 0 for permanent):</yellow>"));
                }
                return ActionResult.CANCEL;
            }

            @Override
            public ActionResult onDrag(GuiDragContext ctx) {
                return ActionResult.CANCEL;
            }
        };
    }

    private static GuiElement createParentElement(PermissionHolder holder, Node parent, Player player, Runnable reopener) {
        return new GuiElement() {
            @Override
            public ItemStack render(GuiView view, int slot) {
                ItemStack item = new ItemStack(Material.BOOK);
                item.setData(DataComponentTypes.ITEM_NAME, Component.text("Parent: " + parent.getKey(), NamedTextColor.GOLD));
                item.setData(DataComponentTypes.LORE, ItemLore.lore(List.of(
                    Component.text("Expiry: " + formatExpiry(parent.getExpiry()), NamedTextColor.GOLD),
                    Component.text("Shift-Click to Set Expiry", NamedTextColor.YELLOW),
                    Component.text("Right-Click to Remove", NamedTextColor.RED)
                )));
                return item;
            }

            @Override
            public ActionResult onClick(GuiClickContext ctx) {
                if (ctx.clickType().isRightClick() && !ctx.clickType().isShiftClick()) {
                    holder.removeParent(parent.getKey());
                    holder.save();
                    reopener.run();
                } else if (ctx.clickType().isShiftClick()) {
                    GuiManager.close(player);
                    ChatInputHandler.await(player, input -> {
                        long exp = parseDuration(input.trim());
                        holder.addParent(new Node(parent.getKey(), true, exp));
                        holder.save();
                        reopener.run();
                    }, TextUtil.parse("<yellow>Enter duration (e.g., 1w 2d 5h, or 0 for permanent):</yellow>"));
                }
                return ActionResult.CANCEL;
            }

            @Override
            public ActionResult onDrag(GuiDragContext ctx) {
                return ActionResult.CANCEL;
            }
        };
    }

    public static void openMainMenu(Player player) {
        Gui.Builder gui = Gui.builder(MenuType.GENERIC_9X3, TextUtil.parse("Permission Manager"));
        gui.addFlags(GuiFlag.DISABLE_ITEM_PICKUP, GuiFlag.DISABLE_ADVANCEMENTS);

        ItemStack groupItem = new ItemStack(Material.NAME_TAG);
        groupItem.setData(DataComponentTypes.ITEM_NAME, Component.text("Groups", NamedTextColor.YELLOW));

        ItemStack userItem = new ItemStack(Material.PLAYER_HEAD);
        userItem.setData(DataComponentTypes.ITEM_NAME, Component.text("Users", NamedTextColor.AQUA));

        gui.set(SlotPosition.top(11), GuiButton.of(groupItem, ctx -> openGroupList(player)));
        gui.set(SlotPosition.top(15), GuiButton.of(userItem, ctx -> openUserList(player)));
        gui.set(SlotPosition.top(22), GuiButton.of(Items.CLOSE.get().getStack(), ctx -> GuiManager.close(player)));

        GuiManager.open(player, gui.build());
    }

    public static void openGroupList(Player player) {
        Gui.Builder gui = Gui.builder(MenuType.GENERIC_9X6, TextUtil.parse("Permission Groups"));
        gui.addFlags(GuiFlag.DISABLE_ITEM_PICKUP, GuiFlag.DISABLE_ADVANCEMENTS);

        List<GuiElement> elements = new ArrayList<>();
        List<PermissionGroup> sortedGroups = Registries.PERMISSION_GROUPS.getAll().values().stream()
            .sorted(Comparator.comparing(PermissionGroup::getId))
            .toList();

        for (PermissionGroup group : sortedGroups) {
            ItemStack item = new ItemStack(Material.PAPER);
            item.setData(DataComponentTypes.ITEM_NAME, Component.text(group.getId(), NamedTextColor.GREEN));
            item.setData(DataComponentTypes.LORE, ItemLore.lore(List.of(
                Component.text("Weight: " + group.getWeight(), NamedTextColor.GRAY),
                Component.text("Nodes: " + group.getNodes().size(), NamedTextColor.GRAY),
                Component.text("Left-Click to Edit", NamedTextColor.YELLOW),
                Component.text("Shift-Left-Click to set Weight", NamedTextColor.YELLOW),
                Component.text("Shift-Right-Click to Delete", NamedTextColor.RED)
            )));

            elements.add(GuiButton.of(item, ctx -> {
                if (ctx.clickType().isShiftClick() && ctx.clickType().isRightClick()) {
                    GuiManager.close(player);
                    ChatInputHandler.await(player, input -> {
                        if (input.trim().equalsIgnoreCase("confirm")) {
                            AbyssalLib.PERMISSION_MANAGER.deleteGroup(group.getId());
                            player.sendMessage(TextUtil.parse("<green>Group deleted.</green>"));
                        } else {
                            player.sendMessage(TextUtil.parse("<red>Deletion cancelled.</red>"));
                        }
                        openGroupList(player);
                    }, TextUtil.parse("<red>Type 'confirm' to delete group " + group.getId() + ".</red>"));
                } else if (ctx.clickType().isShiftClick() && ctx.clickType().isLeftClick()) {
                    GuiManager.close(player);
                    ChatInputHandler.await(player, input -> {
                        try {
                            group.setWeight(Integer.parseInt(input.trim()));
                            group.save();
                            player.sendMessage(TextUtil.parse("<green>Weight updated.</green>"));
                        } catch (NumberFormatException e) {
                            player.sendMessage(TextUtil.parse("<red>Invalid integer.</red>"));
                        }
                        openGroupList(player);
                    }, TextUtil.parse("<yellow>Enter new weight (integer):</yellow>"));
                } else {
                    openGroupEditor(player, group);
                }
            }));
        }

        ItemStack createGroupItem = new ItemStack(Material.EMERALD_BLOCK);
        createGroupItem.setData(DataComponentTypes.ITEM_NAME, Component.text("+ Create New Group", NamedTextColor.GREEN));

        gui.set(SlotPosition.top(49), GuiButton.of(Items.BACK.get().getStack(), ctx -> openMainMenu(player)));
        gui.set(SlotPosition.top(50), GuiButton.of(createGroupItem, ctx -> {
            GuiManager.close(player);
            ChatInputHandler.await(player, input -> {
                String id = input.trim().toLowerCase(Locale.ROOT);
                if (!id.matches("^[a-z0-9_]+$")) {
                    player.sendMessage(TextUtil.parse("<red>Invalid group name. Use lowercase, numbers, and underscores.</red>"));
                    openGroupList(player);
                    return;
                }
                if (Registries.PERMISSION_GROUPS.contains(id)) {
                    player.sendMessage(TextUtil.parse("<red>Group already exists.</red>"));
                    openGroupList(player);
                    return;
                }
                PermissionGroup newGroup = new PermissionGroup(id);
                Registries.PERMISSION_GROUPS.register(id, newGroup);
                newGroup.save();
                openGroupEditor(player, newGroup);
            }, TextUtil.parse("<yellow>Enter new group ID:</yellow>"));
        }));

        setupPages(player, gui, elements, PermissionMenu::openMainMenu, false);
    }

    public static void openGroupEditor(Player player, PermissionGroup group) {
        Gui.Builder gui = Gui.builder(MenuType.GENERIC_9X6, TextUtil.parse("Edit Group: " + group.getId()));
        gui.addFlags(GuiFlag.DISABLE_ITEM_PICKUP, GuiFlag.DISABLE_ADVANCEMENTS);

        ItemStack weightItem = new ItemStack(Material.ANVIL);
        weightItem.setData(DataComponentTypes.ITEM_NAME, Component.text("Weight: " + group.getWeight(), NamedTextColor.YELLOW));
        weightItem.setData(DataComponentTypes.LORE, ItemLore.lore(List.of(Component.text("Click to edit", NamedTextColor.GRAY))));
        gui.set(SlotPosition.top(4), GuiButton.of(weightItem, ctx -> {
            GuiManager.close(player);
            ChatInputHandler.await(player, input -> {
                try {
                    group.setWeight(Integer.parseInt(input.trim()));
                    group.save();
                } catch (NumberFormatException ignored) {
                    player.sendMessage(TextUtil.parse("<red>Invalid integer.</red>"));
                }
                openGroupEditor(player, group);
            }, TextUtil.parse("<yellow>Enter new weight (integer):</yellow>"));
        }));

        ItemStack deleteItem = new ItemStack(Material.BARRIER);
        deleteItem.setData(DataComponentTypes.ITEM_NAME, Component.text("Delete Group", NamedTextColor.RED));
        gui.set(SlotPosition.top(8), GuiButton.of(deleteItem, ctx -> {
            GuiManager.close(player);
            ChatInputHandler.await(player, input -> {
                if (input.trim().equalsIgnoreCase("confirm")) {
                    AbyssalLib.PERMISSION_MANAGER.deleteGroup(group.getId());
                    openGroupList(player);
                } else {
                    player.sendMessage(TextUtil.parse("<red>Deletion cancelled.</red>"));
                    openGroupEditor(player, group);
                }
            }, TextUtil.parse("<red>Type 'confirm' to delete group " + group.getId() + ".</red>"));
        }));

        List<GuiElement> elements = new ArrayList<>();

        List<Node> sortedNodes = group.getNodes().stream()
            .sorted((n1, n2) -> NODE_COMPARATOR.compare(n1.getKey(), n2.getKey()))
            .toList();

        for (Node node : sortedNodes) {
            elements.add(createNodeElement(group, node, player, () -> openGroupEditor(player, group)));
        }

        List<Node> sortedParents = group.getParentNodes().stream()
            .sorted((n1, n2) -> NODE_COMPARATOR.compare(n1.getKey(), n2.getKey()))
            .toList();

        for (Node parent : sortedParents) {
            elements.add(createParentElement(group, parent, player, () -> openGroupEditor(player, group)));
        }

        ItemStack addNode = new ItemStack(Material.EMERALD_BLOCK);
        addNode.setData(DataComponentTypes.ITEM_NAME, Component.text("Add Permission Node", NamedTextColor.GREEN));
        gui.set(SlotPosition.top(48), GuiButton.of(addNode, ctx -> openAddPermission(player, group, () -> openGroupEditor(player, group))));

        gui.set(SlotPosition.top(49), GuiButton.of(Items.BACK.get().getStack(), ctx -> openGroupList(player)));

        ItemStack addGroup = new ItemStack(Material.GOLD_BLOCK);
        addGroup.setData(DataComponentTypes.ITEM_NAME, Component.text("Add Parent Group", NamedTextColor.YELLOW));
        gui.set(SlotPosition.top(50), GuiButton.of(addGroup, ctx -> openAddGroup(player, group, () -> openGroupEditor(player, group))));

        PagedLayer<GuiElement> layer = setupPages(player, gui, elements, PermissionMenu::openGroupList, true);

        gui.onTick(v -> {
            if (group.clearExpired()) {
                group.save();
                openGroupEditor(player, group);
            } else {
                layer.renderTo(v);
            }
        });
    }

    public static void openUserList(Player player) {
        Gui.Builder gui = Gui.builder(MenuType.GENERIC_9X6, TextUtil.parse("Known Users"));
        gui.addFlags(GuiFlag.DISABLE_ITEM_PICKUP, GuiFlag.DISABLE_ADVANCEMENTS);

        List<GuiElement> elements = new ArrayList<>();
        Map<UUID, String> knownUsers = AbyssalLib.PERMISSION_MANAGER.getKnownUsers();

        List<Map.Entry<UUID, String>> sortedUsers = knownUsers.entrySet().stream()
            .sorted((e1, e2) -> {
                String n1 = e1.getValue() != null ? e1.getValue() : e1.getKey().toString();
                String n2 = e2.getValue() != null ? e2.getValue() : e2.getKey().toString();
                return n1.compareToIgnoreCase(n2);
            })
            .toList();

        for (Map.Entry<UUID, String> entry : sortedUsers) {
            UUID u = entry.getKey();
            String name = entry.getValue();
            boolean isOnline = Bukkit.getPlayer(u) != null;

            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            item.setData(DataComponentTypes.ITEM_NAME, Component.text(name != null ? name : u.toString(), isOnline ? NamedTextColor.AQUA : NamedTextColor.GRAY));
            item.setData(DataComponentTypes.LORE, ItemLore.lore(List.of(Component.text(isOnline ? "Online" : "Offline", isOnline ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY))));

            elements.add(GuiButton.of(item, ctx -> {
                PermissionUser user = AbyssalLib.PERMISSION_MANAGER.getUser(u);
                openUserEditor(player, user);
            }));
        }

        gui.set(SlotPosition.top(49), GuiButton.of(Items.BACK.get().getStack(), ctx -> openMainMenu(player)));
        setupPages(player, gui, elements, PermissionMenu::openMainMenu, false);
    }

    public static void openUserEditor(Player player, PermissionUser user) {
        Gui.Builder gui = Gui.builder(MenuType.GENERIC_9X6, TextUtil.parse("Edit User: " + user.getName()));
        gui.addFlags(GuiFlag.DISABLE_ITEM_PICKUP, GuiFlag.DISABLE_ADVANCEMENTS);

        List<GuiElement> elements = new ArrayList<>();

        List<Node> sortedNodes = user.getNodes().stream()
            .sorted((n1, n2) -> NODE_COMPARATOR.compare(n1.getKey(), n2.getKey()))
            .toList();

        for (Node node : sortedNodes) {
            elements.add(createNodeElement(user, node, player, () -> openUserEditor(player, user)));
        }

        List<Node> sortedParents = user.getParentNodes().stream()
            .sorted((n1, n2) -> NODE_COMPARATOR.compare(n1.getKey(), n2.getKey()))
            .toList();

        for (Node group : sortedParents) {
            elements.add(createParentElement(user, group, player, () -> openUserEditor(player, user)));
        }

        ItemStack addNode = new ItemStack(Material.EMERALD_BLOCK);
        addNode.setData(DataComponentTypes.ITEM_NAME, Component.text("Add Permission Node", NamedTextColor.GREEN));
        gui.set(SlotPosition.top(48), GuiButton.of(addNode, ctx -> openAddPermission(player, user, () -> openUserEditor(player, user))));

        gui.set(SlotPosition.top(49), GuiButton.of(Items.BACK.get().getStack(), ctx -> openUserList(player)));

        ItemStack addGroup = new ItemStack(Material.GOLD_BLOCK);
        addGroup.setData(DataComponentTypes.ITEM_NAME, Component.text("Add Parent Group", NamedTextColor.YELLOW));
        gui.set(SlotPosition.top(50), GuiButton.of(addGroup, ctx -> openAddGroup(player, user, () -> openUserEditor(player, user))));

        PagedLayer<GuiElement> layer = setupPages(player, gui, elements, PermissionMenu::openUserList, true);

        gui.onTick(v -> {
            if (user.clearExpired()) {
                user.save();
                openUserEditor(player, user);
            } else {
                layer.renderTo(v);
            }
        });
    }

    public static void openAddPermission(Player player, PermissionHolder holder, Runnable onBack) {
        Gui.Builder gui = Gui.builder(MenuType.ANVIL, TextUtil.parse("Search/Add Node"));
        gui.addFlags(GuiFlag.DISABLE_ITEM_PICKUP, GuiFlag.DISABLE_ADVANCEMENTS);

        ItemStack invisibleFiller = Items.INVISIBLE_ITEM.get().getStack();
        invisibleFiller.setData(DataComponentTypes.ITEM_NAME, Component.text(" "));
        invisibleFiller.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build());
        gui.set(SlotPosition.top(0), GuiItem.of(invisibleFiller));

        Set<String> allPerms = new HashSet<>();
        for (Permission perm : Bukkit.getPluginManager().getPermissions()) {
            allPerms.add(perm.getName());
        }
        allPerms.addAll(Registries.PERMISSIONS.getAll().keySet());

        List<String> sortedPerms = allPerms.stream()
            .sorted(NODE_COMPARATOR)
            .toList();

        List<GuiElement> allElements = new ArrayList<>();
        for (String node : sortedPerms) {
            ItemStack item = new ItemStack(Material.PAPER);
            item.setData(DataComponentTypes.ITEM_NAME, Component.text(node, NamedTextColor.WHITE));

            String desc = "";
            PermissionNode pNode = Registries.PERMISSIONS.get(node);
            if (pNode != null && pNode.getDescription() != null && !pNode.getDescription().isEmpty()) {
                desc = pNode.getDescription();
            } else {
                Permission bkPerm = Bukkit.getPluginManager().getPermission(node);
                if (bkPerm != null && !bkPerm.getDescription().isEmpty()) {
                    desc = bkPerm.getDescription();
                }
            }

            List<Component> lore = new ArrayList<>();
            if (!desc.isEmpty()) lore.add(Component.text(desc, NamedTextColor.GRAY));
            lore.add(Component.text("Left-Click to set True", NamedTextColor.GREEN));
            lore.add(Component.text("Right-Click to set False", NamedTextColor.RED));
            item.setData(DataComponentTypes.LORE, ItemLore.lore(lore));

            allElements.add(GuiButton.of(item, ctx -> {
                holder.setPermission(new Node(node, ctx.clickType().isLeftClick()));
                holder.save();
                onBack.run();
            }));
        }

        PagedLayer<GuiElement> layer = PagedLayer.of(allElements, BOTTOM_SLOTS, GuiView.Segment.BOTTOM);

        class SearchState { String lastQuery = ""; }
        SearchState state = new SearchState();

        gui.onOpen(view -> {
            setupBackup(view);
            layer.setFilter(e -> true);
            layer.renderTo(view);
        });
        gui.onClose(PermissionMenu::loadBackup);

        gui.onTick(view -> {
            if (view.getInventoryView() instanceof org.bukkit.inventory.view.AnvilView anvil) {
                String query = Optional.ofNullable(anvil.getRenameText()).orElse("").trim();
                if (!query.equals(state.lastQuery)) {
                    state.lastQuery = query;
                    String[] tokens = query.toLowerCase(Locale.ROOT).split(" ");

                    layer.setFilter(e -> {
                        ItemStack i = e.render(view, 0);
                        if (i == null) return false;
                        Component name = i.getData(DataComponentTypes.ITEM_NAME);
                        assert name != null;
                        String node = PlainTextComponentSerializer.plainText().serialize(name);
                        boolean isCustom = Registries.PERMISSIONS.contains(node);
                        String lowerNode = node.toLowerCase(Locale.ROOT);

                        for (String token : tokens) {
                            if (token.isEmpty()) continue;
                            if (token.equals("@_custom")) { if (!isCustom) return false; }
                            else if (token.equals("@_bukkit")) { if (isCustom) return false; }
                            else if (token.startsWith("@")) {
                                String targetNs = token.substring(1);
                                if (!lowerNode.contains(targetNs)) return false;
                            }
                            else { if (!lowerNode.contains(token)) return false; }
                        }
                        return true;
                    });
                    layer.renderTo(view);
                }

                if (!query.isEmpty() && !query.startsWith("@")) {
                    ItemStack custom = new ItemStack(Material.WRITTEN_BOOK);
                    custom.setData(DataComponentTypes.ITEM_NAME, Component.text(query, NamedTextColor.AQUA));
                    custom.setData(DataComponentTypes.LORE, ItemLore.lore(List.of(
                        Component.text("Custom Node", NamedTextColor.GOLD),
                        Component.text("Left-Click to set True", NamedTextColor.GREEN),
                        Component.text("Right-Click to set False", NamedTextColor.RED)
                    )));

                    view.getGui().getElements().put(SlotPosition.top(2), GuiButton.of(custom, ctx -> {
                        holder.setPermission(new Node(query, ctx.clickType().isLeftClick()));
                        holder.save();
                        onBack.run();
                    }));
                    view.getTop().setItem(2, custom);
                } else {
                    view.getGui().getElements().remove(SlotPosition.top(2));
                    view.getTop().setItem(2, null);
                }
            }
        });

        gui.set(SlotPosition.bottom(0), GuiButton.of(Items.BACKWARD.get().getStack(), ctx -> {
            layer.previous(ctx.view());
            layer.renderTo(ctx.view());
        }));
        gui.set(SlotPosition.bottom(4), GuiButton.of(Items.BACK.get().getStack(), ctx -> onBack.run()));
        gui.set(SlotPosition.bottom(8), GuiButton.of(Items.FORWARD.get().getStack(), ctx -> {
            layer.next(ctx.view());
            layer.renderTo(ctx.view());
        }));

        GuiManager.open(player, gui.build());
    }

    public static void openAddGroup(Player player, PermissionHolder holder, Runnable onBack) {
        Gui.Builder gui = Gui.builder(MenuType.ANVIL, TextUtil.parse("Search/Add Group"));
        gui.addFlags(GuiFlag.DISABLE_ITEM_PICKUP, GuiFlag.DISABLE_ADVANCEMENTS);

        ItemStack invisibleFiller = Items.INVISIBLE_ITEM.get().getStack();
        invisibleFiller.setData(DataComponentTypes.ITEM_NAME, Component.text(" "));
        invisibleFiller.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build());
        gui.set(SlotPosition.top(0), GuiItem.of(invisibleFiller));

        List<GuiElement> allElements = new ArrayList<>();
        List<PermissionGroup> sortedGroups = Registries.PERMISSION_GROUPS.getAll().values().stream()
            .sorted(Comparator.comparing(PermissionGroup::getId))
            .toList();

        for (PermissionGroup group : sortedGroups) {
            ItemStack item = new ItemStack(Material.NAME_TAG);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(group.getId(), NamedTextColor.YELLOW));
            meta.lore(List.of(Component.text("Click to Add", NamedTextColor.GREEN)));
            item.setItemMeta(meta);

            allElements.add(GuiButton.of(item, ctx -> {
                holder.addParent(new Node(group.getId()));
                holder.save();
                onBack.run();
            }));
        }

        PagedLayer<GuiElement> layer = PagedLayer.of(allElements, BOTTOM_SLOTS, GuiView.Segment.BOTTOM);

        class SearchState { String lastQuery = ""; }
        SearchState state = new SearchState();

        gui.onOpen(view -> {
            setupBackup(view);
            layer.setFilter(e -> true);
            layer.renderTo(view);
        });
        gui.onClose(PermissionMenu::loadBackup);

        gui.onTick(view -> {
            if (view.getInventoryView() instanceof org.bukkit.inventory.view.AnvilView anvil) {
                String query = Optional.ofNullable(anvil.getRenameText()).orElse("").trim();
                if (!query.equals(state.lastQuery)) {
                    state.lastQuery = query;

                    layer.setFilter(e -> {
                        ItemStack i = e.render(view, 0);
                        if (i == null) return false;
                        Component name = i.getData(DataComponentTypes.ITEM_NAME);
                        assert name != null;
                        String plain = PlainTextComponentSerializer.plainText().serialize(name);
                        return plain.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT));
                    });
                    layer.renderTo(view);
                }
            }
        });

        gui.set(SlotPosition.bottom(0), GuiButton.of(Items.BACKWARD.get().getStack(), ctx -> {
            layer.previous(ctx.view());
            layer.renderTo(ctx.view());
        }));
        gui.set(SlotPosition.bottom(4), GuiButton.of(Items.BACK.get().getStack(), ctx -> onBack.run()));
        gui.set(SlotPosition.bottom(8), GuiButton.of(Items.FORWARD.get().getStack(), ctx -> {
            layer.next(ctx.view());
            layer.renderTo(ctx.view());
        }));

        GuiManager.open(player, gui.build());
    }

    private static PagedLayer<GuiElement> setupPages(Player player, Gui.Builder gui, List<GuiElement> elements, java.util.function.Consumer<Player> onBack, boolean isEditor) {
        List<SlotPosition> positions = SlotUtil.grid(GuiView.Segment.TOP, 0, 5, 9, 6, 9);
        PagedLayer<GuiElement> layer = PagedLayer.of(elements, positions.stream().mapToInt(SlotPosition::index).toArray(), GuiView.Segment.TOP);

        gui.addLayer(layer);
        gui.set(SlotPosition.top(45), GuiButton.of(Items.BACKWARD.get().getStack(), ctx -> layer.previous(ctx.view())));
        if (!isEditor) {
            gui.set(SlotPosition.top(49), GuiButton.of(Items.BACK.get().getStack(), ctx -> onBack.accept(player)));
        }
        gui.set(SlotPosition.top(53), GuiButton.of(Items.FORWARD.get().getStack(), ctx -> layer.next(ctx.view())));

        GuiManager.open(player, gui.build());
        return layer;
    }
}