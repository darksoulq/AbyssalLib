package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import org.bukkit.inventory.ItemStack;

public class ToolComponent extends DataComponent<Tool> implements Vanilla {
    public static final Codec<ToolComponent> CODEC = ExtraCodecs.TOOL.xmap(
            ToolComponent::new,
            ToolComponent::getValue
    );
    public static final DataComponentType<ToolComponent> TYPE = DataComponentType.valued(CODEC, ToolComponent::new);

    public ToolComponent(Tool tool) {
        super(tool);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
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
