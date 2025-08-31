package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapId;
import org.bukkit.inventory.ItemStack;

public class MapID extends DataComponent<MapId> implements Vanilla {
    private static final Codec<DataComponent<MapId>> CODEC = Codec.of(null, null);

    public MapID(MapId blocks) {
        super(Identifier.of(DataComponentTypes.MAP_ID.key().asString()), blocks, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.MAP_ID, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.MAP_ID);
    }
}
