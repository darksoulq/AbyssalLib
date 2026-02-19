package com.github.darksoulq.abyssallib.world.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;

/**
 * A dialog implementation specifically for displaying server link buttons.
 */
@SuppressWarnings("UnstableApiUsage")
public class ServerLinks extends BaseDialog<ServerLinks> {

    /**
     * The title of the links menu.
     */
    private final Component title;

    /**
     * The primary action button for this dialog.
     */
    private final ActionButton action;

    /**
     * The number of columns for the link button grid.
     */
    private final int columns;

    /**
     * The pixel width of the link buttons.
     */
    private final int buttonWidth;

    /**
     * Constructs a ServerLinks dialog with basic parameters.
     *
     * @param title       The title of the menu.
     * @param columns     The grid column count.
     * @param buttonWidth The width of buttons in the grid.
     */
    public ServerLinks(Component title, int columns, int buttonWidth) {
        this(title, null, columns, buttonWidth);
    }

    /**
     * Constructs a ServerLinks dialog with a primary action button and layout settings.
     *
     * @param title       The title of the menu.
     * @param action      The primary {@link ActionButton}.
     * @param columns     The grid column count.
     * @param buttonWidth The width of buttons in the grid.
     */
    public ServerLinks(Component title, ActionButton action, int columns, int buttonWidth) {
        this.title = title;
        this.action = action;
        this.columns = columns;
        this.buttonWidth = buttonWidth;
    }

    /**
     * Builds the ServerLinks dialog into a Paper {@link Dialog}.
     *
     * @return The configured Dialog instance.
     */
    @Override
    public Dialog build() {
        return Dialog.create(builder -> builder.empty()
            .type(DialogType.serverLinks(action, columns, buttonWidth))
            .base(DialogBase.builder(title)
                .body(bodies)
                .inputs(inputs)
                .externalTitle(externalTitle)
                .afterAction(afterAction)
                .canCloseWithEscape(canCloseWithEscape)
                .build()));
    }
}