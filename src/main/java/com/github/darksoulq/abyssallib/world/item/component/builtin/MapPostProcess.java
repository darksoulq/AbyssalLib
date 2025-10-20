package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.item.MapPostProcessing;
import org.bukkit.inventory.ItemStack;

public class MapPostProcess extends DataComponent<MapPostProcessing> implements Vanilla {
    private static final Codec<DataComponent<MapPostProcessing>> CODEC = Codec.enumCodec(MapPostProcessing.class).xmap(
            MapPostProcess::new,
            m -> m.value
    );

    public MapPostProcess(MapPostProcessing postProcess) {
        super(Identifier.of(DataComponentTypes.MAP_POST_PROCESSING.key().asString()), postProcess, CODEC);
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
