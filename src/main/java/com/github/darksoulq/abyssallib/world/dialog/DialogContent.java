package com.github.darksoulq.abyssallib.world.dialog;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.body.ItemDialogBody;
import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import io.papermc.paper.registry.data.dialog.input.*;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.List;

/**
 * Static factory for generating inputs, buttons, and body content for Dialogs.
 */
@SuppressWarnings("UnstableApiUsage")
public class DialogContent {

    /**
     * Creates a single option selection input.
     *
     * @param name    The internal name of the input.
     * @param label   The display label.
     * @param entries The list of possible options.
     * @return A configured {@link SingleOptionDialogInput}.
     */
    public static SingleOptionDialogInput single(String name, Component label, List<SingleOptionDialogInput.OptionEntry> entries) {
        return DialogInput.singleOption(name, label, entries).build();
    }

    /**
     * Creates a single option selection input with label visibility toggle.
     *
     * @param name         The internal name of the input.
     * @param label        The display label.
     * @param entries      The list of possible options.
     * @param labelVisible Whether the label is shown to the player.
     * @return A configured {@link SingleOptionDialogInput}.
     */
    public static SingleOptionDialogInput single(String name, Component label, List<SingleOptionDialogInput.OptionEntry> entries, boolean labelVisible) {
        return DialogInput.singleOption(name, label, entries)
            .labelVisible(labelVisible)
            .build();
    }

    /**
     * Creates a single option selection input with a specific width.
     *
     * @param name    The internal name of the input.
     * @param label   The display label.
     * @param entries The list of possible options.
     * @param width   The width of the input element.
     * @return A configured {@link SingleOptionDialogInput}.
     */
    public static SingleOptionDialogInput single(String name, Component label, List<SingleOptionDialogInput.OptionEntry> entries, int width) {
        return DialogInput.singleOption(name, label, entries)
            .width(width)
            .build();
    }

    /**
     * Creates a single option selection input with specific width and label visibility.
     *
     * @param name         The internal name of the input.
     * @param label        The display label.
     * @param entries      The list of possible options.
     * @param labelVisible Whether the label is shown.
     * @param width        The width of the input element.
     * @return A configured {@link SingleOptionDialogInput}.
     */
    public static SingleOptionDialogInput single(String name, Component label, List<SingleOptionDialogInput.OptionEntry> entries, boolean labelVisible,
                                                 int width) {
        return DialogInput.singleOption(name, label, entries)
            .labelVisible(labelVisible)
            .width(width)
            .build();
    }

    /**
     * Creates a basic text input field.
     *
     * @param name  The internal name of the input.
     * @param label The display label.
     * @return A configured {@link TextDialogInput}.
     */
    public static TextDialogInput text(String name, Component label) {
        return DialogInput.text(name, label)
            .build();
    }

    /**
     * Creates a text input field with label visibility toggle.
     *
     * @param name         The internal name of the input.
     * @param label        The display label.
     * @param labelVisible Whether the label is shown.
     * @return A configured {@link TextDialogInput}.
     */
    public static TextDialogInput text(String name, Component label, boolean labelVisible) {
        return DialogInput.text(name, label)
            .labelVisible(labelVisible)
            .build();
    }

    /**
     * Creates a text input field with width and label visibility.
     *
     * @param name         The internal name of the input.
     * @param label        The display label.
     * @param labelVisible Whether the label is shown.
     * @param width        The width of the text field.
     * @return A configured {@link TextDialogInput}.
     */
    public static TextDialogInput text(String name, Component label, boolean labelVisible, int width) {
        return DialogInput.text(name, label)
            .labelVisible(labelVisible)
            .width(width)
            .build();
    }

    /**
     * Creates a text input field with an initial value.
     *
     * @param name    The internal name of the input.
     * @param label   The display label.
     * @param initial The initial text in the field.
     * @return A configured {@link TextDialogInput}.
     */
    public static TextDialogInput text(String name, Component label, String initial) {
        return DialogInput.text(name, label)
            .initial(initial)
            .build();
    }

    /**
     * Creates a text input field with visibility toggle and initial value.
     *
     * @param name         The internal name of the input.
     * @param label        The display label.
     * @param labelVisible Whether the label is shown.
     * @param initial      The initial text in the field.
     * @return A configured {@link TextDialogInput}.
     */
    public static TextDialogInput text(String name, Component label, boolean labelVisible, String initial) {
        return DialogInput.text(name, label)
            .initial(initial)
            .labelVisible(labelVisible)
            .build();
    }

    /**
     * Creates a text input field with width and initial value.
     *
     * @param name    The internal name of the input.
     * @param label   The display label.
     * @param width   The width of the text field.
     * @param initial The initial text in the field.
     * @return A configured {@link TextDialogInput}.
     */
    public static TextDialogInput text(String name, Component label, int width, String initial) {
        return DialogInput.text(name, label)
            .initial(initial)
            .width(width)
            .build();
    }

    /**
     * Creates a text input field with visibility, width, and initial value.
     *
     * @param name         The internal name of the input.
     * @param label        The display label.
     * @param labelVisible Whether the label is shown.
     * @param width        The width of the text field.
     * @param initial      The initial text in the field.
     * @return A configured {@link TextDialogInput}.
     */
    public static TextDialogInput text(String name, Component label, boolean labelVisible, int width, String initial) {
        return DialogInput.text(name, label)
            .initial(initial)
            .labelVisible(labelVisible)
            .width(width)
            .build();
    }

    /**
     * Creates a text input field with width and character limit.
     *
     * @param name      The internal name of the input.
     * @param label     The display label.
     * @param width     The width of the text field.
     * @param maxLength The maximum allowed characters.
     * @return A configured {@link TextDialogInput}.
     */
    public static TextDialogInput text(String name, Component label, int width, int maxLength) {
        return DialogInput.text(name, label)
            .maxLength(maxLength)
            .width(width)
            .build();
    }

    /**
     * Creates a text input field with visibility, width, and character limit.
     *
     * @param name         The internal name of the input.
     * @param label        The display label.
     * @param labelVisible Whether the label is shown.
     * @param width        The width of the text field.
     * @param maxLength    The maximum allowed characters.
     * @return A configured {@link TextDialogInput}.
     */
    public static TextDialogInput text(String name, Component label, boolean labelVisible, int width, int maxLength) {
        return DialogInput.text(name, label)
            .labelVisible(labelVisible)
            .maxLength(maxLength)
            .width(width)
            .build();
    }

    /**
     * Creates a text input field with visibility, width, initial value, and character limit.
     *
     * @param name         The internal name of the input.
     * @param label        The display label.
     * @param labelVisible Whether the label is shown.
     * @param width        The width of the text field.
     * @param initial      The initial text.
     * @param maxLength    The maximum allowed characters.
     * @return A configured {@link TextDialogInput}.
     */
    public static TextDialogInput text(String name, Component label, boolean labelVisible, int width, String initial, int maxLength) {
        return DialogInput.text(name, label)
            .initial(initial)
            .labelVisible(labelVisible)
            .maxLength(maxLength)
            .width(width)
            .build();
    }

    /**
     * Creates a multiline text input field.
     *
     * @param name         The internal name of the input.
     * @param label        The display label.
     * @param labelVisible Whether the label is shown.
     * @param width        The width of the field.
     * @param initial      The initial text.
     * @param maxLength    The maximum characters.
     * @param height       The visual height of the field.
     * @return A configured {@link TextDialogInput}.
     */
    public static TextDialogInput text(String name, Component label, boolean labelVisible, int width, String initial, int maxLength, int height) {
        return text(name, label, labelVisible, width, initial, maxLength, null, height);
    }

    /**
     * Creates a highly specific multiline text input field.
     *
     * @param name         The internal name of the input.
     * @param label        The display label.
     * @param labelVisible Whether the label is shown.
     * @param width        The width of the field.
     * @param initial      The initial text.
     * @param maxLength    The maximum characters.
     * @param maxLines     Optional limit on the number of lines.
     * @param height       The visual height of the field.
     * @return A configured {@link TextDialogInput}.
     */
    public static TextDialogInput text(String name, Component label, boolean labelVisible, int width, String initial, int maxLength, Integer maxLines, int height) {
        return DialogInput.text(name, label)
            .initial(initial)
            .labelVisible(labelVisible)
            .maxLength(maxLength)
            .multiline(TextDialogInput.MultilineOptions.create(maxLines, height))
            .width(width)
            .build();
    }

    /**
     * Creates a boolean (true/false) toggle input.
     *
     * @param name  The internal name.
     * @param label The display label.
     * @return A configured {@link BooleanDialogInput}.
     */
    public static BooleanDialogInput bool(String name, Component label) {
        return bool(name, label, false);
    }

    /**
     * Creates a boolean toggle with custom value labels.
     *
     * @param name       The internal name.
     * @param label      The display label.
     * @param trueValue  The text to show when true.
     * @param falseValue The text to show when false.
     * @return A configured {@link BooleanDialogInput}.
     */
    public static BooleanDialogInput bool(String name, Component label, String trueValue, String falseValue) {
        return bool(name, label, trueValue, falseValue, false);
    }

    /**
     * Creates a boolean toggle with an initial state.
     *
     * @param name    The internal name.
     * @param label   The display label.
     * @param initial The starting state.
     * @return A configured {@link BooleanDialogInput}.
     */
    public static BooleanDialogInput bool(String name, Component label, boolean initial) {
        return bool(name, label, "true", "false", initial);
    }

    /**
     * Creates a boolean toggle with custom labels and initial state.
     *
     * @param name       The internal name.
     * @param label      The display label.
     * @param trueValue  Text for true.
     * @param falseValue Text for false.
     * @param initial    Starting state.
     * @return A configured {@link BooleanDialogInput}.
     */
    public static BooleanDialogInput bool(String name, Component label, String trueValue, String falseValue, boolean initial) {
        return DialogInput.bool(name, label)
            .onFalse(falseValue)
            .onTrue(trueValue)
            .initial(initial)
            .build();
    }

    /**
     * Creates a number range (slider) input.
     *
     * @param name  The internal name.
     * @param label The display label.
     * @param min   Minimum value.
     * @param max   Maximum value.
     * @return A configured {@link NumberRangeDialogInput}.
     */
    public static NumberRangeDialogInput range(String name, Component label, float min, float max) {
        return DialogInput.numberRange(name, label, min, max)
            .build();
    }

    /**
     * Creates a number range input with width.
     *
     * @param name  The internal name.
     * @param label The display label.
     * @param width Input width.
     * @param min   Minimum value.
     * @param max   Maximum value.
     * @return A configured {@link NumberRangeDialogInput}.
     */
    public static NumberRangeDialogInput range(String name, Component label, int width, float min, float max) {
        return DialogInput.numberRange(name, label, min, max)
            .width(width)
            .build();
    }

    /**
     * Creates a number range input with width and step size.
     *
     * @param name  The internal name.
     * @param label The display label.
     * @param width Input width.
     * @param min   Minimum value.
     * @param max   Maximum value.
     * @param step  The increment size.
     * @return A configured {@link NumberRangeDialogInput}.
     */
    public static NumberRangeDialogInput range(String name, Component label, int width, float min, float max, float step) {
        return DialogInput.numberRange(name, label, min, max)
            .step(step)
            .width(width)
            .build();
    }

    /**
     * Creates a number range input with width, initial value, and step.
     *
     * @param name    The internal name.
     * @param label   The display label.
     * @param width   Input width.
     * @param min     Minimum value.
     * @param max     Maximum value.
     * @param initial Starting value.
     * @param step    Increment size.
     * @return A configured {@link NumberRangeDialogInput}.
     */
    public static NumberRangeDialogInput range(String name, Component label, int width, float min, float max, float initial, float step) {
        return DialogInput.numberRange(name, label, min, max)
            .initial(initial)
            .step(step)
            .width(width)
            .build();
    }

    /**
     * Creates a number range input with custom label formatting.
     *
     * @param name        The internal name.
     * @param label       The display label.
     * @param labelFormat The printf-style format string for values.
     * @param min         Min value.
     * @param max         Max value.
     * @return A configured {@link NumberRangeDialogInput}.
     */
    public static NumberRangeDialogInput range(String name, Component label, String labelFormat, float min, float max) {
        return DialogInput.numberRange(name, label, min, max)
            .labelFormat(labelFormat)
            .build();
    }

    /**
     * Creates a number range input with formatting and width.
     *
     * @param name        The internal name.
     * @param label       The display label.
     * @param labelFormat The format string.
     * @param width       Input width.
     * @param min         Min value.
     * @param max         Max value.
     * @return A configured {@link NumberRangeDialogInput}.
     */
    public static NumberRangeDialogInput range(String name, Component label, String labelFormat, int width, float min, float max) {
        return DialogInput.numberRange(name, label, min, max)
            .labelFormat(labelFormat)
            .width(width)
            .build();
    }

    /**
     * Creates a number range input with formatting, width, and step.
     *
     * @param name        The internal name.
     * @param label       The display label.
     * @param labelFormat The format string.
     * @param width       Input width.
     * @param min         Min value.
     * @param max         Max value.
     * @param step        Increment size.
     * @return A configured {@link NumberRangeDialogInput}.
     */
    public static NumberRangeDialogInput range(String name, Component label, String labelFormat, int width, float min, float max, float step) {
        return DialogInput.numberRange(name, label, min, max)
            .step(step)
            .labelFormat(labelFormat)
            .width(width)
            .build();
    }

    /**
     * Creates a fully specified number range input.
     *
     * @param name        The internal name.
     * @param label       The display label.
     * @param labelFormat The format string.
     * @param width       Input width.
     * @param min         Min value.
     * @param max         Max value.
     * @param initial     Initial value.
     * @param step        Increment size.
     * @return A configured {@link NumberRangeDialogInput}.
     */
    public static NumberRangeDialogInput range(String name, Component label, String labelFormat, int width, float min, float max, float initial, float step) {
        return DialogInput.numberRange(name, label, min, max)
            .initial(initial)
            .step(step)
            .labelFormat(labelFormat)
            .width(width)
            .build();
    }

    /**
     * Creates an action button linked to a custom click ID.
     *
     * @param label    The button text.
     * @param actionId The {@link Key} identifying the action.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button (Component label, Key actionId) {
        return button(label, null, actionId, null);
    }

    /**
     * Creates an action button with custom NBT data.
     *
     * @param label     The button text.
     * @param actionId  The action {@link Key}.
     * @param additions Extra NBT data.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button (Component label, Key actionId, BinaryTagHolder additions) {
        return button(label, null, actionId, additions);
    }

    /**
     * Creates an action button with a tooltip.
     *
     * @param label    The button text.
     * @param tooltip  The hover tooltip.
     * @param actionId The action {@link Key}.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button (Component label, Component tooltip, Key actionId) {
        return button(label, tooltip, actionId, null);
    }

    /**
     * Creates an action button with tooltip and NBT additions.
     *
     * @param label     The button text.
     * @param tooltip   The tooltip.
     * @param actionId  The action {@link Key}.
     * @param additions Extra NBT data.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button (Component label, Component tooltip, Key actionId, BinaryTagHolder additions) {
        return ActionButton.builder(label)
            .action(DialogAction.customClick(actionId, additions))
            .tooltip(tooltip)
            .build();
    }

    /**
     * Creates an action button with specific width.
     *
     * @param label    The button text.
     * @param width    Button width.
     * @param actionId The action {@link Key}.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button (Component label, int width, Key actionId) {
        return button(label, null, width, actionId, null);
    }

    /**
     * Creates an action button with width and NBT data.
     *
     * @param label     The button text.
     * @param width     Button width.
     * @param actionId  The action {@link Key}.
     * @param additions Extra NBT data.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button (Component label, int width, Key actionId, BinaryTagHolder additions) {
        return button(label, null, width, actionId, additions);
    }

    /**
     * Creates an action button with tooltip and width.
     *
     * @param label    The button text.
     * @param tooltip  The tooltip.
     * @param width    Button width.
     * @param actionId The action {@link Key}.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button (Component label, Component tooltip, int width, Key actionId) {
        return button(label, tooltip, width, actionId, null);
    }

    /**
     * Creates a fully specified button linked to a Key action.
     *
     * @param label     Button text.
     * @param tooltip   Tooltip text.
     * @param width     Visual width.
     * @param actionId  Action {@link Key}.
     * @param additions Extra NBT data.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button (Component label, Component tooltip, int width, Key actionId, BinaryTagHolder additions) {
        return ActionButton.builder(label)
            .action(DialogAction.customClick(actionId, additions))
            .tooltip(tooltip)
            .width(width)
            .build();
    }

    /**
     * Creates a button with a direct functional callback.
     *
     * @param label  Button text.
     * @param action The callback code to run on click.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button(Component label, DialogActionCallback action) {
        return button(label, null, action, Duration.ofHours(12), -1);
    }

    /**
     * Creates a callback button with a limited lifetime.
     *
     * @param label    Button text.
     * @param action   Callback code.
     * @param lifetime How long the callback remains valid.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button(Component label, DialogActionCallback action, TemporalAmount lifetime) {
        return button(label, null, action, lifetime, -1);
    }

    /**
     * Creates a callback button with limited uses.
     *
     * @param label  Button text.
     * @param action Callback code.
     * @param uses   Number of times the button can be clicked.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button(Component label, DialogActionCallback action, int uses) {
        return button(label, null, action, Duration.ofHours(12), uses);
    }

    /**
     * Creates a callback button with a tooltip.
     *
     * @param label   Button text.
     * @param tooltip Tooltip text.
     * @param action  Callback code.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button(Component label, Component tooltip, DialogActionCallback action) {
        return button(label, tooltip, action, Duration.ofHours(12), -1);
    }

    /**
     * Creates a callback button with tooltip and lifetime.
     *
     * @param label    Button text.
     * @param tooltip  Tooltip text.
     * @param action   Callback code.
     * @param lifetime Expiration duration.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button(Component label, Component tooltip, DialogActionCallback action, TemporalAmount lifetime) {
        return button(label, tooltip, action, lifetime, -1);
    }

    /**
     * Creates a callback button with tooltip and limited uses.
     *
     * @param label   Button text.
     * @param tooltip Tooltip text.
     * @param action  Callback code.
     * @param uses    Max usage count.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button(Component label, Component tooltip, DialogActionCallback action, int uses) {
        return button(label, tooltip, action, Duration.ofHours(12), uses);
    }

    /**
     * Creates a callback button with tooltip, lifetime, and usage limits.
     *
     * @param label    Button text.
     * @param tooltip  Tooltip text.
     * @param action   Callback code.
     * @param lifetime Expiration duration.
     * @param uses     Max usage count.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button(Component label, Component tooltip, DialogActionCallback action, TemporalAmount lifetime, int uses) {
        return ActionButton.builder(label)
            .action(DialogAction.customClick(action, ClickCallback.Options.builder().uses(uses).lifetime(lifetime).build()))
            .tooltip(tooltip)
            .build();
    }

    /**
     * Creates a callback button with width.
     *
     * @param label  Button text.
     * @param width  Visual width.
     * @param action Callback code.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button(Component label, int width, DialogActionCallback action) {
        return button(label, null, width, action, Duration.ofHours(12), -1);
    }

    /**
     * Creates a callback button with width and lifetime.
     *
     * @param label    Button text.
     * @param width    Visual width.
     * @param action   Callback code.
     * @param lifetime Expiration duration.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button(Component label, int width, DialogActionCallback action, TemporalAmount lifetime) {
        return button(label, null, width, action, lifetime, -1);
    }

    /**
     * Creates a callback button with width and usage limits.
     *
     * @param label  Button text.
     * @param width  Visual width.
     * @param action Callback code.
     * @param uses   Max usage count.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button(Component label, int width, DialogActionCallback action, int uses) {
        return button(label, null, width, action, Duration.ofHours(12), uses);
    }

    /**
     * Creates a callback button with tooltip and width.
     *
     * @param label   Button text.
     * @param tooltip Tooltip text.
     * @param width   Visual width.
     * @param action  Callback code.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button(Component label, Component tooltip, int width, DialogActionCallback action) {
        return button(label, tooltip, width, action, Duration.ofHours(12), -1);
    }

    /**
     * Creates a callback button with tooltip, width, and lifetime.
     *
     * @param label    Button text.
     * @param tooltip  Tooltip text.
     * @param width    Visual width.
     * @param action   Callback code.
     * @param lifetime Expiration duration.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button(Component label, Component tooltip, int width, DialogActionCallback action, TemporalAmount lifetime) {
        return button(label, tooltip, width, action, lifetime, -1);
    }

    /**
     * Creates a callback button with tooltip, width, and usage limits.
     *
     * @param label   Button text.
     * @param tooltip Tooltip text.
     * @param width   Visual width.
     * @param action  Callback code.
     * @param uses    Max usage count.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button(Component label, Component tooltip, int width, DialogActionCallback action, int uses) {
        return button(label, tooltip, width, action, Duration.ofHours(12), uses);
    }

    /**
     * Creates a fully specified button linked to a functional callback.
     *
     * @param label    Button text.
     * @param tooltip  Tooltip text.
     * @param width    Visual width.
     * @param action   Callback code.
     * @param lifetime Expiration duration.
     * @param uses     Max usage count.
     * @return A configured {@link ActionButton}.
     */
    public static ActionButton button(Component label, Component tooltip, int width, DialogActionCallback action, TemporalAmount lifetime, int uses) {
        return ActionButton.builder(label)
            .action(DialogAction.customClick(action, ClickCallback.Options.builder().uses(uses).lifetime(lifetime).build()))
            .tooltip(tooltip)
            .width(width)
            .build();
    }

    /**
     * Displays an item stack within the dialog body.
     *
     * @param stack The {@link ItemStack} to display.
     * @return A configured {@link ItemDialogBody}.
     */
    public static ItemDialogBody item(ItemStack stack) {
        return item(stack, null, 16, 16, false, false);
    }

    /**
     * Displays an item stack with a text description.
     *
     * @param stack       The item.
     * @param description The text description.
     * @return A configured {@link ItemDialogBody}.
     */
    public static ItemDialogBody item(ItemStack stack, PlainMessageDialogBody description) {
        return item(stack, description, 16, 16, false, false);
    }

    /**
     * Displays an item stack with specific dimensions.
     *
     * @param stack  The item.
     * @param width  Icon width.
     * @param height Icon height.
     * @return A configured {@link ItemDialogBody}.
     */
    public static ItemDialogBody item(ItemStack stack, int width, int height) {
        return item(stack, null, width, height, false, false);
    }

    /**
     * Displays an item stack with description and dimensions.
     *
     * @param stack       The item.
     * @param description Text description.
     * @param width       Icon width.
     * @param height      Icon height.
     * @return A configured {@link ItemDialogBody}.
     */
    public static ItemDialogBody item(ItemStack stack, PlainMessageDialogBody description, int width, int height) {
        return item(stack, description, width, height, false, false);
    }

    /**
     * Displays an item stack with toggles for decorations and tooltips.
     *
     * @param stack        The item.
     * @param showDeco     Whether to show stack size/enchants.
     * @param showTooltips Whether to show the vanilla tooltip on hover.
     * @return A configured {@link ItemDialogBody}.
     */
    public static ItemDialogBody item(ItemStack stack, boolean showDeco, boolean showTooltips) {
        return item(stack, null, 16, 16, showDeco, showTooltips);
    }

    /**
     * Displays an item stack with description and toggle settings.
     *
     * @param stack        The item.
     * @param description  Text description.
     * @param showDeco     Decoration toggle.
     * @param showTooltips Tooltip toggle.
     * @return A configured {@link ItemDialogBody}.
     */
    public static ItemDialogBody item(ItemStack stack, PlainMessageDialogBody description, boolean showDeco, boolean showTooltips) {
        return item(stack, description, 16, 16, showDeco, showTooltips);
    }

    /**
     * Fully specified factory for item body components.
     *
     * @param stack        The item stack.
     * @param description  Optional text description.
     * @param width        Visual width.
     * @param height       Visual height.
     * @param showDeco     Show stack decorations.
     * @param showTooltips Show item tooltips.
     * @return A configured {@link ItemDialogBody}.
     */
    public static ItemDialogBody item(ItemStack stack, PlainMessageDialogBody description, int width, int height, boolean showDeco, boolean showTooltips) {
        return DialogBody.item(stack)
            .description(description)
            .width(width)
            .height(height)
            .showDecorations(showDeco)
            .showTooltip(showTooltips)
            .build();
    }

    /**
     * Creates a simple text message for the dialog body.
     *
     * @param text The {@link Component} to display.
     * @return A configured {@link PlainMessageDialogBody}.
     */
    public static PlainMessageDialogBody text(Component text) {
        return DialogBody.plainMessage(text);
    }

    /**
     * Creates a text message with specific visual width.
     *
     * @param text  The component.
     * @param width The width.
     * @return A configured {@link PlainMessageDialogBody}.
     */
    public static PlainMessageDialogBody text(Component text, int width) {
        return DialogBody.plainMessage(text, width);
    }
}