package com.github.darksoulq.abyssallib.world.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;

/**
 * A dialog implementation representing a binary choice, typically used for confirmations.
 */
@SuppressWarnings("UnstableApiUsage")
public class Confirmation extends BaseDialog<Confirmation> {

    /**
     * The title component of the confirmation dialog.
     */
    private final Component title;

    /**
     * The button representing the positive or "Yes" action.
     */
    private final ActionButton yes;

    /**
     * The button representing the negative or "No" action.
     */
    private final ActionButton no;

    /**
     * Constructs a new Confirmation dialog.
     *
     * @param title The {@link Component} to display as the title.
     * @param yes   The {@link ActionButton} for the confirm action.
     * @param no    The {@link ActionButton} for the cancel action.
     */
    public Confirmation(Component title, ActionButton yes, ActionButton no) {
        this.title = title;
        this.yes = yes;
        this.no = no;
    }

    /**
     * Builds the Confirmation dialog into a Paper {@link Dialog}.
     *
     * @return The configured Dialog instance.
     */
    @Override
    public Dialog build() {
        return Dialog.create(builder -> builder.empty()
            .type(DialogType.confirmation(yes, no))
            .base(DialogBase.builder(title)
                .body(bodies)
                .inputs(inputs)
                .externalTitle(externalTitle)
                .afterAction(afterAction)
                .canCloseWithEscape(canCloseWithEscape)
                .build()));
    }
}