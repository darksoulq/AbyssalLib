package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

public class IntangibleProjectile extends DataComponent<Boolean> implements Vanilla {
    private static final Codec<IntangibleProjectile> CODEC = Codecs.BOOLEAN.xmap(
            b -> new IntangibleProjectile(),
            IntangibleProjectile::getValue
    );

    public IntangibleProjectile() {
        super(Identifier.of(DataComponentTypes.INTANGIBLE_PROJECTILE.key().asString()), true, CODEC);
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
