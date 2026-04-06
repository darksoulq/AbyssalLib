package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;

import java.util.Map;

public class ZombieVillagerEntityAdapter extends EntityAdapter<ZombieVillager> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof ZombieVillager;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, ZombieVillager value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("villager_profession"), Codecs.NAMESPACED_KEY.encode(ops, value.getVillagerProfession().getKey()));
        map.put(ops.createString("villager_type"), Codecs.NAMESPACED_KEY.encode(ops, value.getVillagerType().getKey()));

        if (value.isConverting()) {
            if (value.getConversionPlayer() != null) {
                map.put(ops.createString("conversion_player_uuid"), Codecs.UUID.encode(ops, value.getConversionPlayer().getUniqueId()));
            }
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof ZombieVillager zombie)) return;

        D profData = map.get(ops.createString("villager_profession"));
        if (profData != null) {
            Try.of(() -> Codecs.NAMESPACED_KEY.decode(ops, profData)).onSuccess(key -> {
                Villager.Profession prof = RegistryAccess.registryAccess().getRegistry(RegistryKey.VILLAGER_PROFESSION).get(key);
                if (prof != null) zombie.setVillagerProfession(prof);
            });
        }

        D typeData = map.get(ops.createString("villager_type"));
        if (typeData != null) {
            Try.of(() -> Codecs.NAMESPACED_KEY.decode(ops, typeData)).onSuccess(key -> {
                Villager.Type type = RegistryAccess.registryAccess().getRegistry(RegistryKey.VILLAGER_TYPE).get(key);
                if (type != null) zombie.setVillagerType(type);
            });
        }

        D conversionPlayerData = map.get(ops.createString("conversion_player_uuid"));
        if (conversionPlayerData != null) {
            Try.of(() -> Codecs.UUID.decode(ops, conversionPlayerData)).onSuccess(uuid -> {
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                zombie.setConversionPlayer(player);
            });
        }
    }
}