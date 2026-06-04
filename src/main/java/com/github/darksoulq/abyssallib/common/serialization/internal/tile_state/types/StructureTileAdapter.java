package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.Structure;
import org.bukkit.block.TileState;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.block.structure.UsageMode;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureTileAdapter extends TileAdapter<Structure> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Structure;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Structure value) {
        Map<D, D> map = new HashMap<>();
        List<DataError> warnings = new ArrayList<>();

        map.put(ops.createString("structure_name"), ops.createString(value.getStructureName()));
        map.put(ops.createString("author"), ops.createString(value.getAuthor()));
        map.put(ops.createString("metadata"), ops.createString(value.getMetadata()));
        map.put(ops.createString("usage_mode"), ops.createString(value.getUsageMode().name()));
        map.put(ops.createString("mirror"), ops.createString(value.getMirror().name()));
        map.put(ops.createString("rotation"), ops.createString(value.getRotation().name()));

        DataResult<D> integrityRes = Codecs.FLOAT.encode(ops, value.getIntegrity()).prependPath("integrity");
        if (integrityRes.isError())
            warnings.add(integrityRes.dataError().orElseGet(() -> DataError.custom(integrityRes.error().get())));
        else map.put(ops.createString("integrity"), integrityRes.getOrThrow());

        DataResult<D> seedRes = Codecs.LONG.encode(ops, value.getSeed()).prependPath("seed");
        if (seedRes.isError())
            warnings.add(seedRes.dataError().orElseGet(() -> DataError.custom(seedRes.error().get())));
        else map.put(ops.createString("seed"), seedRes.getOrThrow());

        DataResult<D> bboxRes = Codecs.BOOLEAN.encode(ops, value.isBoundingBoxVisible()).prependPath("bounding_box_visible");
        if (bboxRes.isError())
            warnings.add(bboxRes.dataError().orElseGet(() -> DataError.custom(bboxRes.error().get())));
        else map.put(ops.createString("bounding_box_visible"), bboxRes.getOrThrow());

        DataResult<D> ignoreEntRes = Codecs.BOOLEAN.encode(ops, value.isIgnoreEntities()).prependPath("ignore_entities");
        if (ignoreEntRes.isError())
            warnings.add(ignoreEntRes.dataError().orElseGet(() -> DataError.custom(ignoreEntRes.error().get())));
        else map.put(ops.createString("ignore_entities"), ignoreEntRes.getOrThrow());

        DataResult<D> showAirRes = Codecs.BOOLEAN.encode(ops, value.isShowAir()).prependPath("show_air");
        if (showAirRes.isError())
            warnings.add(showAirRes.dataError().orElseGet(() -> DataError.custom(showAirRes.error().get())));
        else map.put(ops.createString("show_air"), showAirRes.getOrThrow());

        DataResult<D> relPosRes = Codecs.VECTOR_I.encode(ops, value.getRelativePosition()).prependPath("relative_position");
        if (relPosRes.isError())
            warnings.add(relPosRes.dataError().orElseGet(() -> DataError.custom(relPosRes.error().get())));
        else map.put(ops.createString("relative_position"), relPosRes.getOrThrow());

        DataResult<D> structSizeRes = Codecs.VECTOR_I.encode(ops, value.getStructureSize()).prependPath("structure_size");
        if (structSizeRes.isError())
            warnings.add(structSizeRes.dataError().orElseGet(() -> DataError.custom(structSizeRes.error().get())));
        else map.put(ops.createString("structure_size"), structSizeRes.getOrThrow());

        return warnings.isEmpty() ? DataResult.success(ops.createMap(map)) : DataResult.partial(ops.createMap(map), warnings);
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof Structure structure)) return DataResult.success(null);

        return ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
            .flatMap(map -> {
                List<DataError> warnings = new ArrayList<>();

                ops.getStringValue(map.get(ops.createString("structure_name"))).ifPresent(structure::setStructureName);
                ops.getStringValue(map.get(ops.createString("author"))).ifPresent(structure::setAuthor);
                ops.getStringValue(map.get(ops.createString("metadata"))).ifPresent(structure::setMetadata);

                ops.getStringValue(map.get(ops.createString("usage_mode"))).ifPresent(s -> {
                    try {
                        structure.setUsageMode(UsageMode.valueOf(s));
                    } catch (Exception ignored) {
                    }
                });
                ops.getStringValue(map.get(ops.createString("mirror"))).ifPresent(s -> {
                    try {
                        structure.setMirror(Mirror.valueOf(s));
                    } catch (Exception ignored) {
                    }
                });
                ops.getStringValue(map.get(ops.createString("rotation"))).ifPresent(s -> {
                    try {
                        structure.setRotation(StructureRotation.valueOf(s));
                    } catch (Exception ignored) {
                    }
                });

                D integrityData = map.get(ops.createString("integrity"));
                if (integrityData != null) {
                    DataResult<Float> res = Codecs.FLOAT.decode(ops, integrityData).prependPath("integrity");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else structure.setIntegrity(res.getOrThrow());
                }

                D seedData = map.get(ops.createString("seed"));
                if (seedData != null) {
                    DataResult<Long> res = Codecs.LONG.decode(ops, seedData).prependPath("seed");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else structure.setSeed(res.getOrThrow());
                }

                D bboxData = map.get(ops.createString("bounding_box_visible"));
                if (bboxData != null) {
                    DataResult<Boolean> res = Codecs.BOOLEAN.decode(ops, bboxData).prependPath("bounding_box_visible");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else structure.setBoundingBoxVisible(res.getOrThrow());
                }

                D ignoreEntData = map.get(ops.createString("ignore_entities"));
                if (ignoreEntData != null) {
                    DataResult<Boolean> res = Codecs.BOOLEAN.decode(ops, ignoreEntData).prependPath("ignore_entities");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else structure.setIgnoreEntities(res.getOrThrow());
                }

                D showAirData = map.get(ops.createString("show_air"));
                if (showAirData != null) {
                    DataResult<Boolean> res = Codecs.BOOLEAN.decode(ops, showAirData).prependPath("show_air");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else structure.setShowAir(res.getOrThrow());
                }

                D relPosData = map.get(ops.createString("relative_position"));
                if (relPosData != null) {
                    DataResult<Vector> res = Codecs.VECTOR_I.decode(ops, relPosData).prependPath("relative_position");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else structure.setRelativePosition(new BlockVector(res.getOrThrow()));
                }

                D structSizeData = map.get(ops.createString("structure_size"));
                if (structSizeData != null) {
                    DataResult<Vector> res = Codecs.VECTOR_I.decode(ops, structSizeData).prependPath("structure_size");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else structure.setStructureSize(new BlockVector(res.getOrThrow()));
                }

                return warnings.isEmpty() ? DataResult.success(null) : DataResult.partial(null, warnings);
            });
    }
}