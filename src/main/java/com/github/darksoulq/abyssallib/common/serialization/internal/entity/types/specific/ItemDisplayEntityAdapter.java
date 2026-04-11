package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;

import java.util.Map;

public class ItemDisplayEntityAdapter extends EntityAdapter<ItemDisplay> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof ItemDisplay;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, ItemDisplay value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("item_display_transform"), Codecs.ITEM_DISPLAY_TRANSFORM.encode(ops, value.getItemDisplayTransform()));

        if (!value.getItemStack().isEmpty()) {
            map.put(ops.createString("item_stack"), Codecs.ITEM_STACK.encode(ops, value.getItemStack()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof ItemDisplay display)) return;

        Try.of(() -> Codecs.ITEM_DISPLAY_TRANSFORM.decode(ops, map.get(ops.createString("item_display_transform")))).onSuccess(display::setItemDisplayTransform);

        D itemData = map.get(ops.createString("item_stack"));
        if (itemData != null) {
            Try.of(() -> Codecs.ITEM_STACK.decode(ops, itemData)).onSuccess(display::setItemStack);
        }
    }
}