package com.github.darksoulq.abyssallib.server.command.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.FileUtils;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.command.Command;
import com.github.darksoulq.abyssallib.server.command.CommandBus;
import com.github.darksoulq.abyssallib.server.command.DefaultConditions;
import com.github.darksoulq.abyssallib.server.command.argument.IdentifierArgument;
import com.github.darksoulq.abyssallib.server.command.argument.RegistryEntryArgument;
import com.github.darksoulq.abyssallib.server.event.custom.entity.CustomEntitySpawnEvent;
import com.github.darksoulq.abyssallib.server.permission.Node;
import com.github.darksoulq.abyssallib.server.permission.PermissionGroup;
import com.github.darksoulq.abyssallib.server.permission.PermissionUser;
import com.github.darksoulq.abyssallib.server.permission.internal.PluginPermissions;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.resource.ResourcePack;
import com.github.darksoulq.abyssallib.server.resource.util.TextOffset;
import com.github.darksoulq.abyssallib.server.translation.ServerTranslator;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import com.github.darksoulq.abyssallib.world.dialog.DialogContent;
import com.github.darksoulq.abyssallib.world.dialog.Dialogs;
import com.github.darksoulq.abyssallib.world.dialog.Notice;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import com.github.darksoulq.abyssallib.world.entity.data.EntityAttributes;
import com.github.darksoulq.abyssallib.world.gui.internal.ItemMenu;
import com.github.darksoulq.abyssallib.world.gui.internal.PermissionMenu;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.ItemCategory;
import com.mojang.brigadier.arguments.BoolArgumentType;
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
import org.bukkit.Location;
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
            .requires(DefaultConditions.hasPerm(PluginPermissions.ITEMS_GIVE))
            .then(Commands.argument("item", RegistryEntryArgument.registryEntry(Registries.ITEMS))
                .executes(InternalCommand::giveOneExecutor)
                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                    .executes(InternalCommand::giveMultiExecutor)
                )
            )
        ).then(Commands.literal("attribute")
            .requires(DefaultConditions.hasPerm(PluginPermissions.ATTRIBUTES_GET))
            .then(Commands.literal("get")
                .then(Commands.argument("selector", ArgumentTypes.entity())
                    .then(Commands.argument("type", IdentifierArgument.identifier())
                        .suggests(InternalCommand::attributeTypeSuggests)
                        .executes(InternalCommand::attributeGetExecutor)
                    )
                )
            )
        ).then(Commands.literal("summon")
            .requires(DefaultConditions.hasPerm(PluginPermissions.ENTITY_SUMMON))
            .then(Commands.argument("location", ArgumentTypes.finePosition(false))
                .then(Commands.argument("entity", RegistryEntryArgument.registryEntry(Registries.ENTITIES))
                    .executes(ctx -> {
                        try {
                            return summonExecutor(ctx);
                        } catch (CloneNotSupportedException e) {
                            throw new RuntimeException(e);
                        }
                    })
                )
            )
        ).then(Commands.literal("statistics")
            .then(Commands.literal("get")
                .requires(DefaultConditions.hasAnyPerm(PluginPermissions.STATISTICS_VIEW_SELF, PluginPermissions.STATISTICS_VIEW_ALL))
                .executes(InternalCommand::getSelfStatistics)
                .then(Commands.argument("player", ArgumentTypes.player())
                    .requires(DefaultConditions.hasPerm(PluginPermissions.STATISTICS_VIEW_ALL))
                    .executes(InternalCommand::getOtherStatistics))
            )
            .then(Commands.literal("view")
                .requires(DefaultConditions.hasAnyPerm(PluginPermissions.STATISTICS_MENU_SELF, PluginPermissions.STATISTICS_MENU_ALL))
                .executes(InternalCommand::getSelfStatisticsMenu)
                .then(Commands.argument("player", ArgumentTypes.player())
                    .requires(ctx -> {
                        org.bukkit.entity.Entity sender = isEntity(ctx);
                        if (sender == null) return false;
                        return sender.hasPermission(PluginPermissions.STATISTICS_MENU_ALL.get().getNode());
                    })
                    .executes(InternalCommand::getOtherStatisticsMenu))
            )
        ).then(Commands.literal("reload")
            .requires(DefaultConditions.hasPerm(PluginPermissions.RELOAD))
            .then(Commands.literal("commands")
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
                    reply(ctx, "<green>Translations reloaded</green>");
                    return 0;
                })
            )
        ).then(Commands.literal("content")
            .then(Commands.literal("items")
                .requires(DefaultConditions.hasPerm(PluginPermissions.CONTENT_ITEMS_VIEW))
                .executes(ctx -> {
                    if (!(ctx.getSource().getExecutor() instanceof Player player)) {
                        reply(ctx, "<red>Only players can run this command</red>");
                        return 0;
                    }
                    ItemMenu.open(player);
                    return Command.SUCCESS;
                })
                .then(Commands.argument("plugin", StringArgumentType.string())
                    .suggests((ctx, builder) -> {
                        Registries.ITEMS.getAll().keySet().stream()
                            .map(key -> key.split(":")[0])
                            .distinct()
                            .sorted()
                            .forEach(builder::suggest);
                        return builder.buildFuture();
                    })
                    .executes(ctx -> {
                        if (!(ctx.getSource().getExecutor() instanceof Player player)) {
                            reply(ctx, "<red>Only players can run this command</red>");
                            return 0;
                        }
                        String ns = ctx.getArgument("plugin", String.class);
                        ItemMenu.openPlugin(player, ns);
                        return Command.SUCCESS;
                    })
                    .then(Commands.argument("category", StringArgumentType.string())
                        .suggests((ctx, builder) -> {
                            String ns = ctx.getArgument("plugin", String.class);
                            boolean hasCustom = false;
                            for (ItemCategory cat : Registries.ITEM_CATEGORIES.getAll().values()) {
                                if (cat.getId().getNamespace().equals(ns)) {
                                    builder.suggest(cat.getId().getPath());
                                    hasCustom = true;
                                }
                            }
                            if (!hasCustom) {
                                builder.suggest("all");
                            }
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            if (!(ctx.getSource().getExecutor() instanceof Player player)) {
                                reply(ctx, "<red>Only players can run this command</red>");
                                return 0;
                            }
                            String ns = ctx.getArgument("plugin", String.class);
                            String catPath = ctx.getArgument("category", String.class);

                            ItemCategory cat = Registries.ITEM_CATEGORIES.get(ns + ":" + catPath);
                            if (cat != null) {
                                ItemMenu.openCategory(player, cat);
                            } else if (catPath.equals("all")) {
                                ItemMenu.openPlugin(player, ns);
                            } else {
                                reply(ctx, "<red>Category not found.</red>");
                            }
                            return Command.SUCCESS;
                        })
                    )
                )
            )
        ).then(Commands.literal("permissions")
            .requires(DefaultConditions.hasPerm(PluginPermissions.PERMISSIONS_EDIT))
            .then(Commands.literal("gui")
                .executes(ctx -> {
                    if (!(ctx.getSource().getExecutor() instanceof Player p)) return 0;
                    PermissionMenu.openMainMenu(p);
                    return Command.SUCCESS;
                })
            )
            .then(Commands.literal("web")
                .requires(DefaultConditions.hasPerm(PluginPermissions.PERMISSIONS_WEB))
                .executes(ctx -> {
                    if (AbyssalLib.PERMISSION_WEB_SERVER == null || !AbyssalLib.PERMISSION_WEB_SERVER.isEnabled()) {
                        reply(ctx, "<red>Web server is not enabled in config.</red>");
                        return 0;
                    }
                    String token = AbyssalLib.PERMISSION_WEB_SERVER.createSession();
                    String url = AbyssalLib.CONFIG.permissions.webProtocol.get() + "://" + AbyssalLib.CONFIG.permissions.webIp.get() + ":" + AbyssalLib.CONFIG.permissions.webPort.get() + "/?token=" + token;
                    reply(ctx, "<green>Web Editor URL: <click:open_url:'" + url + "'><aqua><u>Click Here</u></aqua></click></green>\n<gray>This link expires in 15 minutes.</gray>");
                    return 1;
                })
            )
            .then(Commands.literal("user")
                .then(Commands.argument("target", StringArgumentType.word())
                    .then(Commands.literal("permission")
                        .then(Commands.literal("set")
                            .then(Commands.argument("node", StringArgumentType.string())
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        UUID target = AbyssalLib.PERMISSION_MANAGER.getUuidFromName(ctx.getArgument("target", String.class));
                                        if (target == null) return 0;
                                        PermissionUser user = AbyssalLib.PERMISSION_MANAGER.getUser(target);
                                        user.setPermission(new Node(ctx.getArgument("node", String.class), ctx.getArgument("value", Boolean.class)));
                                        user.save();
                                        reply(ctx, "<green>Permission set for user.</green>");
                                        return 1;
                                    })
                                )
                            )
                        )
                        .then(Commands.literal("unset")
                            .then(Commands.argument("node", StringArgumentType.string())
                                .executes(ctx -> {
                                    UUID target = AbyssalLib.PERMISSION_MANAGER.getUuidFromName(ctx.getArgument("target", String.class));
                                    if (target == null) return 0;
                                    PermissionUser user = AbyssalLib.PERMISSION_MANAGER.getUser(target);
                                    user.unsetPermission(ctx.getArgument("node", String.class));
                                    user.save();
                                    reply(ctx, "<green>Permission unset for user.</green>");
                                    return 1;
                                })
                            )
                        )
                    )
                    .then(Commands.literal("parent")
                        .then(Commands.literal("add")
                            .then(Commands.argument("group", StringArgumentType.word())
                                .executes(ctx -> {
                                    UUID target = AbyssalLib.PERMISSION_MANAGER.getUuidFromName(ctx.getArgument("target", String.class));
                                    if (target == null) return 0;
                                    PermissionUser user = AbyssalLib.PERMISSION_MANAGER.getUser(target);
                                    user.addParent(new Node(ctx.getArgument("group", String.class)));
                                    user.save();
                                    reply(ctx, "<green>Group added to user.</green>");
                                    return 1;
                                })
                            )
                        )
                        .then(Commands.literal("remove")
                            .then(Commands.argument("group", StringArgumentType.word())
                                .executes(ctx -> {
                                    UUID target = AbyssalLib.PERMISSION_MANAGER.getUuidFromName(ctx.getArgument("target", String.class));
                                    if (target == null) return 0;
                                    PermissionUser user = AbyssalLib.PERMISSION_MANAGER.getUser(target);
                                    user.removeParent(ctx.getArgument("group", String.class));
                                    user.save();
                                    reply(ctx, "<green>Group removed from user.</green>");
                                    return 1;
                                })
                            )
                        )
                    )
                )
            )
            .then(Commands.literal("group")
                .then(Commands.argument("id", StringArgumentType.word())
                    .then(Commands.literal("create")
                        .executes(ctx -> {
                            String id = ctx.getArgument("id", String.class);
                            if (Registries.PERMISSION_GROUPS.contains(id)) {
                                reply(ctx, "<red>Group already exists.</red>");
                                return 0;
                            }
                            PermissionGroup group = new PermissionGroup(id);
                            Registries.PERMISSION_GROUPS.register(id, group);
                            group.save();
                            reply(ctx, "<green>Group created.</green>");
                            return 1;
                        })
                    )
                    .then(Commands.literal("delete")
                        .executes(ctx -> {
                            String id = ctx.getArgument("id", String.class);
                            if (!Registries.PERMISSION_GROUPS.contains(id)) {
                                reply(ctx, "<red>Group does not exist.</red>");
                                return 0;
                            }
                            AbyssalLib.PERMISSION_MANAGER.deleteGroup(id);
                            reply(ctx, "<green>Group deleted.</green>");
                            return 1;
                        })
                    )
                    .then(Commands.literal("setweight")
                        .then(Commands.argument("weight", IntegerArgumentType.integer())
                            .executes(ctx -> {
                                String id = ctx.getArgument("id", String.class);
                                PermissionGroup group = Registries.PERMISSION_GROUPS.get(id);
                                if (group == null) return 0;
                                group.setWeight(ctx.getArgument("weight", Integer.class));
                                group.save();
                                reply(ctx, "<green>Weight set.</green>");
                                return 1;
                            })
                        )
                    )
                    .then(Commands.literal("permission")
                        .then(Commands.literal("set")
                            .then(Commands.argument("node", StringArgumentType.string())
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        String id = ctx.getArgument("id", String.class);
                                        PermissionGroup group = Registries.PERMISSION_GROUPS.get(id);
                                        if (group == null) return 0;
                                        group.setPermission(new Node(ctx.getArgument("node", String.class), ctx.getArgument("value", Boolean.class)));
                                        group.save();
                                        reply(ctx, "<green>Permission set for group.</green>");
                                        return 1;
                                    })
                                )
                            )
                        )
                        .then(Commands.literal("unset")
                            .then(Commands.argument("node", StringArgumentType.string())
                                .executes(ctx -> {
                                    String id = ctx.getArgument("id", String.class);
                                    PermissionGroup group = Registries.PERMISSION_GROUPS.get(id);
                                    if (group == null) return 0;
                                    group.unsetPermission(ctx.getArgument("node", String.class));
                                    group.save();
                                    reply(ctx, "<green>Permission unset for group.</green>");
                                    return 1;
                                })
                            )
                        )
                    )
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

        Item item = ctx.getArgument("item", Item.class);
        player.getInventory().addItem(item.getStack().asQuantity(amount));
        return Command.SUCCESS;
    }

    private static int giveOneExecutor(CommandContext<CommandSourceStack> ctx) {
        return giveItem(ctx, 1);
    }

    private static int giveMultiExecutor(CommandContext<CommandSourceStack> ctx) {
        return giveItem(ctx, ctx.getArgument("amount", int.class));
    }

    public static int attributeGetExecutor(CommandContext<CommandSourceStack> ctx) {
        Identifier key = ctx.getArgument("type", Identifier.class);
        EntitySelectorArgumentResolver selector = ctx.getArgument("selector", EntitySelectorArgumentResolver.class);

        Try.of(() -> selector.resolve(ctx.getSource()))
            .onSuccess(entities -> {
                if (!entities.isEmpty()) {
                    EntityAttributes data = EntityAttributes.of(entities.getFirst());
                    data.load();
                    Map<String, String> attribMap = data.getAllAttributes();
                    if (attribMap.containsKey(key.toString())) {
                        ctx.getSource().getSender().sendMessage(key + ": " + attribMap.get(key.toString()));
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

    @SuppressWarnings("unchecked")
    public int summonExecutor(CommandContext<CommandSourceStack> ctx) throws CloneNotSupportedException, CommandSyntaxException {
        FinePositionResolver position = ctx.getArgument("location", FinePositionResolver.class);
        org.bukkit.entity.Entity sender = isEntity(ctx.getSource());
        if (sender == null) return Command.SUCCESS;
        Location loc = position.resolve(ctx.getSource()).toLocation(sender.getWorld());

        CustomEntity<? extends LivingEntity> entity = ctx.getArgument("entity", CustomEntity.class);
        entity.clone().spawn(loc, CustomEntitySpawnEvent.SpawnReason.PLUGIN);

        return Command.SUCCESS;
    }

    private static int sendStats(CommandContext<CommandSourceStack> ctx, Player target) {
        PlayerStatistics stats = PlayerStatistics.of(target);
        if (stats.get().isEmpty()) {
            reply(ctx, "<gray>No statistics found.</gray>");
            return 0;
        }

        String msg = stats.get().stream()
            .map(stat -> {
                String key = "<lang:%s.stat.%s>".formatted(stat.getId().getNamespace(), stat.getId().getPath());
                return "<aqua>%s</aqua> <gray>=</gray> <yellow>%s</yellow>".formatted(key, stat.getValue());
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
            String langKey = "<lang:%s.stat.%s>".formatted(stat.getId().getNamespace(), stat.getId().getPath());
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