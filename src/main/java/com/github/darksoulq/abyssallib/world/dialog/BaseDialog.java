package com.github.darksoulq.abyssallib.world.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
@SuppressWarnings("UnstableApiUsage")
public abstract class BaseDialog<T extends BaseDialog<T>> {
    DialogBase.DialogAfterAction afterAction = DialogBase.DialogAfterAction.CLOSE;
    Component externalTitle = null;
    boolean canCloseWithEscape = true;
    List<DialogInput> inputs = new ArrayList<>();
    List<DialogBody> bodies = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public T after(DialogBase.DialogAfterAction afterAction) {
        this.afterAction = afterAction;
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T external(Component title) {
        this.externalTitle = title;
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T closeWithEscape(boolean should) {
        this.canCloseWithEscape = should;
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T input(DialogInput input) {
        this.inputs.add(input);
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T body(DialogBody body) {
        this.bodies.add(body);
        return (T) this;
    }

    public abstract Dialog build();
}
