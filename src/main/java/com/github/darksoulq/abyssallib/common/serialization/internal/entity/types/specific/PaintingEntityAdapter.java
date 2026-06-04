package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
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
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Painting value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("facing", Codecs.STRING, value.getFacing().name())
            .write("art_key", Codecs.STRING, value.getArt().assetId().asString());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Painting painting)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("facing", Codecs.STRING, opt -> opt.ifPresent(faceStr -> {
                try {
                    painting.setFacingDirection(BlockFace.valueOf(faceStr), true);
                } catch (Exception ignored) {
                }
            }))
            .readOptional("art_key", Codecs.STRING, opt -> opt.ifPresent(keyStr -> {
                try {
                    Art art = RegistryAccess.registryAccess().getRegistry(RegistryKey.PAINTING_VARIANT).get(Key.key(keyStr));
                    if (art != null) painting.setArt(art, true);
                } catch (Exception ignored) {
                }
            }));

        return ctx.result();
    }
}