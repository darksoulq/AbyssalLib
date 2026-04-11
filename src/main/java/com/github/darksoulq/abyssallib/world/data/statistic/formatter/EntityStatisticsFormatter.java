package com.github.darksoulq.abyssallib.world.data.statistic.formatter;

import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import com.github.darksoulq.abyssallib.world.data.statistic.StatisticFormatter;
import com.github.darksoulq.abyssallib.world.dialog.DialogContent;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.text.Component;

public class EntityStatisticsFormatter implements StatisticFormatter {

    @Override
    public DialogBody formatDialog(Statistic stat, int value) {
        String langKey = "<lang:entity.%s.%s>".formatted(stat.target().namespace(), stat.target().value());
        return DialogContent.text(TextUtil.parse("<green>" + langKey + "</green> <gray>=</gray> <yellow>" + value + "</yellow>"));
    }

    @Override
    public Component formatChat(Statistic stat, int value) {
        String catKey = "<lang:stat_type.%s.%s>".formatted(stat.type().id().namespace(), stat.type().id().value());
        String langKey = "<lang:entity.%s.%s>".formatted(stat.target().namespace(), stat.target().value());
        return TextUtil.parse("<dark_gray>▪</dark_gray> <aqua>" + catKey + "</aqua> <dark_gray>»</dark_gray> <green>" + langKey + "</green> <gray>—</gray> <yellow>" + value + "</yellow>");
    }
}