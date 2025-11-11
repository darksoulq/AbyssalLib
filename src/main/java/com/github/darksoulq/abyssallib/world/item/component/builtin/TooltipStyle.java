package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;

public class TooltipStyle extends DataComponent<Key> implements Vanilla {
    private static final Codec<TooltipStyle> CODEC = Codecs.KEY.xmap(
            TooltipStyle::new,
            TooltipStyle::getValue
    );

    public TooltipStyle(Key id) {
        super(Identifier.of(DataComponentTypes.TOOLTIP_STYLE.key().asString()), id, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.TOOLTIP_STYLE, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.TOOLTIP_STYLE);
    }
}
