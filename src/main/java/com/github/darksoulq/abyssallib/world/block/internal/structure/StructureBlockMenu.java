package com.github.darksoulq.abyssallib.world.block.internal.structure;

import com.github.darksoulq.abyssallib.server.chat.ChatInputHandler;
import com.github.darksoulq.abyssallib.world.block.property.Property;
import com.github.darksoulq.abyssallib.world.gui.Gui;
import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.gui.SlotPosition;
import com.github.darksoulq.abyssallib.world.gui.impl.GuiButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class StructureBlockMenu {

    private final StructureBlockEntity tile;

    public StructureBlockMenu(StructureBlockEntity tile) {
        this.tile = tile;
    }

    public void open(Player player) {
        Gui.Builder builder = Gui.builder(MenuType.GENERIC_9X6, Component.text("Structure Block"));
        Gui gui = builder.build();
        refresh(gui, player);
        GuiManager.open(player, gui);
    }

    private void refresh(Gui gui, Player player) {
        gui.getElements().clear();
        StructureMode mode = tile.mode.get();

        gui.getElements().put(SlotPosition.top(0), makeButton(Material.STRUCTURE_BLOCK, "Mode: " + mode.name(),
            (v, c) -> {
                StructureMode[] modes = StructureMode.values();
                int nextIndex = (tile.mode.get().ordinal() + 1) % modes.length;
                tile.mode.set(modes[nextIndex]);
                tile.updateParticles();
                refresh(gui, player);
            }, "Click to cycle mode"));

        gui.getElements().put(SlotPosition.top(4), makeButton(Material.NAME_TAG, "Name: " + tile.structureName.get(),
            (v, c) -> {
                v.close(player);
                ChatInputHandler.await(player, (input) -> {
                    String clean = input.contains(":") ? input : "default:" + input;
                    tile.structureName.set(clean);
                    new StructureBlockMenu(tile).open(player);
                }, Component.text("Enter structure name (namespace:id):", NamedTextColor.GREEN));
            }, "Click to rename"));

        gui.getElements().put(SlotPosition.top(21), makeIntButton(Material.RED_STAINED_GLASS_PANE, "Pos X", tile.offsetX, gui, player));
        gui.getElements().put(SlotPosition.top(22), makeIntButton(Material.GREEN_STAINED_GLASS_PANE, "Pos Y", tile.offsetY, gui, player));
        gui.getElements().put(SlotPosition.top(23), makeIntButton(Material.BLUE_STAINED_GLASS_PANE, "Pos Z", tile.offsetZ, gui, player));

        if (mode == StructureMode.SAVE) {
            setupSaveMode(gui, player);
        } else {
            setupLoadMode(gui, player);
        }
    }

    private void setupSaveMode(Gui gui, Player player) {
        gui.getElements().put(SlotPosition.top(30), makeIntButton(Material.RED_CONCRETE, "Size X", tile.sizeX, gui, player, 1, 48));
        gui.getElements().put(SlotPosition.top(31), makeIntButton(Material.GREEN_CONCRETE, "Size Y", tile.sizeY, gui, player, 1, 48));
        gui.getElements().put(SlotPosition.top(32), makeIntButton(Material.BLUE_CONCRETE, "Size Z", tile.sizeZ, gui, player, 1, 48));

        gui.getElements().put(SlotPosition.top(49), makeButton(Material.STRUCTURE_VOID, "SAVE",
            (v, c) -> {
                if (tile.save()) player.sendMessage(Component.text("Structure saved successfully!", NamedTextColor.GREEN));
                else player.sendMessage(Component.text("Structure save failed.", NamedTextColor.RED));
            }, "Click to save structure"));
        gui.getElements().put(SlotPosition.top(45), makeButton(Material.ENDER_EYE, "Show Bounding Box: " + (tile.showBoundingBox.get() ? "ON" : "OFF"),
            (v, c) -> {
                tile.showBoundingBox.set(!tile.showBoundingBox.get());
                tile.updateParticles();
                refresh(gui, player);
            }, "Click to toggle"));
    }

    private void setupLoadMode(Gui gui, Player player) {
        if (tile.particles != null) tile.particles.stop();

        gui.getElements().put(SlotPosition.top(30), makeButton(Material.COMPARATOR, "Rotation: " + tile.rotation.get().name(),
            (v, c) -> {
                StructureRotation[] rots = StructureRotation.values();
                int next = (tile.rotation.get().ordinal() + 1) % rots.length;
                tile.rotation.set(rots[next]);
                refresh(gui, player);
            }, "Click to cycle"));

        gui.getElements().put(SlotPosition.top(31), makeButton(Material.REPEATER, "Mirror: " + tile.mirror.get().name(),
            (v, c) -> {
                Mirror[] mirrors = Mirror.values();
                int next = (tile.mirror.get().ordinal() + 1) % mirrors.length;
                tile.mirror.set(mirrors[next]);
                refresh(gui, player);
            }, "Click to cycle"));

        gui.getElements().put(SlotPosition.top(32), makeButton(Material.ANVIL, "Integrity: " + String.format("%.1f", tile.integrity.get()),
            (v, c) -> {
                float val = tile.integrity.get() + (c.isLeftClick() ? 0.1f : -0.1f);
                tile.integrity.set(Math.max(0.0f, Math.min(1.0f, val)));
                refresh(gui, player);
            }, "L: +0.1, R: -0.1"));

        gui.getElements().put(SlotPosition.top(49), makeButton(Material.STRUCTURE_VOID, "LOAD",
            (v, c) -> {
                if (tile.load()) player.sendMessage(Component.text("Structure loaded successfully!", NamedTextColor.GREEN));
                else player.sendMessage(Component.text("Structure load failed. Check name.", NamedTextColor.RED));
            }, "Click to load structure"));
    }

    private GuiButton makeIntButton(Material mat, String name, Property<Integer> prop, Gui gui, Player player) {
        return makeIntButton(mat, name, prop, gui, player, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private GuiButton makeIntButton(Material mat, String name, Property<Integer> prop, Gui gui, Player player, int min, int max) {
        return makeButton(mat, name + ": " + prop.get(), (v, c) -> {
            int change = c.isShiftClick() ? 10 : 1;
            if (!c.isLeftClick()) change = -change;
            int newVal = prop.get() + change;
            prop.set(Math.max(min, Math.min(max, newVal)));
            tile.updateParticles();
            refresh(gui, player);
        }, "L: +1, R: -1", "Shift: +/- 10");
    }

    private GuiButton makeButton(Material mat, String name, java.util.function.BiConsumer<com.github.darksoulq.abyssallib.world.gui.GuiView, ClickType> action, String... lore) {
        ItemStack stack = new ItemStack(mat);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.text(name, NamedTextColor.WHITE));
        if (lore.length > 0) {
            meta.lore(Arrays.stream(lore).map(l -> Component.text(l, NamedTextColor.GRAY)).toList());
        }
        stack.setItemMeta(meta);
        return GuiButton.of(stack, action);
    }
}