package com.github.darksoulq.abyssallib.server.command.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.command.BaseCommand;
import com.github.darksoulq.abyssallib.server.command.CommandResult;
import com.github.darksoulq.abyssallib.server.command.DefaultConditions;
import com.github.darksoulq.abyssallib.server.permission.Node;
import com.github.darksoulq.abyssallib.server.permission.PermissionGroup;
import com.github.darksoulq.abyssallib.server.permission.PermissionUser;
import com.github.darksoulq.abyssallib.server.permission.internal.PluginPermissions;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gui.internal.PermissionMenu;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PermissionsCommand extends BaseCommand {

    public PermissionsCommand() {
        super("permissions", "perms");
        setRequirement(DefaultConditions.hasPerm(PluginPermissions.PERMISSIONS_EDIT).or(DefaultConditions.consoleOnly()));

        LiteralArgumentBuilder<CommandSourceStack> gui = Commands.literal("gui");
        RequiredArgumentBuilder<CommandSourceStack, PlayerSelectorArgumentResolver> targets = Commands.argument("targets", ArgumentTypes.players());
        LiteralArgumentBuilder<CommandSourceStack> web = Commands.literal("web").requires(DefaultConditions.hasPerm(PluginPermissions.PERMISSIONS_WEB).or(DefaultConditions.consoleOnly()));

        LiteralArgumentBuilder<CommandSourceStack> userLit = Commands.literal("user");
        RequiredArgumentBuilder<CommandSourceStack, String> target = Commands.argument("target", StringArgumentType.word());

        LiteralArgumentBuilder<CommandSourceStack> permissionLit = Commands.literal("permission");
        LiteralArgumentBuilder<CommandSourceStack> setLit = Commands.literal("set");
        LiteralArgumentBuilder<CommandSourceStack> unsetLit = Commands.literal("unset");
        RequiredArgumentBuilder<CommandSourceStack, String> node = Commands.argument("node", StringArgumentType.string());
        RequiredArgumentBuilder<CommandSourceStack, Boolean> value = Commands.argument("value", BoolArgumentType.bool());

        LiteralArgumentBuilder<CommandSourceStack> parentLit = Commands.literal("parent");
        LiteralArgumentBuilder<CommandSourceStack> addLit = Commands.literal("add");
        LiteralArgumentBuilder<CommandSourceStack> removeLit = Commands.literal("remove");
        RequiredArgumentBuilder<CommandSourceStack, String> groupArg = Commands.argument("group", StringArgumentType.word());

        LiteralArgumentBuilder<CommandSourceStack> groupLit = Commands.literal("group");
        RequiredArgumentBuilder<CommandSourceStack, String> id = Commands.argument("id", StringArgumentType.word());
        LiteralArgumentBuilder<CommandSourceStack> createLit = Commands.literal("create");
        LiteralArgumentBuilder<CommandSourceStack> deleteLit = Commands.literal("delete");
        LiteralArgumentBuilder<CommandSourceStack> setweightLit = Commands.literal("setweight");
        RequiredArgumentBuilder<CommandSourceStack, Integer> weight = Commands.argument("weight", IntegerArgumentType.integer());

        addSyntax(ctx -> {
            Player p = CommandUtil.getPlayer(ctx);
            if (p == null) {
                CommandUtil.reply(ctx, "<red>Only players can execute this command.</red>");
                return CommandResult.failure();
            }
            PermissionMenu.openMainMenu(p);
            return CommandResult.success();
        }, gui);

        addSyntax(ctx -> {
            for (Player t : ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource())) {
                PermissionMenu.openMainMenu(t);
            }
            return CommandResult.success();
        }, gui, targets);

        addSyntax(ctx -> {
            if (AbyssalLib.PERMISSION_WEB_SERVER == null || !AbyssalLib.PERMISSION_WEB_SERVER.isEnabled()) {
                CommandUtil.reply(ctx, "<red>Web server is not enabled in config</red>");
                return CommandResult.failure();
            }
            String token = AbyssalLib.PERMISSION_WEB_SERVER.createSession();
            String url = AbyssalLib.CONFIG.permissions.webProtocol.get() + "://" + AbyssalLib.CONFIG.permissions.webIp.get() + ":" + AbyssalLib.CONFIG.permissions.webPort.get() + "/?token=" + token;
            CommandUtil.reply(ctx, "Web Editor URL: <click:open_url:'" + url + "'><aqua><u>Click Here</u></aqua></click>\n<gray>This link expires in 15 minutes.</gray>");
            return CommandResult.success();
        }, web);

        addSyntax(ctx -> {
            UUID t = AbyssalLib.PERMISSION_MANAGER.getUuidFromName(ctx.getArgument("target", String.class));
            if (t == null) {
                CommandUtil.reply(ctx, "<red>User not found</red>");
                return CommandResult.failure();
            }
            PermissionUser u = AbyssalLib.PERMISSION_MANAGER.getUser(t);
            u.setPermission(new Node(ctx.getArgument("node", String.class), ctx.getArgument("value", Boolean.class)));
            u.save();
            CommandUtil.reply(ctx, "Permission set for user");
            return CommandResult.success();
        }, userLit, target, permissionLit, setLit, node, value);

        addSyntax(ctx -> {
            UUID t = AbyssalLib.PERMISSION_MANAGER.getUuidFromName(ctx.getArgument("target", String.class));
            if (t == null) {
                CommandUtil.reply(ctx, "<red>User not found</red>");
                return CommandResult.failure();
            }
            PermissionUser u = AbyssalLib.PERMISSION_MANAGER.getUser(t);
            u.unsetPermission(ctx.getArgument("node", String.class));
            u.save();
            CommandUtil.reply(ctx, "Permission unset for user");
            return CommandResult.success();
        }, userLit, target, permissionLit, unsetLit, node);

        addSyntax(ctx -> {
            UUID t = AbyssalLib.PERMISSION_MANAGER.getUuidFromName(ctx.getArgument("target", String.class));
            if (t == null) {
                CommandUtil.reply(ctx, "<red>User not found</red>");
                return CommandResult.failure();
            }
            PermissionUser u = AbyssalLib.PERMISSION_MANAGER.getUser(t);
            u.addParent(new Node(ctx.getArgument("group", String.class)));
            u.save();
            CommandUtil.reply(ctx, "Group added to user");
            return CommandResult.success();
        }, userLit, target, parentLit, addLit, groupArg);

        addSyntax(ctx -> {
            UUID t = AbyssalLib.PERMISSION_MANAGER.getUuidFromName(ctx.getArgument("target", String.class));
            if (t == null) {
                CommandUtil.reply(ctx, "<red>User not found</red>");
                return CommandResult.failure();
            }
            PermissionUser u = AbyssalLib.PERMISSION_MANAGER.getUser(t);
            u.removeParent(ctx.getArgument("group", String.class));
            u.save();
            CommandUtil.reply(ctx, "Group removed from user");
            return CommandResult.success();
        }, userLit, target, parentLit, removeLit, groupArg);

        addSyntax(ctx -> {
            String groupId = ctx.getArgument("id", String.class);
            if (Registries.PERMISSION_GROUPS.contains(groupId)) {
                CommandUtil.reply(ctx, "<red>Group already exists</red>");
                return CommandResult.failure();
            }
            PermissionGroup g = new PermissionGroup(groupId);
            Registries.PERMISSION_GROUPS.register(groupId, g);
            g.save();
            CommandUtil.reply(ctx, "Group created");
            return CommandResult.success();
        }, groupLit, id, createLit);

        addSyntax(ctx -> {
            String groupId = ctx.getArgument("id", String.class);
            if (!Registries.PERMISSION_GROUPS.contains(groupId)) {
                CommandUtil.reply(ctx, "<red>Group does not exist</red>");
                return CommandResult.failure();
            }
            AbyssalLib.PERMISSION_MANAGER.deleteGroup(groupId);
            CommandUtil.reply(ctx, "Group deleted");
            return CommandResult.success();
        }, groupLit, id, deleteLit);

        addSyntax(ctx -> {
            String groupId = ctx.getArgument("id", String.class);
            PermissionGroup g = Registries.PERMISSION_GROUPS.get(groupId);
            if (g == null) {
                CommandUtil.reply(ctx, "<red>Group not found</red>");
                return CommandResult.failure();
            }
            g.setWeight(ctx.getArgument("weight", Integer.class));
            g.save();
            CommandUtil.reply(ctx, "Weight set");
            return CommandResult.success();
        }, groupLit, id, setweightLit, weight);

        addSyntax(ctx -> {
            String groupId = ctx.getArgument("id", String.class);
            PermissionGroup g = Registries.PERMISSION_GROUPS.get(groupId);
            if (g == null) {
                CommandUtil.reply(ctx, "<red>Group not found</red>");
                return CommandResult.failure();
            }
            g.setPermission(new Node(ctx.getArgument("node", String.class), ctx.getArgument("value", Boolean.class)));
            g.save();
            CommandUtil.reply(ctx, "Permission set for group");
            return CommandResult.success();
        }, groupLit, id, permissionLit, setLit, node, value);

        addSyntax(ctx -> {
            String groupId = ctx.getArgument("id", String.class);
            PermissionGroup g = Registries.PERMISSION_GROUPS.get(groupId);
            if (g == null) {
                CommandUtil.reply(ctx, "<red>Group not found</red>");
                return CommandResult.failure();
            }
            g.unsetPermission(ctx.getArgument("node", String.class));
            g.save();
            CommandUtil.reply(ctx, "Permission unset for group");
            return CommandResult.success();
        }, groupLit, id, permissionLit, unsetLit, node);
    }
}