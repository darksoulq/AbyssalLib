package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;

import java.util.Map;

public class CatEntityAdapter extends EntityAdapter<Cat> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Cat;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Cat value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("cat_variant", Codecs.NAMESPACED_KEY, value.getCatType().getKey());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Cat cat)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("cat_variant", Codecs.NAMESPACED_KEY, opt -> opt.ifPresent(key -> {
            Cat.Type type = RegistryAccess.registryAccess().getRegistry(RegistryKey.CAT_VARIANT).get(key);
            if (type != null) cat.setCatType(type);
        }));

        return ctx.result();
    }
}