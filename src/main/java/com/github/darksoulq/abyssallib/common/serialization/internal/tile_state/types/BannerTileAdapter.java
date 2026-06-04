package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
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
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Banner value) {
        List<DataError> warnings = new ArrayList<>();
        List<D> patterns = new ArrayList<>();

        for (int i = 0; i < value.getPatterns().size(); i++) {
            Pattern p = value.getPatterns().get(i);
            DataResult<D> res = ExtraCodecs.BANNER_PATTERN.encode(ops, p).prependPath("[" + i + "]");

            if (res.isError()) {
                warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
            } else {
                patterns.add(res.getOrThrow());
                if (res.isPartial()) warnings.addAll(res.warnings());
            }
        }

        return warnings.isEmpty() ? DataResult.success(ops.createList(patterns)) : DataResult.partial(ops.createList(patterns), warnings);
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof Banner banner)) return DataResult.success(null);

        return ops.getList(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("List", "Unknown")))
            .flatMap(list -> {
                List<DataError> warnings = new ArrayList<>();
                List<Pattern> patterns = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {
                    DataResult<Pattern> res = ExtraCodecs.BANNER_PATTERN.decode(ops, list.get(i)).prependPath("[" + i + "]");
                    if (res.isError()) {
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    } else {
                        patterns.add(res.getOrThrow());
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                banner.setPatterns(patterns);
                return warnings.isEmpty() ? DataResult.success(null) : DataResult.partial(null, warnings);
            });
    }
}