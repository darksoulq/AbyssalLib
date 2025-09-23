package com.github.darksoulq.abyssallib.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Manages registration and lifecycle of custom commands using the Brigadier command system.
 * This class is intended to be initialized once via {@link #init(CommandDispatcher)}
 */
public class CommandBus {
    /**
     * The command dispatcher used to register commands.
     */
    private static CommandDispatcher<CommandSourceStack> dispatcher;

    /**
     * A list of registered commands.
     */
    private static final List<RegisteredCommand> registered = new ArrayList<>();

    /**
     * A record representing a registered command, containing the mod ID, the method that handles the command, and the handler object.
     */
    private record RegisteredCommand(String modid, Method method, Object handler) {}

    /**
     * Initializes the {@code CommandBus} with the given Brigadier {@link CommandDispatcher}.
     *
     * @param dispatcher The command dispatcher used to register commands.
     */
    public static void init(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandBus.dispatcher = dispatcher;
    }

    /**
     * Registers all methods in the given handler class that are annotated with {@link Command}.
     * Each method must accept a single parameter of type {@link LiteralArgumentBuilder}.
     *
     * @param modid   The mod ID this command belongs to.
     * @param handler The object containing command methods.
     */
    public static void register(String modid, Object handler) {
        for (Method method : handler.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                Command command = method.getAnnotation(Command.class);
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

    /**
     * Reloads all previously registered commands by unregistering and re-registering them. (handled in /abyssallib reload)
     */
    public static void reloadAll() {
        unregisterAll();

        for (RegisteredCommand cmd : registered) {
            LiteralArgumentBuilder<CommandSourceStack> root = LiteralArgumentBuilder.literal(cmd.method.getAnnotation(Command.class).name());

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

    /**
     * Unregisters all commands that have been previously registered.
     */
    private static void unregisterAll() {
        CommandDispatcher<CommandSourceStack> dispatcher = getDispatcher();
        Collection<CommandNode<CommandSourceStack>> commandMap = dispatcher.getRoot().getChildren();
        List<String> toRemove = new ArrayList<>();

        for (CommandNode<CommandSourceStack> child : commandMap) {
            String name = child.getName();
            boolean ours = registered.stream().anyMatch(r -> r.method.getAnnotation(Command.class).name().equals(name));
            if (ours) toRemove.add(name);
        }

        for (String name : toRemove) {
            dispatcher.getRoot().removeCommand(name);
        }
    }

    /**
     * Retrieves the command dispatcher used for registering commands.
     *
     * @return The command dispatcher.
     */
    public static CommandDispatcher<CommandSourceStack> getDispatcher() {
        return dispatcher;
    }
}
