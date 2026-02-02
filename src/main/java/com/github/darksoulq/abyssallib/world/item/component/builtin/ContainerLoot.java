package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SeededContainerLoot;
import org.bukkit.inventory.ItemStack;

public class ContainerLoot extends DataComponent<SeededContainerLoot> implements Vanilla {
    public static final Codec<ContainerLoot> CODEC = ExtraCodecs.SEEDED_CONTAINER_LOOT.xmap(
            ContainerLoot::new,
            ContainerLoot::getValue
    );
    public static final DataComponentType<ContainerLoot> TYPE = DataComponentType.valued(CODEC, ContainerLoot::new);

    public ContainerLoot(SeededContainerLoot loot) {
        super(loot);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.CONTAINER_LOOT, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.CONTAINER_LOOT);
    }
}
