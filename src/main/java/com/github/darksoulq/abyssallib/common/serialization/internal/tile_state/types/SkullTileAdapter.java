package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.block.Skull;
import org.bukkit.block.TileState;

import java.util.HashMap;
import java.util.Map;

public class SkullTileAdapter extends TileAdapter<Skull> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Skull;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops, Skull value) throws Codec.CodecException {
        Map<D, D> map = new HashMap<>();

        if (value.getProfile() != null) {
            map.put(ops.createString("profile"), ExtraCodecs.RESOLVABLE_PROFILE.encode(ops, value.getProfile()));
        }

        if (value.getNoteBlockSound() != null) {
            map.put(ops.createString("note_block_sound"), Codecs.NAMESPACED_KEY.encode(ops, value.getNoteBlockSound()));
        }

        if (value.customName() != null) {
            map.put(ops.createString("custom_name"), Codecs.TEXT_COMPONENT.encode(ops, value.customName()));
        }

        return ops.createMap(map);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof Skull skull)) return;
        Map<D, D> map = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map for Skull"));

        D profileData = map.get(ops.createString("profile"));
        if (profileData != null) {
            Try.of(() -> ExtraCodecs.RESOLVABLE_PROFILE.decode(ops, profileData)).onSuccess(skull::setProfile);
        }

        D soundData = map.get(ops.createString("note_block_sound"));
        if (soundData != null) {
            Try.of(() -> Codecs.NAMESPACED_KEY.decode(ops, soundData)).onSuccess(skull::setNoteBlockSound);
        }

        D nameData = map.get(ops.createString("custom_name"));
        if (nameData != null) {
            Try.of(() -> Codecs.TEXT_COMPONENT.decode(ops, nameData)).onSuccess(skull::customName);
        }
    }
}