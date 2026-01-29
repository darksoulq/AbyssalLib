package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;

public class ResistantDamage extends DataComponent<Key> implements Vanilla {
    public static final Codec<ResistantDamage> CODEC = Codecs.KEY.xmap(
            ResistantDamage::new,
            ResistantDamage::getValue
    );
    public static final DataComponentType<ResistantDamage> TYPE = DataComponentType.valued(CODEC, v -> new ResistantDamage((DamageResistant) v));

    public ResistantDamage(DamageResistant resists) {
        super(resists.types().key());
    }
    public ResistantDamage(Key resists) {
        super(resists);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(TagKey.create(RegistryKey.DAMAGE_TYPE, value)));
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.DAMAGE_RESISTANT);
    }
}
