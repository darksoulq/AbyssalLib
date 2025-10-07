package com.github.darksoulq.abyssallib.server.command.internal;

import com.github.darksoulq.abyssallib.server.command.Command;
import com.github.darksoulq.abyssallib.server.command.CommandBus;
import com.github.darksoulq.abyssallib.server.event.custom.entity.EntitySpawnEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.entity.Entity;
import com.github.darksoulq.abyssallib.world.entity.data.EntityAttributes;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import io.papermc.paper.math.FinePosition;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class InternalCommand {

    @Command(name="abyssallib")
    public void register(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(Commands.literal("give")
                        .requires(sender -> sender
                                .getSender().hasPermission("abyssallib.admin.give"))
                        .then(Commands.argument("namespace_id", ArgumentTypes.namespacedKey())
                                .suggests(InternalCommand::giveSuggests)
                                .executes(InternalCommand::giveExecutor)
                        )
                )
                .then(Commands.literal("attribute")
                        .requires(sender -> sender
                                .getSender().hasPermission("abyssallib.admin.attribute"))
                        .then(Commands.literal("get")
                                .then(Commands.argument("selector", ArgumentTypes.entity())
                                        .then(Commands.argument("type", ArgumentTypes.namespacedKey())
                                                .suggests(InternalCommand::attributeTypeSuggests)
                                                .executes(InternalCommand::attributeGetExecutor)
                                        )
                                )
                        )
                )
                .then(Commands.literal("summon")
                        .requires(sender -> sender.getSender().hasPermission("abyssallib.admin.summon"))
                        .then(Commands.argument("location", ArgumentTypes.finePosition(false))
                                .then(Commands.argument("namespace_id", ArgumentTypes.namespacedKey())
                                        .executes(ctx -> {
                                            try {
                                                return summonExecutor(ctx);
                                            } catch (CloneNotSupportedException e) {
                                                throw new RuntimeException(e);
                                            }
                                        })
                                        .suggests(InternalCommand::summonSuggests)
                                )
                        )
                )
                .then(Commands.literal("reload")
                        .requires(sender -> sender
                                .getSender().hasPermission("abyssallib.admin.reload"))
                        .then(Commands.literal("commmands")
                                .executes(ctx -> {
                                    CommandBus.reloadAll();
                                    return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                                })
                        )
                );
    }

    private static int giveExecutor(CommandContext<CommandSourceStack> ctx) {
        NamespacedKey namespaceId = ctx.getArgument("namespace_id", NamespacedKey.class);
        CommandSender sender = ctx.getSource().getSender();
        org.bukkit.entity.Entity executor = ctx.getSource().getExecutor();

        if (!(executor instanceof Player player)) {
            sender.sendPlainMessage("Only a player can run this command!");
            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
        }
        if (!Registries.ITEMS.contains(namespaceId.asString())) {
            sender.sendPlainMessage("Not an item");
            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
        }

        player.getInventory().addItem(Registries.ITEMS.get(namespaceId.asString()).getStack().clone());

        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }
    public static CompletableFuture<Suggestions> giveSuggests(final CommandContext<CommandSourceStack> ctx,
                                                              final SuggestionsBuilder builder) {
        for (Item item: Registries.ITEMS.getAll().values()) {
            builder.suggest(item.getId().toString());
        }
        return builder.buildFuture();
    }

    public static int attributeGetExecutor(CommandContext<CommandSourceStack> ctx) {
        NamespacedKey key = ctx.getArgument("type", NamespacedKey.class);
        EntitySelectorArgumentResolver selector = ctx.getArgument("selector", EntitySelectorArgumentResolver.class);
        List<org.bukkit.entity.Entity> entities;
        try {
            entities = selector.resolve(ctx.getSource());
            if (!entities.isEmpty()) {
                EntityAttributes data = EntityAttributes.of(entities.getFirst());
                data.load();
                Map<String, String> attribMap = data.getAllAttributes();
                if (attribMap.containsKey(key.toString())) {
                    ctx.getSource().getSender().sendMessage(key.toString() + ": " + attribMap.get(key.toString()));
                }
            }
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }
    public static CompletableFuture<Suggestions> attributeTypeSuggests(final CommandContext<CommandSourceStack> ctx,
                                                                       final SuggestionsBuilder builder) {
        EntitySelectorArgumentResolver selector = ctx.getArgument("selector", EntitySelectorArgumentResolver.class);
        try {
            List<org.bukkit.entity.Entity> entities = selector.resolve(ctx.getSource());
            if (!entities.isEmpty()) {
                EntityAttributes data = EntityAttributes.of(entities.getFirst());
                data.load();
                for (String attrib : data.getAllAttributes().keySet()) {
                    builder.suggest(attrib);
                }
                return builder.buildFuture();
            }
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        return builder.buildFuture();
    }

    public static int summonExecutor(CommandContext<CommandSourceStack> ctx) throws CloneNotSupportedException, CommandSyntaxException {
        FinePositionResolver position = ctx.getArgument("location", FinePositionResolver.class);
        Location loc = position.resolve(ctx.getSource()).toLocation(ctx.getSource().getExecutor().getWorld());
        NamespacedKey id = ctx.getArgument("namespace_id", NamespacedKey.class);

        if (!Registries.ENTITIES.contains(id.asString())) {
            ctx.getSource().getExecutor().sendPlainMessage("Not an entity");
            return com.mojang.brigadier.Command.SINGLE_SUCCESS;
        }

        Registries.ENTITIES.get(id.asString()).clone().spawn(loc, EntitySpawnEvent.SpawnReason.PLUGIN);
        return Command.SUCCESS;
    }
    public static CompletableFuture<Suggestions> summonSuggests(final CommandContext<CommandSourceStack> ctx,
                                                              final SuggestionsBuilder builder) {
        for (Entity<? extends LivingEntity> entity : Registries.ENTITIES.getAll().values()) {
            builder.suggest(entity.getId().toString());
        }

        return builder.buildFuture();
    }
}
