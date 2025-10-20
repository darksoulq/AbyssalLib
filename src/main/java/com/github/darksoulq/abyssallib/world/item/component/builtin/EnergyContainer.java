package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.energy.impl.SimpleEnergyContainer;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;

public class EnergyContainer extends DataComponent<SimpleEnergyContainer> {
    public static final Codec<DataComponent<SimpleEnergyContainer>> CODEC = SimpleEnergyContainer.CODEC.xmap(EnergyContainer::new,
            e -> e.value);

    public EnergyContainer(SimpleEnergyContainer container) {
        super(Identifier.of("abyssallib", "energy_container"), container, CODEC);
    }
}
