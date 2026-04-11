package com.github.darksoulq.abyssallib.world.data.statistic.formatter;

import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import com.github.darksoulq.abyssallib.world.data.statistic.StatisticFormatter;
import com.github.darksoulq.abyssallib.world.dialog.DialogContent;
import com.github.darksoulq.abyssallib.world.item.Item;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemStatisticFormatter implements StatisticFormatter {

    @Override
    public DialogBody formatDialog(Statistic stat, int value) {
        ItemStack stack = getItem(stat);
        if (stack == null || stack.getType().isAir()) {
            return new DefaultStatisticFormatter().formatDialog(stat, value);
        }

        String langKey = "<lang:item.%s.%s>".formatted(stat.target().namespace(), stat.target().value());
        return DialogContent.item(stack, DialogContent.text(TextUtil.parse("<green>" + langKey + "</green> <gray>=</gray> <yellow>" + value + "</yellow>")));
    }

    @Override
    public Component formatChat(Statistic stat, int value) {
        String catKey = "<lang:%s.stat_type.%s>".formatted(stat.type().id().namespace(), stat.type().id().value());
        String langKey = "<lang:item.%s.%s>".formatted(stat.target().namespace(), stat.target().value());
        
        ItemStack stack = getItem(stat);
        if (stack == null || stack.getType().isAir()) {
            return new DefaultStatisticFormatter().formatChat(stat, value);
        }

        Component itemName = TextUtil.parse("<green><hover:show_item:'%s'>%s</hover></green>".formatted(stat.target().asString(), langKey));
        return TextUtil.parse("<dark_gray>▪</dark_gray> <aqua>" + catKey + "</aqua> <dark_gray>»</dark_gray> ").append(itemName).append(TextUtil.parse(" <gray>—</gray> <yellow>" + value + "</yellow>"));
    }

    private ItemStack getItem(Statistic stat) {
        if (stat.target().namespace().equals("minecraft")) {
            Material mat = Material.matchMaterial(stat.target().value());
            if (mat != null) return new ItemStack(mat);
        } else {
            Item customItem = Registries.ITEMS.get(stat.target().asString());
            if (customItem != null) return customItem.getStack();
        }
        return null;
    }
}