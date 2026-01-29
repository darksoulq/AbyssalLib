package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SuspiciousStewEffects;
import io.papermc.paper.potion.SuspiciousEffectEntry;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SuspiciousStewEffect extends DataComponent<List<SuspiciousEffectEntry>> implements Vanilla {
    public static final Codec<SuspiciousStewEffect> CODEC = ExtraCodecs.SUSPICIOUS_EFFECT_ENTRY.list().xmap(
            SuspiciousStewEffect::new,
            SuspiciousStewEffect::getValue
    );
    public static final DataComponentType<SuspiciousStewEffect> TYPE = DataComponentType.valued(CODEC, v -> new SuspiciousStewEffect((SuspiciousStewEffects) v));

    public SuspiciousStewEffect(SuspiciousStewEffects effects) {
        super(effects.effects());
    }
    public SuspiciousStewEffect(List<SuspiciousEffectEntry> effects) {
        super(effects);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.suspiciousStewEffects(value));
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS);
    }
}
