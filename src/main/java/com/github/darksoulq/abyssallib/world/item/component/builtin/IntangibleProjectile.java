package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class IntangibleProjectile extends DataComponent<Boolean> implements Vanilla {
    public static final Codec<IntangibleProjectile> CODEC = Codecs.STRING.optional().xmap(
            b -> new IntangibleProjectile(),
            d -> Optional.empty()
    );
    public static final DataComponentType<IntangibleProjectile> TYPE = DataComponentType.valued(CODEC, v -> new IntangibleProjectile());

    public IntangibleProjectile() {
        super(true);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.INTANGIBLE_PROJECTILE);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.INTANGIBLE_PROJECTILE);
    }
}
