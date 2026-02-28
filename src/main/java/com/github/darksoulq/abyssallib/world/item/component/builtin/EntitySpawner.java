package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import net.kyori.adventure.key.Key;

public class EntitySpawner extends DataComponent<Key> {
    public static final Codec<EntitySpawner> CODEC = Codecs.KEY.xmap(
            EntitySpawner::new,
            EntitySpawner::getValue
    );
    public static final DataComponentType<EntitySpawner> TYPE = DataComponentType.simple(CODEC);

    public EntitySpawner(Key id) {
        super(id);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }
}
