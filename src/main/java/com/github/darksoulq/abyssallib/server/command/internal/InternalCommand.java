package com.github.darksoulq.abyssallib.server.command.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.SavedEntity;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.common.serialization.ops.NbtOps;
import com.github.darksoulq.abyssallib.common.serialization.ops.YamlOps;
import com.github.darksoulq.abyssallib.common.util.FileUtils;
import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.command.Command;
import com.github.darksoulq.abyssallib.server.command.CommandBus;
import com.github.darksoulq.abyssallib.server.command.DefaultConditions;
import com.github.darksoulq.abyssallib.server.command.argument.EnumArgument;
import com.github.darksoulq.abyssallib.server.command.argument.NbtCompoundArgument;
import com.github.darksoulq.abyssallib.server.command.argument.RegistryEntryArgument;
import com.github.darksoulq.abyssallib.server.event.custom.entity.CustomEntitySpawnEvent;
import com.github.darksoulq.abyssallib.server.permission.Node;
import com.github.darksoulq.abyssallib.server.permission.PermissionGroup;
import com.github.darksoulq.abyssallib.server.permission.PermissionUser;
import com.github.darksoulq.abyssallib.server.permission.internal.PluginPermissions;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.resource.ResourcePack;
import com.github.darksoulq.abyssallib.server.translation.ServerTranslator;
import com.github.darksoulq.abyssallib.world.advancement.AdvancementFrame;
import com.github.darksoulq.abyssallib.world.advancement.Toast;
import com.github.darksoulq.abyssallib.world.data.attribute.EntityAttributes;
import com.github.darksoulq.abyssallib.world.data.loot.LootTable;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatisticMenu;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import com.github.darksoulq.abyssallib.world.data.statistic.StatisticFormatter;
import com.github.darksoulq.abyssallib.world.data.statistic.formatter.DefaultStatisticFormatter;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import com.github.darksoulq.abyssallib.world.gen.internal.StructureLocator;
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
import net.kyori.adventure.key.Key;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.darksoulq.abyssallib.server.resource.ResourcePack.HASH_MAP;
import static com.github.darksoulq.abyssallib.server.resource.ResourcePack.UUID_MAP;

public class InternalCommand {

    public enum ExportFormat {
        NBT, JSON, YAML
    }

    @Command(name = "abyssallib")
    public void register(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(Commands.literal("give")
            .requires(DefaultConditions.hasPerm(PluginPermissions.ITEMS_GIVE))
            .then(Commands.argument("targets", ArgumentTypes.players())
                .then(Commands.argument("item", RegistryEntryArgument.registryEntry(Registries.ITEMS))
                    .executes(ctx -> giveMultiExecutor(ctx, 1))
                    .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> giveMultiExecutor(ctx, ctx.getArgument("amount", Integer.class)))
                    )
                )
            )
        ).then(Commands.literal("attribute")
            .requires(DefaultConditions.hasPerm(PluginPermissions.ATTRIBUTES_GET))
            .then(Commands.literal("get")
                .then(Commands.argument("selector", ArgumentTypes.entity())
                    .then(Commands.argument("type", ArgumentTypes.key())
                        .suggests(InternalCommand::attributeTypeSuggests)
                        .executes(InternalCommand::attributeGetExecutor)
                    )
                )
            )
        ).then(Commands.literal("locate")
            .requires(DefaultConditions.hasPerm(PluginPermissions.LOCATE))
            .then(Commands.literal("structure")
                .then(Commands.argument("structure", ArgumentTypes.key())
                    .suggests((ctx, builder) -> {
                        Registries.STRUCTURES.getAll().keySet().forEach(builder::suggest);
                        return builder.buildFuture();
                    })
                    .executes(ctx -> locateExecutor(ctx, null))
                    .then(Commands.argument("location", ArgumentTypes.finePosition(false))
                        .executes(ctx -> locateExecutor(ctx, ctx.getArgument("location", FinePositionResolver.class)))
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
                        } catch (CloneNotSupportedException | CommandSyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    })
                )
            )
        ).then(Commands.literal("loot")
            .requires(DefaultConditions.hasPerm(PluginPermissions.LOOT_SET))
            .then(Commands.literal("set")
                .then(Commands.argument("table", RegistryEntryArgument.registryEntry(Registries.LOOT_TABLES))
                    .executes(InternalCommand::setLootTableLookingExecutor)
                )
            )
            .then(Commands.literal("at")
                .then(Commands.argument("location", ArgumentTypes.finePosition(false))
                    .then(Commands.argument("table", RegistryEntryArgument.registryEntry(Registries.LOOT_TABLES))
                        .executes(InternalCommand::setLootTableExecutor)
                    )
                )
            )
        ).then(Commands.literal("toast")
            .requires(DefaultConditions.hasPerm(PluginPermissions.TOAST_SEND))
            .then(Commands.argument("targets", ArgumentTypes.players())
                .then(Commands.literal("held")
                    .requires(DefaultConditions.playerOnly())
                    .then(Commands.argument("frame", EnumArgument.enumArg(AdvancementFrame.class))
                        .then(Commands.argument("title", StringArgumentType.string())
                            .executes(ctx -> sendToastExecutor(ctx, true, false))
                            .then(Commands.argument("subtitle", StringArgumentType.string())
                                .executes(ctx -> sendToastExecutor(ctx, true, true))
                            )
                        )
                    )
                )
                .then(Commands.literal("custom")
                    .then(Commands.argument("frame", EnumArgument.enumArg(AdvancementFrame.class))
                        .then(Commands.argument("title", StringArgumentType.string())
                            .then(Commands.argument("nbt", NbtCompoundArgument.nbt())
                                .executes(ctx -> sendToastExecutor(ctx, false, false))
                            )
                            .then(Commands.argument("subtitle", StringArgumentType.string())
                                .then(Commands.argument("nbt", NbtCompoundArgument.nbt())
                                    .executes(ctx -> sendToastExecutor(ctx, false, true))
                                )
                            )
                        )
                    )
                )
            )
        ).then(Commands.literal("serialize")
            .then(Commands.literal("item")
                .requires(DefaultConditions.playerOnly().and(DefaultConditions.hasPerm(PluginPermissions.SERIALIZE_ITEM)))
                .then(Commands.argument("format", EnumArgument.enumArg(ExportFormat.class))
                    .executes(InternalCommand::serializeItemExecutor)
                )
            )
            .then(Commands.literal("block")
                .requires(DefaultConditions.playerOnly().and(DefaultConditions.hasPerm(PluginPermissions.SERIALIZE_BLOCK)))
                .then(Commands.argument("format", EnumArgument.enumArg(ExportFormat.class))
                    .executes(InternalCommand::serializeBlockExecutor)
                )
            )
            .then(Commands.literal("entity")
                .requires(DefaultConditions.playerOnly().and(DefaultConditions.hasPerm(PluginPermissions.SERIALIZE_ENTITY)))
                .then(Commands.argument("format", EnumArgument.enumArg(ExportFormat.class))
                    .executes(InternalCommand::serializeEntityExecutor)
                )
            )
        ).then(Commands.literal("statistics")
            .then(Commands.literal("get")
                .requires(DefaultConditions.hasAnyPerm(PluginPermissions.STATISTICS_VIEW_SELF, PluginPermissions.STATISTICS_VIEW_ALL))
                .executes(InternalCommand::getSelfStatistics)
                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                    .executes(InternalCommand::getSelfStatistics)
                )
                .then(Commands.argument("player", ArgumentTypes.player())
                    .requires(DefaultConditions.hasPerm(PluginPermissions.STATISTICS_VIEW_ALL))
                    .executes(InternalCommand::getOtherStatistics)
                    .then(Commands.argument("page", IntegerArgumentType.integer(1))
                        .executes(InternalCommand::getOtherStatistics)
                    )
                )
            )
            .then(Commands.literal("view")
                .requires(DefaultConditions.hasAnyPerm(PluginPermissions.STATISTICS_MENU_SELF, PluginPermissions.STATISTICS_MENU_ALL))
                .executes(InternalCommand::getSelfStatisticsMenu)
                .then(Commands.argument("player", ArgumentTypes.player())
                    .requires(DefaultConditions.hasPerm(PluginPermissions.STATISTICS_VIEW_ALL))
                    .executes(InternalCommand::getOtherStatisticsMenu))
            )
        ).then(Commands.literal("reload")
            .requires(DefaultConditions.hasPerm(PluginPermissions.RELOAD))
            .then(Commands.literal("commands")
                .executes(ctx -> {
                    CommandBus.reloadAll();
                    return Command.SUCCESS;
                })
            )
            .then(Commands.literal("pack")
                .executes(ctx -> {
                    refreshInternalPacks();
                    List<ResourcePackInfo> rps = new ArrayList<>();
                    loadRPInfos(rps, true);
                    if (!rps.isEmpty()) {
                        Bukkit.getServer().sendResourcePacks(ResourcePackRequest.resourcePackRequest()
                            .packs(rps)
                            .build()
                        );
                    }
                    ctx.getSource().getSender().sendRichMessage("<red>Reload complete</red>");
                    return Command.SUCCESS;
                })
            )
            .then(Commands.literal("translations")
                .executes(ctx -> {
                    ServerTranslator.reload();
                    reply(ctx, "<green>Translations reloaded</green>");
                    return Command.SUCCESS;
                })
            )
        ).then(Commands.literal("content")
            .requires(DefaultConditions.hasPerm(PluginPermissions.CONTENT_ITEMS_VIEW))
            .then(Commands.literal("items")
                .executes(ctx -> {
                    Player player = getPlayer(ctx);
                    if (player == null) return Command.FAILURE;
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
                        Player player = getPlayer(ctx);
                        if (player == null) return Command.FAILURE;
                        String ns = ctx.getArgument("plugin", String.class);
                        ItemMenu.openPlugin(player, ns);
                        return Command.SUCCESS;
                    })
                    .then(Commands.argument("category", StringArgumentType.string())
                        .suggests((ctx, builder) -> {
                            String ns = ctx.getArgument("plugin", String.class);
                            boolean hasCustom = false;
                            for (ItemCategory cat : Registries.ITEM_CATEGORIES.getAll().values()) {
                                if (cat.getId().namespace().equals(ns)) {
                                    builder.suggest(cat.getId().value());
                                    hasCustom = true;
                                }
                            }
                            if (!hasCustom) builder.suggest("all");
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            Player player = getPlayer(ctx);
                            if (player == null) return Command.FAILURE;
                            String ns = ctx.getArgument("plugin", String.class);
                            String catPath = ctx.getArgument("category", String.class);

                            ItemCategory cat = Registries.ITEM_CATEGORIES.get(ns + ":" + catPath);
                            if (cat != null) {
                                ItemMenu.openCategory(player, cat);
                            } else if (catPath.equals("all")) {
                                ItemMenu.openPlugin(player, ns);
                            } else {
                                reply(ctx, "<red>Category not found.</red>");
                                return Command.FAILURE;
                            }
                            return Command.SUCCESS;
                        })
                    )
                )
            )
        ).then(Commands.literal("permissions")
            .requires(DefaultConditions.hasPerm(PluginPermissions.PERMISSIONS_EDIT).or(DefaultConditions.consoleOnly()))
            .then(Commands.literal("gui")
                .executes(ctx -> {
                    Player p = getPlayer(ctx);
                    if (p == null) return Command.FAILURE;
                    PermissionMenu.openMainMenu(p);
                    return Command.SUCCESS;
                })
                .then(Commands.argument("targets", ArgumentTypes.players())
                    .executes(ctx -> {
                        for (Player target : ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource())) {
                            PermissionMenu.openMainMenu(target);
                        }
                        return Command.SUCCESS;
                    })
                )
            )
            .then(Commands.literal("web")
                .requires(DefaultConditions.hasPerm(PluginPermissions.PERMISSIONS_WEB).or(DefaultConditions.consoleOnly()))
                .executes(ctx -> {
                    if (AbyssalLib.PERMISSION_WEB_SERVER == null || !AbyssalLib.PERMISSION_WEB_SERVER.isEnabled()) {
                        reply(ctx, "<red>Web server is not enabled in config.</red>");
                        return Command.FAILURE;
                    }
                    String token = AbyssalLib.PERMISSION_WEB_SERVER.createSession();
                    String url = AbyssalLib.CONFIG.permissions.webProtocol.get() + "://" + AbyssalLib.CONFIG.permissions.webIp.get() + ":" + AbyssalLib.CONFIG.permissions.webPort.get() + "/?token=" + token;
                    reply(ctx, "<green>Web Editor URL: <click:open_url:'" + url + "'><aqua><u>Click Here</u></aqua></click></green>\n<gray>This link expires in 15 minutes.</gray>");
                    return Command.SUCCESS;
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
                                        if (target == null) return Command.FAILURE;
                                        PermissionUser user = AbyssalLib.PERMISSION_MANAGER.getUser(target);
                                        user.setPermission(new Node(ctx.getArgument("node", String.class), ctx.getArgument("value", Boolean.class)));
                                        user.save();
                                        reply(ctx, "<green>Permission set for user.</green>");
                                        return Command.SUCCESS;
                                    })
                                )
                            )
                        )
                        .then(Commands.literal("unset")
                            .then(Commands.argument("node", StringArgumentType.string())
                                .executes(ctx -> {
                                    UUID target = AbyssalLib.PERMISSION_MANAGER.getUuidFromName(ctx.getArgument("target", String.class));
                                    if (target == null) return Command.FAILURE;
                                    PermissionUser user = AbyssalLib.PERMISSION_MANAGER.getUser(target);
                                    user.unsetPermission(ctx.getArgument("node", String.class));
                                    user.save();
                                    reply(ctx, "<green>Permission unset for user.</green>");
                                    return Command.SUCCESS;
                                })
                            )
                        )
                    )
                    .then(Commands.literal("parent")
                        .then(Commands.literal("add")
                            .then(Commands.argument("group", StringArgumentType.word())
                                .executes(ctx -> {
                                    UUID target = AbyssalLib.PERMISSION_MANAGER.getUuidFromName(ctx.getArgument("target", String.class));
                                    if (target == null) return Command.FAILURE;
                                    PermissionUser user = AbyssalLib.PERMISSION_MANAGER.getUser(target);
                                    user.addParent(new Node(ctx.getArgument("group", String.class)));
                                    user.save();
                                    reply(ctx, "<green>Group added to user.</green>");
                                    return Command.SUCCESS;
                                })
                            )
                        )
                        .then(Commands.literal("remove")
                            .then(Commands.argument("group", StringArgumentType.word())
                                .executes(ctx -> {
                                    UUID target = AbyssalLib.PERMISSION_MANAGER.getUuidFromName(ctx.getArgument("target", String.class));
                                    if (target == null) return Command.FAILURE;
                                    PermissionUser user = AbyssalLib.PERMISSION_MANAGER.getUser(target);
                                    user.removeParent(ctx.getArgument("group", String.class));
                                    user.save();
                                    reply(ctx, "<green>Group removed from user.</green>");
                                    return Command.SUCCESS;
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
                                return Command.FAILURE;
                            }
                            PermissionGroup group = new PermissionGroup(id);
                            Registries.PERMISSION_GROUPS.register(id, group);
                            group.save();
                            reply(ctx, "<green>Group created.</green>");
                            return Command.SUCCESS;
                        })
                    )
                    .then(Commands.literal("delete")
                        .executes(ctx -> {
                            String id = ctx.getArgument("id", String.class);
                            if (!Registries.PERMISSION_GROUPS.contains(id)) {
                                reply(ctx, "<red>Group does not exist.</red>");
                                return Command.FAILURE;
                            }
                            AbyssalLib.PERMISSION_MANAGER.deleteGroup(id);
                            reply(ctx, "<green>Group deleted.</green>");
                            return Command.SUCCESS;
                        })
                    )
                    .then(Commands.literal("setweight")
                        .then(Commands.argument("weight", IntegerArgumentType.integer())
                            .executes(ctx -> {
                                String id = ctx.getArgument("id", String.class);
                                PermissionGroup group = Registries.PERMISSION_GROUPS.get(id);
                                if (group == null) return Command.FAILURE;
                                group.setWeight(ctx.getArgument("weight", Integer.class));
                                group.save();
                                reply(ctx, "<green>Weight set.</green>");
                                return Command.SUCCESS;
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
                                        if (group == null) return Command.FAILURE;
                                        group.setPermission(new Node(ctx.getArgument("node", String.class), ctx.getArgument("value", Boolean.class)));
                                        group.save();
                                        reply(ctx, "<green>Permission set for group.</green>");
                                        return Command.SUCCESS;
                                    })
                                )
                            )
                        )
                        .then(Commands.literal("unset")
                            .then(Commands.argument("node", StringArgumentType.string())
                                .executes(ctx -> {
                                    String id = ctx.getArgument("id", String.class);
                                    PermissionGroup group = Registries.PERMISSION_GROUPS.get(id);
                                    if (group == null) return Command.FAILURE;
                                    group.unsetPermission(ctx.getArgument("node", String.class));
                                    group.save();
                                    reply(ctx, "<green>Permission unset for group.</green>");
                                    return Command.SUCCESS;
                                })
                            )
                        )
                    )
                )
            )
        );
    }

    private static int serializeItemExecutor(CommandContext<CommandSourceStack> ctx) {
        Player player = getPlayer(ctx);
        if (player == null) return Command.FAILURE;

        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.isEmpty()) {
            reply(ctx, "<red>You must be holding an item to serialize.</red>");
            return Command.FAILURE;
        }

        ExportFormat format = ctx.getArgument("format", ExportFormat.class);
        return serializeAndSend(ctx, format, Codecs.ITEM_STACK, held);
    }

    private static int serializeBlockExecutor(CommandContext<CommandSourceStack> ctx) {
        Player player = getPlayer(ctx);
        if (player == null) return Command.FAILURE;

        Block block = player.getTargetBlockExact(10);
        if (block == null) {
            reply(ctx, "<red>You must be looking at a block to serialize.</red>");
            return Command.FAILURE;
        }

        BlockInfo info = BlockInfo.resolve(block);
        ExportFormat format = ctx.getArgument("format", ExportFormat.class);
        return serializeAndSend(ctx, format, ExtraCodecs.BLOCK_INFO, info);
    }

    private static int serializeEntityExecutor(CommandContext<CommandSourceStack> ctx) {
        Player player = getPlayer(ctx);
        if (player == null) return Command.FAILURE;

        Entity target = player.getTargetEntity(10);
        if (target == null) {
            reply(ctx, "<red>You must be looking at an entity to serialize.</red>");
            return Command.FAILURE;
        }

        ExportFormat format = ctx.getArgument("format", ExportFormat.class);
        String result;

        try {
            switch (format) {
                case NBT -> {
                    SavedEntity saved = SavedEntity.create(target, NbtOps.INSTANCE);
                    result = ExtraCodecs.SAVED_ENTITY.encode(NbtOps.INSTANCE, saved).toString();
                }
                case JSON -> {
                    SavedEntity saved = SavedEntity.create(target, JsonOps.INSTANCE);
                    result = JsonOps.INSTANCE.mapper.writeValueAsString(ExtraCodecs.SAVED_ENTITY.encode(JsonOps.INSTANCE, saved));
                }
                case YAML -> {
                    SavedEntity saved = SavedEntity.create(target, YamlOps.INSTANCE);
                    result = YamlOps.dump(ExtraCodecs.SAVED_ENTITY.encode(YamlOps.INSTANCE, saved));
                }
                default -> {
                    return Command.FAILURE;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            reply(ctx, "<red>Failed to serialize entity.</red>");
            return Command.FAILURE;
        }

        reply(ctx, "<green>Entity successfully serialized! <click:copy_to_clipboard:'" + result.replace("'", "\\'") + "'><aqua><u>Click here to copy</u></aqua></click></green>");
        return Command.SUCCESS;
    }

    private static <T> int serializeAndSend(CommandContext<CommandSourceStack> ctx, ExportFormat format, Codec<T> codec, T value) {
        String result;
        try {
            switch (format) {
                case NBT -> result = codec.encode(NbtOps.INSTANCE, value).toString();
                case JSON -> result = JsonOps.INSTANCE.mapper.writeValueAsString(codec.encode(JsonOps.INSTANCE, value));
                case YAML -> result = YamlOps.dump(codec.encode(YamlOps.INSTANCE, value));
                default -> {
                    return Command.FAILURE;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            reply(ctx, "<red>Failed to serialize object.</red>");
            return Command.FAILURE;
        }

        reply(ctx, "<green>Successfully serialized! <click:copy_to_clipboard:'" + result.replace("'", "\\'") + "'><aqua><u>Click here to copy</u></aqua></click></green>");
        return Command.SUCCESS;
    }

    private static int sendToastExecutor(CommandContext<CommandSourceStack> ctx, boolean held, boolean hasSubtitle) throws CommandSyntaxException {
        List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
        ItemStack iconItem;

        if (held) {
            Player player = getPlayer(ctx);
            if (player == null) return Command.FAILURE;
            iconItem = player.getInventory().getItemInMainHand();
        } else {
            CompoundTag nbt = ctx.getArgument("nbt", CompoundTag.class);
            try {
                iconItem = Codecs.ITEM_STACK.decode(NbtOps.INSTANCE, nbt);
            } catch (Exception e) {
                reply(ctx, "<red>Failed to parse custom NBT item.</red>");
                return Command.FAILURE;
            }
        }

        AdvancementFrame advFrame = ctx.getArgument("frame", AdvancementFrame.class);
        String titleStr = ctx.getArgument("title", String.class);
        String subtitleStr = hasSubtitle ? ctx.getArgument("subtitle", String.class) : null;

        for (Player p : targets) {
            Component title = ServerTranslator.parseText(titleStr, p);
            Toast.Builder builder = Toast.builder()
                .titlle(title)
                .icon(iconItem)
                .frame(advFrame);

            if (hasSubtitle) {
                Component subtitle = ServerTranslator.parseText(subtitleStr, p);
                builder.subtitle(subtitle);
            }

            Toast toast = builder.build();
            toast.send(p);
        }

        reply(ctx, "<green>Toast sent to " + targets.size() + " players.</green>");
        return Command.SUCCESS;
    }

    private static int locateExecutor(CommandContext<CommandSourceStack> ctx, FinePositionResolver posResolver) throws CommandSyntaxException {
        Location sourceLoc;
        if (posResolver != null) {
            sourceLoc = posResolver.resolve(ctx.getSource()).toLocation(ctx.getSource().getLocation().getWorld());
        } else {
            sourceLoc = ctx.getSource().getLocation();
        }

        if (sourceLoc.getWorld() == null) return Command.FAILURE;

        Key structureKey = ctx.getArgument("structure", Key.class);
        String structureId = structureKey.asString();

        reply(ctx, "<yellow>Locating " + structureId + "...</yellow>");

        StructureLocator.locate(sourceLoc.getWorld(), structureId, sourceLoc, 100).thenAccept(loc -> {
            if (loc != null) {
                int distance = (int) sourceLoc.distance(loc);
                reply(ctx, "<green>Found " + structureId + " at <aqua><click:suggest_command:'/tp " + loc.getBlockX() + " ~ " + loc.getBlockZ() + "'>" + loc.getBlockX() + " ~ " + loc.getBlockZ() + "</click></aqua> <gray>(" + distance + " blocks away)</gray></green>");
            } else {
                reply(ctx, "<red>Could not find " + structureId + " nearby.</red>");
            }
        });

        return Command.SUCCESS;
    }

    public static void loadRPInfos(List<ResourcePackInfo> rps, boolean reload) {
        if (reload) AbyssalLib.PACK_SERVER.loadThirdPartyPacks();
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

    private static int giveMultiExecutor(CommandContext<CommandSourceStack> ctx, int amount) throws CommandSyntaxException {
        List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
        Item item = ctx.getArgument("item", Item.class);

        for (Player player : targets) {
            player.getInventory().addItem(item.getStack().asQuantity(amount));
        }

        return Command.SUCCESS;
    }

    public static int attributeGetExecutor(CommandContext<CommandSourceStack> ctx) {
        Key key = ctx.getArgument("type", Key.class);
        EntitySelectorArgumentResolver selector = ctx.getArgument("selector", EntitySelectorArgumentResolver.class);

        Try.of(() -> selector.resolve(ctx.getSource()))
            .onSuccess(entities -> {
                if (!entities.isEmpty()) {
                    EntityAttributes data = EntityAttributes.of(entities.getFirst());
                    data.load();
                    Map<String, String> attribMap = data.getAllAttributes();
                    if (attribMap.containsKey(key.toString())) {
                        ctx.getSource().getSender().sendMessage(Component.text(key + ": " + attribMap.get(key.toString())));
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
    public static int summonExecutor(CommandContext<CommandSourceStack> ctx) throws CloneNotSupportedException, CommandSyntaxException {
        FinePositionResolver position = ctx.getArgument("location", FinePositionResolver.class);
        Location sourceLoc = ctx.getSource().getLocation();
        if (sourceLoc.getWorld() == null) return Command.FAILURE;
        Location loc = position.resolve(ctx.getSource()).toLocation(sourceLoc.getWorld());

        CustomEntity<? extends LivingEntity> entity = ctx.getArgument("entity", CustomEntity.class);
        entity.clone().spawn(loc, CustomEntitySpawnEvent.SpawnReason.PLUGIN);

        return Command.SUCCESS;
    }

    private static int setLootTableLookingExecutor(CommandContext<CommandSourceStack> ctx) {
        if (!(ctx.getSource().getExecutor() instanceof Player player)) return Command.FAILURE;

        LootTable table = ctx.getArgument("table", LootTable.class);
        String tableId = Registries.LOOT_TABLES.getId(table);

        Entity targetEntity = player.getTargetEntity(10);
        if (targetEntity != null) {
            PersistentDataContainer pdc = targetEntity.getPersistentDataContainer();
            pdc.set(new NamespacedKey("abyssallib", "custom_loot_table"), PersistentDataType.STRING, tableId);
            reply(ctx, "<green>Loot table injected into entity successfully.</green>");
            return Command.SUCCESS;
        }

        Block block = player.getTargetBlockExact(10);
        if (block != null && block.getState() instanceof Container container) {
            PersistentDataContainer pdc = container.getPersistentDataContainer();
            pdc.set(new NamespacedKey("abyssallib", "loot_table"), PersistentDataType.STRING, tableId);
            pdc.set(new NamespacedKey("abyssallib", "loot_seed"), PersistentDataType.LONG, ThreadLocalRandom.current().nextLong());
            container.update();
            reply(ctx, "<green>Loot table injected into container successfully.</green>");
            return Command.SUCCESS;
        }

        return Command.FAILURE;
    }

    private static int setLootTableExecutor(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        FinePositionResolver position = ctx.getArgument("location", FinePositionResolver.class);
        Location sourceLoc = ctx.getSource().getLocation();
        if (sourceLoc.getWorld() == null) return Command.FAILURE;

        Location loc = position.resolve(ctx.getSource()).toLocation(sourceLoc.getWorld());
        LootTable table = ctx.getArgument("table", LootTable.class);
        String tableId = Registries.LOOT_TABLES.getId(table);

        Block block = loc.getBlock();
        if (block.getState() instanceof Container container) {
            PersistentDataContainer pdc = container.getPersistentDataContainer();
            pdc.set(new NamespacedKey("abyssallib", "loot_table"), PersistentDataType.STRING, tableId);
            pdc.set(new NamespacedKey("abyssallib", "loot_seed"), PersistentDataType.LONG, ThreadLocalRandom.current().nextLong());
            container.update();
            reply(ctx, "<green>Loot table injected into container successfully.</green>");
            return Command.SUCCESS;
        }

        Collection<Entity> entities = loc.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5);
        if (!entities.isEmpty()) {
            Entity target = entities.iterator().next();
            PersistentDataContainer pdc = target.getPersistentDataContainer();
            pdc.set(new NamespacedKey("abyssallib", "custom_loot_table"), PersistentDataType.STRING, tableId);
            reply(ctx, "<green>Loot table injected into entity successfully.</green>");
            return Command.SUCCESS;
        }

        reply(ctx, "<red>Target block is not a valid container, and no entity was found.</red>");
        return Command.FAILURE;
    }

    private static int sendStats(CommandContext<CommandSourceStack> ctx, Player target, int page) {
        Map<Statistic, Integer> statsMap = PlayerStatistics.of(target).getAll();
        if (statsMap.isEmpty()) {
            reply(ctx, "<red>✖</red> <gray>No statistics found for <white>" + target.getName() + "</white>.</gray>");
            return Command.SUCCESS;
        }

        List<Map.Entry<Statistic, Integer>> entries = new ArrayList<>(statsMap.entrySet());
        entries.sort(Comparator.comparing((Map.Entry<Statistic, Integer> e) -> e.getKey().type().id().asString())
            .thenComparing(e -> e.getKey().target().asString()));

        int itemsPerPage = 8;
        int maxPages = (int) Math.ceil((double) entries.size() / itemsPerPage);
        page = Math.max(1, Math.min(page, maxPages));

        int start = (page - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, entries.size());

        Component message = TextUtil.parse("\n<gold><st>      </st></gold> <yellow><b>Statistics:</b> " + target.getName() + "</yellow> <gray>(" + page + "/" + maxPages + ")</gray> <gold><st>      </st></gold>\n");

        for (int i = start; i < end; i++) {
            Map.Entry<Statistic, Integer> entry = entries.get(i);
            Statistic stat = entry.getKey();

            StatisticFormatter formatter = Registries.STATISTIC_FORMATTERS.get(stat.type().id().asString());
            if (formatter == null) formatter = new DefaultStatisticFormatter();

            message = message.append(formatter.formatChat(stat, entry.getValue())).append(Component.newline());
        }

        message = message.append(TextUtil.parse("<gold><st>                                            </st></gold>\n"));

        String footer = "  ";
        if (page > 1) {
            footer += "<click:run_command:'/abyssallib statistics get " + target.getName() + " " + (page - 1) + "'><hover:show_text:'<gray>Previous Page'><gold>«</gold> <yellow>Previous</yellow></hover></click>    ";
        } else {
            footer += "<dark_gray>« Previous</dark_gray>    ";
        }

        if (page < maxPages) {
            footer += "<click:run_command:'/abyssallib statistics get " + target.getName() + " " + (page + 1) + "'><hover:show_text:'<gray>Next Page'><yellow>Next</yellow> <gold>»</gold></hover></click>";
        } else {
            footer += "<dark_gray>Next »</dark_gray>";
        }

        message = message.append(TextUtil.parse(footer + "\n"));

        ctx.getSource().getSender().sendMessage(message);
        return Command.SUCCESS;
    }

    public static int getSelfStatistics(CommandContext<CommandSourceStack> ctx) {
        Player player = getPlayer(ctx);
        int page = 1;
        try {
            page = ctx.getArgument("page", Integer.class);
        } catch (IllegalArgumentException ignored) {}
        return player == null ? Command.FAILURE : sendStats(ctx, player, page);
    }

    public static int getOtherStatistics(CommandContext<CommandSourceStack> ctx) {
        Player target = resolvePlayer(ctx);
        int page = 1;
        try {
            page = ctx.getArgument("page", Integer.class);
        } catch (IllegalArgumentException ignored) {}
        return target == null ? Command.FAILURE : sendStats(ctx, target, page);
    }

    public static int getSelfStatisticsMenu(CommandContext<CommandSourceStack> ctx) {
        Player player = getPlayer(ctx);
        if (player != null) PlayerStatisticMenu.open(player, player);
        return Command.SUCCESS;
    }

    public static int getOtherStatisticsMenu(CommandContext<CommandSourceStack> ctx) {
        Player target = resolvePlayer(ctx);
        Player viewer = getPlayer(ctx);
        if (target != null && viewer != null) PlayerStatisticMenu.open(viewer, target);
        return Command.SUCCESS;
    }

    private static void reply(CommandContext<CommandSourceStack> ctx, String message) {
        ctx.getSource().getSender().sendRichMessage(message);
    }

    private static Player getPlayer(CommandContext<CommandSourceStack> ctx) {
        return ctx.getSource().getExecutor() instanceof Player p ? p : null;
    }

    private static Player resolvePlayer(CommandContext<CommandSourceStack> ctx) {
        PlayerSelectorArgumentResolver resolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
        return Try.of(() -> resolver.resolve(ctx.getSource()).getFirst()).orElse(null);
    }
}