package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

public class Unbreakable extends DataComponent<Boolean> implements Vanilla {
    private static final Codec<DataComponent<Boolean>> CODEC = Codecs.BOOLEAN.xmap(
            b -> new Unbreakable(),
            u -> u.value
    );

    public Unbreakable() {
        super(Identifier.of(DataComponentTypes.UNBREAKABLE.key().asString()), true, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.UNBREAKABLE);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.UNBREAKABLE);
    }
}
