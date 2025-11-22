package com.github.darksoulq.abyssallib.world.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class MultiAction extends BaseDialog<MultiAction> {
    private final Component title;
    private final List<ActionButton> actions = new ArrayList<>();
    private final ActionButton exitButton;
    private final Integer columns;

    public MultiAction(Component title) {
        this(title, null, null);
    }
    public MultiAction(Component title, ActionButton exitButton, Integer columns) {
        this.title = title;
        this.exitButton = exitButton;
        this.columns = columns;
    }

    public MultiAction action(ActionButton button) {
        this.actions.add(button);
        return this;
    }

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

    private boolean othersNull() {
        return columns == null;
    }
}
