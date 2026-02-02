package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class DisplayTooltip extends DataComponent<TooltipDisplay> implements Vanilla {
    public static final Codec<DisplayTooltip> CODEC = ExtraCodecs.TOOLTIP_DISPLAY.xmap(
            DisplayTooltip::new,
            DisplayTooltip::getValue
    );
    public static final DataComponentType<DisplayTooltip> TYPE = DataComponentType.valued(CODEC, DisplayTooltip::new);

    public DisplayTooltip(TooltipDisplay display) {
        super(display);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
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
