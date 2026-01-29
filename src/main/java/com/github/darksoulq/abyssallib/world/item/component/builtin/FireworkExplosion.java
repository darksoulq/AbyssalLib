package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class FireworkExplosion extends DataComponent<FireworkEffect> implements Vanilla {
    public static final Codec<FireworkExplosion> CODEC = ExtraCodecs.FIREWORK_EFFECT.xmap(
            FireworkExplosion::new,
            FireworkExplosion::getValue
    );
    public static final DataComponentType<FireworkExplosion> TYPE = DataComponentType.valued(CODEC, FireworkExplosion::new);

    public FireworkExplosion(FireworkEffect effect) {
        super(effect);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
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
