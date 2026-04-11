package com.github.darksoulq.abyssallib.world.data.statistic.formatter;

import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import com.github.darksoulq.abyssallib.world.data.statistic.StatisticFormatter;
import com.github.darksoulq.abyssallib.world.dialog.DialogContent;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.text.Component;

public class DefaultStatisticFormatter implements StatisticFormatter {

    @Override
    public DialogBody formatDialog(Statistic stat, int value) {
        String langKey = "<lang:%s.stat.%s>".formatted(stat.target().namespace(), stat.target().value());
        return DialogContent.text(TextUtil.parse("<green>" + langKey + "</green> <gray>=</gray> <yellow>" + value + "</yellow>"));
    }

    @Override
    public Component formatChat(Statistic stat, int value) {
        String catKey = "<lang:%s.stat_type.%s>".formatted(stat.type().id().namespace(), stat.type().id().value());
        String langKey = "<lang:%s.stat.%s>".formatted(stat.target().namespace(), stat.target().value());
        return TextUtil.parse("<dark_gray>▪</dark_gray> <aqua>" + catKey + "</aqua> <dark_gray>»</dark_gray> <green>" + langKey + "</green> <gray>—</gray> <yellow>" + value + "</yellow>");
    }
}