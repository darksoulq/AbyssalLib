package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import org.bukkit.inventory.ItemStack;

public class PotionContent extends DataComponent<PotionContents> implements Vanilla {
    private static final Codec<DataComponent<PotionContents>> CODEC = Codec.of(null, null);

    public PotionContent(PotionContents contents) {
        super(Identifier.of(DataComponentTypes.POTION_CONTENTS.key().asString()), contents, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.POTION_CONTENTS, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.POTION_CONTENTS);
    }
}
