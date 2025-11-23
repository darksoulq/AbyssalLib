package com.github.darksoulq.abyssallib.world.gui.internal;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.world.gui.*;
import com.github.darksoulq.abyssallib.world.gui.impl.GuiButton;
import com.github.darksoulq.abyssallib.world.gui.impl.PaginatedElements;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.Items;
import com.github.darksoulq.abyssallib.world.item.component.builtin.ItemName;
import com.github.darksoulq.abyssallib.world.item.component.builtin.Lore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;

import java.util.*;
import java.util.stream.Collectors;

public class ItemMenu {
    public static void open(Player player) {
        Gui.Builder gui = new Gui.Builder(MenuType.GENERIC_9X6, TextUtil.parse("<white><offset><texture></white><width>Plugins [Items]",
                Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(-8)),
                Placeholder.parsed("texture", GuiTextures.ITEM_MAIN_MENU.toMiniMessageString()),
                Placeholder.parsed("width", TextOffset.getOffsetMinimessage(-170))));
        gui.addFlags(GuiFlag.DISABLE_BOTTOM, GuiFlag.DISABLE_ADVANCEMENTS);

        List<GuiElement> elements = new ArrayList<>();
        Map<String, Long> namespaceCounts =
                Registries.ITEMS.getAll().keySet().stream()
                        .map(key -> key.split(":")[0])
                        .collect(Collectors.groupingBy(
                                ns -> ns,
                                TreeMap::new,
                                Collectors.counting()
                        ));


        for (String plugin : namespaceCounts.keySet()) {
            Item icon = getPluginIcon(plugin, namespaceCounts.get(plugin));
            if (icon == null) continue;

            elements.add(GuiButton.of(icon.getStack(), (view, click) -> {
                open(player, plugin);
                GuiManager.openViews.remove(view.getInventoryView());
            }));
        }
        setupPages(player, gui, elements);
    }

    public static void open(Player player, String namespace) {
        Gui.Builder gui = new Gui.Builder(MenuType.GENERIC_9X6, TextUtil.parse("<offset><white><texture></white><width>Items [<namespace>]",
                Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(-8)),
                Placeholder.parsed("texture", GuiTextures.ITEM_MAIN_MENU.toMiniMessageString()),
                Placeholder.parsed("width", TextOffset.getOffsetMinimessage(-170)),
                Placeholder.parsed("namespace", "<lang:plugin." + namespace + ">")));
        gui.addFlags(GuiFlag.DISABLE_ITEM_PICKUP, GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_BOTTOM);

        List<GuiElement> elements = new ArrayList<>();
        for (String str : Registries.ITEMS.getAll().keySet()) {
            if (!str.startsWith(namespace)) continue;
            if (str.endsWith("plugin_icon")) continue;
            elements.add(GuiButton.of(Registries.ITEMS.get(str).getStack().asOne(), (view, click) -> {
                if (!player.hasPermission("abyssallib.admin.give")) return;
                player.give(Registries.ITEMS.get(str).getStack().asOne());
            }));
        }

        setupPages(player, gui, elements);
    }

    private static void setupPages(Player player, Gui.Builder gui, List<GuiElement> elements) {
        List<SlotPosition> positions = SlotUtil.grid(GuiView.Segment.TOP, 0, 5, 9, 6, 9);
        PaginatedElements elem = new PaginatedElements(elements, positions.stream().mapToInt(SlotPosition::index).toArray(), GuiView.Segment.TOP);

        gui.addLayer(elem);
        gui.set(SlotPosition.top(45), GuiButton.of(Items.BACKWARD.get().getStack(), (view, click) -> elem.prev(view)));
        gui.set(SlotPosition.top(53), GuiButton.of(Items.FORWARD.get().getStack(), (view, click) -> elem.next(view)));

        GuiManager.open(player, gui.build());
    }

    private static Item getPluginIcon(String plugin, Long amount) {
        Item icon = Registries.ITEMS.get(plugin + ":plugin_icon").clone();
        if (icon == null) return null;
        icon.setData(new ItemName(Component.translatable("plugin." + plugin, plugin)));
        icon.setData(new Lore(List.of(TextUtil.parse("<!italic><yellow><amount></yellow></!italic><white> Items</white>",
                Placeholder.unparsed("amount", String.valueOf(amount))))));
        return icon;
    }
}
