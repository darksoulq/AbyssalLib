package com.github.darksoulq.abyssallib.world.dialog;

import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.body.ItemDialogBody;
import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import io.papermc.paper.registry.data.dialog.input.*;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class DialogContent {
    // Inputs
    public static SingleOptionDialogInput single(String name, Component label, List<SingleOptionDialogInput.OptionEntry> entries) {
        return DialogInput.singleOption(name, label, entries)
                .build();
    }
    public static SingleOptionDialogInput single(String name, Component label, List<SingleOptionDialogInput.OptionEntry> entries, boolean labelVisible) {
        return DialogInput.singleOption(name, label, entries)
                .labelVisible(labelVisible)
                .build();
    }
    public static SingleOptionDialogInput single(String name, Component label, List<SingleOptionDialogInput.OptionEntry> entries, int width) {
        return DialogInput.singleOption(name, label, entries)
                .width(width)
                .build();
    }
    public static SingleOptionDialogInput single(String name, Component label, List<SingleOptionDialogInput.OptionEntry> entries, boolean labelVisible,
                                                 int width) {
        return DialogInput.singleOption(name, label, entries)
                .labelVisible(labelVisible)
                .width(width)
                .build();
    }

    public static TextDialogInput text(String name, Component label) {
        return DialogInput.text(name, label)
                .build();
    }
    public static TextDialogInput text(String name, Component label, boolean labelVisible) {
        return DialogInput.text(name, label)
                .labelVisible(labelVisible)
                .build();
    }
    public static TextDialogInput text(String name, Component label, boolean labelVisible, int width) {
        return DialogInput.text(name, label)
                .labelVisible(labelVisible)
                .width(width)
                .build();
    }
    public static TextDialogInput text(String name, Component label, String initial) {
        return DialogInput.text(name, label)
                .initial(initial)
                .build();
    }
    public static TextDialogInput text(String name, Component label, boolean labelVisible, String initial) {
        return DialogInput.text(name, label)
                .initial(initial)
                .labelVisible(labelVisible)
                .build();
    }
    public static TextDialogInput text(String name, Component label, int width, String initial) {
        return DialogInput.text(name, label)
                .initial(initial)
                .width(width)
                .build();
    }
    public static TextDialogInput text(String name, Component label, boolean labelVisible, int width, String initial) {
        return DialogInput.text(name, label)
                .initial(initial)
                .labelVisible(labelVisible)
                .width(width)
                .build();
    }
    public static TextDialogInput text(String name, Component label, int width, int maxLength) {
        return DialogInput.text(name, label)
                .maxLength(maxLength)
                .width(width)
                .build();
    }
    public static TextDialogInput text(String name, Component label, boolean labelVisible, int width, int maxLength) {
        return DialogInput.text(name, label)
                .labelVisible(labelVisible)
                .maxLength(maxLength)
                .width(width)
                .build();
    }
    public static TextDialogInput text(String name, Component label, boolean labelVisible, int width, String initial, int maxLength) {
        return DialogInput.text(name, label)
                .initial(initial)
                .labelVisible(labelVisible)
                .maxLength(maxLength)
                .width(width)
                .build();
    }
    public static TextDialogInput text(String name, Component label, boolean labelVisible, int width, String initial, int maxLength, int height) {
        return text(name, label, labelVisible, width, initial, maxLength, null, height);
    }
    public static TextDialogInput text(String name, Component label, boolean labelVisible, int width, String initial, int maxLength, Integer maxLines, int height) {
        return DialogInput.text(name, label)
                .initial(initial)
                .labelVisible(labelVisible)
                .maxLength(maxLength)
                .multiline(TextDialogInput.MultilineOptions.create(maxLines, height))
                .width(width)
                .build();
    }

    public static BooleanDialogInput bool(String name, Component label) {
        return bool(name, label, false);
    }
    public static BooleanDialogInput bool(String name, Component label, String trueValue, String falseValue) {
        return bool(name, label, trueValue, falseValue, false);
    }
    public static BooleanDialogInput bool(String name, Component label, boolean initial) {
        return bool(name, label, "true", "false", initial);
    }
    public static BooleanDialogInput bool(String name, Component label, String trueValue, String falseValue, boolean initial) {
        return DialogInput.bool(name, label)
                .onFalse(falseValue)
                .onTrue(trueValue)
                .initial(initial)
                .build();
    }

    public static NumberRangeDialogInput range(String name, Component label, float min, float max) {
        return DialogInput.numberRange(name, label, min, max)
                .build();
    }
    public static NumberRangeDialogInput range(String name, Component label, int width, float min, float max) {
        return DialogInput.numberRange(name, label, min, max)
                .width(width)
                .build();
    }
    public static NumberRangeDialogInput range(String name, Component label, int width, float min, float max, float step) {
        return DialogInput.numberRange(name, label, min, max)
                .step(step)
                .width(width)
                .build();
    }
    public static NumberRangeDialogInput range(String name, Component label, int width, float min, float max, float initial, float step) {
        return DialogInput.numberRange(name, label, min, max)
                .initial(initial)
                .step(step)
                .width(width)
                .build();
    }
    public static NumberRangeDialogInput range(String name, Component label, String labelFormat, float min, float max) {
        return DialogInput.numberRange(name, label, min, max)
                .labelFormat(labelFormat)
                .build();
    }
    public static NumberRangeDialogInput range(String name, Component label, String labelFormat, int width, float min, float max) {
        return DialogInput.numberRange(name, label, min, max)
                .labelFormat(labelFormat)
                .width(width)
                .build();
    }
    public static NumberRangeDialogInput range(String name, Component label, String labelFormat, int width, float min, float max, float step) {
        return DialogInput.numberRange(name, label, min, max)
                .step(step)
                .labelFormat(labelFormat)
                .width(width)
                .build();
    }
    public static NumberRangeDialogInput range(String name, Component label, String labelFormat, int width, float min, float max, float initial, float step) {
        return DialogInput.numberRange(name, label, min, max)
                .initial(initial)
                .step(step)
                .labelFormat(labelFormat)
                .width(width)
                .build();
    }

    public static ActionButton button (Component label, Key actionId) {
        return button(label, null, actionId, null);
    }
    public static ActionButton button (Component label, Key actionId, BinaryTagHolder additions) {
        return button(label, null, actionId, additions);
    }
    public static ActionButton button (Component label, Component tooltip, Key actionId) {
        return button(label, tooltip, actionId, null);
    }
    public static ActionButton button (Component label, Component tooltip, Key actionId, BinaryTagHolder additions) {
        return ActionButton.builder(label)
                .action(DialogAction.customClick(actionId, additions))
                .tooltip(tooltip)
                .build();
    }
    public static ActionButton button (Component label, int width, Key actionId) {
        return button(label, null, width, actionId, null);
    }
    public static ActionButton button (Component label, int width, Key actionId, BinaryTagHolder additions) {
        return button(label, null, width, actionId, additions);
    }
    public static ActionButton button (Component label, Component tooltip, int width, Key actionId) {
        return button(label, tooltip, width, actionId, null);
    }
    public static ActionButton button (Component label, Component tooltip, int width, Key actionId, BinaryTagHolder additions) {
        return ActionButton.builder(label)
                .action(DialogAction.customClick(actionId, additions))
                .tooltip(tooltip)
                .width(width)
                .build();
    }
    public static ActionButton button(Component label, DialogActionCallback action) {
        return button(label, null, action, Duration.ofHours(12), -1);
    }
    public static ActionButton button(Component label, DialogActionCallback action, TemporalAmount lifetime) {
        return button(label, null, action, lifetime, -1);
    }
    public static ActionButton button(Component label, DialogActionCallback action, int uses) {
        return button(label, null, action, Duration.ofHours(12), uses);
    }
    public static ActionButton button(Component label, Component tooltip, DialogActionCallback action) {
        return button(label, tooltip, action, Duration.ofHours(12), -1);
    }
    public static ActionButton button(Component label, Component tooltip, DialogActionCallback action, TemporalAmount lifetime) {
        return button(label, tooltip, action, lifetime, -1);
    }
    public static ActionButton button(Component label, Component tooltip, DialogActionCallback action, int uses) {
        return button(label, tooltip, action, Duration.ofHours(12), uses);
    }
    public static ActionButton button(Component label, Component tooltip, DialogActionCallback action, TemporalAmount lifetime, int uses) {
        return ActionButton.builder(label)
                .action(DialogAction.customClick(action, ClickCallback.Options.builder().uses(uses).lifetime(lifetime).build()))
                .tooltip(tooltip)
                .build();
    }
    public static ActionButton button(Component label, int width, DialogActionCallback action) {
        return button(label, null, width, action, Duration.ofHours(12), -1);
    }
    public static ActionButton button(Component label, int width, DialogActionCallback action, TemporalAmount lifetime) {
        return button(label, null, width, action, lifetime, -1);
    }
    public static ActionButton button(Component label, int width, DialogActionCallback action, int uses) {
        return button(label, null, width, action, Duration.ofHours(12), uses);
    }
    public static ActionButton button(Component label, Component tooltip, int width, DialogActionCallback action) {
        return button(label, tooltip, width, action, Duration.ofHours(12), -1);
    }
    public static ActionButton button(Component label, Component tooltip, int width, DialogActionCallback action, TemporalAmount lifetime) {
        return button(label, tooltip, width, action, lifetime, -1);
    }
    public static ActionButton button(Component label, Component tooltip, int width, DialogActionCallback action, int uses) {
        return button(label, tooltip, width, action, Duration.ofHours(12), uses);
    }
    public static ActionButton button(Component label, Component tooltip, int width, DialogActionCallback action, TemporalAmount lifetime, int uses) {
        return ActionButton.builder(label)
                .action(DialogAction.customClick(action, ClickCallback.Options.builder().uses(uses).lifetime(lifetime).build()))
                .tooltip(tooltip)
                .width(width)
                .build();
    }

    // Body
    public static ItemDialogBody item(ItemStack stack) {
        return item(stack, null, 16, 16, false, false);
    }
    public static ItemDialogBody item(ItemStack stack, PlainMessageDialogBody description) {
        return item(stack, description, 16, 16, false, false);
    }
    public static ItemDialogBody item(ItemStack stack, int width, int height) {
        return item(stack, null, width, height, false, false);
    }
    public static ItemDialogBody item(ItemStack stack, PlainMessageDialogBody description, int width, int height) {
        return item(stack, description, width, height, false, false);
    }
    public static ItemDialogBody item(ItemStack stack, boolean showDeco, boolean showTooltips) {
        return item(stack, null, 16, 16, showDeco, showTooltips);
    }
    public static ItemDialogBody item(ItemStack stack, PlainMessageDialogBody description, boolean showDeco, boolean showTooltips) {
        return item(stack, description, 16, 16, showDeco, showTooltips);
    }
    public static ItemDialogBody item(ItemStack stack, PlainMessageDialogBody description, int width, int height, boolean showDeco, boolean showTooltips) {
        return DialogBody.item(stack)
                .description(description)
                .width(width)
                .height(height)
                .showDecorations(showDeco)
                .showTooltip(showTooltips)
                .build();
    }

    public static PlainMessageDialogBody text(Component text) {
        return DialogBody.plainMessage(text);
    }
    public static PlainMessageDialogBody text(Component text, int width) {
        return DialogBody.plainMessage(text, width);
    }
}
