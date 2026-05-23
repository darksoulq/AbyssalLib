package com.github.darksoulq.abyssallib.server.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;

@FunctionalInterface
public interface CommandExecutor {
    CommandResult execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException;
}