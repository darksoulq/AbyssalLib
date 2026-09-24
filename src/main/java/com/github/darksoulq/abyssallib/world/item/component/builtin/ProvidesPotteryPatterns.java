package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.block.pot.PotPatternType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;

public class ProvidesPotteryPatterns extends DataComponent<PotPatternType> implements Vanilla {
    public static final Codec<ProvidesPotteryPatterns> CODEC = Codecs.KEY.xmap(
        key -> new ProvidesPotteryPatterns(getType(key)),
        patterns -> patterns.getValue().getKey()
    );
    public static final DataComponentType<ProvidesPotteryPatterns> TYPE = DataComponentType.valued(CODEC, ProvidesPotteryPatterns::new);

    public ProvidesPotteryPatterns(PotPatternType type) {
        super(type);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.PROVIDES_POTTERY_PATTERN, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.PROVIDES_POTTERY_PATTERN);
    }

    private static PotPatternType getType(Key key) {
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.DECORATED_POT_PATTERN).getOrThrow(key);
    }
}
