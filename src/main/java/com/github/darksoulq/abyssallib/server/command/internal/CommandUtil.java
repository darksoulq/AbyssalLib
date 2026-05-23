package com.github.darksoulq.abyssallib.server.command.internal;

import com.github.darksoulq.abyssallib.common.util.Try;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;

public class CommandUtil {

    public static void reply(CommandContext<CommandSourceStack> ctx, String message) {
        ctx.getSource().getSender().sendRichMessage(message);
    }

    public static Player getPlayer(CommandContext<CommandSourceStack> ctx) {
        return ctx.getSource().getExecutor() instanceof Player p ? p : null;
    }

    public static Player resolvePlayer(CommandContext<CommandSourceStack> ctx) {
        PlayerSelectorArgumentResolver resolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
        return Try.of(() -> resolver.resolve(ctx.getSource()).getFirst()).orElse(null);
    }
}