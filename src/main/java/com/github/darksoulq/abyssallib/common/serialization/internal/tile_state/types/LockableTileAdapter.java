package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.reflection.Reflect;
import com.github.darksoulq.abyssallib.common.reflection.ReflectField;
import com.github.darksoulq.abyssallib.common.reflection.ReflectMethod;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
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
    public <D> D serialize(DynamicOps<D> ops, Lockable value) throws Codec.CodecException {
        BaseContainerBlockEntity snapshot = GET_SNAPSHOT.invoke(value).getOrNull();
        if (snapshot == null) throw new Codec.CodecException("No snapshot available");

        LockCode lockCode = LOCK_CODE_FIELD.get(snapshot).getOrNull();
        if (lockCode == null) throw new Codec.CodecException("LockCode is null");

        try {
            RegistryOps<JsonElement> registryOps = MinecraftServer.getServer().registryAccess().createSerializationContext(JsonOps.INSTANCE);
            JsonElement jsonElement = LockCode.CODEC.encodeStart(registryOps, lockCode).getOrThrow();

            if (jsonElement.isJsonPrimitive() && jsonElement.getAsString().isEmpty()) {
                throw new Codec.CodecException("LockCode is empty");
            }

            return Codecs.STRING.encode(ops, jsonElement.toString());
        } catch (Codec.CodecException e) {
            throw e;
        } catch (Exception e) {
            throw new Codec.CodecException("Failed to serialize LockCode", e);
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof Lockable)) return;

        BaseContainerBlockEntity snapshot = GET_SNAPSHOT.invoke(base).getOrNull();
        if (snapshot == null) return;

        String jsonStr = Codecs.STRING.decode(ops, input);
        try {
            JsonElement jsonElement = JsonParser.parseString(jsonStr);
            RegistryOps<JsonElement> registryOps = MinecraftServer.getServer().registryAccess().createSerializationContext(JsonOps.INSTANCE);
            LockCode lockCode = LockCode.CODEC.parse(registryOps, jsonElement).getOrThrow();
            LOCK_CODE_FIELD.set(snapshot, lockCode);
        } catch (Exception e) {
            throw new Codec.CodecException("Failed to decode LockCode", e);
        }
    }
}