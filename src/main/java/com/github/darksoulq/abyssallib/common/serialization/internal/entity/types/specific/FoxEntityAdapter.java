package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fox;

import java.util.Map;

public class FoxEntityAdapter extends EntityAdapter<Fox> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Fox;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Fox value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("fox_type"), Codecs.STRING.encode(ops, value.getFoxType().name()));
        map.put(ops.createString("is_crouching"), Codecs.BOOLEAN.encode(ops, value.isCrouching()));
        map.put(ops.createString("is_sleeping"), Codecs.BOOLEAN.encode(ops, value.isSleeping()));
        map.put(ops.createString("is_faceplanted"), Codecs.BOOLEAN.encode(ops, value.isFaceplanted()));
        map.put(ops.createString("is_interested"), Codecs.BOOLEAN.encode(ops, value.isInterested()));
        map.put(ops.createString("is_leaping"), Codecs.BOOLEAN.encode(ops, value.isLeaping()));
        map.put(ops.createString("is_defending"), Codecs.BOOLEAN.encode(ops, value.isDefending()));

        if (value.getFirstTrustedPlayer() != null) {
            map.put(ops.createString("first_trusted_uuid"), Codecs.UUID.encode(ops, value.getFirstTrustedPlayer().getUniqueId()));
        }
        if (value.getSecondTrustedPlayer() != null) {
            map.put(ops.createString("second_trusted_uuid"), Codecs.UUID.encode(ops, value.getSecondTrustedPlayer().getUniqueId()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Fox fox)) return;

        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("fox_type")))).onSuccess(s -> fox.setFoxType(Fox.Type.valueOf(s)));
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_crouching")))).onSuccess(fox::setCrouching);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_sleeping")))).onSuccess(fox::setSleeping);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_faceplanted")))).onSuccess(fox::setFaceplanted);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_interested")))).onSuccess(fox::setInterested);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_leaping")))).onSuccess(fox::setLeaping);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_defending")))).onSuccess(fox::setDefending);

        D firstTrusted = map.get(ops.createString("first_trusted_uuid"));
        if (firstTrusted != null) {
            Try.of(() -> Codecs.UUID.decode(ops, firstTrusted)).onSuccess(uuid -> fox.setFirstTrustedPlayer(Bukkit.getOfflinePlayer(uuid)));
        }

        D secondTrusted = map.get(ops.createString("second_trusted_uuid"));
        if (secondTrusted != null) {
            Try.of(() -> Codecs.UUID.decode(ops, secondTrusted)).onSuccess(uuid -> fox.setSecondTrustedPlayer(Bukkit.getOfflinePlayer(uuid)));
        }
    }
}