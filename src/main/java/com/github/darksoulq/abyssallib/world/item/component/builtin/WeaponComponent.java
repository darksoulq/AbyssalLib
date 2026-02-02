package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Weapon;
import org.bukkit.inventory.ItemStack;

public class WeaponComponent extends DataComponent<Weapon> implements Vanilla {
    public static final Codec<WeaponComponent> CODEC = ExtraCodecs.WEAPON.xmap(
            WeaponComponent::new,
            WeaponComponent::getValue
    );
    public static final DataComponentType<WeaponComponent> TYPE = DataComponentType.valued(CODEC, WeaponComponent::new);

    public WeaponComponent(Weapon weapon) {
        super(weapon);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
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
