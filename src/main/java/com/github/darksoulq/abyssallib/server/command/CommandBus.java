package com.github.darksoulq.abyssallib.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
    private record RegisteredCommand(String pluginId, Method method, Object handler) {}

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
     * @param pluginId   The mod ID this command belongs to.
     * @param handler The object containing command methods.
     */
    public static void register(String pluginId, Object handler) {
        for (Method method : handler.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                Command command = method.getAnnotation(Command.class);
                String commandName = command.name();

                LiteralArgumentBuilder<CommandSourceStack> root = LiteralArgumentBuilder.literal(commandName);
                List<LiteralArgumentBuilder<CommandSourceStack>> aliases = new ArrayList<>();
                for (String alias : command.aliases()) {
                    aliases.add(LiteralArgumentBuilder.literal(alias));
                }
                if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == LiteralArgumentBuilder.class) {
                    try {
                        method.invoke(handler, root);
                        for (LiteralArgumentBuilder<CommandSourceStack> alias : aliases) {
                            method.invoke(handler, alias);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new IllegalArgumentException("Method " + method + " must have exactly one parameter of type LiteralArgumentBuilder.");
                }

                registered.add(new RegisteredCommand(pluginId, method, handler));
                getDispatcher().register(root);
                for (LiteralArgumentBuilder<CommandSourceStack> alias : aliases) {
                    getDispatcher().register(alias);
                }
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
            List<LiteralArgumentBuilder<CommandSourceStack>> aliases = new ArrayList<>();
            for (String alias : cmd.method.getAnnotation(Command.class).aliases()) {
                aliases.add(LiteralArgumentBuilder.literal(alias));
            }

            if (cmd.method.getParameterCount() == 1 && cmd.method.getParameterTypes()[0] == LiteralArgumentBuilder.class) {
                try {
                    cmd.method.invoke(cmd.handler, root);
                    for (LiteralArgumentBuilder<CommandSourceStack> alias : aliases) {
                        cmd.method.invoke(cmd.handler, alias);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                throw new IllegalArgumentException("Method " + cmd.method + " must have exactly one parameter of type LiteralArgumentBuilder.");
            }

            getDispatcher().register(root);
            for (LiteralArgumentBuilder<CommandSourceStack> alias : aliases) {
                getDispatcher().register(alias);
            }
            Bukkit.getOnlinePlayers().forEach(p ->
                MinecraftServer.getServer().resources.managers().commands.sendCommands(((CraftPlayer) p).getHandle())
            );
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
            boolean ours = registered.stream().anyMatch(r ->(r.method.getAnnotation(Command.class).name().equals(name)
                    || Arrays.asList(r.method.getAnnotation(Command.class).aliases()).contains(name)));
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
