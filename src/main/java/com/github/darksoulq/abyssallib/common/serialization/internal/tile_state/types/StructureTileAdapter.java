package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.block.Structure;
import org.bukkit.block.TileState;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.block.structure.UsageMode;
import org.bukkit.util.BlockVector;

import java.util.HashMap;
import java.util.Map;

public class StructureTileAdapter extends TileAdapter<Structure> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Structure;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Structure value) throws Codec.CodecException {
        Map<D, D> map = new HashMap<>();

        map.put(ops.createString("structure_name"), ops.createString(value.getStructureName()));
        map.put(ops.createString("author"), ops.createString(value.getAuthor()));
        map.put(ops.createString("metadata"), ops.createString(value.getMetadata()));
        map.put(ops.createString("usage_mode"), ops.createString(value.getUsageMode().name()));
        map.put(ops.createString("mirror"), ops.createString(value.getMirror().name()));
        map.put(ops.createString("rotation"), ops.createString(value.getRotation().name()));
        
        map.put(ops.createString("integrity"), Codecs.FLOAT.encode(ops, value.getIntegrity()));
        map.put(ops.createString("seed"), Codecs.LONG.encode(ops, value.getSeed()));
        
        map.put(ops.createString("bounding_box_visible"), Codecs.BOOLEAN.encode(ops, value.isBoundingBoxVisible()));
        map.put(ops.createString("ignore_entities"), Codecs.BOOLEAN.encode(ops, value.isIgnoreEntities()));
        map.put(ops.createString("show_air"), Codecs.BOOLEAN.encode(ops, value.isShowAir()));

        map.put(ops.createString("relative_position"), Codecs.VECTOR_I.encode(ops, value.getRelativePosition()));
        map.put(ops.createString("structure_size"), Codecs.VECTOR_I.encode(ops, value.getStructureSize()));

        return ops.createMap(map);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof Structure structure)) return;
        Map<D, D> map = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map for Structure"));

        ops.getStringValue(map.get(ops.createString("structure_name"))).ifPresent(structure::setStructureName);
        ops.getStringValue(map.get(ops.createString("author"))).ifPresent(structure::setAuthor);
        ops.getStringValue(map.get(ops.createString("metadata"))).ifPresent(structure::setMetadata);
        ops.getStringValue(map.get(ops.createString("usage_mode"))).ifPresent(s -> Try.run(() -> structure.setUsageMode(UsageMode.valueOf(s))));
        ops.getStringValue(map.get(ops.createString("mirror"))).ifPresent(s -> Try.run(() -> structure.setMirror(Mirror.valueOf(s))));
        ops.getStringValue(map.get(ops.createString("rotation"))).ifPresent(s -> Try.run(() -> structure.setRotation(StructureRotation.valueOf(s))));

        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("integrity")))).onSuccess(structure::setIntegrity);
        Try.of(() -> Codecs.LONG.decode(ops, map.get(ops.createString("seed")))).onSuccess(structure::setSeed);

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("bounding_box_visible")))).onSuccess(structure::setBoundingBoxVisible);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("ignore_entities")))).onSuccess(structure::setIgnoreEntities);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("show_air")))).onSuccess(structure::setShowAir);

        Try.of(() -> Codecs.VECTOR_I.decode(ops, map.get(ops.createString("relative_position")))).onSuccess(v -> structure.setRelativePosition(new BlockVector(v)));
        Try.of(() -> Codecs.VECTOR_I.decode(ops, map.get(ops.createString("structure_size")))).onSuccess(v -> structure.setStructureSize(new BlockVector(v)));
    }
}