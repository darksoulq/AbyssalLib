package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Spellcaster;

import java.util.Map;

public class SpellcasterEntityAdapter extends EntityAdapter<Spellcaster> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Spellcaster;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Spellcaster value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("spell", Codecs.STRING, value.getSpell().name());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Spellcaster spellcaster)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("spell", Codecs.STRING, opt -> opt.ifPresent(spell -> {
            try {
                spellcaster.setSpell(Spellcaster.Spell.valueOf(spell));
            } catch (Exception ignored) {
            }
        }));

        return ctx.result();
    }
}