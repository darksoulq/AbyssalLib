package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

public class DyedColor extends DataComponent<Color> implements Vanilla {
    public static final Codec<DyedColor> CODEC = ExtraCodecs.COLOR.xmap(
            DyedColor::new,
            DyedColor::getValue
    );
    public static final DataComponentType<DyedColor> TYPE = DataComponentType.valued(CODEC, v -> new DyedColor((DyedItemColor) v));

    public DyedColor(DyedItemColor color) {
        super(color.color());
    }
    public DyedColor(Color color) {
        super(color);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(value));
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.DYED_COLOR);
    }
}
