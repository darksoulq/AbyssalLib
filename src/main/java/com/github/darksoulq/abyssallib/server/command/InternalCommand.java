package com.github.darksoulq.abyssallib.server.command;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.config.legacy.Config;
import com.github.darksoulq.abyssallib.server.registry.BuiltinRegistries;
import com.github.darksoulq.abyssallib.world.level.inventory.gui.builtin.ModMenu;
import com.github.darksoulq.abyssallib.world.level.item.Item;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class InternalCommand {

    @AbyssalCommand(name="abyssallib")
    public void register(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(Commands.literal("give")
                        .then(Commands.argument("namespace_id", ArgumentTypes.namespacedKey())
                                .requires(sender -> sender
                                        .getSender().hasPermission("abyssallib.admin.give"))
                                .suggests(InternalCommand::giveSuggests)
                                .executes(InternalCommand::giveExecutor)
                        )
                )
                .then(Commands.literal("reload")
                        .then(Commands.literal("config")
                                .executes((ctz) -> {
                                    Config.reloadAll();
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        .then(Commands.literal("commmands")
                                .executes(ctx -> {
                                    CommandBus.INSTANCE.reloadAll();
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("modmenu")
                        .executes(ctx -> {
                            AbyssalLib.GUI_MANAGER.openGui((Player) ctx.getSource().getSender(), new ModMenu());
                            return Command.SINGLE_SUCCESS;
                        })
                );
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

        player.getInventory().addItem(BuiltinRegistries.ITEMS.get(namespaceId.asString()).stack());

        return Command.SINGLE_SUCCESS;
    }

    public static CompletableFuture<Suggestions> giveSuggests(final CommandContext<CommandSourceStack> ctx,
                                                              final SuggestionsBuilder builder) {
        for (Item item: BuiltinRegistries.ITEMS.getAll().values()) {
            builder.suggest(item.getId().toString());
        }
        return builder.buildFuture();
    }
}
