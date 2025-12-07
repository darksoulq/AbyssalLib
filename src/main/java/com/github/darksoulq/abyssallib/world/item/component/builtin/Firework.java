package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Fireworks;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class Firework extends DataComponent<Fireworks> implements Vanilla {
    public static final Codec<Firework> CODEC = ExtraCodecs.FIREWORKS.xmap(
            Firework::new,
            Firework::getValue
    );

    public Firework(Fireworks fireworks) {
        super(Identifier.of(DataComponentTypes.FIREWORKS.key().asString()), fireworks, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.FIREWORKS, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.FIREWORKS);
    }
}
