package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class Waxed extends DataComponent<Boolean> implements Vanilla {
    public static final Codec<Waxed> CODEC = Codecs.STRING.optional().xmap(
        b -> new Waxed(),
        d -> Optional.empty()
    );
    public static final DataComponentType<Waxed> TYPE = DataComponentType.valued(CODEC, v -> new Waxed());

    public Waxed() {
        super(true);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.WAXED);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.WAXED);
    }
}
