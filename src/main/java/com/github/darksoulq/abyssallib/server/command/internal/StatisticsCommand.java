package com.github.darksoulq.abyssallib.server.command.internal;

import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.command.BaseCommand;
import com.github.darksoulq.abyssallib.server.command.CommandResult;
import com.github.darksoulq.abyssallib.server.command.DefaultConditions;
import com.github.darksoulq.abyssallib.server.permission.internal.PluginPermissions;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatisticMenu;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import com.github.darksoulq.abyssallib.world.data.statistic.StatisticFormatter;
import com.github.darksoulq.abyssallib.world.data.statistic.formatter.DefaultStatisticFormatter;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class StatisticsCommand extends BaseCommand {

    public StatisticsCommand() {
        super("statistics", "stats");

        LiteralArgumentBuilder<CommandSourceStack> get = Commands.literal("get").requires(DefaultConditions.hasAnyPerm(PluginPermissions.STATISTICS_VIEW_SELF, PluginPermissions.STATISTICS_VIEW_ALL));
        LiteralArgumentBuilder<CommandSourceStack> view = Commands.literal("view").requires(DefaultConditions.hasAnyPerm(PluginPermissions.STATISTICS_MENU_SELF, PluginPermissions.STATISTICS_MENU_ALL));
        RequiredArgumentBuilder<CommandSourceStack, Integer> page = Commands.argument("page", IntegerArgumentType.integer(1));
        RequiredArgumentBuilder<CommandSourceStack, PlayerSelectorArgumentResolver> player = Commands.argument("player", ArgumentTypes.player()).requires(DefaultConditions.hasPerm(PluginPermissions.STATISTICS_VIEW_ALL));

        addSyntax(StatisticsCommand::getSelfStatistics, get);
        addSyntax(StatisticsCommand::getSelfStatistics, get, page);
        addSyntax(StatisticsCommand::getOtherStatistics, get, player);
        addSyntax(StatisticsCommand::getOtherStatistics, get, player, page);

        addSyntax(StatisticsCommand::getSelfStatisticsMenu, view);
        addSyntax(StatisticsCommand::getOtherStatisticsMenu, view, player);
    }

    private static CommandResult sendStats(CommandContext<CommandSourceStack> ctx, Player target, int page) {
        Map<Statistic, Integer> statsMap = PlayerStatistics.of(target).getAll();
        if (statsMap.isEmpty()) {
            CommandUtil.reply(ctx, "<red>✖</red> <gray>No statistics found for <white>" + target.getName() + "</white></gray>");
            return CommandResult.success();
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
        return CommandResult.success();
    }

    private static CommandResult getSelfStatistics(CommandContext<CommandSourceStack> ctx) {
        Player player = CommandUtil.getPlayer(ctx);
        int page = 1;
        try {
            page = ctx.getArgument("page", Integer.class);
        } catch (IllegalArgumentException ignored) {}

        if (player == null) {
            CommandUtil.reply(ctx, "<red>Only players can execute this command.</red>");
            return CommandResult.failure();
        }
        return sendStats(ctx, player, page);
    }

    private static CommandResult getOtherStatistics(CommandContext<CommandSourceStack> ctx) {
        Player target = CommandUtil.resolvePlayer(ctx);
        int page = 1;
        try {
            page = ctx.getArgument("page", Integer.class);
        } catch (IllegalArgumentException ignored) {}

        if (target == null) {
            CommandUtil.reply(ctx, "<red>Player not found</red>");
            return CommandResult.failure();
        }
        return sendStats(ctx, target, page);
    }

    private static CommandResult getSelfStatisticsMenu(CommandContext<CommandSourceStack> ctx) {
        Player player = CommandUtil.getPlayer(ctx);
        if (player != null) {
            PlayerStatisticMenu.open(player, player);
            return CommandResult.success();
        }
        CommandUtil.reply(ctx, "<red>Only players can execute this command.</red>");
        return CommandResult.failure();
    }

    private static CommandResult getOtherStatisticsMenu(CommandContext<CommandSourceStack> ctx) {
        Player target = CommandUtil.resolvePlayer(ctx);
        Player viewer = CommandUtil.getPlayer(ctx);
        if (viewer == null) {
            CommandUtil.reply(ctx, "<red>Only players can execute this command.</red>");
            return CommandResult.failure();
        }
        if (target == null) {
            CommandUtil.reply(ctx, "<red>Player not found</red>");
            return CommandResult.failure();
        }
        PlayerStatisticMenu.open(viewer, target);
        return CommandResult.success();
    }
}