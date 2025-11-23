package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;

public class EntitySpawner extends DataComponent<Identifier> {
    private static final Codec<EntitySpawner> CODEC = Identifier.CODEC.xmap(
            EntitySpawner::new,
            EntitySpawner::getValue
    );

    public EntitySpawner(Identifier id) {
        super(Identifier.of(AbyssalLib.MODID, "entity_spawner"), id, CODEC);
    }
}
