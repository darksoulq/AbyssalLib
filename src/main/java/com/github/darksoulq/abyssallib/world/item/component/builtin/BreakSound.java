package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;

public class BreakSound extends DataComponent<Key> implements Vanilla {
    private static final Codec<DataComponent<Key>> CODEC = Codec.of(null, null);

    public BreakSound(Key key) {
        super(Identifier.of(DataComponentTypes.BREAK_SOUND.key().asString()), key, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.BREAK_SOUND, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.BREAK_SOUND);
    }
}
