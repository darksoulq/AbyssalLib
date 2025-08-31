package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapDecorations;
import org.bukkit.inventory.ItemStack;

public class MapDecorates extends DataComponent<MapDecorations> implements Vanilla {
    private static final Codec<DataComponent<MapDecorations>> CODEC = Codec.of(null, null);

    public MapDecorates(MapDecorations decor) {
        super(Identifier.of(DataComponentTypes.MAP_DECORATIONS.key().asString()), decor, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.MAP_DECORATIONS, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.MAP_DECORATIONS);
    }
}
