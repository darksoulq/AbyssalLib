package com.github.darksoulq.abyssallib.world.gui.internal;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.permission.internal.PluginPermissions;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.world.gui.*;
import com.github.darksoulq.abyssallib.world.gui.element.GuiButton;
import com.github.darksoulq.abyssallib.world.gui.layer.PagedLayer;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.ItemCategory;
import com.github.darksoulq.abyssallib.world.item.Items;
import com.github.darksoulq.abyssallib.world.item.component.builtin.ItemName;
import com.github.darksoulq.abyssallib.world.item.component.builtin.Lore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;

import java.util.*;
import java.util.stream.Collectors;

public class ItemMenu {

    public static void open(Player player) {
        Gui.Builder gui = Gui.builder(MenuType.GENERIC_9X6, TextUtil.parse("<white><offset><texture></white><width>Plugins [Items]",
            Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(-8)),
            Placeholder.parsed("texture", GuiTextures.ITEM_MAIN_MENU.toMiniMessageString()),
            Placeholder.parsed("width", TextOffset.getOffsetMinimessage(-170))));

        gui.addFlags(GuiFlag.DISABLE_BOTTOM, GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_ITEM_PICKUP);

        List<GuiElement> elements = new ArrayList<>();
        Map<String, Long> namespaceCounts = Registries.ITEMS.getAll().keySet().stream()
            .map(key -> key.split(":")[0])
            .collect(Collectors.groupingBy(ns -> ns, TreeMap::new, Collectors.counting()));

        for (Map.Entry<String, Long> entry : namespaceCounts.entrySet()) {
            String plugin = entry.getKey();
            long count = entry.getValue();

            if (count <= 1) continue;

            Item icon = getPluginIcon(plugin, count - 1);
            if (icon == null) continue;

            elements.add(GuiButton.of(icon.getStack(), ctx -> {
                openPlugin(player, plugin);
                GuiManager.openViews.remove(ctx.view().getInventoryView());
            }));
        }
        setupPages(player, gui, elements);
    }

    public static void openPlugin(Player player, String namespace) {
        List<ItemCategory> categories = Registries.ITEM_CATEGORIES.getAll().values().stream()
            .filter(cat -> cat.getId().getNamespace().equals(namespace))
            .toList();

        if (categories.isEmpty()) {
            List<Item> defaultItems = Registries.ITEMS.getAll().entrySet().stream()
                .filter(e -> e.getKey().startsWith(namespace + ":"))
                .filter(e -> !e.getKey().endsWith(":plugin_icon"))
                .map(Map.Entry::getValue)
                .toList();

            if (defaultItems.isEmpty()) return;

            Item pluginIconItem = Registries.ITEMS.get(namespace + ":plugin_icon");
            ItemStack iconStack = pluginIconItem != null ? pluginIconItem.getStack().clone() : new ItemStack(Material.STONE);

            ItemCategory defaultCat = ItemCategory.builder(Identifier.of(namespace, "all"))
                .icon(iconStack)
                .addAll(defaultItems)
                .build();

            openCategory(player, defaultCat);
        } else {
            Gui.Builder gui = Gui.builder(MenuType.GENERIC_9X6, TextUtil.parse("<offset><white><texture></white><width>Categories [<namespace>]",
                Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(-8)),
                Placeholder.parsed("texture", GuiTextures.ITEM_MAIN_MENU.toMiniMessageString()),
                Placeholder.parsed("width", TextOffset.getOffsetMinimessage(-170)),
                Placeholder.parsed("namespace", "<lang:plugin." + namespace + ">")));

            gui.addFlags(GuiFlag.DISABLE_BOTTOM, GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_ITEM_PICKUP);

            List<GuiElement> elements = new ArrayList<>();
            for (ItemCategory cat : categories) {
                if (cat.getItems().isEmpty()) continue;
                elements.add(GuiButton.of(buildCategoryIcon(cat), ctx -> {
                    openCategory(player, cat);
                    GuiManager.openViews.remove(ctx.view().getInventoryView());
                }));
            }

            gui.set(SlotPosition.top(49), GuiButton.of(Items.BACK.get().getStack(), ctx -> {
                open(player);
                GuiManager.openViews.remove(ctx.view().getInventoryView());
            }));

            setupPages(player, gui, elements);
        }
    }

    public static void openCategory(Player player, ItemCategory category) {
        Gui.Builder gui = Gui.builder(MenuType.GENERIC_9X6, TextUtil.parse("<offset><white><texture></white><width><title>",
            Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(-8)),
            Placeholder.parsed("texture", GuiTextures.ITEM_MAIN_MENU.toMiniMessageString()),
            Placeholder.parsed("width", TextOffset.getOffsetMinimessage(-170)),
            Placeholder.component("title", category.getTitle())));

        gui.addFlags(GuiFlag.DISABLE_ITEM_PICKUP, GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_BOTTOM);

        List<GuiElement> elements = new ArrayList<>();
        for (Item item : category.getItems()) {
            elements.add(GuiButton.of(item.getStack().asOne(), ctx -> {
                if (!PluginPermissions.ITEMS_GIVE.get().has(player)) return;
                player.getInventory().addItem(item.getStack().asOne());
            }));
        }

        gui.set(SlotPosition.top(49), GuiButton.of(Items.BACK.get().getStack(), ctx -> {
            boolean hasCat = Registries.ITEM_CATEGORIES.getAll().values().stream()
                .anyMatch(c -> c.getId().getNamespace().equals(category.getId().getNamespace()));
            if (hasCat) {
                openPlugin(player, category.getId().getNamespace());
            } else {
                open(player);
            }
            GuiManager.openViews.remove(ctx.view().getInventoryView());
        }));

        setupPages(player, gui, elements);
    }

    private static void setupPages(Player player, Gui.Builder gui, List<GuiElement> elements) {
        List<SlotPosition> positions = SlotUtil.grid(GuiView.Segment.TOP, 0, 5, 9, 6, 9);
        PagedLayer<GuiElement> layer = PagedLayer.of(elements, positions.stream().mapToInt(SlotPosition::index).toArray(), GuiView.Segment.TOP);

        gui.addLayer(layer);
        gui.set(SlotPosition.top(45), GuiButton.of(Items.BACKWARD.get().getStack(), ctx -> layer.previous(ctx.view())));
        gui.set(SlotPosition.top(53), GuiButton.of(Items.FORWARD.get().getStack(), ctx -> layer.next(ctx.view())));

        GuiManager.open(player, gui.build());
    }

    private static ItemStack buildCategoryIcon(ItemCategory category) {
        ItemStack stack = category.getIcon().clone();
        Item item = new Item(stack);
        item.setData(new ItemName(category.getTitle()));
        item.setData(new Lore(List.of(TextUtil.parse("<!italic><yellow><amount></yellow></!italic><white> Items</white>",
            Placeholder.unparsed("amount", String.valueOf(category.getItems().size()))))));
        return item.getStack();
    }

    private static Item getPluginIcon(String plugin, Long amount) {
        Item icon = Registries.ITEMS.get(plugin + ":plugin_icon");
        if (icon == null) return null;
        icon = icon.clone();
        icon.setData(new ItemName(Component.translatable("plugin." + plugin, plugin)));
        icon.setData(new Lore(List.of(TextUtil.parse("<!italic><yellow><amount></yellow></!italic><white> Items</white>",
            Placeholder.unparsed("amount", String.valueOf(amount))))));
        return icon;
    }
}