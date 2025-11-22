package com.github.darksoulq.abyssallib.world.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;

@SuppressWarnings("UnstableApiUsage")
public class Notice extends BaseDialog<Notice> {
    private final Component title;
    private final ActionButton action;

    public Notice(Component title) {
        this(title, null);
    }
    public Notice(Component title, ActionButton action) {
        this.title = title;
        this.action = action;
    }

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
