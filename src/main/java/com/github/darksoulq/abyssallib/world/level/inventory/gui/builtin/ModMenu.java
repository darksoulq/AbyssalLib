package com.github.darksoulq.abyssallib.world.level.inventory.gui.builtin;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.config.legacy.Config;
import com.github.darksoulq.abyssallib.server.config.legacy.ConfigParser;
import com.github.darksoulq.abyssallib.server.config.legacy.ConfigSpec;
import com.github.darksoulq.abyssallib.server.registry.BuiltinRegistries;
import com.github.darksoulq.abyssallib.server.resource.glyph.Glyph;
import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.impl.ChestGui;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.slot.ButtonSlot;
import com.github.darksoulq.abyssallib.world.level.item.Item;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModMenu extends ChestGui {
    private int currentPage = 0;
    private static final int ITEMS_PER_PAGE = 45;
    private final Component title;
    private enum ViewMode {
        MODS, MODMENU, CONFIG, ITEMS
    }

    private ViewMode mode = ViewMode.MODS;
    private String selectedMod = null;

    public ModMenu() {
        super(Component.translatable("space.-8")
                .append(MiniMessage.miniMessage().deserialize(Glyph.replacePlaceholders("<white>:abyssallib:items_ui_main:</white>")))
                .append(Component.text("Items")), 6);
        title = Component.translatable("space.-8")
                .append(MiniMessage.miniMessage().deserialize(Glyph.replacePlaceholders("<white>:abyssallib:items_ui_main:</white>")))
                .append(Component.text("Items"));

    }
    public ModMenu(Component title) {
        super(title, 6);
        this.title = title;
    }

    @Override
    public void init(Player player) {
        fillGui(player);
    }

    private void fillGui(Player player) {
        getSlotList(player, Type.TOP).clear();
        inventory(player, Type.TOP).clear();

        Component expectedTitle = (mode == ViewMode.MODS)
                ? Component.translatable("space.-8")
                .append(MiniMessage.miniMessage().deserialize(Glyph.replacePlaceholders("<white>:abyssallib:items_ui_main:</white>")))
                .append(Component.text("Items"))
                : Component.translatable("space.-8")
                .append(MiniMessage.miniMessage().deserialize(Glyph.replacePlaceholders("<white>:abyssallib:items_ui_display:</white>")))
                .append(Component.text("Items - " + selectedMod));

        if (!expectedTitle.equals(title)) {
            reopenWithTitle(player, expectedTitle);
            return;
        }

        if (mode == ViewMode.MODS) {
            fillModList(player);
        } else if (selectedMod != null) {
            if (mode == ViewMode.MODMENU) {
                fillModMenu(player, selectedMod);
            } else if (mode == ViewMode.ITEMS) {
                fillItemList(player, selectedMod);
            } else if (mode == ViewMode.CONFIG) {
                fillConfigList(player, selectedMod);
            }
        }
    }


    private void fillModList(Player player) {
        List<String> mods = new ArrayList<>();
        List<String> temp = new ArrayList<>();
        BuiltinRegistries.ITEMS.getAll().forEach((id, item) -> {
            String modid = id.split(":")[0];
            if (!temp.contains(modid)) temp.add(modid);
        });
        temp.forEach(ids -> {
            if (!BuiltinRegistries.ITEMS.contains(ids) || Config.get(ids) != null) mods.add(ids);
        });

        int totalPages = (int) Math.ceil((double) mods.size() / ITEMS_PER_PAGE);
        int startIndex = 0;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, mods.size());
        List<String> pageItems = mods.subList(startIndex, endIndex);

        for (int i = 0; i < pageItems.size(); i++) {
            String mod = pageItems.get(i);
            ItemStack item = new ItemStack(Material.DIRT);
            item.setData(DataComponentTypes.ITEM_NAME, Component.text(mod));

            slot(player, Type.TOP, new ButtonSlot(i, item, ctx -> {
                selectedMod = mod;
                currentPage = 0;
                mode = ViewMode.MODMENU;
                fillGui(ctx.player);
            }));
        }

        slot(player, Type.TOP, new ButtonSlot(45, ItemStackHelper.named(Material.AIR, "Previous Page"), ctx -> {
            if (currentPage > 0) {
                currentPage--;
                fillGui(ctx.player);
            }
        }));
        slot(player, Type.TOP, new ButtonSlot(53, ItemStackHelper.named(Material.AIR, "Next Page"), ctx -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                fillGui(ctx.player);
            }
        }));
    }

    private void fillModMenu(Player player, String mod) {
        List<String> availableOptions = new ArrayList<>();
        if (Config.get(mod) != null) availableOptions.add("config");
        for (Item item : BuiltinRegistries.ITEMS.getAll().values()) {
            if (item.getId().toString().startsWith(mod + ":")) {
                availableOptions.add("items");
                break;
            }
        }
        if (availableOptions.contains("config") && availableOptions.contains("items")) {
            slot(player, Type.TOP, new ButtonSlot(19, ItemStackHelper.named(Material.BOOK, "Config"), ctx -> {
                currentPage = 0;
                mode = ViewMode.CONFIG;
                fillGui(ctx.player);
            }));
            slot(player, Type.TOP, new ButtonSlot(25, ItemStackHelper.named(Material.IRON_SWORD, "Items"), ctx -> {
                currentPage = 0;
                mode = ViewMode.ITEMS;
                fillGui(ctx.player);
            }));
        } else if (availableOptions.contains("config")) {
            slot(player, Type.TOP, new ButtonSlot(22, ItemStackHelper.named(Material.BOOK, "Config"), ctx -> {
                currentPage = 0;
                mode = ViewMode.CONFIG;
                fillGui(ctx.player);
            }));
        } else if (availableOptions.contains("items")) {
            slot(player, Type.TOP, new ButtonSlot(22, ItemStackHelper.named(Material.IRON_SWORD, "Items"), ctx -> {
                currentPage = 0;
                mode = ViewMode.ITEMS;
                fillGui(ctx.player);
            }));
        }
        slot(player, Type.TOP, new ButtonSlot(49, ItemStackHelper.named(Material.AIR, "Back to Mods"), ctx -> {
            mode = ViewMode.MODS;
            selectedMod = null;
            currentPage = 0;
            fillGui(ctx.player);
        }));
    }

    private void fillConfigList(Player player, String mod) {
        ConfigSpec spec = Config.get(mod);
        if (spec == null) return;

        List<Map.Entry<String, Object>> entries = new ArrayList<>(spec.getAllValues().entrySet());
        int totalPages = (int) Math.ceil((double) entries.size() / ITEMS_PER_PAGE);
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, entries.size());

        List<Map.Entry<String, Object>> pageEntries = entries.subList(startIndex, endIndex);

        for (int i = 0; i < pageEntries.size(); i++) {
            String key = pageEntries.get(i).getKey();
            Object value = pageEntries.get(i).getValue();

            Material material;
            String displayName = key + ": " + value;

            if (value instanceof Boolean boolVal) {
                material = boolVal ? Material.LIME_CONCRETE : Material.RED_CONCRETE;
            } else if (value instanceof Integer) {
                material = Material.CYAN_CONCRETE;
            } else if (value instanceof Float) {
                material = Material.LIGHT_BLUE_CONCRETE;
            } else if (value instanceof Double) {
                material = Material.BLUE_CONCRETE;
            } else if (value instanceof Long) {
                material = Material.PURPLE_CONCRETE;
            } else if (value instanceof String) {
                material = Material.ORANGE_CONCRETE;
            } else if (value instanceof List<?>) {
                material = Material.GRAY_CONCRETE;
                displayName = key + ": List";
            } else {
                material = Material.BARRIER;
                displayName = key + ": Unsupported";
            }

            ItemStack display = ItemStackHelper.named(material, displayName);

            slot(player, Type.TOP, new ButtonSlot(i, display, ctx -> {
                if (value instanceof Boolean boolVal) {
                    spec.set(spec.getDefinitionType(key), key, !boolVal);
                    Config.saveAll();
                    fillGui(ctx.player);
                    return;
                }

                if (value instanceof Number || value instanceof String || value instanceof List<?>) {
                    ctx.player.closeInventory();
                    ctx.player.sendMessage(Component.text("Enter new value for " + key + " in chat (or type 'cancel' to abort):"));

                    AbyssalLib.CHAT_INPUT_HANDLER.await(ctx.player, input -> {
                        if (input.equalsIgnoreCase("cancel")) {
                            ctx.player.sendMessage(Component.text("Cancelled."));
                            AbyssalLib.GUI_MANAGER.openGui(player, this);
                            return;
                        }

                        try {
                            Object parsed;
                            if (value instanceof List<?> defaultList) {
                                parsed = ConfigParser.parseList(input, defaultList);
                            } else {
                                parsed = ConfigParser.parseTypedString(input);
                            }

                            spec.set(spec.getDefinitionType(key), key, parsed);
                            Config.saveAll();
                        } catch (Exception e) {
                            ctx.player.sendMessage(Component.text("Invalid input. Use suffixes (`i`, `f`, `L`) or comma-separated lists."));
                        }

                        AbyssalLib.GUI_MANAGER.openGui(player, this);
                    });
                }
            }));
        }

        slot(player, Type.TOP, new ButtonSlot(49, ItemStackHelper.named(Material.AIR, "Back"), ctx -> {
            mode = ViewMode.MODMENU;
            currentPage = 0;
            fillGui(ctx.player);
        }));
        slot(player, Type.TOP, new ButtonSlot(45, ItemStackHelper.named(Material.AIR, "Previous Page"), ctx -> {
            if (currentPage > 0) {
                currentPage--;
                fillGui(ctx.player);
            }
        }));
        slot(player, Type.TOP, new ButtonSlot(53, ItemStackHelper.named(Material.AIR, "Next Page"), ctx -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                fillGui(ctx.player);
            }
        }));
    }

    private void fillItemList(Player player, String mod) {
        List<Item> itemList = new ArrayList<>();
        for (Map.Entry<String, Item> entry : BuiltinRegistries.ITEMS.getAll().entrySet()) {
            if (mod.equals(Identifier.of(entry.getKey()).namespace())) itemList.add(entry.getValue());
        }

        int totalPages = (int) Math.ceil((double) itemList.size() / ITEMS_PER_PAGE);
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, itemList.size());
        List<Item> pageItems = itemList.subList(startIndex, endIndex);

        for (int i = 0; i < pageItems.size(); i++) {
            Item abyssalItem = pageItems.get(i);
            slot(player, Type.TOP, new ButtonSlot(i, abyssalItem.stack(), ctx -> {
                ctx.player.give(abyssalItem.stack());
            }));
        }
        slot(player, Type.TOP, new ButtonSlot(49, ItemStackHelper.named(Material.AIR, "Back"), ctx -> {
            mode = ViewMode.MODMENU;
            currentPage = 0;
            fillGui(ctx.player);
        }));
        slot(player, Type.TOP, new ButtonSlot(45, ItemStackHelper.named(Material.AIR, "Previous Page"), ctx -> {
            if (currentPage > 0) {
                currentPage--;
                fillGui(ctx.player);
            }
        }));
        slot(player, Type.TOP, new ButtonSlot(53, ItemStackHelper.named(Material.AIR, "Next Page"), ctx -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                fillGui(ctx.player);
            }
        }));
    }

    private void reopenWithTitle(Player player, Component title) {
        player.closeInventory();
        ModMenu reopened = new ModMenu(title);
        reopened.mode = this.mode;
        reopened.selectedMod = this.selectedMod;
        reopened.currentPage = this.currentPage;
        AbyssalLib.GUI_MANAGER.openGui(player, reopened);
    }

    private static class ItemStackHelper {
        public static ItemStack named(Material mat, String name) {
            ItemStack item = new ItemStack(mat);
            item.setData(DataComponentTypes.ITEM_NAME, Component.text(name));
            return item;
        }
    }
}
