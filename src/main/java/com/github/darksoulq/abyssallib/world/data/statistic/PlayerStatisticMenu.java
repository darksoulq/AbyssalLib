package com.github.darksoulq.abyssallib.world.data.statistic;

import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.statistic.formatter.DefaultStatisticFormatter;
import com.github.darksoulq.abyssallib.world.dialog.DialogContent;
import com.github.darksoulq.abyssallib.world.dialog.Dialogs;
import com.github.darksoulq.abyssallib.world.dialog.Notice;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerStatisticMenu {

    public static void open(Player viewer, Player target) {
        openCategoryMenu(viewer, target);
    }

    private static void openCategoryMenu(Player viewer, Player target) {
        var multiAction = Dialogs.multiAction(TextUtil.parse("<gold>Statistics - " + target.getName() + "</gold>"));
        
        for (StatisticType type : Registries.STATISTIC_TYPES.getAll().values()) {
            String catKey = "<lang:stat_type.%s.%s>".formatted(type.id().namespace(), type.id().value());
            multiAction.action(DialogContent.button(TextUtil.parse("<aqua>" + catKey + "</aqua>"), 
                (response, audience) -> openStatList(viewer, target, type)));
        }
        
        viewer.showDialog(multiAction.build());
    }

    private static void openStatList(Player viewer, Player target, StatisticType type) {
        Map<Statistic, Integer> statsMap = PlayerStatistics.of(target).getAll();
        List<Map.Entry<Statistic, Integer>> filtered = new ArrayList<>();

        for (Map.Entry<Statistic, Integer> entry : statsMap.entrySet()) {
            if (entry.getKey().type().equals(type)) {
                filtered.add(entry);
            }
        }

        String catKey = "<lang:stat_type.%s.%s>".formatted(type.id().namespace(), type.id().value());
        Notice notice = Dialogs.notice(TextUtil.parse("<gold>Stats: " + catKey + "</gold>"),
            DialogContent.button(TextUtil.parse("<red>Back</red>"),
                (response, audience) -> openCategoryMenu(viewer, target)));

        if (filtered.isEmpty()) {
            notice.body(DialogContent.text(TextUtil.parse("<gray>No statistics recorded in this category.</gray>")));
        } else {
            StatisticFormatter formatter = Registries.STATISTIC_FORMATTERS.get(type.id().asString());
            if (formatter == null) {
                formatter = new DefaultStatisticFormatter();
            }

            for (Map.Entry<Statistic, Integer> entry : filtered) {
                notice.body(formatter.formatDialog(entry.getKey(), entry.getValue()));
            }
        }

        viewer.showDialog(notice.build());
    }
}