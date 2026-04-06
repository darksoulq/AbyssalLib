package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Piglin;

import java.util.List;
import java.util.Map;

public class PiglinEntityAdapter extends EntityAdapter<Piglin> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Piglin;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Piglin value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("is_able_to_hunt"), Codecs.BOOLEAN.encode(ops, value.isAbleToHunt()));
        map.put(ops.createString("is_charging_crossbow"), Codecs.BOOLEAN.encode(ops, value.isChargingCrossbow()));
        map.put(ops.createString("is_dancing"), Codecs.BOOLEAN.encode(ops, value.isDancing()));

        List<String> barterList = value.getBarterList().stream().map(Enum::name).toList();
        if (!barterList.isEmpty()) {
            map.put(ops.createString("barter_list"), Codecs.STRING.list().encode(ops, barterList));
        }

        List<String> interestList = value.getInterestList().stream().map(Enum::name).toList();
        if (!interestList.isEmpty()) {
            map.put(ops.createString("interest_list"), Codecs.STRING.list().encode(ops, interestList));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Piglin piglin)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_able_to_hunt")))).onSuccess(piglin::setIsAbleToHunt);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_charging_crossbow")))).onSuccess(piglin::setChargingCrossbow);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_dancing")))).onSuccess(piglin::setDancing);

        D barterData = map.get(ops.createString("barter_list"));
        if (barterData != null) {
            Try.of(() -> Codecs.STRING.list().decode(ops, barterData)).onSuccess(list -> {
                for (String mat : list) piglin.addBarterMaterial(Material.valueOf(mat));
            });
        }

        D interestData = map.get(ops.createString("interest_list"));
        if (interestData != null) {
            Try.of(() -> Codecs.STRING.list().decode(ops, interestData)).onSuccess(list -> {
                for (String mat : list) piglin.addMaterialOfInterest(Material.valueOf(mat));
            });
        }
    }
}