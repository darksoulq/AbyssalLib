package me.darksoul.abyssalLib.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.ArrayList;
import java.util.List;

public class CommandBus {
    private static CommandDispatcher<CommandSourceStack> dispatcher;
    public static CommandBus INSTANCE;

    private final List<RegisteredCommand> registered = new ArrayList<>();

    private record RegisteredCommand(String modid, AbyssalCommand command) {}

    public static CommandBus init(CommandDispatcher<CommandSourceStack> dispatcher) {
        INSTANCE = new CommandBus();
        CommandBus.dispatcher = dispatcher;
        return INSTANCE;
    }

    public void register(String modid, AbyssalCommand command) {
        registered.add(new RegisteredCommand(modid, command));

        LiteralArgumentBuilder<CommandSourceStack> root = LiteralArgumentBuilder.literal(command.name());
        command.register(root);

        getDispatcher().register(root);
    }

    public void reloadAll() {
        unregisterAll();

        for (RegisteredCommand cmd : registered) {
            LiteralArgumentBuilder<CommandSourceStack> root = LiteralArgumentBuilder.literal(cmd.command.name());
            cmd.command.register(root);
            getDispatcher().register(root);
        }
    }

    private void unregisterAll() {
        var dispatcher = getDispatcher();
        var commandMap = dispatcher.getRoot().getChildren();
        List<String> toRemove = new ArrayList<>();

        for (var child : commandMap) {
            String name = child.getName();
            boolean ours = registered.stream().anyMatch(r -> r.command.name().equals(name));
            if (ours) toRemove.add(name);
        }

        for (String name : toRemove) {
            dispatcher.getRoot().removeCommand(name);
        }
    }

    private CommandDispatcher<CommandSourceStack> getDispatcher() {
        return dispatcher;
    }
}
