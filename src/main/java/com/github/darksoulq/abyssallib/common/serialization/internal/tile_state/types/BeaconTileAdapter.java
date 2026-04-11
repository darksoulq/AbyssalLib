package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.Beacon;
import org.bukkit.block.TileState;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class BeaconTileAdapter extends TileAdapter<Beacon> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Beacon;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Beacon value) throws Codec.CodecException {
        Map<D, D> map = new HashMap<>();
        if (value.getPrimaryEffect() != null) {
            map.put(ops.createString("primary"), ExtraCodecs.POTION_EFFECT_TYPE.encode(ops, value.getPrimaryEffect().getType()));
        }
        if (value.getSecondaryEffect() != null) {
            map.put(ops.createString("secondary"), ExtraCodecs.POTION_EFFECT_TYPE.encode(ops, value.getSecondaryEffect().getType()));
        }
        return ops.createMap(map);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof Beacon beacon)) return;
        Map<D, D> map = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map for Beacon"));
        
        D primaryData = map.get(ops.createString("primary"));
        if (primaryData != null) {
            PotionEffectType type = ExtraCodecs.POTION_EFFECT_TYPE.decode(ops, primaryData);
            beacon.setPrimaryEffect(type);
        }

        D secondaryData = map.get(ops.createString("secondary"));
        if (secondaryData != null) {
            PotionEffectType type = ExtraCodecs.POTION_EFFECT_TYPE.decode(ops, secondaryData);
            beacon.setSecondaryEffect(type);
        }
    }
}