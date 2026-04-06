package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Spellcaster;

import java.util.Map;

public class SpellcasterEntityAdapter extends EntityAdapter<Spellcaster> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Spellcaster;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Spellcaster value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("spell"), Codecs.STRING.encode(ops, value.getSpell().name()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Spellcaster spellcaster)) return;

        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("spell")))).onSuccess(s -> spellcaster.setSpell(Spellcaster.Spell.valueOf(s)));
    }
}