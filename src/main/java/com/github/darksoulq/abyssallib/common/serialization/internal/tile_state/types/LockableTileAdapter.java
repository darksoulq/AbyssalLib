package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.reflection.Reflect;
import com.github.darksoulq.abyssallib.common.reflection.ReflectField;
import com.github.darksoulq.abyssallib.common.reflection.ReflectMethod;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.LockCode;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import org.bukkit.block.Lockable;
import org.bukkit.block.TileState;
import org.bukkit.craftbukkit.block.CraftBlockEntityState;

public class LockableTileAdapter extends TileAdapter<Lockable> {

    private static final ReflectMethod<BaseContainerBlockEntity> GET_SNAPSHOT = Reflect.of(CraftBlockEntityState.class)
        .<BaseContainerBlockEntity>method("getSnapshot")
        .getOrNull();

    private static final ReflectField<LockCode> LOCK_CODE_FIELD = Reflect.of(BaseContainerBlockEntity.class)
        .fieldByType(LockCode.class)
        .getOrNull();

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Lockable && GET_SNAPSHOT != null && LOCK_CODE_FIELD != null;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Lockable value) {
        BaseContainerBlockEntity snapshot = GET_SNAPSHOT.invoke(value).getOrNull();
        if (snapshot == null) return DataResult.error(DataError.custom("No snapshot available"));

        LockCode lockCode = LOCK_CODE_FIELD.get(snapshot).getOrNull();
        if (lockCode == null) return DataResult.error(DataError.custom("LockCode is null"));

        try {
            RegistryOps<JsonElement> registryOps = MinecraftServer.getServer().registryAccess().createSerializationContext(JsonOps.INSTANCE);
            JsonElement jsonElement = LockCode.CODEC.encodeStart(registryOps, lockCode).getOrThrow();

            if (jsonElement.isJsonPrimitive() && jsonElement.getAsString().isEmpty()) {
                return DataResult.error(DataError.custom("LockCode is empty"));
            }

            return Codecs.STRING.encode(ops, jsonElement.toString());
        } catch (Exception e) {
            return DataResult.error(DataError.custom("Failed to serialize LockCode: " + e.getMessage()));
        }
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof Lockable)) return DataResult.success(null);

        BaseContainerBlockEntity snapshot = GET_SNAPSHOT.invoke(base).getOrNull();
        if (snapshot == null) return DataResult.success(null);

        return Codecs.STRING.decode(ops, input).flatMap(jsonStr -> {
            try {
                JsonElement jsonElement = JsonParser.parseString(jsonStr);
                RegistryOps<JsonElement> registryOps = MinecraftServer.getServer().registryAccess().createSerializationContext(JsonOps.INSTANCE);
                LockCode lockCode = LockCode.CODEC.parse(registryOps, jsonElement).getOrThrow();
                LOCK_CODE_FIELD.set(snapshot, lockCode);
                return DataResult.success(null);
            } catch (Exception e) {
                return DataResult.error(DataError.custom("Failed to decode LockCode: " + e.getMessage()));
            }
        });
    }
}