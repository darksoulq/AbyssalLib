package com.github.darksoulq.abyssallib.world.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;

@SuppressWarnings("UnstableApiUsage")
public class ServerLinks extends BaseDialog<ServerLinks> {
    private final Component title;
    private final ActionButton action;
    private final int columns;
    private final int buttonWidth;

    public ServerLinks(Component title, int columns, int buttonWidth) {
        this(title, null, columns, buttonWidth);
    }
    public ServerLinks(Component title, ActionButton action, int columns, int buttonWidth) {
        this.title = title;
        this.action = action;
        this.columns = columns;
        this.buttonWidth = buttonWidth;
    }

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
