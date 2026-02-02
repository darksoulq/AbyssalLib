package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BlocksAttacks;
import org.bukkit.inventory.ItemStack;

public class BlockAttacks extends DataComponent<BlocksAttacks> implements Vanilla {
    public static final Codec<BlockAttacks> CODEC = ExtraCodecs.BLOCKS_ATTACKS.xmap(
            BlockAttacks::new,
            BlockAttacks::getValue
    );
    public static final DataComponentType<BlockAttacks> TYPE = DataComponentType.valued(CODEC, BlockAttacks::new);

    public BlockAttacks(BlocksAttacks blocks) {
        super(blocks);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.BLOCKS_ATTACKS, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.BLOCKS_ATTACKS);
    }
}
