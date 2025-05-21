package com.github.darksoulq.abyssallib.gui.builtin;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.config.Config;
import com.github.darksoulq.abyssallib.config.ConfigParser;
import com.github.darksoulq.abyssallib.config.ConfigSpec;
import com.github.darksoulq.abyssallib.gui.impl.ChestGui;
import com.github.darksoulq.abyssallib.gui.slot.ButtonSlot;
import com.github.darksoulq.abyssallib.item.Item;
import com.github.darksoulq.abyssallib.registry.BuiltinRegistries;
import com.github.darksoulq.abyssallib.resource.glyph.Glyph;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ModMenu extends ChestGui {
    private int currentPage = 0;
    private static final int ITEMS_PER_PAGE = 45;
    private Component currentTitle;
    private ViewMode mode = ViewMode.MODS;
    private String selectedMod = null;

    private enum ViewMode {
        MODS, MODMENU, CONFIG, ITEMS
    }

    public ModMenu() {
        super(Component.text("Mod Menu"), 6);
        this.currentTitle = buildTitle("Items");
    }

    @Override
    public void init(Player viewer) {
        fillGui(viewer);
    }

    private void fillGui(Player viewer) {
        sharedSlots.TOP.clear();
        inventory(viewer.getPlayer(), Type.TOP).clear();

        Component expectedTitle = (mode == ViewMode.MODS)
                ? buildTitle("Items")
                : buildTitle("Items - " + selectedMod);

        if (!Objects.equals(currentTitle, expectedTitle)) {
            reopenWithTitle(expectedTitle, viewer);
            return;
        }

        switch (mode) {
            case MODS -> fillModList(viewer);
            case MODMENU -> fillModMenu(viewer, selectedMod);
            case CONFIG -> fillConfigList(viewer, selectedMod);
            case ITEMS -> fillItemList(viewer, selectedMod);
        }
    }

    private Component buildTitle(String label) {
        return Component.translatable("space.-8")
                .append(MiniMessage.miniMessage().deserialize(Glyph.replacePlaceholders("<white>:abyssallib:items_ui_main:</white>")))
                .append(Component.text(label));
    }

    private void fillModList(Player player) {
        List<String> mods = new ArrayList<>();
        Set<String> found = new HashSet<>();

        BuiltinRegistries.ITEMS.getMap().forEach((id, item) -> {
            String modid = id.split(":")[0];
            if (found.add(modid)) {
                if (!BuiltinRegistries.ITEMS.getFor(modid).isEmpty() || Config.get(modid) != null) {
                    mods.add(modid);
                }
            }
        });

        int totalPages = (mods.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, mods.size());

        for (int i = start; i < end; i++) {
            String mod = mods.get(i);
            ItemStack item = ItemStackHelper.named(Material.DIRT, mod);
            slot(Type.TOP, new ButtonSlot(i - start, item, ctx -> {
                selectedMod = mod;
                currentPage = 0;
                mode = ViewMode.MODMENU;
                fillGui(ctx.player);
            }));
        }

        addPaginationButtons(player, totalPages);
    }

    private void fillModMenu(Player player, String mod) {
        List<String> options = new ArrayList<>();
        if (Config.get(mod) != null) options.add("config");
        if (!BuiltinRegistries.ITEMS.getFor(mod).isEmpty()) options.add("items");

        if (options.contains("config"))
            slot(Type.TOP, new ButtonSlot(options.contains("items") ? 19 : 22, ItemStackHelper.named(Material.BOOK, "Config"), ctx -> {
                mode = ViewMode.CONFIG;
                currentPage = 0;
                fillGui(ctx.player);
            }));

        if (options.contains("items"))
            slot(Type.TOP, new ButtonSlot(options.contains("config") ? 25 : 22, ItemStackHelper.named(Material.IRON_SWORD, "Items"), ctx -> {
                mode = ViewMode.ITEMS;
                currentPage = 0;
                fillGui(ctx.player);
            }));

        slot(Type.TOP, new ButtonSlot(49, ItemStackHelper.named(Material.BARRIER, "Back to Mods"), ctx -> {
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
        int totalPages = (entries.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, entries.size());

        for (int i = start; i < end; i++) {
            Map.Entry<String, Object> entry = entries.get(i);
            String key = entry.getKey();
            Object value = entry.getValue();

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

            slot(Type.TOP, new ButtonSlot(i - start, display, ctx -> {
                if (value instanceof Boolean boolVal) {
                    spec.set(spec.getDefinitionType(key), key, !boolVal);
                    Config.saveAll();
                    fillGui(ctx.player);
                } else {
                    ctx.player.closeInventory();
                    ctx.player.sendMessage(Component.text("Enter new value for " + key + " (or 'cancel'):"));
                    AbyssalLib.CHAT_INPUT_HANDLER.await(ctx.player, input -> {
                        if (input.equalsIgnoreCase("cancel")) {
                            ctx.player.sendMessage(Component.text("Cancelled."));
                            AbyssalLib.GUI_MANAGER.openGui(player, this);
                            return;
                        }
                        try {
                            Object parsed = (value instanceof List<?> list)
                                    ? ConfigParser.parseList(input, list)
                                    : ConfigParser.parseTypedString(input);
                            spec.set(spec.getDefinitionType(key), key, parsed);
                            Config.saveAll();
                        } catch (Exception e) {
                            ctx.player.sendMessage(Component.text("Invalid input."));
                        }
                        AbyssalLib.GUI_MANAGER.openGui(player, this);
                    });
                }
            }));
        }

        addPaginationButtons(player, totalPages);
        slot(Type.TOP, new ButtonSlot(49, ItemStackHelper.named(Material.BARRIER, "Back"), ctx -> {
            mode = ViewMode.MODMENU;
            currentPage = 0;
            fillGui(ctx.player);
        }));
    }

    private void fillItemList(Player player, String mod) {
        List<Item> items = BuiltinRegistries.ITEMS.getFor(mod);
        int totalPages = (items.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, items.size());

        for (int i = start; i < end; i++) {
            Item item = items.get(i);
            slot(Type.TOP, new ButtonSlot(i - start, item.stack(), ctx -> ctx.player.give(item.stack())));
        }

        addPaginationButtons(player, totalPages);
        slot(Type.TOP, new ButtonSlot(49, ItemStackHelper.named(Material.BARRIER, "Back"), ctx -> {
            mode = ViewMode.MODMENU;
            currentPage = 0;
            fillGui(ctx.player);
        }));
    }

    private void addPaginationButtons(Player player, int totalPages) {
        slot(Type.TOP, new ButtonSlot(45, ItemStackHelper.named(Material.ARROW, "Previous Page"), ctx -> {
            if (currentPage > 0) {
                currentPage--;
                fillGui(ctx.player);
            }
        }));
        slot(Type.TOP, new ButtonSlot(53, ItemStackHelper.named(Material.ARROW, "Next Page"), ctx -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                fillGui(ctx.player);
            }
        }));
    }

    private void reopenWithTitle(Component title, Player player) {
        ModMenu reopened = new ModMenu();
        reopened.mode = this.mode;
        reopened.selectedMod = this.selectedMod;
        reopened.currentPage = this.currentPage;
        reopened.currentTitle = title;
        AbyssalLib.GUI_MANAGER.openGui(player, reopened);
    }

    private static class ItemStackHelper {
        static ItemStack named(Material mat, String name) {
            ItemStack item = new ItemStack(mat);
            item.setData(DataComponentTypes.ITEM_NAME, Component.text(name));
            return item;
        }
    }
}
