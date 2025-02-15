package me.darksoul.abyssalLib.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.darksoul.abyssalLib.item.AItem;
import me.darksoul.abyssalLib.util.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class ICommands {
    private static final String HELP = "Usage: /abyssallib give <namespace:id>";

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("abyssallib")
                .then(Commands.literal("give")
                        .then(Commands.argument("namespace_id", StringArgumentType.string())
                                .suggests(ICommands::giveSuggests)
                                .executes(ICommands::giveExecutor)));
    }

    private static int giveExecutor(CommandContext<CommandSourceStack> ctx) {
        String namespaceId = StringArgumentType.getString(ctx, "namespace_id");
        namespaceId.replaceAll("'", "");
        CommandSender sender = ctx.getSource().getSender();
        Entity executor = ctx.getSource().getExecutor();

        if (!(executor instanceof Player player)) {
            sender.sendPlainMessage("Only a player can run this command!");
            return Command.SINGLE_SUCCESS;
        }
        if (!AItem.getItemIDsAsString().contains(namespaceId)) {
            sender.sendPlainMessage("Not an item");
            return Command.SINGLE_SUCCESS;
        }

        player.getInventory().addItem(AItem.getAItem(StringUtils.toNamespacedKey(namespaceId)).getItem());

        return Command.SINGLE_SUCCESS;
    }

    public static CompletableFuture<Suggestions> giveSuggests(final CommandContext<CommandSourceStack> ctx,
                                                              final SuggestionsBuilder builder) {
        for (String id: AItem.getItemIDsAsString()) {
            builder.suggest("'" + id + "'");
        }
        return builder.buildFuture();
    }
}
