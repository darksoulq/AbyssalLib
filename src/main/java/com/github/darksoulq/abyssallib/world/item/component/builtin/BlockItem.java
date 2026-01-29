package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;

public class BlockItem extends DataComponent<Identifier> {
    public static final Codec<BlockItem> CODEC = Codecs.IDENTIFIER.xmap(
            BlockItem::new,
            BlockItem::getValue
    );
    public static final DataComponentType<BlockItem> TYPE = DataComponentType.simple(CODEC);

    public BlockItem(Identifier blockId) {
        super(blockId);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }
}
