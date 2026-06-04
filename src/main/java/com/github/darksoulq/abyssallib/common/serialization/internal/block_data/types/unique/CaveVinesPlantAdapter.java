package com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.CaveVinesPlant;

public class CaveVinesPlantAdapter extends Adapter<CaveVinesPlant> {
    @Override
    public boolean doesApply(BlockData data) {
        return data instanceof CaveVinesPlant;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, CaveVinesPlant value) {
        return Codecs.BOOLEAN.encode(ops, value.hasBerries());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base) {
        if (!(base instanceof CaveVinesPlant caveVinesPlant))
            return DataResult.error(DataError.custom("Base is not CaveVinesPlant, got: " + base.getClass().getSimpleName()));

        return Codecs.BOOLEAN.decode(ops, input).flatMap(value -> {
            caveVinesPlant.setBerries(value);
            return DataResult.success(null);
        });
    }
}