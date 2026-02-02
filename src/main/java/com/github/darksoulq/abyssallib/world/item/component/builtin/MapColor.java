package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapItemColor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

public class MapColor extends DataComponent<Color> implements Vanilla {
    public static final Codec<MapColor> CODEC = ExtraCodecs.COLOR.xmap(
            MapColor::new,
            MapColor::getValue
    );
    public static final DataComponentType<MapColor> TYPE = DataComponentType.valued(CODEC, v -> new MapColor((MapItemColor) v));

    public MapColor(MapItemColor color) {
        super(color.color());
    }
    public MapColor(Color color) {
        super(color);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.MAP_COLOR, MapItemColor.mapItemColor().color(value).build());
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.MAP_COLOR);
    }
}
