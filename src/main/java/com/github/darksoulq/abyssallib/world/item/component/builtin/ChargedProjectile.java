package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ChargedProjectiles;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ChargedProjectile extends DataComponent<List<ItemStack>> implements Vanilla {
    public static final Codec<ChargedProjectile> CODEC = Codecs.ITEM_STACK.list().xmap(
            ChargedProjectile::new,
            ChargedProjectile::getValue
    );
    public static final DataComponentType<ChargedProjectile> TYPE = DataComponentType.valued(CODEC, v -> new ChargedProjectile((ChargedProjectiles) v));

    public ChargedProjectile(ChargedProjectiles projectiles) {
        super(projectiles.projectiles());
    }
    public ChargedProjectile(List<ItemStack> projectiles) {
        super(projectiles);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
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
