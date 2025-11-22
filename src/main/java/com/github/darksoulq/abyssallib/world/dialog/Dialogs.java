package com.github.darksoulq.abyssallib.world.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.text.Component;

@SuppressWarnings("UnstableApiUsage")
public class Dialogs {
    public static Confirmation confirmation(Component title, ActionButton yes, ActionButton no) {
        return new Confirmation(title, yes, no);
    }
    public static DialogList dialogList(Component title, RegistrySet<Dialog> dialogs) {
        return dialogList(title, dialogs, null, null, null);
    }
    public static DialogList dialogList(Component title, RegistrySet<Dialog> dialogs, int columns, int buttonWidth) {
        return dialogList(title, dialogs, null, columns, buttonWidth);
    }
    public static DialogList dialogList(Component title, RegistrySet<Dialog> dialogs, ActionButton exit, Integer columns, Integer buttonWidth) {
        return new DialogList(title, dialogs, exit, columns, buttonWidth);
    }
    public static Notice notice(Component title) {
        return notice(title, null);
    }
    public static Notice notice(Component title, ActionButton action) {
        return new Notice(title, action);
    }
    public static ServerLinks serverLinks(Component title, int columns, int buttonWidth) {
        return serverLinks(title, null, columns, buttonWidth);
    }
    public static ServerLinks serverLinks(Component title, ActionButton action, int columns, int buttonWidth) {
        return new ServerLinks(title, action, columns, buttonWidth);
    }
    public static MultiAction multiAction(Component title) {
        return new MultiAction(title);
    }
    public static MultiAction multiAction(Component title, int columns) {
        return multiAction(title, null, columns);
    }
    public static MultiAction multiAction(Component title, ActionButton exitButton, int columns) {
        return new MultiAction(title, exitButton, columns);
    }
}
