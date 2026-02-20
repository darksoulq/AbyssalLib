package com.github.darksoulq.abyssallib.server.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a command definition for registration via the {@link CommandBus}.
 *
 * <p>Methods annotated with {@code @Command} must accept a single parameter of type
 * {@link com.mojang.brigadier.builder.LiteralArgumentBuilder LiteralArgumentBuilder} and will be invoked during command registration.</p>
 *
 * <p>The method must be registered using {@link CommandBus#register(String, Object)} to be active.
 * The provided {@code name} is used as the literal root node of the Brigadier command.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * @Command(name = "example")
 * public void exampleCommand(LiteralArgumentBuilder<CommandSourceStack> builder) {
 *     builder.executes(ctx -> {
 *         return 1;
 *     });
 * }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    int SUCCESS = 1;
    int FAILURE = 0;

    /**
     * The name of the command. This becomes the literal root node in the Brigadier tree.
     *
     * @return the command name
     */
    String name();
    String[] aliases() default {};
}
