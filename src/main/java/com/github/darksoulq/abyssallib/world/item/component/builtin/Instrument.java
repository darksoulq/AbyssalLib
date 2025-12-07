package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.MusicInstrument;
import org.bukkit.inventory.ItemStack;

public class Instrument extends DataComponent<Key> implements Vanilla {
    public static final Codec<Instrument> CODEC = Codecs.KEY.xmap(
            v -> new Instrument(RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT).getOrThrow(v)),
            Instrument::getValue
    );

    public Instrument(MusicInstrument instrument) {
        super(Identifier.of(DataComponentTypes.INSTRUMENT.key().asString()),
                RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT).getKeyOrThrow(instrument), CODEC);
    }
    public Instrument(Key instrument) {
        super(Identifier.of(DataComponentTypes.INSTRUMENT.key().asString()), instrument, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.INSTRUMENT,
                RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT).getOrThrow(value));
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.INSTRUMENT);
    }
}
