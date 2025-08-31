package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BlocksAttacks;
import org.bukkit.inventory.ItemStack;

public class BlockAttacks extends DataComponent<BlocksAttacks> implements Vanilla {
    private static final Codec<DataComponent<BlocksAttacks>> CODEC = Codec.of(null, null);

    public BlockAttacks(BlocksAttacks blocks) {
        super(Identifier.of(DataComponentTypes.BLOCKS_ATTACKS.key().asString()), blocks, CODEC);
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
