package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class Unbreakable extends DataComponent<Boolean> implements Vanilla {
    public static final Codec<Unbreakable> CODEC = Codecs.STRING.optional().xmap(
            b -> new Unbreakable(),
            d -> Optional.empty()
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
