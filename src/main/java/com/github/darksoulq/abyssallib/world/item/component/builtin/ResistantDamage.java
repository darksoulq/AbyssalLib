package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.PatternType;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ResistantDamage extends DataComponent<List<Key>> implements Vanilla {
    public static final Codec<ResistantDamage> CODEC = Codec.oneOf(
        Codecs.KEY.xmap(
            ResistantDamage::new,
            component -> {
                if (component.value.size() != 1) {
                    throw new Codec.CodecException("Expected exactly 1 key.");
                }
                return component.value.getFirst();
            }
        ),
        Codecs.KEY.list().xmap(
            ResistantDamage::new,
            ResistantDamage::getValue
        )
    );
    public static final DataComponentType<ResistantDamage> TYPE = DataComponentType.valued(CODEC, v -> new ResistantDamage((DamageResistant) v));

    public ResistantDamage(DamageResistant resists) {
        super(resolveKeys(resists.types()));
    }
    public ResistantDamage(Key resists) {
        super(List.of(resists));
    }

    public ResistantDamage(List<Key> resists) {
        super(resists);
    }

    private static List<Key> resolveKeys(RegistryKeySet<DamageType> types) {
        Registry<DamageType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE);
        List<Key> keys = new ArrayList<>();

        types.resolve(registry).forEach(type -> {
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
        List<TypedKey<DamageType>> keys = new ArrayList<>();

        for (Key key : value) {
            keys.add(TypedKey.create(RegistryKey.DAMAGE_TYPE, key));
        }

        stack.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(RegistrySet.keySet(RegistryKey.DAMAGE_TYPE, keys)));
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.DAMAGE_RESISTANT);
    }
}
