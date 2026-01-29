package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;

public class BreakSound extends DataComponent<Key> implements Vanilla {
    public static final Codec<BreakSound> CODEC = Codecs.KEY.xmap(
            BreakSound::new,
            BreakSound::getValue
    );
    public static final DataComponentType<BreakSound> TYPE = DataComponentType.valued(CODEC, BreakSound::new);

    public BreakSound(Key key) {
        super(key);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
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
