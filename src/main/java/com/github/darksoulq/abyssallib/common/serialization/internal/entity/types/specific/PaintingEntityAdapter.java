package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Art;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Painting;

import java.util.Map;

public class PaintingEntityAdapter extends EntityAdapter<Painting> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Painting;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Painting value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("facing"), Codecs.STRING.encode(ops, value.getFacing().name()));
        map.put(ops.createString("art_key"), Codecs.STRING.encode(ops, value.getArt().assetId().asString()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Painting painting)) return;

        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("facing")))).onSuccess(s -> painting.setFacingDirection(BlockFace.valueOf(s), true));

        D artData = map.get(ops.createString("art_key"));
        if (artData != null) {
            Try.of(() -> Codecs.STRING.decode(ops, artData)).onSuccess(keyString -> {
                Art art = RegistryAccess.registryAccess().getRegistry(RegistryKey.PAINTING_VARIANT).get(Key.key(keyString));
                if (art != null) painting.setArt(art, true);
            });
        }
    }
}