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

public class CommandBus {

    private static CommandDispatcher<CommandSourceStack> dispatcher;
    private static final List<RegisteredCommand> registered = new ArrayList<>();

    private record RegisteredCommand(
        String pluginId,
        Object handler,
        Method legacyMethod,
        BaseCommand newCommand
    ) {
    }

    public static void init(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandBus.dispatcher = dispatcher;
    }

    public static void register(String pluginId, Object handler) {
        for (Method method : handler.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                Command command = method.getAnnotation(Command.class);
                registerLegacyMethod(pluginId, handler, method, command);
            }
        }
    }

    public static void register(String pluginId, BaseCommand command) {
        registered.add(new RegisteredCommand(pluginId, null, null, command));
        registerToDispatcher(command.getRoot(), command.getAliasBuilders());
    }

    private static void registerLegacyMethod(String pluginId, Object handler, Method method, Command command) {
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
                return;
            }
        } else {
            throw new IllegalArgumentException("Method " + method + " must have exactly one parameter of type LiteralArgumentBuilder.");
        }

        registered.add(new RegisteredCommand(pluginId, handler, method, null));
        registerToDispatcher(root, aliases);
    }

    private static void registerToDispatcher(LiteralArgumentBuilder<CommandSourceStack> root, List<LiteralArgumentBuilder<CommandSourceStack>> aliases) {
        getDispatcher().register(root);
        for (LiteralArgumentBuilder<CommandSourceStack> alias : aliases) {
            getDispatcher().register(alias);
        }
    }

    public static void reloadAll() {
        unregisterAll();

        for (RegisteredCommand cmd : registered) {
            if (cmd.newCommand != null) {
                registerToDispatcher(cmd.newCommand.getRoot(), cmd.newCommand.getAliasBuilders());
            } else if (cmd.legacyMethod != null) {
                Command command = cmd.legacyMethod.getAnnotation(Command.class);
                registerLegacyMethod(cmd.pluginId, cmd.handler, cmd.legacyMethod, command);
                registered.removeLast();
            }
        }

        Bukkit.getOnlinePlayers().forEach(p -> {
                //? if <=26.1.2 {
                /*MinecraftServer.getServer().resources.managers().commands.sendCommands(((CraftPlayer) p).getHandle());
                 *///?} else {
                MinecraftServer.getServer().getCommands().sendCommands(((CraftPlayer) p).getHandle());
                //?}
            }
        );
    }

    private static void unregisterAll() {
        CommandDispatcher<CommandSourceStack> dispatcher = getDispatcher();
        Collection<CommandNode<CommandSourceStack>> commandMap = dispatcher.getRoot().getChildren();
        List<String> toRemove = new ArrayList<>();

        for (CommandNode<CommandSourceStack> child : commandMap) {
            String name = child.getName();
            boolean ours = registered.stream().anyMatch(r -> {
                if (r.newCommand != null) {
                    return r.newCommand.getName().equals(name) || Arrays.asList(r.newCommand.getAliases()).contains(name);
                } else {
                    Command cmd = r.legacyMethod.getAnnotation(Command.class);
                    return cmd.name().equals(name) || Arrays.asList(cmd.aliases()).contains(name);
                }
            });
            if (ours) toRemove.add(name);
        }

        for (String name : toRemove) {
            dispatcher.getRoot().removeCommand(name);
        }
    }

    public static CommandDispatcher<CommandSourceStack> getDispatcher() {
        return dispatcher;
    }
}