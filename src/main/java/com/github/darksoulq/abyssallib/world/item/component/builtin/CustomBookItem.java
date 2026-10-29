package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import net.kyori.adventure.key.Key;

public class CustomBookItem extends DataComponent<Key> {
    public static final Codec<CustomBookItem> CODEC = Codecs.KEY.xmap(
        CustomBookItem::new,
        CustomBookItem::getValue
    );
    public static final DataComponentType<CustomBookItem> TYPE = DataComponentType.simple(CODEC);

    public CustomBookItem(Key bookId) {
        super(bookId);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }
}