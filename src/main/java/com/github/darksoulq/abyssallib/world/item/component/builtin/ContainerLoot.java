package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SeededContainerLoot;
import org.bukkit.inventory.ItemStack;

public class ContainerLoot extends DataComponent<SeededContainerLoot> implements Vanilla {
    private static final Codec<DataComponent<SeededContainerLoot>> CODEC = Codec.of(null, null);

    public ContainerLoot(SeededContainerLoot loot) {
        super(Identifier.of(DataComponentTypes.CONTAINER_LOOT.key().asString()), loot, CODEC);
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
