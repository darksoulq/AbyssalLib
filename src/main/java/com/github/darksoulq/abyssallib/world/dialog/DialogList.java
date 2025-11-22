package com.github.darksoulq.abyssallib.world.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.text.Component;

@SuppressWarnings("UnstableApiUsage")
public class DialogList extends BaseDialog<DialogList> {
    private final Component title;
    private final RegistrySet<Dialog> dialogs;
    private final ActionButton exitAction;
    private final Integer columns;
    private final Integer buttonWidth;

    public DialogList(Component title, RegistrySet<Dialog> dialogs) {
        this(title, dialogs, null, null, null);
    }
    public DialogList(Component title, RegistrySet<Dialog> dialogs, ActionButton exitAction, Integer columns, Integer width) {
        this.title = title;
        this.dialogs = dialogs;
        this.exitAction = exitAction;
        this.columns = columns;
        this.buttonWidth = width;
    }

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

    private boolean othersNull() {
        return columns == null || buttonWidth == null;
    }
}
