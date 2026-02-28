package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import net.kyori.adventure.key.Key;

public class CustomMarker extends DataComponent<Key> {
    public static final Codec<CustomMarker> CODEC = Codecs.KEY.xmap(
        CustomMarker::new,
        CustomMarker::getValue
    );
    public static final DataComponentType<CustomMarker> TYPE = DataComponentType.simple(CODEC);

    public CustomMarker(Key id) {
        super(id);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }
}