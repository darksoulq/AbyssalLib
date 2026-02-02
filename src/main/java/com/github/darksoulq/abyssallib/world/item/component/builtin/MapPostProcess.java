package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.item.MapPostProcessing;
import org.bukkit.inventory.ItemStack;

public class MapPostProcess extends DataComponent<MapPostProcessing> implements Vanilla {
    public static final Codec<MapPostProcess> CODEC = Codec.enumCodec(MapPostProcessing.class).xmap(
            MapPostProcess::new,
            MapPostProcess::getValue
    );
    public static final DataComponentType<MapPostProcess> TYPE = DataComponentType.valued(CODEC, MapPostProcess::new);

    public MapPostProcess(MapPostProcessing postProcess) {
        super(postProcess);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.MAP_POST_PROCESSING, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.MAP_POST_PROCESSING);
    }
}
