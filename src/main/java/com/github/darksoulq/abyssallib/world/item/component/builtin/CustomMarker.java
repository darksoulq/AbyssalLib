package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;

public class CustomMarker extends DataComponent<Identifier> {
    public static final Codec<CustomMarker> CODEC = Codecs.IDENTIFIER.xmap(
        CustomMarker::new,
        CustomMarker::getValue
    );
    public static final DataComponentType<CustomMarker> TYPE = DataComponentType.simple(CODEC);

    public CustomMarker(Identifier id) {
        super(id);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }
}