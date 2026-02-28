package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import net.kyori.adventure.key.Key;

public class BlockItem extends DataComponent<Key> {
    public static final Codec<BlockItem> CODEC = Codecs.KEY.xmap(
            BlockItem::new,
            BlockItem::getValue
    );
    public static final DataComponentType<BlockItem> TYPE = DataComponentType.simple(CODEC);

    public BlockItem(Key blockId) {
        super(blockId);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }
}
