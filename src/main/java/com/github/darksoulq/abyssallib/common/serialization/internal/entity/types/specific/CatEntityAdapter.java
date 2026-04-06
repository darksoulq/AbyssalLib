package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
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
    public <D> void serialize(DynamicOps<D> ops, Cat value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("cat_variant"), Codecs.NAMESPACED_KEY.encode(ops, value.getCatType().getKey()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Cat cat)) return;

        D typeData = map.get(ops.createString("cat_variant"));
        if (typeData != null) {
            Try.of(() -> Codecs.NAMESPACED_KEY.decode(ops, typeData)).onSuccess(key -> {
                Cat.Type type = RegistryAccess.registryAccess().getRegistry(RegistryKey.CAT_VARIANT).get(key);
                if (type != null) cat.setCatType(type);
            });
        }
    }
}