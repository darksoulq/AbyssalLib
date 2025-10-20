package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class DisplayTooltip extends DataComponent<TooltipDisplay> implements Vanilla {
    private static final Codec<DataComponent<TooltipDisplay>> CODEC = ExtraCodecs.TOOLTIP_DISPLAY.xmap(
            DisplayTooltip::new,
            d -> d.value
    );

    public DisplayTooltip(TooltipDisplay display) {
        super(Identifier.of(DataComponentTypes.TOOLTIP_DISPLAY.key().asString()), display, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.TOOLTIP_DISPLAY, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.TOOLTIP_DISPLAY);
    }
}
