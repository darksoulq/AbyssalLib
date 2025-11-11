package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DeathProtect extends DataComponent<List<ConsumeEffect>> implements Vanilla {
    private static final Codec<DeathProtect> CODEC = ExtraCodecs.CONSUME_EFFECT.list().xmap(
            DeathProtect::new,
            DeathProtect::getValue
    );

    public DeathProtect(List<ConsumeEffect> effects) {
        super(Identifier.of(DataComponentTypes.DEATH_PROTECTION.key().asString()), effects, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.DEATH_PROTECTION, DeathProtection.deathProtection().addEffects(value).build());
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.DEATH_PROTECTION);
    }
}
