package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ChargedProjectiles;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import org.bukkit.inventory.ItemStack;

public class ChargedProjectile extends DataComponent<ChargedProjectiles> implements Vanilla {
    private static final Codec<DataComponent<ChargedProjectiles>> CODEC = Codecs.ITEM_STACK.list().xmap(
            l -> new ChargedProjectile(ChargedProjectiles.chargedProjectiles(l)),
            c -> c.value.projectiles()
    );

    public ChargedProjectile(ChargedProjectiles projectiles) {
        super(Identifier.of(DataComponentTypes.CHARGED_PROJECTILES.key().asString()), projectiles, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.CHARGED_PROJECTILES, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.CHARGED_PROJECTILES);
    }
}
