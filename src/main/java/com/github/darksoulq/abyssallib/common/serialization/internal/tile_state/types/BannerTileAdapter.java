package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.Banner;
import org.bukkit.block.TileState;
import org.bukkit.block.banner.Pattern;

import java.util.ArrayList;
import java.util.List;

public class BannerTileAdapter extends TileAdapter<Banner> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Banner;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Banner value) throws Codec.CodecException {
        List<D> patterns = new ArrayList<>();
        for (Pattern p : value.getPatterns()) {
            patterns.add(ExtraCodecs.BANNER_PATTERN.encode(ops, p));
        }
        return ops.createList(patterns);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof Banner banner)) return;
        ops.getList(input).ifPresent(list -> {
            List<Pattern> patterns = new ArrayList<>();
            for (D pData : list) {
                try {
                    patterns.add(ExtraCodecs.BANNER_PATTERN.decode(ops, pData));
                } catch (Exception ignored) {}
            }
            banner.setPatterns(patterns);
        });
    }
}