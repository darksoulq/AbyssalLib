package com.github.darksoulq.abyssallib.world.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;

/**
 * A standard notice dialog used to display information with an optional primary action button.
 */
@SuppressWarnings("UnstableApiUsage")
public class Notice extends BaseDialog<Notice> {

    /**
     * The main title component of the notice.
     */
    private final Component title;

    /**
     * The primary action button displayed at the bottom of the notice.
     */
    private final ActionButton action;

    /**
     * Creates a notice with a title and no specific action button.
     *
     * @param title The {@link Component} to display as the title.
     */
    public Notice(Component title) {
        this(title, null);
    }

    /**
     * Creates a notice with a title and a primary action button.
     *
     * @param title  The {@link Component} to display as the title.
     * @param action The {@link ActionButton} to associate with this notice.
     */
    public Notice(Component title, ActionButton action) {
        this.title = title;
        this.action = action;
    }

    /**
     * Builds the Notice into a Paper {@link Dialog}.
     *
     * @return The configured Dialog instance.
     */
    @Override
    public Dialog build() {
        return Dialog.create(builder -> builder.empty()
            .type(action == null ? DialogType.notice() : DialogType.notice(action))
            .base(DialogBase.builder(title)
                .body(bodies)
                .inputs(inputs)
                .externalTitle(externalTitle)
                .afterAction(afterAction)
                .canCloseWithEscape(canCloseWithEscape)
                .build()));
    }
}