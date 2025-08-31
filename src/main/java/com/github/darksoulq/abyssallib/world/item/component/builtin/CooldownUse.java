package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseCooldown;
import org.bukkit.inventory.ItemStack;

public class CooldownUse extends DataComponent<UseCooldown> implements Vanilla {
    private static final Codec<DataComponent<UseCooldown>> CODEC = Codec.of(null, null);

    public CooldownUse(UseCooldown cd) {
        super(Identifier.of(DataComponentTypes.USE_COOLDOWN.key().asString()), cd, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.USE_COOLDOWN, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.USE_COOLDOWN);
    }
}
