package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapId;
import org.bukkit.inventory.ItemStack;

public class MapID extends DataComponent<Integer> implements Vanilla {
    public static final Codec<MapID> CODEC = Codecs.INT.xmap(
            MapID::new,
            MapID::getValue
    );
    public static final DataComponentType<MapID> TYPE = DataComponentType.valued(CODEC, v -> new MapID((MapId) v));

    public MapID(MapId id) {
        super(id.id());
    }
    public MapID(int id) {
        super(id);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.MAP_ID, MapId.mapId(value));
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.MAP_ID);
    }
}
