package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.inventory.ItemStack;

public class ModelData extends DataComponent<CustomModelData> implements Vanilla {
    public static final Codec<ModelData> CODEC = ExtraCodecs.CUSTOM_MODEL_DATA.xmap(
            ModelData::new,
            ModelData::getValue
    );

    public ModelData(CustomModelData data) {
        super(Identifier.of(DataComponentTypes.CUSTOM_MODEL_DATA.key().asString()), data, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.CUSTOM_MODEL_DATA);
    }
}
