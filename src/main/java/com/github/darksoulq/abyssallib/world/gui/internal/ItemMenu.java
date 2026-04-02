package com.github.darksoulq.abyssallib.world.gui.internal;

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
import net.kyori.adventure.key.Key;
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
        Gui.Builder gui = Gui.builder(MenuType.GENERIC_9X6, TextUtil.parse("<white><offset><texture></white><re_offset>Plugins [Items]",
            Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(-8)),
            Placeholder.parsed("texture", GuiTextures.GENERIC_9X6_PAGE_MENU.toMiniMessageString()),
            Placeholder.parsed("re_offset", TextOffset.getOffsetMinimessage(-170))));

        gui.tickInterval(0);
        gui.addFlags(GuiFlag.DISABLE_BOTTOM, GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_ITEM_PICKUP);

        List<GuiElement> elements = new ArrayList<>();
        Set<String> namespaces = Registries.ITEMS.getAll().keySet().stream()
            .map(key -> key.split(":")[0])
            .collect(Collectors.toCollection(TreeSet::new));

        for (String plugin : namespaces) {
            List<ItemCategory> pluginCategories = Registries.ITEM_CATEGORIES.getAll().values().stream()
                .filter(cat -> cat.getId().namespace().equals(plugin))
                .toList();

            long categoryCount = pluginCategories.size();
            long itemCount;

            if (categoryCount == 0) {
                itemCount = Registries.ITEMS.getAll().keySet().stream()
                    .filter(key -> key.startsWith(plugin + ":") && !key.endsWith(":plugin_icon"))
                    .count();
                if (itemCount == 0) continue;
            } else {
                itemCount = pluginCategories.stream()
                    .mapToInt(cat -> cat.getItems().size())
                    .sum();
            }

            Item icon = getPluginIcon(plugin, itemCount, categoryCount);

            elements.add(GuiButton.of(icon.getStack(), ctx -> {
                openPlugin(player, plugin);
                GuiManager.remove(ctx.view());
            }));
        }
        setupPages(player, gui, elements);
    }

    public static void openPlugin(Player player, String namespace) {
        List<ItemCategory> categories = Registries.ITEM_CATEGORIES.getAll().values().stream()
            .filter(cat -> cat.getId().namespace().equals(namespace))
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

            ItemCategory defaultCat = ItemCategory.builder(Key.key(namespace, "all"))
                .icon(iconStack)
                .addAll(defaultItems)
                .build();

            openCategory(player, defaultCat);
        } else {
            Gui.Builder gui = Gui.builder(MenuType.GENERIC_9X6, TextUtil.parse("<offset><white><texture></white><re_offset>Categories [<namespace>]",
                Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(-8)),
                Placeholder.parsed("texture", GuiTextures.GENERIC_9X6_PAGE_MENU.toMiniMessageString()),
                Placeholder.parsed("re_offset", TextOffset.getOffsetMinimessage(-170)),
                Placeholder.parsed("namespace", "<lang:plugin." + namespace + ">")));

            gui.tickInterval(0);
            gui.addFlags(GuiFlag.DISABLE_BOTTOM, GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_ITEM_PICKUP);

            List<GuiElement> elements = new ArrayList<>();
            for (ItemCategory cat : categories) {
                if (cat.getItems().isEmpty()) continue;
                elements.add(GuiButton.of(buildCategoryIcon(cat), ctx -> {
                    openCategory(player, cat);
                    GuiManager.remove(ctx.view());
                }));
            }

            gui.set(SlotPosition.top(49), GuiButton.of(Items.BACK.getStack(), ctx -> {
                open(player);
                GuiManager.remove(ctx.view());
            }));

            setupPages(player, gui, elements);
        }
    }

    public static void openCategory(Player player, ItemCategory category) {
        Gui.Builder gui = Gui.builder(MenuType.GENERIC_9X6, TextUtil.parse("<offset><white><texture></white><width><title>",
            Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(-8)),
            Placeholder.parsed("texture", GuiTextures.GENERIC_9X6_PAGE_MENU.toMiniMessageString()),
            Placeholder.parsed("width", TextOffset.getOffsetMinimessage(-170)),
            Placeholder.component("title", category.getTitle())));

        gui.tickInterval(0);
        gui.addFlags(GuiFlag.DISABLE_ITEM_PICKUP, GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_BOTTOM);

        List<GuiElement> elements = new ArrayList<>();
        for (Item item : category.getItems()) {
            elements.add(GuiButton.of(item.getStack().asOne(), ctx -> {
                if (!PluginPermissions.ITEMS_GIVE.has(player)) return;
                player.getInventory().addItem(item.getStack().asOne());
            }));
        }

        gui.set(SlotPosition.top(49), GuiButton.of(Items.BACK.getStack(), ctx -> {
            boolean hasCat = Registries.ITEM_CATEGORIES.getAll().values().stream()
                .anyMatch(c -> c.getId().namespace().equals(category.getId().namespace()));
            if (hasCat) {
                openPlugin(player, category.getId().namespace());
            } else {
                open(player);
            }
            GuiManager.remove(ctx.view());
        }));

        setupPages(player, gui, elements);
    }

    private static void setupPages(Player player, Gui.Builder gui, List<GuiElement> elements) {
        List<SlotPosition> positions = SlotUtil.grid(GuiView.Segment.TOP, 0, 5, 9, 6, 9);
        PagedLayer<GuiElement> layer = PagedLayer.of(elements, positions.stream().mapToInt(SlotPosition::index).toArray(), GuiView.Segment.TOP);

        gui.addLayer(layer);

        gui.set(SlotPosition.top(45), GuiButton.of(Items.BACKWARD.getStack(), ctx -> {
            layer.previous(ctx.view());
            ctx.view().render();
        }));

        gui.set(SlotPosition.top(53), GuiButton.of(Items.FORWARD.getStack(), ctx -> {
            layer.next(ctx.view());
            ctx.view().render();
        }));

        GuiManager.open(player, gui.build());
    }

    private static ItemStack buildCategoryIcon(ItemCategory category) {
        ItemStack stack = category.getIcon().clone();
        Item item = new Item(stack);
        item.setData(new ItemName(category.getTitle()));
        item.setData(new Lore(List.of(
            TextUtil.parse("<!italic><yellow><amount></yellow></!italic><white> Items</white>", Placeholder.unparsed("amount", String.valueOf(category.getItems().size())))
        )));
        return item.getStack();
    }

    private static Item getPluginIcon(String plugin, long itemCount, long categoryCount) {
        Item icon = Registries.ITEMS.get(plugin + ":plugin_icon");

        if (icon == null) {
            icon = new Item(new ItemStack(Material.APPLE));
        } else {
            icon = icon.clone();
        }

        icon.setData(new ItemName(Component.translatable("plugin." + plugin, plugin)));

        List<Component> lore = new ArrayList<>();
        if (categoryCount > 0) {
            lore.add(TextUtil.parse("<!italic><yellow><amount></yellow></!italic><white> Categories</white>", Placeholder.unparsed("amount", String.valueOf(categoryCount))));
        }
        lore.add(TextUtil.parse("<!italic><yellow><amount></yellow></!italic><white> Items</white>", Placeholder.unparsed("amount", String.valueOf(itemCount))));

        icon.setData(new Lore(lore));
        return icon;
    }
}