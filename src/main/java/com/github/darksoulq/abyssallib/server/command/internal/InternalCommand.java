package com.github.darksoulq.abyssallib.server.command.internal;

import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.command.Command;
import com.github.darksoulq.abyssallib.server.command.CommandBus;
import com.github.darksoulq.abyssallib.server.event.custom.entity.EntitySpawnEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import com.github.darksoulq.abyssallib.world.dialog.DialogContent;
import com.github.darksoulq.abyssallib.world.dialog.Dialogs;
import com.github.darksoulq.abyssallib.world.dialog.Notice;
import com.github.darksoulq.abyssallib.world.entity.Entity;
import com.github.darksoulq.abyssallib.world.entity.data.EntityAttributes;
import com.github.darksoulq.abyssallib.world.gui.GuiManager;
import com.github.darksoulq.abyssallib.world.gui.internal.ItemMenu;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
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
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.dialog.Dialog;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class InternalCommand {

    @Command(name="abyssallib")
    public void register(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(Commands.literal("give")
                        .requires(sender -> sender
                                .getSender().hasPermission("abyssallib.admin.give")
                        ).then(Commands.argument("namespace_id", ArgumentTypes.namespacedKey())
                                .suggests(InternalCommand::giveSuggests)
                                .executes(InternalCommand::giveOneExecutor)
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(InternalCommand::giveMultiExecutor)
                                )
                        )
                ).then(Commands.literal("attribute")
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
                ).then(Commands.literal("summon")
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
                ).then(Commands.literal("statistics")
                        .then(Commands.literal("get")
                                .requires(ctx -> ctx.getSender().hasPermission("abyssallib.player.statistic_self")
                                        || ctx.getSender().hasPermission("abyssallib.admin.statistic_all"))
                                .executes(InternalCommand::getSelfStatistics)
                                .then(Commands.argument("player", ArgumentTypes.player())
                                        .requires(ctx -> ctx.getSender().hasPermission("abyssallib.admin.statistic_all"))
                                        .executes(InternalCommand::getOtherStatistics))
                        )
                        .then(Commands.literal("view")
                                .requires(ctx -> {
                                    org.bukkit.entity.Entity sender = isEntity(ctx);
                                    if (sender == null) return false;
                                    return sender.hasPermission("abyssallib.player.statistic_self.menu")
                                            || sender.hasPermission("abyssallib.admin.statistic_all.menu");
                                })
                                .executes(InternalCommand::getSelfStatisticsMenu)
                                .then(Commands.argument("player", ArgumentTypes.player())
                                        .requires(ctx -> {
                                            org.bukkit.entity.Entity sender = isEntity(ctx);
                                            if (sender == null) return false;
                                            return sender.hasPermission("abyssallib.admin.statistic_all.menu");
                                        })
                                        .executes(InternalCommand::getOtherStatisticsMenu))
                        )
                ).then(Commands.literal("reload")
                        .requires(sender -> sender
                                .getSender().hasPermission("abyssallib.admin.reload"))
                        .then(Commands.literal("commmands")
                                .executes(ctx -> {
                                    CommandBus.reloadAll();
                                    return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                                })
                        )
                ).then(Commands.literal("content")
                        .then(Commands.literal("items")
                                .executes(ctx -> {
                                    if (!(ctx.getSource().getExecutor() instanceof Player player)) {
                                        ctx.getSource().getSender().sendRichMessage("<red>Only players can run this command</red>");
                                        return 0;
                                    };
                                    ItemMenu.open(player);
                                    return Command.SUCCESS;
                                })
                                .then(Commands.argument("plugin", StringArgumentType.string())
                                        .suggests((ctx, builder) -> {
                                            Set<String> sortedNamespaces = Registries.ITEMS.getAll().keySet().stream()
                                                    .map(key -> key.split(":")[0])
                                                    .sorted()
                                                    .collect(Collectors.toCollection(LinkedHashSet::new));
                                            sortedNamespaces.forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> {
                                            if (!(ctx.getSource().getExecutor() instanceof Player player)) {
                                                ctx.getSource().getSender().sendRichMessage("<red>Only players can run this command</red>");
                                                return 0;
                                            };
                                            String ns = ctx.getArgument("plugin", String.class);
                                            Item icon = Registries.ITEMS.get(ns + ":plugin_icon");
                                            if (icon == null) return Command.SUCCESS;
                                            ItemMenu.open(player, ns);
                                            return Command.SUCCESS;
                                        }))
                        )
                );
    }

    private static int giveOneExecutor(CommandContext<CommandSourceStack> ctx) {
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

        return Command.SUCCESS;
    }
    private static int giveMultiExecutor(CommandContext<CommandSourceStack> ctx) {
        int amount = ctx.getArgument("amount", int.class);
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

        player.getInventory().addItem(Registries.ITEMS.get(namespaceId.asString()).getStack().asQuantity(amount));

        return Command.SUCCESS;
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
        return Command.SUCCESS;
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
        org.bukkit.entity.Entity sender = isEntity(ctx.getSource());
        if (sender == null) return Command.SUCCESS;
        Location loc = position.resolve(ctx.getSource()).toLocation(sender.getWorld());
        NamespacedKey id = ctx.getArgument("namespace_id", NamespacedKey.class);

        if (!Registries.ENTITIES.contains(id.asString())) {
            sender.sendPlainMessage("Not an entity");
            return Command.SUCCESS;
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

    public static int getSelfStatistics(CommandContext<CommandSourceStack> ctx) {
        org.bukkit.entity.Entity sender = isEntity(ctx.getSource());
        if (!(sender instanceof Player player)) return Command.SUCCESS;
        handleSendStats(player, ctx);
        return Command.SUCCESS;
    }
    public static int getOtherStatistics(CommandContext<CommandSourceStack> ctx) {
        PlayerSelectorArgumentResolver playerResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
        Player player = Try.get(() -> playerResolver.resolve(ctx.getSource()).getFirst(), (Player) null);
        if (player == null) return Command.SUCCESS;
        handleSendStats(player, ctx);
        return Command.SUCCESS;
    }
    public static int getSelfStatisticsMenu(CommandContext<CommandSourceStack> ctx) {
        org.bukkit.entity.Entity sender = isEntity(ctx.getSource());
        if (!(sender instanceof Player player)) return Command.SUCCESS;
        player.showDialog(createStatDialog(player));
        return Command.SUCCESS;
    }
    public static int getOtherStatisticsMenu(CommandContext<CommandSourceStack> ctx) {
        PlayerSelectorArgumentResolver playerResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
        Player player = Try.get(() -> playerResolver.resolve(ctx.getSource()).getFirst(), (Player) null);
        if (player == null) return Command.SUCCESS;
        org.bukkit.entity.Entity entity = isEntity(ctx.getSource());
        if (!(entity instanceof Player viewer)) return Command.SUCCESS;
        viewer.showDialog(createStatDialog(player));
        return Command.SUCCESS;
    }

    private static org.bukkit.entity.Entity isEntity(CommandSourceStack ctx) {
        CommandSender sender = ctx.getExecutor() == null ? ctx.getSender() : ctx.getExecutor();
        if (!(sender instanceof org.bukkit.entity.Entity entity)) return null;
        return entity;
    }
    private static void handleSendStats(Player player, CommandContext<CommandSourceStack> ctx) {
        PlayerStatistics stats = PlayerStatistics.of(player);
        StringBuilder sb = new StringBuilder();
        List<Statistic> list = stats.get();
        if (list.isEmpty()) {
            sb.append("<gray>No statistics found.</gray>");
        }

        for (Statistic stat : list) {
            String langKey = "<lang:%s.stat.%s>"
                    .formatted(stat.getId().getNamespace(), stat.getId().getPath());

            sb.append("<aqua>")
                    .append(langKey)
                    .append("</aqua> <gray>=</gray> <yellow>")
                    .append(stat.getValue())
                    .append("</yellow>\n");
        }
        ctx.getSource().getSender().sendRichMessage(sb.toString());
    }
    private static Dialog createStatDialog(Player player) {
        Notice dialog = Dialogs.notice(TextUtil.parse("Statistics"));
        PlayerStatistics stats = PlayerStatistics.of(player);
        if (stats.get().isEmpty()) dialog.body(DialogContent.text(TextUtil.parse("<gray>No statistics found.</gray>")));
        for (Statistic stat : stats.get()) {
            String langKey = "<lang:%s.stat.%s>"
                    .formatted(stat.getId().getNamespace(), stat.getId().getPath());

            dialog.body(DialogContent.text(TextUtil.parse(TextOffset.getOffsetMinimessage(40) + langKey + " = " + stat.getValue())));
        }
        return dialog.build();
    }
}
