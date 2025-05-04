package me.darksoul.abyssalLib.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CommandBus {
    private static CommandDispatcher<CommandSourceStack> dispatcher;
    public static CommandBus INSTANCE;

    private final List<RegisteredCommand> registered = new ArrayList<>();

    private record RegisteredCommand(String modid, Method method, Object handler) {}

    public static void init(CommandDispatcher<CommandSourceStack> dispatcher) {
        INSTANCE = new CommandBus();
        CommandBus.dispatcher = dispatcher;
    }

    public void register(String modid, Object handler) {
        for (Method method : handler.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(AbyssalCommand.class)) {
                AbyssalCommand command = method.getAnnotation(AbyssalCommand.class);
                String commandName = command.name();

                registered.add(new RegisteredCommand(modid, method, handler));

                LiteralArgumentBuilder<CommandSourceStack> root = LiteralArgumentBuilder.literal(commandName);
                if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == LiteralArgumentBuilder.class) {
                    try {
                        method.invoke(handler, root);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new IllegalArgumentException("Method " + method + " must have exactly one parameter of type LiteralArgumentBuilder.");
                }

                getDispatcher().register(root);
            }
        }
    }

    public void reloadAll() {
        unregisterAll();

        for (RegisteredCommand cmd : registered) {
            LiteralArgumentBuilder<CommandSourceStack> root = LiteralArgumentBuilder.literal(cmd.method.getAnnotation(AbyssalCommand.class).name());

            if (cmd.method.getParameterCount() == 1 && cmd.method.getParameterTypes()[0] == LiteralArgumentBuilder.class) {
                try {
                    cmd.method.invoke(cmd.handler, root);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                throw new IllegalArgumentException("Method " + cmd.method + " must have exactly one parameter of type LiteralArgumentBuilder.");
            }

            getDispatcher().register(root);
        }
    }

    private void unregisterAll() {
        var dispatcher = getDispatcher();
        var commandMap = dispatcher.getRoot().getChildren();
        List<String> toRemove = new ArrayList<>();

        for (var child : commandMap) {
            String name = child.getName();
            boolean ours = registered.stream().anyMatch(r -> r.method.getAnnotation(AbyssalCommand.class).name().equals(name));
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
