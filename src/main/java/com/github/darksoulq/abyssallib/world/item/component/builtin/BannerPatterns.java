package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BannerPatternLayers;
import org.bukkit.inventory.ItemStack;

public class BannerPatterns extends DataComponent<BannerPatternLayers> implements Vanilla {
    public static final Codec<BannerPatterns> CODEC = ExtraCodecs.BANNER_PATTERN_LAYERS.xmap(
            BannerPatterns::new,
            BannerPatterns::getValue
    );

    public BannerPatterns(BannerPatternLayers layers) {
        super(Identifier.of(DataComponentTypes.BANNER_PATTERNS.key().asString()), layers, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.BANNER_PATTERNS, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.BANNER_PATTERNS);
    }
}
