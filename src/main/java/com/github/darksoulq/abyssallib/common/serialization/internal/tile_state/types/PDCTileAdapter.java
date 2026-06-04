package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Base64;

public class PDCTileAdapter extends TileAdapter<TileState> {

    @Override
    public boolean doesApply(TileState state) {
        return !state.getPersistentDataContainer().isEmpty();
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, TileState value) {
        try {
            byte[] bytes = value.getPersistentDataContainer().serializeToBytes();
            return DataResult.success(ops.createString(Base64.getEncoder().encodeToString(bytes)));
        } catch (Exception e) {
            return DataResult.error(DataError.custom("Failed to serialize PDC to bytes: " + e.getMessage()));
        }
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        return ops.getStringValue(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("String", "Unknown")))
            .flatMap(base64 -> {
                try {
                    byte[] bytes = Base64.getDecoder().decode(base64);
                    PersistentDataContainer pdc = base.getPersistentDataContainer();
                    pdc.readFromBytes(bytes, false);
                    return DataResult.success(null);
                } catch (Exception e) {
                    return DataResult.error(DataError.custom("Failed to deserialize PDC from bytes: " + e.getMessage()));
                }
            });
    }
}