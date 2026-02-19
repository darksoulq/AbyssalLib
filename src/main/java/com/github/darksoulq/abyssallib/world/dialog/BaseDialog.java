package com.github.darksoulq.abyssallib.world.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract base class providing a fluent builder-style API for creating Minecraft Dialogs.
 *
 * @param <T> The type of the dialog implementation, used for method chaining.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class BaseDialog<T extends BaseDialog<T>> {

    /**
     * The action to perform after the dialog is interacted with.
     */
    DialogBase.DialogAfterAction afterAction = DialogBase.DialogAfterAction.CLOSE;

    /**
     * An optional title displayed externally to the dialog window.
     */
    Component externalTitle = null;

    /**
     * Whether the user can close this dialog by pressing the Escape key.
     */
    boolean canCloseWithEscape = true;

    /**
     * The list of interactive input elements within the dialog.
     */
    List<DialogInput> inputs = new ArrayList<>();

    /**
     * The list of static or decorative content elements within the dialog.
     */
    List<DialogBody> bodies = new ArrayList<>();

    /**
     * Sets the behavior of the dialog after an action is performed.
     *
     * @param afterAction The {@link DialogBase.DialogAfterAction} to apply.
     * @return This dialog instance for chaining.
     */
    @SuppressWarnings("unchecked")
    public T after(DialogBase.DialogAfterAction afterAction) {
        this.afterAction = afterAction;
        return (T) this;
    }

    /**
     * Sets an external title for the dialog.
     *
     * @param title The {@link Component} to display as an external title.
     * @return This dialog instance for chaining.
     */
    @SuppressWarnings("unchecked")
    public T external(Component title) {
        this.externalTitle = title;
        return (T) this;
    }

    /**
     * Sets whether the dialog should close when the Escape key is pressed.
     *
     * @param should True if escape should close the dialog.
     * @return This dialog instance for chaining.
     */
    @SuppressWarnings("unchecked")
    public T closeWithEscape(boolean should) {
        this.canCloseWithEscape = should;
        return (T) this;
    }

    /**
     * Adds an input element to the dialog.
     *
     * @param input The {@link DialogInput} to add.
     * @return This dialog instance for chaining.
     */
    @SuppressWarnings("unchecked")
    public T input(DialogInput input) {
        this.inputs.add(input);
        return (T) this;
    }

    /**
     * Adds a body element to the dialog.
     *
     * @param body The {@link DialogBody} to add.
     * @return This dialog instance for chaining.
     */
    @SuppressWarnings("unchecked")
    public T body(DialogBody body) {
        this.bodies.add(body);
        return (T) this;
    }

    /**
     * Constructs the final {@link Dialog} object.
     *
     * @return A built Dialog ready to be shown to a player.
     */
    public abstract Dialog build();
}