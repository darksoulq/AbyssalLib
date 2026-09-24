package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MobVisibility;
import org.bukkit.inventory.ItemStack;

public class MobVisible extends DataComponent<MobVisibility> implements Vanilla {
    public static final Codec<MobVisible> CODEC = RecordBuilder.create(instance -> instance.group(
        ExtraCodecs.ENTITY_TYPE_KEYS.fieldOf("entities").forGetter((MobVisible mobVisible) -> mobVisible.getValue().targetingEntityTypes()),
        Codecs.FLOAT.range(0, 10).fieldOf("visibility").forGetter((MobVisible mobVisible) -> mobVisible.getValue().visibility())
    ).apply(instance, (entities, visibility) -> new MobVisible(MobVisibility.mobVisibility(entities, visibility))));
    public static final DataComponentType<MobVisible> TYPE = DataComponentType.valued(CODEC, MobVisible::new);

    public MobVisible(MobVisibility visibility) {
        super(visibility);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.MOB_VISIBILITY, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.MOB_VISIBILITY);
    }
}
