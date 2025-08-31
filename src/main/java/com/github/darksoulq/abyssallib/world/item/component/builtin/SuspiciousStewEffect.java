package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SuspiciousStewEffects;
import org.bukkit.inventory.ItemStack;

public class SuspiciousStewEffect extends DataComponent<SuspiciousStewEffects> implements Vanilla {
    private static final Codec<DataComponent<SuspiciousStewEffects>> CODEC = Codec.of(null, null);

    public SuspiciousStewEffect(SuspiciousStewEffects effects) {
        super(Identifier.of(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS.key().asString()), effects, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS);
    }
}
