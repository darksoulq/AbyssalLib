package me.darksoul.abyssalLib.gui.builtin;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.config.Config;
import me.darksoul.abyssalLib.config.ConfigParser;
import me.darksoul.abyssalLib.config.ConfigSpec;
import me.darksoul.abyssalLib.gui.ChestGui;
import me.darksoul.abyssalLib.gui.slot.ButtonSlot;
import me.darksoul.abyssalLib.item.Item;
import me.darksoul.abyssalLib.registry.BuiltinRegistries;
import me.darksoul.abyssalLib.resource.glyph.Glyph;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
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

    public ModMenu(Player player) {
        super(player, Component.translatable("space.-8")
                .append(MiniMessage.miniMessage().deserialize(Glyph.replacePlaceholders("<white>:abyssallib:items_ui_main:</white>")))
                .append(Component.text("Items")), 6);
        title = Component.translatable("space.-8")
                .append(MiniMessage.miniMessage().deserialize(Glyph.replacePlaceholders("<white>:abyssallib:items_ui_main:</white>")))
                .append(Component.text("Items"));

    }
    public ModMenu(Player player, Component title) {
        super(player, title, 6);
        this.title = title;
    }

    @Override
    public void init(Player player) {
        fillGui(player);
    }

    private void fillGui(Player player) {
        slots.clear();
        inventory().getTopInventory().clear();

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
            fillModList();
        } else if (selectedMod != null) {
            if (mode == ViewMode.MODMENU) {
                fillModMenu(selectedMod);
            } else if (mode == ViewMode.ITEMS) {
                fillItemList(selectedMod);
            } else if (mode == ViewMode.CONFIG) {
                fillConfigList(selectedMod);
            }
        }
    }


    private void fillModList() {
        List<String> mods = new ArrayList<>();
        List<String> temp = new ArrayList<>();
        BuiltinRegistries.ITEMS.getMap().forEach((id, item) -> {
            String modid = id.split(":")[0];
            if (!temp.contains(modid)) temp.add(modid);
        });
        temp.forEach(ids -> {
            if (!BuiltinRegistries.ITEMS.getFor(ids).isEmpty() || Config.get(ids) != null) mods.add(ids);
        });

        int totalPages = (int) Math.ceil((double) mods.size() / ITEMS_PER_PAGE);
        int startIndex = 0;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, mods.size());
        List<String> pageItems = mods.subList(startIndex, endIndex);

        for (int i = 0; i < pageItems.size(); i++) {
            String mod = pageItems.get(i);
            ItemStack item = new ItemStack(Material.DIRT);
            item.setData(DataComponentTypes.ITEM_NAME, Component.text(mod));

            slot(new ButtonSlot(i, item, ctx -> {
                selectedMod = mod;
                currentPage = 0;
                mode = ViewMode.MODMENU;
                fillGui(ctx.player());
            }));
        }

        slot(new ButtonSlot(45, ItemStackHelper.named(Material.AIR, "Previous Page"), ctx -> {
            if (currentPage > 0) {
                currentPage--;
                fillGui(ctx.player());
            }
        }));
        slot(new ButtonSlot(53, ItemStackHelper.named(Material.AIR, "Next Page"), ctx -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                fillGui(ctx.player());
            }
        }));
    }

    private void fillModMenu(String mod) {
        List<String> availableOptions = new ArrayList<>();
        if (Config.get(mod) != null) availableOptions.add("config");
        for (Item item : BuiltinRegistries.ITEMS.getAll()) {
            if (item.getId().toString().startsWith(mod + ":")) {
                availableOptions.add("items");
                break;
            }
        }
        if (availableOptions.contains("config") && availableOptions.contains("items")) {
            slot(new ButtonSlot(19, ItemStackHelper.named(Material.BOOK, "Config"), ctx -> {
                currentPage = 0;
                mode = ViewMode.CONFIG;
                fillGui(ctx.player());
            }));
            slot(new ButtonSlot(25, ItemStackHelper.named(Material.IRON_SWORD, "Items"), ctx -> {
                currentPage = 0;
                mode = ViewMode.ITEMS;
                fillGui(ctx.player());
            }));
        } else if (availableOptions.contains("config")) {
            slot(new ButtonSlot(22, ItemStackHelper.named(Material.BOOK, "Config"), ctx -> {
                currentPage = 0;
                mode = ViewMode.CONFIG;
                fillGui(ctx.player());
            }));
        } else if (availableOptions.contains("items")) {
            slot(new ButtonSlot(22, ItemStackHelper.named(Material.IRON_SWORD, "Items"), ctx -> {
                currentPage = 0;
                mode = ViewMode.ITEMS;
                fillGui(ctx.player());
            }));
        }
        slot(new ButtonSlot(49, ItemStackHelper.named(Material.AIR, "Back to Mods"), ctx -> {
            mode = ViewMode.MODS;
            selectedMod = null;
            currentPage = 0;
            fillGui(ctx.player());
        }));
    }

    private void fillConfigList(String mod) {
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

            slot(new ButtonSlot(i, display, ctx -> {
                if (value instanceof Boolean boolVal) {
                    spec.set(key, !boolVal);
                    Config.saveAll();
                    fillGui(ctx.player());
                    return;
                }

                if (value instanceof Number || value instanceof String || value instanceof List<?>) {
                    ctx.player().closeInventory();
                    ctx.player().sendMessage(Component.text("Enter new value for " + key + " in chat (or type 'cancel' to abort):"));

                    AbyssalLib.CHAT_INPUT_HANDLER.await(ctx.player(), input -> {
                        if (input.equalsIgnoreCase("cancel")) {
                            ctx.player().sendMessage(Component.text("Cancelled."));
                            AbyssalLib.GUI_MANAGER.openGui(this);
                            return;
                        }

                        try {
                            Object parsed;
                            if (value instanceof List<?> defaultList) {
                                parsed = ConfigParser.parseList(input, defaultList);
                            } else {
                                parsed = ConfigParser.parseTypedString(input);
                            }

                            spec.set(key, parsed);
                            Config.saveAll();
                        } catch (Exception e) {
                            ctx.player().sendMessage(Component.text("Invalid input. Use suffixes (`i`, `f`, `L`) or comma-separated lists."));
                        }

                        AbyssalLib.GUI_MANAGER.openGui(this);
                    });
                }
            }));
        }

        slot(new ButtonSlot(49, ItemStackHelper.named(Material.AIR, "Back"), ctx -> {
            mode = ViewMode.MODMENU;
            currentPage = 0;
            fillGui(ctx.player());
        }));
        slot(new ButtonSlot(45, ItemStackHelper.named(Material.AIR, "Previous Page"), ctx -> {
            if (currentPage > 0) {
                currentPage--;
                fillGui(ctx.player());
            }
        }));
        slot(new ButtonSlot(53, ItemStackHelper.named(Material.AIR, "Next Page"), ctx -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                fillGui(ctx.player());
            }
        }));
    }

    private void fillItemList(String mod) {
        List<Item> itemList = BuiltinRegistries.ITEMS.getFor(mod);

        int totalPages = (int) Math.ceil((double) itemList.size() / ITEMS_PER_PAGE);
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, itemList.size());
        List<Item> pageItems = itemList.subList(startIndex, endIndex);

        for (int i = 0; i < pageItems.size(); i++) {
            Item abyssalItem = pageItems.get(i);
            slot(new ButtonSlot(i, abyssalItem, ctx -> {
                ctx.player().give(abyssalItem);
            }));
        }
        slot(new ButtonSlot(49, ItemStackHelper.named(Material.AIR, "Back"), ctx -> {
            mode = ViewMode.MODMENU;
            currentPage = 0;
            fillGui(ctx.player());
        }));
        slot(new ButtonSlot(45, ItemStackHelper.named(Material.AIR, "Previous Page"), ctx -> {
            if (currentPage > 0) {
                currentPage--;
                fillGui(ctx.player());
            }
        }));
        slot(new ButtonSlot(53, ItemStackHelper.named(Material.AIR, "Next Page"), ctx -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                fillGui(ctx.player());
            }
        }));
    }

    private void reopenWithTitle(Player player, Component title) {
        player.closeInventory();
        ModMenu reopened = new ModMenu(player, title);
        reopened.mode = this.mode;
        reopened.selectedMod = this.selectedMod;
        reopened.currentPage = this.currentPage;
        AbyssalLib.GUI_MANAGER.openGui(reopened);
    }

    private static class ItemStackHelper {
        public static ItemStack named(Material mat, String name) {
            ItemStack item = new ItemStack(mat);
            item.setData(DataComponentTypes.ITEM_NAME, Component.text(name));
            return item;
        }
    }
}
