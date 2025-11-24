package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ChargedProjectiles;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ChargedProjectile extends DataComponent<List<ItemStack>> implements Vanilla {
    private static final Codec<ChargedProjectile> CODEC = Codecs.ITEM_STACK.list().xmap(
            ChargedProjectile::new,
            ChargedProjectile::getValue
    );

    public ChargedProjectile(ChargedProjectiles projectiles) {
        super(Identifier.of(DataComponentTypes.CHARGED_PROJECTILES.key().asString()), projectiles.projectiles(), CODEC);
    }
    public ChargedProjectile(List<ItemStack> projectiles) {
        super(Identifier.of(DataComponentTypes.CHARGED_PROJECTILES.key().asString()), projectiles, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectiles.chargedProjectiles(value));
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.CHARGED_PROJECTILES);
    }
}
