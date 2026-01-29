package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.energy.EnergyNetwork;
import com.github.darksoulq.abyssallib.server.command.CommandBus;
import com.github.darksoulq.abyssallib.server.command.internal.InternalCommand;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.server.PacketSendEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.translation.ServerTranslator;
import com.github.darksoulq.abyssallib.server.translation.internal.PacketTranslator;
import com.github.darksoulq.abyssallib.world.block.internal.BlockManager;
import com.github.darksoulq.abyssallib.world.data.internal.MapLoader;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import com.github.darksoulq.abyssallib.world.data.loot.LootTable;
import com.github.darksoulq.abyssallib.world.data.loot.MergeStrategy;
import com.github.darksoulq.abyssallib.world.data.loot.internal.LootLoader;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.data.tag.TagLoader;
import com.github.darksoulq.abyssallib.world.entity.data.EntityAttributes;
import com.github.darksoulq.abyssallib.world.entity.internal.EntityManager;
import com.github.darksoulq.abyssallib.world.entity.internal.NaturalSpawnRegistry;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenLoader;
import com.github.darksoulq.abyssallib.world.multiblock.internal.MultiblockManager;
import com.github.darksoulq.abyssallib.world.recipe.RecipeLoader;
import com.github.darksoulq.abyssallib.world.structure.internal.StructureLoader;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ServerEvents {
    @SubscribeEvent(ignoreCancelled = false)
    public void onServerLoad(ServerLoadEvent e) {
        if (e.getType() == ServerLoadEvent.LoadType.STARTUP) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    MapLoader.load();
                    ServerTranslator.init();
                    LootLoader.load();
                    CommandBus.register(AbyssalLib.PLUGIN_ID, new InternalCommand());
                    BlockManager.load();
                    MultiblockManager.load();
                    EntityManager.load();
                    EntityManager.restoreEntities();
                    EnergyNetwork.load();
                    EntityAttributes.init();
                    PlayerStatistics.init();
                    RecipeLoader.reload();
                    TagLoader.loadTags();
                    NaturalSpawnRegistry.load();
                    StructureLoader.load();
                    WorldGenLoader.load();
                    AbyssalLib.PACK_SERVER.loadThirdPartyPacks();
                }
            }.runTaskLater(AbyssalLib.getInstance(), 10);
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPlayer() == null) return;
        PacketTranslator.process(event.getPacket(), event.getPlayer());
    }

    @SubscribeEvent
    public void onLoot(LootGenerateEvent e) {
        String key = e.getLootTable().getKey().toString();

        for (LootTable table : Registries.LOOT_TABLES.getAll().values()) {
            if (table.getVanillaId() != null && table.getVanillaId().equals(key)) {
                if (table.getMergeStrategy() == MergeStrategy.NONE) continue;

                if (table.getMergeStrategy() == MergeStrategy.REPLACE) {
                    e.getLoot().clear();
                }

                LootContext context = LootContext.builder(e.getEntity() != null ? e.getEntity().getLocation() : e.getLootContext().getLocation())
                    .looter(e.getLootContext().getKiller())
                    .killer(e.getLootContext().getKiller())
                    .victim(e.getEntity())
                    .luck(e.getLootContext().getLuck())
                    .build();

                List<ItemStack> generated = table.generate(context);
                e.getLoot().addAll(generated);
            }
        }
    }

    @SubscribeEvent
    public void onEntityDeath(EntityDeathEvent e) {
        NamespacedKey entityKey = e.getEntityType().getKey();
        String vanillaTableKey = "minecraft:entities/" + entityKey.getKey();

        for (LootTable table : Registries.LOOT_TABLES.getAll().values()) {
            if (table.getVanillaId() != null && table.getVanillaId().equals(vanillaTableKey)) {
                if (table.getMergeStrategy() == MergeStrategy.NONE) continue;

                if (table.getMergeStrategy() == MergeStrategy.REPLACE) {
                    e.getDrops().clear();
                }

                EntityDamageEvent damageEvent = e.getEntity().getLastDamageCause();
                Entity killer = null;
                if (damageEvent instanceof EntityDamageByEntityEvent edev) {
                    killer = edev.getDamager();
                }
                LootContext.Builder builder = LootContext.builder(e.getEntity().getLocation()).victim(e.getEntity());

                if (killer != null) {
                    builder.looter(killer)
                        .killer(killer);
                    if (killer instanceof LivingEntity livingEntity) builder.tool(((CraftLivingEntity) livingEntity).getHandle().getMainHandItem().getBukkitStack());
                }

                List<ItemStack> generated = table.generate(builder.build());
                e.getDrops().addAll(generated);
            }
        }
    }
}