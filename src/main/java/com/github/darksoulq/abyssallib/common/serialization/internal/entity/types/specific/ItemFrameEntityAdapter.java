package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.Rotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;

import java.util.Map;

public class ItemFrameEntityAdapter extends EntityAdapter<ItemFrame> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof ItemFrame;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, ItemFrame value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("rotation"), Codecs.STRING.encode(ops, value.getRotation().name()));
        map.put(ops.createString("is_visible"), Codecs.BOOLEAN.encode(ops, value.isVisible()));
        map.put(ops.createString("is_fixed"), Codecs.BOOLEAN.encode(ops, value.isFixed()));
        map.put(ops.createString("item_drop_chance"), Codecs.FLOAT.encode(ops, value.getItemDropChance()));

        if (!value.getItem().isEmpty()) {
            map.put(ops.createString("item"), Codecs.ITEM_STACK.encode(ops, value.getItem()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof ItemFrame frame)) return;

        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("rotation")))).onSuccess(s -> frame.setRotation(Rotation.valueOf(s)));
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_visible")))).onSuccess(frame::setVisible);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_fixed")))).onSuccess(frame::setFixed);
        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("item_drop_chance")))).onSuccess(frame::setItemDropChance);

        D itemData = map.get(ops.createString("item"));
        if (itemData != null) {
            Try.of(() -> Codecs.ITEM_STACK.decode(ops, itemData)).onSuccess(frame::setItem);
        }
    }
}