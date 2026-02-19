package com.github.darksoulq.abyssallib.world.block.internal.structure;

import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.chat.ChatInputHandler;
import com.github.darksoulq.abyssallib.server.event.context.gui.GuiClickContext;
import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.world.block.property.Property;
import com.github.darksoulq.abyssallib.world.gui.Gui;
import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.gui.SlotPosition;
import com.github.darksoulq.abyssallib.world.gui.element.GuiButton;
import com.github.darksoulq.abyssallib.world.gui.internal.GuiTextures;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.Items;
import com.github.darksoulq.abyssallib.world.item.component.builtin.ItemName;
import com.github.darksoulq.abyssallib.world.item.component.builtin.Lore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MenuType;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class StructureBlockMenu {

    private final StructureBlockEntity tile;

    public StructureBlockMenu(StructureBlockEntity tile) {
        this.tile = tile;
    }

    public void open(Player player) {
        Gui.Builder builder = Gui.builder(MenuType.GENERIC_9X6, TextUtil.parse("<white><offset><texture></white><width>Structure Block",
            Placeholder.parsed("offset", TextOffset.getOffsetMinimessage(-8)),
            Placeholder.parsed("texture", GuiTextures.STRUCTURE_BLOCK_MENU.toMiniMessageString()),
            Placeholder.parsed("width", TextOffset.getOffsetMinimessage(-170))));
        Gui gui = builder.build();
        refresh(gui, player);
        GuiManager.open(player, gui);
    }

    private void refresh(Gui gui, Player player) {
        gui.getElements().clear();
        StructureMode mode = tile.mode.get();
        Item modeItem = mode == StructureMode.LOAD ? Items.LOAD_STRUCTURE.get() : Items.SAVE.get();

        gui.getElements().put(SlotPosition.top(0), makeButton(modeItem,
            Component.text("Mode: ", NamedTextColor.GRAY).append(Component.text(mode.name(), NamedTextColor.YELLOW)),
            ctx -> {
                StructureMode[] modes = StructureMode.values();
                int nextIndex = (tile.mode.get().ordinal() + 1) % modes.length;
                tile.mode.set(modes[nextIndex]);
                tile.updateParticles();
                refresh(gui, player);
            },
            Component.text("Click to cycle mode", NamedTextColor.GRAY)
        ));

        gui.getElements().put(SlotPosition.top(4), makeButton(Items.NAME_STRUCTURE.get(),
            Component.text("Name: ", NamedTextColor.GRAY).append(Component.text(tile.structureName.get(), NamedTextColor.GREEN)),
            ctx -> {
                ctx.view().close(player);
                ChatInputHandler.await(player, (input) -> {
                    String clean = input.contains(":") ? input : "default:" + input;
                    tile.structureName.set(clean);
                    new StructureBlockMenu(tile).open(player);
                }, Component.text("Enter structure name (namespace:id):", NamedTextColor.GREEN));
            },
            Component.text("Click to rename", NamedTextColor.GRAY)
        ));

        gui.getElements().put(SlotPosition.top(21), makeIntButton(Items.X.get(), "Pos X", tile.offsetX, gui, player));
        gui.getElements().put(SlotPosition.top(22), makeIntButton(Items.Y.get(), "Pos Y", tile.offsetY, gui, player));
        gui.getElements().put(SlotPosition.top(23), makeIntButton(Items.Z.get(), "Pos Z", tile.offsetZ, gui, player));

        if (mode == StructureMode.SAVE) {
            setupSaveMode(gui, player);
        } else {
            setupLoadMode(gui, player);
        }
    }

    private void setupSaveMode(Gui gui, Player player) {
        gui.getElements().put(SlotPosition.top(30), makeIntButton(Items.SIZE_X.get(), "Size X", tile.sizeX, gui, player, 1, 48));
        gui.getElements().put(SlotPosition.top(31), makeIntButton(Items.SIZE_Y.get(), "Size Y", tile.sizeY, gui, player, 1, 48));
        gui.getElements().put(SlotPosition.top(32), makeIntButton(Items.SIZE_Z.get(), "Size Z", tile.sizeZ, gui, player, 1, 48));

        gui.getElements().put(SlotPosition.top(49), makeButton(Items.CHECKMARK.get(),
            Component.text("SAVE", NamedTextColor.GREEN, TextDecoration.BOLD),
            ctx -> {
                if (tile.save()) player.sendMessage(Component.text("Structure saved successfully!", NamedTextColor.GREEN));
                else player.sendMessage(Component.text("Structure save failed.", NamedTextColor.RED));
            },
            Component.text("Click to save structure", NamedTextColor.GRAY)
        ));

        boolean showBox = tile.showBoundingBox.get();
        gui.getElements().put(SlotPosition.top(45), makeButton(Items.BOUNDING_TOGGLE.get(),
            Component.text("Bounding Box: ", NamedTextColor.GRAY).append(Component.text(showBox ? "ON" : "OFF", showBox ? NamedTextColor.GREEN : NamedTextColor.RED)),
            ctx -> {
                tile.showBoundingBox.set(!showBox);
                tile.updateParticles();
                refresh(gui, player);
            },
            Component.text("Click to toggle visibility", NamedTextColor.GRAY)
        ));
    }

    private void setupLoadMode(Gui gui, Player player) {
        if (tile.particles != null) tile.particles.stop();

        gui.getElements().put(SlotPosition.top(30), makeButton(Items.ROTATE.get(),
            Component.text("Rotation: ", NamedTextColor.GRAY).append(Component.text(tile.rotation.get().name(), NamedTextColor.YELLOW)),
            ctx -> {
                StructureRotation[] rots = StructureRotation.values();
                int next = (tile.rotation.get().ordinal() + 1) % rots.length;
                tile.rotation.set(rots[next]);
                refresh(gui, player);
            },
            Component.text("Click to cycle rotation", NamedTextColor.GRAY)
        ));

        gui.getElements().put(SlotPosition.top(31), makeButton(Items.MIRROR.get(),
            Component.text("Mirror: ", NamedTextColor.GRAY).append(Component.text(tile.mirror.get().name(), NamedTextColor.YELLOW)),
            ctx -> {
                Mirror[] mirrors = Mirror.values();
                int next = (tile.mirror.get().ordinal() + 1) % mirrors.length;
                tile.mirror.set(mirrors[next]);
                refresh(gui, player);
            },
            Component.text("Click to cycle mirror", NamedTextColor.GRAY)
        ));

        gui.getElements().put(SlotPosition.top(32), makeButton(Items.INTEGRITY.get(),
            Component.text("Integrity: ", NamedTextColor.GRAY).append(Component.text(String.format("%.1f", tile.integrity.get()), NamedTextColor.AQUA)),
            ctx -> {
                float val = tile.integrity.get() + (ctx.clickType().isLeftClick() ? 0.1f : -0.1f);
                tile.integrity.set(Math.max(0.0f, Math.min(1.0f, val)));
                refresh(gui, player);
            },
            TextUtil.parse("<white><icon_left> <gray>+0.1 | <white><icon_right> <gray>-0.1",
                Placeholder.parsed("icon_left", GuiTextures.MOUSE_LEFT.toMiniMessageString()),
                Placeholder.parsed("icon_right", GuiTextures.MOUSE_RIGHT.toMiniMessageString()))
        ));

        gui.getElements().put(SlotPosition.top(49), makeButton(Items.CHECKMARK.get(),
            Component.text("LOAD", NamedTextColor.GREEN, TextDecoration.BOLD),
            ctx -> {
                if (tile.load()) player.sendMessage(Component.text("Structure loaded successfully!", NamedTextColor.GREEN));
                else player.sendMessage(Component.text("Structure load failed. Check name.", NamedTextColor.RED));
            },
            Component.text("Click to load structure", NamedTextColor.GRAY)
        ));
    }

    private GuiButton makeIntButton(Item item, String name, Property<Integer> prop, Gui gui, Player player) {
        return makeIntButton(item, name, prop, gui, player, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private GuiButton makeIntButton(Item item, String nameStr, Property<Integer> prop, Gui gui, Player player, int min, int max) {
        Component name = Component.text(nameStr + ": ", NamedTextColor.GRAY).append(Component.text(prop.get(), NamedTextColor.AQUA));
        return makeButton(item, name, ctx -> {
                int change = ctx.clickType().isShiftClick() ? 10 : 1;
                if (!ctx.clickType().isLeftClick()) change = -change;
                int newVal = prop.get() + change;
                prop.set(Math.max(min, Math.min(max, newVal)));
                tile.updateParticles();
                refresh(gui, player);
            },
            TextUtil.parse("<white><icon_left> <gray>+1 | <white><icon_right> <gray>-1",
                Placeholder.parsed("icon_left", GuiTextures.MOUSE_LEFT.toMiniMessageString()),
                Placeholder.parsed("icon_right", GuiTextures.MOUSE_RIGHT.toMiniMessageString())),
            TextUtil.parse("<white>Shift + Click <gray>+/- 10")
        );
    }

    private GuiButton makeButton(Item item, Component name, Consumer<GuiClickContext> action, Component... lore) {
        Item clone = item.clone();
        clone.setData(new ItemName(name.decoration(TextDecoration.ITALIC, false)));
        List<Component> cleanLore = Arrays.stream(lore)
            .map(c -> c.decoration(TextDecoration.ITALIC, false))
            .toList();
        clone.setData(new Lore(cleanLore));
        return GuiButton.of(clone.getStack(), action);
    }
}