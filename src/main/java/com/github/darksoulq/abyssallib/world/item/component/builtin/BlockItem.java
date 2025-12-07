package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;

public class BlockItem extends DataComponent<Identifier> {
    public static final Codec<BlockItem> CODEC = Codecs.IDENTIFIER.xmap(
            BlockItem::new,
            BlockItem::getValue
    );

    public BlockItem(Identifier blockId) {
        super(Identifier.of("abyssallib", "block_item"), blockId, CODEC);
    }
}
