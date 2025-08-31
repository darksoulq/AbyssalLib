package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import org.bukkit.inventory.ItemStack;

public class ResistantDamage extends DataComponent<DamageResistant> implements Vanilla {
    private static final Codec<DataComponent<DamageResistant>> CODEC = Codec.of(null, null);

    public ResistantDamage(DamageResistant resist) {
        super(Identifier.of(DataComponentTypes.DAMAGE_RESISTANT.key().asString()), resist, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.DAMAGE_RESISTANT, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.DAMAGE_RESISTANT);
    }
}
