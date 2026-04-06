package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Panda;

import java.util.Map;

public class PandaEntityAdapter extends EntityAdapter<Panda> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Panda;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Panda value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("main_gene"), Codecs.STRING.encode(ops, value.getMainGene().name()));
        map.put(ops.createString("hidden_gene"), Codecs.STRING.encode(ops, value.getHiddenGene().name()));
        map.put(ops.createString("is_rolling"), Codecs.BOOLEAN.encode(ops, value.isRolling()));
        map.put(ops.createString("is_sneezing"), Codecs.BOOLEAN.encode(ops, value.isSneezing()));
        map.put(ops.createString("is_on_back"), Codecs.BOOLEAN.encode(ops, value.isOnBack()));
        map.put(ops.createString("is_eating"), Codecs.BOOLEAN.encode(ops, value.isEating()));
        map.put(ops.createString("sneeze_ticks"), Codecs.INT.encode(ops, value.getSneezeTicks()));
        map.put(ops.createString("eating_ticks"), Codecs.INT.encode(ops, value.getEatingTicks()));
        map.put(ops.createString("unhappy_ticks"), Codecs.INT.encode(ops, value.getUnhappyTicks()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Panda panda)) return;

        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("main_gene")))).onSuccess(s -> panda.setMainGene(Panda.Gene.valueOf(s)));
        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("hidden_gene")))).onSuccess(s -> panda.setHiddenGene(Panda.Gene.valueOf(s)));
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_rolling")))).onSuccess(panda::setRolling);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_sneezing")))).onSuccess(panda::setSneezing);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_on_back")))).onSuccess(panda::setOnBack);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_eating")))).onSuccess(panda::setEating);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("sneeze_ticks")))).onSuccess(panda::setSneezeTicks);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("eating_ticks")))).onSuccess(panda::setEatingTicks);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("unhappy_ticks")))).onSuccess(panda::setUnhappyTicks);
    }
}