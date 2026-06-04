package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.CommandMinecart;

import java.util.Map;

public class CommandMinecartEntityAdapter extends EntityAdapter<CommandMinecart> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof CommandMinecart;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, CommandMinecart value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("command", Codecs.STRING, value.getCommand());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof CommandMinecart minecart)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("command", Codecs.STRING, opt -> opt.ifPresent(minecart::setCommand));

        return ctx.result();
    }
}