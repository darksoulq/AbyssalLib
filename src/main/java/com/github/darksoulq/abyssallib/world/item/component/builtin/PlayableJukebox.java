package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.JukeboxPlayable;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.JukeboxSong;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;

public class PlayableJukebox extends DataComponent<JukeboxPlayable> implements Vanilla {
    private static final Codec<PlayableJukebox> CODEC = Codecs.KEY.xmap(
            PlayableJukebox::new,
            p -> p.value.jukeboxSong().getKey()
    );

    public PlayableJukebox(JukeboxPlayable song) {
        super(Identifier.of(DataComponentTypes.JUKEBOX_PLAYABLE.key().asString()), song, CODEC);
    }
    public PlayableJukebox(JukeboxSong song) {
        super(Identifier.of(DataComponentTypes.JUKEBOX_PLAYABLE.key().asString()), JukeboxPlayable.jukeboxPlayable(song).build(), CODEC);
    }

    public PlayableJukebox(Key songId) {
        super(Identifier.of(DataComponentTypes.JUKEBOX_PLAYABLE.key().asString()), JukeboxPlayable.jukeboxPlayable(
                RegistryAccess.registryAccess().getRegistry(RegistryKey.JUKEBOX_SONG).getOrThrow(songId)
        ).build(), CODEC);
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
