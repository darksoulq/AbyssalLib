package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;

public class CustomMarker extends DataComponent<Identifier> {
    private static final Codec<DataComponent<Identifier>> CODEC = Identifier.CODEC.xmap(
            CustomMarker::new, (inst) -> inst.value
    );

    public CustomMarker(Identifier id) {
        super(Identifier.of("abyssallib", "marker"), id, CODEC);
    }
}
