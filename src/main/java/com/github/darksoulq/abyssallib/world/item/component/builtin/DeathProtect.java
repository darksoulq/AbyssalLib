package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DeathProtection;
import org.bukkit.inventory.ItemStack;

public class DeathProtect extends DataComponent<DeathProtection> implements Vanilla {
    private static final Codec<DataComponent<DeathProtection>> CODEC = Codec.of(null, null);

    public DeathProtect(DeathProtection prot) {
        super(Identifier.of(DataComponentTypes.DEATH_PROTECTION.key().asString()), prot, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.DEATH_PROTECTION, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.DEATH_PROTECTION);
    }
}
