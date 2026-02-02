package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.JukeboxPlayable;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.JukeboxSong;
import org.bukkit.inventory.ItemStack;

public class PlayableJukebox extends DataComponent<JukeboxPlayable> implements Vanilla {
    public static final Codec<PlayableJukebox> CODEC = Codecs.KEY.xmap(
            PlayableJukebox::new,
            p -> p.value.jukeboxSong().getKey()
    );
    public static final DataComponentType<PlayableJukebox> TYPE = DataComponentType.valued(CODEC, v -> new PlayableJukebox((JukeboxPlayable) v));

    public PlayableJukebox(JukeboxPlayable song) {
        super(song);
    }
    public PlayableJukebox(JukeboxSong song) {
        super(JukeboxPlayable.jukeboxPlayable(song).build());
    }
    public PlayableJukebox(Key songId) {
        super(JukeboxPlayable.jukeboxPlayable( RegistryAccess.registryAccess().getRegistry(RegistryKey.JUKEBOX_SONG).getOrThrow(songId)).build());
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
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
