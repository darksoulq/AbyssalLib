package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
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
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, ZombieVillager value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("villager_profession", Codecs.NAMESPACED_KEY, value.getVillagerProfession().getKey())
            .write("villager_type", Codecs.NAMESPACED_KEY, value.getVillagerType().getKey());

        if (value.isConverting()) {
            if (value.getConversionPlayer() != null) {
                ctx.write("conversion_player_uuid", Codecs.UUID, value.getConversionPlayer().getUniqueId());
            }
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof ZombieVillager zombie)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("villager_profession", Codecs.NAMESPACED_KEY, opt -> opt.ifPresent(key -> {
                Villager.Profession prof = RegistryAccess.registryAccess().getRegistry(RegistryKey.VILLAGER_PROFESSION).get(key);
                if (prof != null) zombie.setVillagerProfession(prof);
            }))
            .readOptional("villager_type", Codecs.NAMESPACED_KEY, opt -> opt.ifPresent(key -> {
                Villager.Type type = RegistryAccess.registryAccess().getRegistry(RegistryKey.VILLAGER_TYPE).get(key);
                if (type != null) zombie.setVillagerType(type);
            }))
            .readOptional("conversion_player_uuid", Codecs.UUID, opt -> opt.ifPresent(uuid -> {
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                zombie.setConversionPlayer(player);
            }));

        return ctx.result();
    }
}