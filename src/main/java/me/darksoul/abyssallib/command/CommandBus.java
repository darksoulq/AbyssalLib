package me.darksoul.abyssallib.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.darksoul.abyssallib.AbyssalLib;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages registration and lifecycle of custom commands using the Brigadier command system.
 * This class is intended to be initialized once via {@link #init(CommandDispatcher)} (which is done in {@link AbyssalLib}) and accessed
 * via {@link #INSTANCE}.
 */
public class CommandBus {
    /**
     * The command dispatcher used to register commands.
     */
    private static CommandDispatcher<CommandSourceStack> dispatcher;
    /**
     * The singleton instance of {@link CommandBus}.
     */
    public static CommandBus INSTANCE;

    /**
     * A list of registered commands.
     */
    private final List<RegisteredCommand> registered = new ArrayList<>();

    /**
     * A record representing a registered command, containing the mod ID, the method that handles the command, and the handler object.
     */
    private record RegisteredCommand(String modid, Method method, Object handler) {}

    /**
     * Initializes the {@code CommandBus} with the given Brigadier {@link CommandDispatcher}.
     * This must be called once before using {@link #INSTANCE}.
     *
     * @param dispatcher The command dispatcher used to register commands.
     */
    public static void init(CommandDispatcher<CommandSourceStack> dispatcher) {
        INSTANCE = new CommandBus();
        CommandBus.dispatcher = dispatcher;
    }

    /**
     * Registers all methods in the given handler class that are annotated with {@link AbyssalCommand}.
     * Each method must accept a single parameter of type {@link LiteralArgumentBuilder}.
     *
     * @param modid   The mod ID this command belongs to.
     * @param handler The object containing command methods.
     */
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

    /**
     * Reloads all previously registered commands by unregistering and re-registering them. (handled in /abyssallib reload)
     */
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

    /**
     * Unregisters all commands that have been previously registered.
     */
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

    /**
     * Retrieves the command dispatcher used for registering commands.
     *
     * @return The command dispatcher.
     */
    private CommandDispatcher<CommandSourceStack> getDispatcher() {
        return dispatcher;
    }
}
