package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class EnchantmentGlintOverride extends DataComponent<Boolean> implements Vanilla {
    private static final Codec<EnchantmentGlintOverride> CODEC = Codecs.BOOLEAN.xmap(
            EnchantmentGlintOverride::new,
            EnchantmentGlintOverride::getValue
    );

    public EnchantmentGlintOverride(boolean value) {
        super(Identifier.of(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE.key().asString()), value, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
    }
}
