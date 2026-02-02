package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;

public class EntitySpawner extends DataComponent<Identifier> {
    public static final Codec<EntitySpawner> CODEC = Codecs.IDENTIFIER.xmap(
            EntitySpawner::new,
            EntitySpawner::getValue
    );
    public static final DataComponentType<EntitySpawner> TYPE = DataComponentType.simple(CODEC);

    public EntitySpawner(Identifier id) {
        super(id);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }
}
