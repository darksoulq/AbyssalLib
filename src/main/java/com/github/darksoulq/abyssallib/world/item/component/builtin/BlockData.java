package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BlockItemDataProperties;
import org.bukkit.inventory.ItemStack;

public class BlockData extends DataComponent<BlockItemDataProperties> implements Vanilla {
    public static final Codec<BlockData> CODEC = Codec.of(null, null);
    public static final DataComponentType<BlockData> TYPE = DataComponentType.valued(CODEC, BlockData::new);

    public BlockData(BlockItemDataProperties props) {
        super(props);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.BLOCK_DATA, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.BLOCK_DATA);
    }
}
