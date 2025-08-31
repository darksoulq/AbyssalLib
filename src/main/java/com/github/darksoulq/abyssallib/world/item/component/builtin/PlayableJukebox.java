package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.JukeboxPlayable;
import org.bukkit.inventory.ItemStack;

public class PlayableJukebox extends DataComponent<JukeboxPlayable> implements Vanilla {
    private static final Codec<DataComponent<JukeboxPlayable>> CODEC = Codec.of(null, null);

    public PlayableJukebox(JukeboxPlayable playable) {
        super(Identifier.of(DataComponentTypes.JUKEBOX_PLAYABLE.key().asString()), playable, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.JUKEBOX_PLAYABLE, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.JUKEBOX_PLAYABLE);
    }
}
