package com.github.darksoulq.abyssallib.world.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.text.Component;

/**
 * A central factory class for instantiating various dialog types.
 * Use this as the primary entry point for creating dialog builders.
 */
@SuppressWarnings("UnstableApiUsage")
public class Dialogs {

    /**
     * Creates a builder for a binary choice confirmation dialog.
     *
     * @param title The dialog title.
     * @param yes   The confirm {@link ActionButton}.
     * @param no    The cancel {@link ActionButton}.
     * @return A new {@link Confirmation} builder.
     */
    public static Confirmation confirmation(Component title, ActionButton yes, ActionButton no) {
        return new Confirmation(title, yes, no);
    }

    /**
     * Creates a builder for a list-based dialog menu with default layout.
     *
     * @param title   The menu title.
     * @param dialogs The set of child {@link Dialog}s to display.
     * @return A new {@link DialogList} builder.
     */
    public static DialogList dialogList(Component title, RegistrySet<Dialog> dialogs) {
        return dialogList(title, dialogs, null, null, null);
    }

    /**
     * Creates a builder for a list-based dialog menu with grid settings.
     *
     * @param title       The menu title.
     * @param dialogs     The set of child {@link Dialog}s.
     * @param columns     The number of columns in the list.
     * @param buttonWidth The pixel width of buttons.
     * @return A new {@link DialogList} builder.
     */
    public static DialogList dialogList(Component title, RegistrySet<Dialog> dialogs, int columns, int buttonWidth) {
        return dialogList(title, dialogs, null, columns, buttonWidth);
    }

    /**
     * The master factory method for creating customized {@link DialogList} builders.
     *
     * @param title       The menu title.
     * @param dialogs     The set of child {@link Dialog}s.
     * @param exit        An optional exit {@link ActionButton}.
     * @param columns     Optional column count.
     * @param buttonWidth Optional button width.
     * @return A new {@link DialogList} builder.
     */
    public static DialogList dialogList(Component title, RegistrySet<Dialog> dialogs, ActionButton exit, Integer columns, Integer buttonWidth) {
        return new DialogList(title, dialogs, exit, columns, buttonWidth);
    }

    /**
     * Creates a builder for a simple notice dialog.
     *
     * @param title The notice title.
     * @return A new {@link Notice} builder.
     */
    public static Notice notice(Component title) {
        return notice(title, null);
    }

    /**
     * Creates a builder for a notice dialog with a specific action button.
     *
     * @param title  The notice title.
     * @param action The primary {@link ActionButton}.
     * @return A new {@link Notice} builder.
     */
    public static Notice notice(Component title, ActionButton action) {
        return new Notice(title, action);
    }

    /**
     * Creates a builder for a server links menu.
     *
     * @param title       The menu title.
     * @param columns     Number of columns.
     * @param buttonWidth Width of link buttons.
     * @return A new {@link ServerLinks} builder.
     */
    public static ServerLinks serverLinks(Component title, int columns, int buttonWidth) {
        return serverLinks(title, null, columns, buttonWidth);
    }

    /**
     * Creates a builder for a server links menu with a primary action button.
     *
     * @param title       The menu title.
     * @param action      The primary {@link ActionButton}.
     * @param columns     Number of columns.
     * @param buttonWidth Width of link buttons.
     * @return A new {@link ServerLinks} builder.
     */
    public static ServerLinks serverLinks(Component title, ActionButton action, int columns, int buttonWidth) {
        return new ServerLinks(title, action, columns, buttonWidth);
    }

    /**
     * Creates a builder for a multi-action grid dialog.
     *
     * @param title The menu title.
     * @return A new {@link MultiAction} builder.
     */
    public static MultiAction multiAction(Component title) {
        return new MultiAction(title);
    }

    /**
     * Creates a builder for a multi-action grid dialog with column settings.
     *
     * @param title   The menu title.
     * @param columns Number of columns in the grid.
     * @return A new {@link MultiAction} builder.
     */
    public static MultiAction multiAction(Component title, int columns) {
        return multiAction(title, null, columns);
    }

    /**
     * Creates a builder for a multi-action grid dialog with exit button and columns.
     *
     * @param title      The menu title.
     * @param exitButton The designated exit {@link ActionButton}.
     * @param columns    Number of columns in the grid.
     * @return A new {@link MultiAction} builder.
     */
    public static MultiAction multiAction(Component title, ActionButton exitButton, int columns) {
        return new MultiAction(title, exitButton, columns);
    }
}