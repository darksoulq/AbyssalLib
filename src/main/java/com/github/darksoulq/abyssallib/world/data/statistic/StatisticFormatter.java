package com.github.darksoulq.abyssallib.world.data.statistic;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.text.Component;

public interface StatisticFormatter {

    DialogBody formatDialog(Statistic stat, int value);

    Component formatChat(Statistic stat, int value);

}