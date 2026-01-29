package com.github.darksoulq.abyssallib.server.command.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.color.ColorProvider;
import com.github.darksoulq.abyssallib.common.util.Easings;
import com.github.darksoulq.abyssallib.common.util.FileUtils;
import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.command.Command;
import com.github.darksoulq.abyssallib.server.command.CommandBus;
import com.github.darksoulq.abyssallib.server.command.DefaultConditions;
import com.github.darksoulq.abyssallib.server.event.custom.entity.CustomEntitySpawnEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.resource.ResourcePack;
import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.server.translation.ServerTranslator;
import com.github.darksoulq.abyssallib.server.util.PermissionConstants;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import com.github.darksoulq.abyssallib.world.dialog.DialogContent;
import com.github.darksoulq.abyssallib.world.dialog.Dialogs;
import com.github.darksoulq.abyssallib.world.dialog.Notice;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import com.github.darksoulq.abyssallib.world.entity.data.EntityAttributes;
import com.github.darksoulq.abyssallib.world.gui.internal.ItemMenu;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.particle.Particles;
import com.github.darksoulq.abyssallib.world.particle.impl.Generators;
import com.github.darksoulq.abyssallib.world.particle.impl.Renderers;
import com.github.darksoulq.abyssallib.world.particle.timeline.Animations;
import com.github.darksoulq.abyssallib.world.particle.timeline.Timeline;
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
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.github.darksoulq.abyssallib.server.resource.ResourcePack.HASH_MAP;
import static com.github.darksoulq.abyssallib.server.resource.ResourcePack.UUID_MAP;

public class InternalCommand {

    @Command(name = "abyssallib")
    public void register(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(Commands.literal("give")
            .requires(DefaultConditions.hasPerm(PermissionConstants.Items.GIVE)
            ).then(Commands.argument("namespace_id", ArgumentTypes.namespacedKey())
                .suggests(InternalCommand::giveSuggests)
                .executes(InternalCommand::giveOneExecutor)
                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                    .executes(InternalCommand::giveMultiExecutor)
                )
            )
        ).then(Commands.literal("attribute")
            .requires(DefaultConditions.hasPerm(PermissionConstants.Attributes.GET))
            .then(Commands.literal("get")
                .then(Commands.argument("selector", ArgumentTypes.entity())
                    .then(Commands.argument("type", ArgumentTypes.namespacedKey())
                        .suggests(InternalCommand::attributeTypeSuggests)
                        .executes(InternalCommand::attributeGetExecutor)
                    )
                )
            )
        ).then(Commands.literal("summon")
            .requires(DefaultConditions.hasPerm(PermissionConstants.Entity.SUMMON))
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
                .requires(DefaultConditions.hasAnyPerm(PermissionConstants.Statistics.VIEW_SELF, PermissionConstants.Statistics.VIEW_ALL))
                .executes(InternalCommand::getSelfStatistics)
                .then(Commands.argument("player", ArgumentTypes.player())
                    .requires(DefaultConditions.hasPerm(PermissionConstants.Statistics.VIEW_ALL))
                    .executes(InternalCommand::getOtherStatistics))
            )
            .then(Commands.literal("view")
                .requires(DefaultConditions.hasAnyPerm(PermissionConstants.Statistics.MENU_SELF, PermissionConstants.Statistics.MENU_ALL))
                .executes(InternalCommand::getSelfStatisticsMenu)
                .then(Commands.argument("player", ArgumentTypes.player())
                    .requires(ctx -> {
                        org.bukkit.entity.Entity sender = isEntity(ctx);
                        if (sender == null) return false;
                        return sender.hasPermission(PermissionConstants.Statistics.MENU_ALL);
                    })
                    .executes(InternalCommand::getOtherStatisticsMenu))
            )
        ).then(Commands.literal("reload")
            .requires(DefaultConditions.hasPerm(PermissionConstants.Other.RELOAD))
            .then(Commands.literal("commmands")
                .executes(ctx -> {
                    CommandBus.reloadAll();
                    return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                })
            )
            .then(Commands.literal("pack")
                .executes(ctx -> {
                    refreshInternalPacks();
                    List<ResourcePackInfo> rps = new ArrayList<>();
                    loadRPInfos(rps);
                    if (!rps.isEmpty()) {
                        Bukkit.getServer().sendResourcePacks(ResourcePackRequest.resourcePackRequest()
                            .packs(rps)
                            .build()
                        );
                    }
                    ctx.getSource().getSender().sendRichMessage("<red>Reload complete</red>");
                    return 0;
                })
            )
            .then(Commands.literal("translations")
                .executes(ctx -> {
                    ServerTranslator.reload();
                    return 0;
                })
            )
        ).then(Commands.literal("content")
            .then(Commands.literal("items")
                .requires(DefaultConditions.hasPerm(PermissionConstants.Content.ITEMS_VIEW))
                .executes(ctx -> {
                    if (!(ctx.getSource().getExecutor() instanceof Player player)) {
                        ctx.getSource().getSender().sendRichMessage("<red>Only players can run this command</red>");
                        return 0;
                    }
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
                        }
                        String ns = ctx.getArgument("plugin", String.class);
                        Item icon = Registries.ITEMS.get(ns + ":plugin_icon");
                        if (icon == null) return Command.SUCCESS;
                        ItemMenu.open(player, ns);
                        return Command.SUCCESS;
                    })
                )
            )
        );
    }

    public static void loadRPInfos(List<ResourcePackInfo> rps) {
        AbyssalLib.PACK_SERVER.loadThirdPartyPacks();
        for (String pluginId : AbyssalLib.PACK_SERVER.registeredPluginIDs()) {
            rps.add(ResourcePackInfo.resourcePackInfo()
                .id(UUID_MAP.get(pluginId))
                .uri(URI.create(AbyssalLib.PACK_SERVER.getUrl(pluginId)))
                .hash(HASH_MAP.get(pluginId))
                .build()
            );
        }
        for (String path : ResourcePack.EXTERNAL_CACHE) {
            rps.add(ResourcePackInfo.resourcePackInfo()
                .id(UUID_MAP.get(path))
                .uri(URI.create(AbyssalLib.PACK_SERVER.getUrl(path)))
                .hash(HASH_MAP.get(path))
                .build());
        }
    }
    public static void refreshInternalPacks() {
        for (String id : new HashSet<>(UUID_MAP.keySet())) {
            if (id.startsWith("external_")) continue;

            Path file = AbyssalLib.PACK_SERVER.getPath(id);
            if (file == null || !Files.exists(file)) continue;

            Try.of(() -> FileUtils.sha1(file))
                .onSuccess(hash -> {
                    HASH_MAP.put(id, hash);
                    UUID_MAP.put(id, UUID.randomUUID());
                })
                .onFailure(Throwable::printStackTrace);
        }
    }

    private static int giveItem(CommandContext<CommandSourceStack> ctx, int amount) {
        Player player = getPlayer(ctx);
        if (player == null) return Command.SUCCESS;

        NamespacedKey id = ctx.getArgument("namespace_id", NamespacedKey.class);
        Item item = Registries.ITEMS.get(id.asString());

        if (item == null) {
            reply(ctx, "<red>Not an item</red>");
            return 0;
        }

        player.getInventory().addItem(item.getStack().asQuantity(amount));
        return Command.SUCCESS;
    }

    private static int giveOneExecutor(CommandContext<CommandSourceStack> ctx) {
        return giveItem(ctx, 1);
    }

    private static int giveMultiExecutor(CommandContext<CommandSourceStack> ctx) {
        return giveItem(ctx, ctx.getArgument("amount", int.class));
    }
    public static CompletableFuture<Suggestions> giveSuggests(final CommandContext<CommandSourceStack> ctx,
                                                              final SuggestionsBuilder builder) {
        for (Item item: Registries.ITEMS.getAll().values()) {
            if (item.getId().getPath().equals("plugin_icon")) continue;
            builder.suggest(item.getId().toString());
        }
        return builder.buildFuture();
    }

    public static int attributeGetExecutor(CommandContext<CommandSourceStack> ctx) {
        NamespacedKey key = ctx.getArgument("type", NamespacedKey.class);
        EntitySelectorArgumentResolver selector = ctx.getArgument("selector", EntitySelectorArgumentResolver.class);

        Try.of(() -> selector.resolve(ctx.getSource()))
            .onSuccess(entities -> {
                if (!entities.isEmpty()) {
                    EntityAttributes data = EntityAttributes.of(entities.getFirst());
                    data.load();
                    Map<String, String> attribMap = data.getAllAttributes();
                    if (attribMap.containsKey(key.toString())) {
                        ctx.getSource().getSender().sendMessage(key.toString() + ": " + attribMap.get(key.toString()));
                    }
                }
            })
            .orElseThrow(RuntimeException::new);
        return Command.SUCCESS;
    }
    public static CompletableFuture<Suggestions> attributeTypeSuggests(final CommandContext<CommandSourceStack> ctx,
                                                                       final SuggestionsBuilder builder) {
        EntitySelectorArgumentResolver selector = ctx.getArgument("selector", EntitySelectorArgumentResolver.class);

        return Try.of(() -> selector.resolve(ctx.getSource()))
            .map(entities -> {
                if (!entities.isEmpty()) {
                    EntityAttributes data = EntityAttributes.of(entities.getFirst());
                    data.load();
                    data.getAllAttributes().keySet().forEach(builder::suggest);
                }
                return builder.buildFuture();
            })
            .orElseThrow(RuntimeException::new);
    }

    public int summonExecutor(CommandContext<CommandSourceStack> ctx) throws CloneNotSupportedException, CommandSyntaxException {
        FinePositionResolver position = ctx.getArgument("location", FinePositionResolver.class);
        org.bukkit.entity.Entity sender = isEntity(ctx.getSource());
        if (sender == null) return Command.SUCCESS;
        Location loc = position.resolve(ctx.getSource()).toLocation(sender.getWorld());
        NamespacedKey id = ctx.getArgument("namespace_id", NamespacedKey.class);

        if (!Registries.ENTITIES.contains(id.asString())) {
            sender.sendPlainMessage("Not an entity");
            return Command.SUCCESS;
        }

        Registries.ENTITIES.get(id.asString()).clone().spawn(loc, CustomEntitySpawnEvent.SpawnReason.PLUGIN);
        return Command.SUCCESS;
    }
    public static CompletableFuture<Suggestions> summonSuggests(final CommandContext<CommandSourceStack> ctx,
                                                              final SuggestionsBuilder builder) {
        for (CustomEntity<? extends LivingEntity> entity : Registries.ENTITIES.getAll().values()) {
            builder.suggest(entity.getId().toString());
        }

        return builder.buildFuture();
    }

    private static int sendStats(CommandContext<CommandSourceStack> ctx, Player target) {
        PlayerStatistics stats = PlayerStatistics.of(target);
        if (stats.get().isEmpty()) {
            reply(ctx, "<gray>No statistics found.</gray>");
            return 0;
        }

        String msg = stats.get().stream()
            .map(stat -> {
                String key = "<lang:%s.stat.%s>"
                    .formatted(stat.getId().getNamespace(), stat.getId().getPath());
                return "<aqua>%s</aqua> <gray>=</gray> <yellow>%s</yellow>"
                    .formatted(key, stat.getValue());
            })
            .collect(Collectors.joining("\n"));

        reply(ctx, msg);
        return Command.SUCCESS;
    }

    public static int getSelfStatistics(CommandContext<CommandSourceStack> ctx) {
        Player player = getPlayer(ctx);
        return player == null ? Command.SUCCESS : sendStats(ctx, player);
    }

    public static int getOtherStatistics(CommandContext<CommandSourceStack> ctx) {
        Player target = resolvePlayer(ctx);
        return target == null ? Command.SUCCESS : sendStats(ctx, target);
    }
    public static int getSelfStatisticsMenu(CommandContext<CommandSourceStack> ctx) {
        Player player = getPlayer(ctx);
        if (player != null) {
            player.showDialog(createStatDialog(player));
        }
        return Command.SUCCESS;
    }
    public static int getOtherStatisticsMenu(CommandContext<CommandSourceStack> ctx) {
        Player target = resolvePlayer(ctx);
        Player viewer = getPlayer(ctx);
        if (target != null && viewer != null) {
            viewer.showDialog(createStatDialog(target));
        }
        return Command.SUCCESS;
    }

    private static org.bukkit.entity.Entity isEntity(CommandSourceStack ctx) {
        CommandSender sender = ctx.getExecutor() == null ? ctx.getSender() : ctx.getExecutor();
        if (!(sender instanceof org.bukkit.entity.Entity entity)) return null;
        return entity;
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

    private static void reply(CommandContext<CommandSourceStack> ctx, String message) {
        ctx.getSource().getSender().sendRichMessage(message);
    }
    private static Player getPlayer(CommandContext<CommandSourceStack> ctx) {
        return ctx.getSource().getExecutor() instanceof Player p ? p : null;
    }
    private static Player resolvePlayer(CommandContext<CommandSourceStack> ctx) {
        PlayerSelectorArgumentResolver resolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
        return Try.of(() -> resolver.resolve(ctx.getSource()).getFirst())
            .orElse(null);
    }
}
