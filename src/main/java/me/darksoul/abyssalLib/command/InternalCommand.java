package me.darksoul.abyssalLib.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import me.darksoul.abyssalLib.item.Item;
import me.darksoul.abyssalLib.registry.BuiltinRegistries;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class InternalCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("abyssallib")
                .then(Commands.literal("give")
                        .then(Commands.argument("namespace_id", ArgumentTypes.namespacedKey())
                                .requires(sender -> sender
                                        .getSender().hasPermission("abyssallib.admin.give"))
                                .suggests(InternalCommand::giveSuggests)
                                .executes(InternalCommand::giveExecutor)));
    }

    private static int giveExecutor(CommandContext<CommandSourceStack> ctx) {
        NamespacedKey namespaceId = ctx.getArgument("namespace_id", NamespacedKey.class);
        CommandSender sender = ctx.getSource().getSender();
        Entity executor = ctx.getSource().getExecutor();

        if (!(executor instanceof Player player)) {
            sender.sendPlainMessage("Only a player can run this command!");
            return Command.SINGLE_SUCCESS;
        }
        if (!BuiltinRegistries.ITEMS.contains(namespaceId.asString())) {
            sender.sendPlainMessage("Not an item");
            return Command.SINGLE_SUCCESS;
        }

        player.getInventory().addItem(BuiltinRegistries.ITEMS.get(namespaceId.asString()));

        return Command.SINGLE_SUCCESS;
    }

    public static CompletableFuture<Suggestions> giveSuggests(final CommandContext<CommandSourceStack> ctx,
                                                              final SuggestionsBuilder builder) {
        for (Item item: BuiltinRegistries.ITEMS.getAll()) {
            builder.suggest(item.getId().toString());
        }
        return builder.buildFuture();
    }
}
