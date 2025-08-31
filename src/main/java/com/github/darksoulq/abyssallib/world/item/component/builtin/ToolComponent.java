package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import org.bukkit.inventory.ItemStack;

public class ToolComponent extends DataComponent<Tool> implements Vanilla {
    private static final Codec<DataComponent<Tool>> CODEC = Codec.of(null, null);

    public ToolComponent(Tool tool) {
        super(Identifier.of(DataComponentTypes.TOOL.key().asString()), tool, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.TOOL, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.TOOL);
    }
}
