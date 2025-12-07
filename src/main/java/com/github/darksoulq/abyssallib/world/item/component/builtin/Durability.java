package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

public class Durability extends DataComponent<Integer> implements Vanilla {
    public static final Codec<Durability> CODEC = Codecs.INT.xmap(
            Durability::new,
            Durability::getValue
    );

    public Durability(int value) {
        super(Identifier.of(DataComponentTypes.DAMAGE.key().asString()), value, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.DAMAGE, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.DAMAGE);
    }
}
