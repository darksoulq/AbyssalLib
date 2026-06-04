package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Panda;

import java.util.Map;

public class PandaEntityAdapter extends EntityAdapter<Panda> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Panda;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Panda value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("main_gene", Codecs.STRING, value.getMainGene().name())
            .write("hidden_gene", Codecs.STRING, value.getHiddenGene().name())
            .write("is_rolling", Codecs.BOOLEAN, value.isRolling())
            .write("is_sneezing", Codecs.BOOLEAN, value.isSneezing())
            .write("is_on_back", Codecs.BOOLEAN, value.isOnBack())
            .write("is_eating", Codecs.BOOLEAN, value.isEating())
            .write("sneeze_ticks", Codecs.INT, value.getSneezeTicks())
            .write("eating_ticks", Codecs.INT, value.getEatingTicks())
            .write("unhappy_ticks", Codecs.INT, value.getUnhappyTicks());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Panda panda)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("main_gene", Codecs.STRING, opt -> opt.ifPresent(geneStr -> {
                try {
                    panda.setMainGene(Panda.Gene.valueOf(geneStr));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("hidden_gene", Codecs.STRING, opt -> opt.ifPresent(geneStr -> {
                try {
                    panda.setHiddenGene(Panda.Gene.valueOf(geneStr));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("is_rolling", Codecs.BOOLEAN, opt -> opt.ifPresent(panda::setRolling))
            .readOptional("is_sneezing", Codecs.BOOLEAN, opt -> opt.ifPresent(panda::setSneezing))
            .readOptional("is_on_back", Codecs.BOOLEAN, opt -> opt.ifPresent(panda::setOnBack))
            .readOptional("is_eating", Codecs.BOOLEAN, opt -> opt.ifPresent(panda::setEating))
            .readOptional("sneeze_ticks", Codecs.INT, opt -> opt.ifPresent(panda::setSneezeTicks))
            .readOptional("eating_ticks", Codecs.INT, opt -> opt.ifPresent(panda::setEatingTicks))
            .readOptional("unhappy_ticks", Codecs.INT, opt -> opt.ifPresent(panda::setUnhappyTicks));

        return ctx.result();
    }
}