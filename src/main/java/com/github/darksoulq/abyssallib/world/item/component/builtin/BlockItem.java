package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;

public class BlockItem extends DataComponent<Identifier> {
    private static final Codec<DataComponent<Identifier>> CODEC = Identifier.CODEC.xmap(
            BlockItem::new, (inst) -> inst.value
    );

    public BlockItem(Identifier blockId) {
        super(Identifier.of("abyssallib", "block_item"), blockId, CODEC);
    }
}
