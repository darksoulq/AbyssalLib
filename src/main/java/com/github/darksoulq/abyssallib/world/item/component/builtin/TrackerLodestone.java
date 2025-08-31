package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import org.bukkit.inventory.ItemStack;

public class TrackerLodestone extends DataComponent<LodestoneTracker> implements Vanilla {
    private static final Codec<DataComponent<LodestoneTracker>> CODEC = Codec.of(null, null);

    public TrackerLodestone(LodestoneTracker tracker) {
        super(Identifier.of(DataComponentTypes.LODESTONE_TRACKER.key().asString()), tracker, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.LODESTONE_TRACKER, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.LODESTONE_TRACKER);
    }
}
