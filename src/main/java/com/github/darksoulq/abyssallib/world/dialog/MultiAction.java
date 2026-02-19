package com.github.darksoulq.abyssallib.world.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * A dialog displaying multiple buttons as primary actions.
 */
@SuppressWarnings("UnstableApiUsage")
public class MultiAction extends BaseDialog<MultiAction> {

    /**
     * The title of the multi-action menu.
     */
    private final Component title;

    /**
     * The list of action buttons displayed in the menu.
     */
    private final List<ActionButton> actions = new ArrayList<>();

    /**
     * An optional button designated for exiting the menu.
     */
    private final ActionButton exitButton;

    /**
     * The number of columns in the button grid.
     */
    private final Integer columns;

    /**
     * Constructs a basic MultiAction dialog with default layout.
     *
     * @param title The title component.
     */
    public MultiAction(Component title) {
        this(title, null, null);
    }

    /**
     * Constructs a MultiAction dialog with exit button and column configuration.
     *
     * @param title      The title component.
     * @param exitButton The designated exit {@link ActionButton}.
     * @param columns    The grid column count.
     */
    public MultiAction(Component title, ActionButton exitButton, Integer columns) {
        this.title = title;
        this.exitButton = exitButton;
        this.columns = columns;
    }

    /**
     * Adds an action button to the list of displayed actions.
     *
     * @param button The {@link ActionButton} to add.
     * @return This dialog instance for chaining.
     */
    public MultiAction action(ActionButton button) {
        this.actions.add(button);
        return this;
    }

    /**
     * Builds the MultiAction dialog into a Paper {@link Dialog}.
     *
     * @return The configured Dialog instance.
     */
    @Override
    public Dialog build() {
        return Dialog.create(builder -> builder.empty()
            .type(othersNull() ? DialogType.multiAction(actions).build() : DialogType.multiAction(actions, exitButton, columns))
            .base(DialogBase.builder(title)
                .body(bodies)
                .inputs(inputs)
                .externalTitle(externalTitle)
                .afterAction(afterAction)
                .canCloseWithEscape(canCloseWithEscape)
                .build()));
    }

    /**
     * Internal check to determine if basic grid logic applies.
     *
     * @return True if columns are not specified.
     */
    private boolean othersNull() {
        return columns == null;
    }
}