package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Weapon;
import org.bukkit.inventory.ItemStack;

public class WeaponComponent extends DataComponent<Weapon> implements Vanilla {
    private static final Codec<DataComponent<Weapon>> CODEC = ExtraCodecs.WEAPON.xmap(
            WeaponComponent::new,
            w -> w.value
    );

    public WeaponComponent(Weapon weapon) {
        super(Identifier.of(DataComponentTypes.WEAPON.key().asString()), weapon, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.WEAPON, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.WEAPON);
    }
}
