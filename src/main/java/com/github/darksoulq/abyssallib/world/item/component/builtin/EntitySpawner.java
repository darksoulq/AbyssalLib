package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;

public class EntitySpawner extends DataComponent<Identifier> {
    public static final Codec<EntitySpawner> CODEC = Codecs.IDENTIFIER.xmap(
            EntitySpawner::new,
            EntitySpawner::getValue
    );

    public EntitySpawner(Identifier id) {
        super(Identifier.of(AbyssalLib.PLUGIN_ID, "entity_spawner"), id, CODEC);
    }
}
