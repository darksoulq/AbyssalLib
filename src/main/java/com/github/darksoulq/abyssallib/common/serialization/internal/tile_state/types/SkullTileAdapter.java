package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Skull;
import org.bukkit.block.TileState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkullTileAdapter extends TileAdapter<Skull> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Skull;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Skull value) {
        Map<D, D> map = new HashMap<>();
        List<DataError> warnings = new ArrayList<>();

        if (value.getProfile() != null) {
            DataResult<D> res = ExtraCodecs.RESOLVABLE_PROFILE.encode(ops, value.getProfile()).prependPath("profile");
            if (res.isError()) warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
            else {
                map.put(ops.createString("profile"), res.getOrThrow());
                if (res.isPartial()) warnings.addAll(res.warnings());
            }
        }

        if (value.getNoteBlockSound() != null) {
            DataResult<D> res = Codecs.NAMESPACED_KEY.encode(ops, value.getNoteBlockSound()).prependPath("note_block_sound");
            if (res.isError()) warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
            else {
                map.put(ops.createString("note_block_sound"), res.getOrThrow());
                if (res.isPartial()) warnings.addAll(res.warnings());
            }
        }

        if (value.customName() != null) {
            DataResult<D> res = Codecs.TEXT_COMPONENT.encode(ops, value.customName()).prependPath("custom_name");
            if (res.isError()) warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
            else {
                map.put(ops.createString("custom_name"), res.getOrThrow());
                if (res.isPartial()) warnings.addAll(res.warnings());
            }
        }

        return warnings.isEmpty() ? DataResult.success(ops.createMap(map)) : DataResult.partial(ops.createMap(map), warnings);
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof Skull skull)) return DataResult.success(null);

        return ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
            .flatMap(map -> {
                List<DataError> warnings = new ArrayList<>();

                D profileData = map.get(ops.createString("profile"));
                if (profileData != null) {
                    DataResult<ResolvableProfile> res = ExtraCodecs.RESOLVABLE_PROFILE.decode(ops, profileData).prependPath("profile");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else {
                        skull.setProfile(res.getOrThrow());
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                D soundData = map.get(ops.createString("note_block_sound"));
                if (soundData != null) {
                    DataResult<NamespacedKey> res = Codecs.NAMESPACED_KEY.decode(ops, soundData).prependPath("note_block_sound");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else {
                        skull.setNoteBlockSound(res.getOrThrow());
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                D nameData = map.get(ops.createString("custom_name"));
                if (nameData != null) {
                    DataResult<Component> res = Codecs.TEXT_COMPONENT.decode(ops, nameData).prependPath("custom_name");
                    if (res.isError())
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    else {
                        skull.customName(res.getOrThrow());
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                return warnings.isEmpty() ? DataResult.success(null) : DataResult.partial(null, warnings);
            });
    }
}