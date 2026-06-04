package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
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
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Piglin value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("is_able_to_hunt", Codecs.BOOLEAN, value.isAbleToHunt())
            .write("is_charging_crossbow", Codecs.BOOLEAN, value.isChargingCrossbow())
            .write("is_dancing", Codecs.BOOLEAN, value.isDancing());

        List<String> barterList = value.getBarterList().stream().map(Enum::name).toList();
        if (!barterList.isEmpty()) {
            ctx.write("barter_list", Codecs.STRING.list(), barterList);
        }

        List<String> interestList = value.getInterestList().stream().map(Enum::name).toList();
        if (!interestList.isEmpty()) {
            ctx.write("interest_list", Codecs.STRING.list(), interestList);
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Piglin piglin)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_able_to_hunt", Codecs.BOOLEAN, opt -> opt.ifPresent(piglin::setIsAbleToHunt))
            .readOptional("is_charging_crossbow", Codecs.BOOLEAN, opt -> opt.ifPresent(piglin::setChargingCrossbow))
            .readOptional("is_dancing", Codecs.BOOLEAN, opt -> opt.ifPresent(piglin::setDancing))
            .readOptional("barter_list", Codecs.STRING.list(), opt -> opt.ifPresent(list -> {
                list.forEach(mat -> {
                    try {
                        piglin.addBarterMaterial(Material.valueOf(mat));
                    } catch (Exception ignored) {
                    }
                });
            }))
            .readOptional("interest_list", Codecs.STRING.list(), opt -> opt.ifPresent(list -> {
                list.forEach(mat -> {
                    try {
                        piglin.addMaterialOfInterest(Material.valueOf(mat));
                    } catch (Exception ignored) {
                    }
                });
            }));

        return ctx.result();
    }
}