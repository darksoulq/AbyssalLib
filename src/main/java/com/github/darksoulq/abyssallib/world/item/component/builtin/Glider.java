package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class Glider extends DataComponent<Boolean> implements Vanilla {
    public static final Codec<Glider> CODEC = Codecs.STRING.optional().xmap(
            b -> new Glider(),
            d -> Optional.empty()
    );

    public Glider() {
        super(Identifier.of(DataComponentTypes.GLIDER.key().asString()), true, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.GLIDER);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.GLIDER);
    }
}
