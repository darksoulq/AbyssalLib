package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class CustomName extends DataComponent<Component> implements Vanilla {
    private static final Codec<CustomName> CODEC = Codecs.TEXT_COMPONENT.xmap(
            CustomName::new,
            CustomName::getValue
    );

    public CustomName(Component name) {
        super(Identifier.of(DataComponentTypes.CUSTOM_NAME.key().asString()), name, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.CUSTOM_NAME, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.CUSTOM_NAME);
    }
}
