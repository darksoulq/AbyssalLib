package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ProvidesBannerPatterns extends DataComponent<List<Key>> implements Vanilla {

    public static final Codec<ProvidesBannerPatterns> CODEC = Codec.oneOf(
        Codecs.KEY.xmap(
            ProvidesBannerPatterns::new,
            component -> {
                if (component.value.size() != 1) {
                    throw new Codec.CodecException("Expected exactly 1 key.");
                }
                return component.value.getFirst();
            }
        ),
        Codecs.KEY.list().xmap(
            ProvidesBannerPatterns::new,
            ProvidesBannerPatterns::getValue
        )
    );

    public static final DataComponentType<ProvidesBannerPatterns> TYPE = DataComponentType.<ProvidesBannerPatterns, RegistryKeySet<PatternType>>valued(
        CODEC,
        ProvidesBannerPatterns::new
    );

    public ProvidesBannerPatterns(RegistryKeySet<PatternType> patterns) {
        super(resolveKeys(patterns));
    }

    public ProvidesBannerPatterns(Key pattern) {
        super(List.of(pattern));
    }

    public ProvidesBannerPatterns(List<Key> patterns) {
        super(patterns);
    }

    private static List<Key> resolveKeys(RegistryKeySet<PatternType> patterns) {
        Registry<PatternType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN);

        List<Key> keys = new ArrayList<>();

        patterns.resolve(registry).forEach(type -> {
            NamespacedKey key = registry.getKey(type);
            if (key != null) {
                keys.add(Key.key(key.asString()));
            }
        });

        return keys;
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        List<TypedKey<PatternType>> keys = new ArrayList<>();

        for (Key key : value) {
            keys.add(TypedKey.create(RegistryKey.BANNER_PATTERN, key));
        }

        stack.setData(DataComponentTypes.PROVIDES_BANNER_PATTERNS, RegistrySet.keySet(RegistryKey.BANNER_PATTERN, keys));
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.PROVIDES_BANNER_PATTERNS);
    }
}