package com.github.darksoulq.abyssallib.world.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.text.Component;

/**
 * A dialog that displays a list of selectable child dialogs.
 */
@SuppressWarnings("UnstableApiUsage")
public class DialogList extends BaseDialog<DialogList> {

    /**
     * The title of the dialog list.
     */
    private final Component title;

    /**
     * The set of {@link Dialog} objects to be displayed in the list.
     */
    private final RegistrySet<Dialog> dialogs;

    /**
     * An optional action button used to exit the list.
     */
    private final ActionButton exitAction;

    /**
     * The number of columns to display in the grid layout.
     */
    private final Integer columns;

    /**
     * The specific pixel width of the buttons in the list.
     */
    private final Integer buttonWidth;

    /**
     * Constructs a basic DialogList with default layout settings.
     *
     * @param title   The title of the menu.
     * @param dialogs The {@link RegistrySet} of dialogs to list.
     */
    public DialogList(Component title, RegistrySet<Dialog> dialogs) {
        this(title, dialogs, null, null, null);
    }

    /**
     * Constructs a DialogList with full layout customization.
     *
     * @param title      The title of the menu.
     * @param dialogs    The {@link RegistrySet} of dialogs to list.
     * @param exitAction The button used to exit the menu.
     * @param columns    The grid column count.
     * @param width      The width of individual buttons.
     */
    public DialogList(Component title, RegistrySet<Dialog> dialogs, ActionButton exitAction, Integer columns, Integer width) {
        this.title = title;
        this.dialogs = dialogs;
        this.exitAction = exitAction;
        this.columns = columns;
        this.buttonWidth = width;
    }

    /**
     * Builds the DialogList into a Paper {@link Dialog}.
     *
     * @return The configured Dialog instance.
     */
    @Override
    public Dialog build() {
        return Dialog.create(builder -> builder.empty()
            .type(othersNull() ? DialogType.dialogList(dialogs).build() : DialogType.dialogList(dialogs, exitAction, columns, buttonWidth))
            .base(DialogBase.builder(title)
                .body(bodies)
                .inputs(inputs)
                .externalTitle(externalTitle)
                .afterAction(afterAction)
                .canCloseWithEscape(canCloseWithEscape)
                .build()));
    }

    /**
     * Internal check to determine if default layout parameters should be used.
     *
     * @return True if layout parameters are missing.
     */
    private boolean othersNull() {
        return columns == null || buttonWidth == null;
    }
}