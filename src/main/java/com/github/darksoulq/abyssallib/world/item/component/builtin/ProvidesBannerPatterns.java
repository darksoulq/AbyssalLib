package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;

public class ProvidesBannerPatterns extends DataComponent<TagKey<PatternType>> implements Vanilla {
    private static final Codec<DataComponent<TagKey<PatternType>>> CODEC = Codec.of(null, null);

    public ProvidesBannerPatterns(TagKey<PatternType> patterns) {
        super(Identifier.of(DataComponentTypes.PROVIDES_BANNER_PATTERNS.key().asString()), patterns, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.PROVIDES_BANNER_PATTERNS, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.PROVIDES_BANNER_PATTERNS);
    }
}
