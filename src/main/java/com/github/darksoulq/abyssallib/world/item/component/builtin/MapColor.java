package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapItemColor;
import org.bukkit.inventory.ItemStack;

public class MapColor extends DataComponent<MapItemColor> implements Vanilla {
    private static final Codec<DataComponent<MapItemColor>> CODEC = Codec.of(null, null);

    public MapColor(MapItemColor color) {
        super(Identifier.of(DataComponentTypes.MAP_COLOR.key().asString()), color, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.MAP_COLOR, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.MAP_COLOR);
    }
}
