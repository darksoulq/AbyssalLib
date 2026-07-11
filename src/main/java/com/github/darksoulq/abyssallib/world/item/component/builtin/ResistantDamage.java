package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemStack;

//? if >=26.1.2 {
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import java.util.ArrayList;
import java.util.List;
//?} else {
/*import io.papermc.paper.registry.tag.TagKey;
*///?}

public class ResistantDamage extends DataComponent</*? if >=26.1.2 {*/List<Key>/*?} else {*//*Key*//*?}*/> implements Vanilla {

    //? if >=26.1.2 {
    public static final Codec<ResistantDamage> CODEC = Codec.oneOf(ResistantDamage.class,
        Codecs.KEY.flatXmap(
            key -> DataResult.success(new ResistantDamage(key)),
            component -> {
                if (component.value.size() != 1) return DataResult.error("Expected exactly 1 key.");
                return DataResult.success(component.value.getFirst());
            }
        ),
        Codecs.KEY.list().xmap(ResistantDamage::new, ResistantDamage::getValue)
    ).describe("ResistantDamage");
    //?} else {
    /*public static final Codec<ResistantDamage> CODEC = Codecs.KEY.xmap(
        ResistantDamage::new,
        ResistantDamage::getValue
    ).describe("ResistantDamage");
    *///?}

    public static final DataComponentType<ResistantDamage> TYPE = DataComponentType.valued(CODEC, v -> new ResistantDamage((DamageResistant) v));

    public ResistantDamage(DamageResistant resists) {
        //? if >=26.1.2 {
        super(resolveKeys(resists.types()));
        //?} else {
        /*super(resists.types().key());
         *///?}
    }

    public ResistantDamage(Key resists) {
        //? if >=26.1.2 {
        super(List.of(resists));
        //?} else {
        /*super(resists);
         *///?}
    }

    //? if >=26.1.2 {
    public ResistantDamage(List<Key> resists) {
        super(resists);
    }

    private static List<Key> resolveKeys(RegistryKeySet<DamageType> types) {
        Registry<DamageType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE);
        List<Key> keys = new ArrayList<>();
        types.resolve(registry).forEach(type -> {
            NamespacedKey key = registry.getKey(type);
            if (key != null) keys.add(Key.key(key.asString()));
        });
        return keys;
    }
    //?}

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        //? if >=26.1.2 {
        List<TypedKey<DamageType>> keys = new ArrayList<>();
        for (Key key : value) {
            keys.add(TypedKey.create(RegistryKey.DAMAGE_TYPE, key));
        }
        stack.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(RegistrySet.keySet(RegistryKey.DAMAGE_TYPE, keys)));
        //?} else {
        /*stack.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(TagKey.create(RegistryKey.DAMAGE_TYPE, value)));
         *///?}
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.DAMAGE_RESISTANT);
    }
}