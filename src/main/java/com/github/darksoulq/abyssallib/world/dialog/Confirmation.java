package com.github.darksoulq.abyssallib.world.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;

@SuppressWarnings("UnstableApiUsage")
public class Confirmation extends BaseDialog<Confirmation> {
    private final Component title;
    private final ActionButton yes;
    private final ActionButton no;

    public Confirmation(Component title, ActionButton yes, ActionButton no) {
        this.title = title;
        this.yes = yes;
        this.no = no;
    }

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
