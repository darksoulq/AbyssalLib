package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import java.util.Map;

public class ItemEntityAdapter extends EntityAdapter<Item> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Item;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Item value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("item_stack", Codecs.ITEM_STACK, value.getItemStack())
            .write("pickup_delay", Codecs.INT, value.getPickupDelay())
            .write("unlimited_lifetime", Codecs.BOOLEAN, value.isUnlimitedLifetime())
            .write("can_mob_pickup", Codecs.BOOLEAN, value.canMobPickup())
            .write("can_player_pickup", Codecs.BOOLEAN, value.canPlayerPickup())
            .write("will_age", Codecs.BOOLEAN, value.willAge())
            .write("health", Codecs.INT, value.getHealth())
            .writeNullable("owner", Codecs.UUID, value.getOwner())
            .writeNullable("thrower", Codecs.UUID, value.getThrower());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Item item)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("item_stack", Codecs.ITEM_STACK, opt -> opt.ifPresent(item::setItemStack))
            .readOptional("pickup_delay", Codecs.INT, opt -> opt.ifPresent(item::setPickupDelay))
            .readOptional("unlimited_lifetime", Codecs.BOOLEAN, opt -> opt.ifPresent(item::setUnlimitedLifetime))
            .readOptional("can_mob_pickup", Codecs.BOOLEAN, opt -> opt.ifPresent(item::setCanMobPickup))
            .readOptional("can_player_pickup", Codecs.BOOLEAN, opt -> opt.ifPresent(item::setCanPlayerPickup))
            .readOptional("will_age", Codecs.BOOLEAN, opt -> opt.ifPresent(item::setWillAge))
            .readOptional("health", Codecs.INT, opt -> opt.ifPresent(item::setHealth))
            .readOptional("owner", Codecs.UUID, opt -> opt.ifPresent(item::setOwner))
            .readOptional("thrower", Codecs.UUID, opt -> opt.ifPresent(item::setThrower));

        return ctx.result();
    }
}