package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;

public class NoteBlockSound extends DataComponent<Key> implements Vanilla {
    public static final Codec<NoteBlockSound> CODEC = Codecs.KEY.xmap(
            NoteBlockSound::new,
            NoteBlockSound::getValue
    );
    public static final DataComponentType<NoteBlockSound> TYPE = DataComponentType.valued(CODEC, NoteBlockSound::new);

    public NoteBlockSound(Key key) {
        super(key);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.NOTE_BLOCK_SOUND, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.NOTE_BLOCK_SOUND);
    }
}
