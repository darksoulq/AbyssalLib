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
 * This class encapsulates the state required to construct Paper-based dialogs, including
 * input elements, body content, and lifecycle actions.
 *
 * @param <T>
 * The type of the dialog implementation, used for method chaining.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class BaseDialog<T extends BaseDialog<T>> {

    /**
     * The action to perform after the dialog is interacted with.
     */
    protected DialogBase.DialogAfterAction afterAction = DialogBase.DialogAfterAction.CLOSE;

    /**
     * An optional title displayed externally to the dialog window.
     */
    protected Component externalTitle = null;

    /**
     * Whether the user can close this dialog by pressing the Escape key.
     */
    protected boolean canCloseWithEscape = true;

    /**
     * The list of interactive input elements within the dialog.
     */
    protected List<DialogInput> inputs = new ArrayList<>();

    /**
     * The list of static or decorative content elements within the dialog.
     */
    protected List<DialogBody> bodies = new ArrayList<>();

    /**
     * Explicit constructor for BaseDialog.
     * Initializes a new builder with default settings: closing on escape
     * and a default CLOSE after-action.
     */
    protected BaseDialog() {
    }

    /**
     * Sets the behavior of the dialog after an action is performed.
     *
     * @param afterAction
     * The {@link DialogBase.DialogAfterAction} to apply upon completion.
     * @return
     * This dialog instance cast to the implementation type {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T after(DialogBase.DialogAfterAction afterAction) {
        this.afterAction = afterAction;
        return (T) this;
    }

    /**
     * Sets an external title for the dialog.
     *
     * @param title
     * The {@link Component} to display as an external title header.
     * @return
     * This dialog instance cast to the implementation type {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T external(Component title) {
        this.externalTitle = title;
        return (T) this;
    }

    /**
     * Sets whether the dialog should close when the Escape key is pressed.
     *
     * @param should
     * True if escape should close the dialog, false otherwise.
     * @return
     * This dialog instance cast to the implementation type {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T closeWithEscape(boolean should) {
        this.canCloseWithEscape = should;
        return (T) this;
    }

    /**
     * Adds an input element to the dialog collection.
     *
     * @param input
     * The {@link DialogInput} interactive element to append.
     * @return
     * This dialog instance cast to the implementation type {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T input(DialogInput input) {
        this.inputs.add(input);
        return (T) this;
    }

    /**
     * Adds a body element to the dialog collection.
     *
     * @param body
     * The {@link DialogBody} content element to append.
     * @return
     * This dialog instance cast to the implementation type {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T body(DialogBody body) {
        this.bodies.add(body);
        return (T) this;
    }

    /**
     * Constructs the final {@link Dialog} object from the configured state.
     *
     * @return
     * A built Dialog ready to be shown to a player via the API.
     */
    public abstract Dialog build();
}