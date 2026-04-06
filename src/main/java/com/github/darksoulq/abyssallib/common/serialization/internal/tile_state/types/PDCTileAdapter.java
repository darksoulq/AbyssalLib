package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
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
    public <D> D serialize(DynamicOps<D> ops, TileState value) throws Codec.CodecException {
        try {
            byte[] bytes = value.getPersistentDataContainer().serializeToBytes();
            return ops.createString(Base64.getEncoder().encodeToString(bytes));
        } catch (Exception e) {
            throw new Codec.CodecException("Failed to serialize PDC to bytes: " + e.getMessage());
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        String base64 = ops.getStringValue(input).orElseThrow(() -> new Codec.CodecException("Expected string for PDC Base64"));
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            PersistentDataContainer pdc = base.getPersistentDataContainer();
            pdc.readFromBytes(bytes, false);
        } catch (Exception e) {
            throw new Codec.CodecException("Failed to deserialize PDC from bytes: " + e.getMessage());
        }
    }
}