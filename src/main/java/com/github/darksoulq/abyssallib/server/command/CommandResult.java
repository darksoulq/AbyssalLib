package com.github.darksoulq.abyssallib.server.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandResult {

    private final int value;
    private final CommandSyntaxException exception;

    private CommandResult(int value, CommandSyntaxException exception) {
        this.value = value;
        this.exception = exception;
    }

    public static CommandResult success() {
        return new CommandResult(1, null);
    }

    public static CommandResult success(int value) {
        return new CommandResult(value, null);
    }

    public static CommandResult failure() {
        return new CommandResult(0, null);
    }

    public static CommandResult error(CommandSyntaxException exception) {
        return new CommandResult(0, exception);
    }

    public int getValue() throws CommandSyntaxException {
        if (exception != null) {
            throw exception;
        }
        return value;
    }
}