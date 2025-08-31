package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;

public class FireworkExplosion extends DataComponent<FireworkEffect> implements Vanilla {
    private static final Codec<DataComponent<FireworkEffect>> CODEC = Codec.of(null, null);

    public FireworkExplosion(FireworkEffect effect) {
        super(Identifier.of(DataComponentTypes.FIREWORK_EXPLOSION.key().asString()), effect, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.FIREWORK_EXPLOSION, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.FIREWORK_EXPLOSION);
    }
}
