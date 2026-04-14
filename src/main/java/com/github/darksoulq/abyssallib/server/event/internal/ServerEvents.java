package com.github.darksoulq.abyssallib.server.event.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.energy.EnergyNetwork;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.command.CommandBus;
import com.github.darksoulq.abyssallib.server.command.internal.InternalCommand;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.custom.server.PacketReceiveEvent;
import com.github.darksoulq.abyssallib.server.event.custom.server.PacketSendEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.translation.ServerTranslator;
import com.github.darksoulq.abyssallib.server.translation.internal.PacketTranslator;
import com.github.darksoulq.abyssallib.server.util.TaskUtil;
import com.github.darksoulq.abyssallib.world.advancement.Advancement;
import com.github.darksoulq.abyssallib.world.advancement.AdvancementLoader;
import com.github.darksoulq.abyssallib.world.block.internal.BlockManager;
import com.github.darksoulq.abyssallib.world.data.internal.MapLoader;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import com.github.darksoulq.abyssallib.world.data.loot.LootLoader;
import com.github.darksoulq.abyssallib.world.data.loot.LootTable;
import com.github.darksoulq.abyssallib.world.data.loot.MergeStrategy;
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics;
import com.github.darksoulq.abyssallib.world.data.tag.TagLoader;
import com.github.darksoulq.abyssallib.world.entity.data.EntityAttributes;
import com.github.darksoulq.abyssallib.world.entity.internal.EntityManager;
import com.github.darksoulq.abyssallib.world.entity.internal.NaturalSpawnRegistry;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenLoader;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenManager;
import com.github.darksoulq.abyssallib.world.recipe.RecipeLoader;
import com.github.darksoulq.abyssallib.world.structure.StructureLoader;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.advancements.TreeNodePosition;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

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
                    EntityManager.load();
                    EntityManager.restoreEntities();
                    EnergyNetwork.load();
                    EntityAttributes.init();
                    Try.run(PlayerStatistics::init);
                    RecipeLoader.reload();
                    TagLoader.loadFolder(new File(AbyssalLib.getInstance().getDataFolder(), "tags"));
                    NaturalSpawnRegistry.load();
                    StructureLoader.load();
                    WorldGenLoader.load();
                    AdvancementLoader.load();
                    AbyssalLib.PACK_SERVER.loadThirdPartyPacks();
                }
            }.runTaskLater(AbyssalLib.getInstance(), 10);
        } else {
            TaskUtil.delayedTask(AbyssalLib.getInstance(), 1, RecipeLoader::reload);
            reloadAdvancements();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPlayer() == null) return;
        Packet<?> original = event.getPacket();
        Packet<?> translated = PacketTranslator.processSend(original, event.getPlayer());

        if (original != translated) {
            event.setPacket(translated);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPlayer() == null) return;
        Packet<?> original = event.getPacket();
        Packet<?> unTranslated = PacketTranslator.processReceive(original, event.getPlayer());

        if (original != unTranslated) {
            event.setPacket(unTranslated);
        }
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
        Entity entity = e.getEntity();
        NamespacedKey customTableKey = new NamespacedKey("abyssallib", "custom_loot_table");
        boolean usedCustom = false;

        if (entity.getPersistentDataContainer().has(customTableKey, PersistentDataType.STRING)) {
            String tableId = entity.getPersistentDataContainer().get(customTableKey, PersistentDataType.STRING);
            LootTable table = Registries.LOOT_TABLES.get(tableId);

            if (table != null && table.getMergeStrategy() == MergeStrategy.NONE) {
                e.getDrops().clear();

                EntityDamageEvent damageEvent = entity.getLastDamageCause();
                Entity killer = null;
                if (damageEvent instanceof EntityDamageByEntityEvent edev) {
                    killer = edev.getDamager();
                }

                LootContext.Builder builder = LootContext.builder(entity.getLocation()).victim(entity);
                if (killer != null) {
                    builder.looter(killer).killer(killer);
                    if (killer instanceof LivingEntity livingEntity) {
                        builder.tool(((CraftLivingEntity) livingEntity).getHandle().getMainHandItem().getBukkitStack());
                    }
                }

                e.getDrops().addAll(table.generate(builder.build()));
                usedCustom = true;
            }
        }

        if (!usedCustom) {
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
                        builder.looter(killer).killer(killer);
                        if (killer instanceof LivingEntity livingEntity) builder.tool(((CraftLivingEntity) livingEntity).getHandle().getMainHandItem().getBukkitStack());
                    }

                    List<ItemStack> generated = table.generate(builder.build());
                    e.getDrops().addAll(generated);
                }
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null) {
            checkAndGenerateLoot(e.getClickedBlock(), e.getPlayer());
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        checkAndGenerateLoot(e.getBlock(), e.getPlayer());
    }

    private void checkAndGenerateLoot(Block block, @Nullable Player looter) {
        if (block.getState() instanceof Container container) {
            PersistentDataContainer pdc = container.getPersistentDataContainer();
            NamespacedKey tableKey = new NamespacedKey("abyssallib", "loot_table");

            if (pdc.has(tableKey, PersistentDataType.STRING)) {
                String tableId = pdc.get(tableKey, PersistentDataType.STRING);
                pdc.remove(tableKey);

                NamespacedKey seedKey = new NamespacedKey("abyssallib", "loot_seed");
                long seed = pdc.getOrDefault(seedKey, PersistentDataType.LONG, new Random().nextLong());
                pdc.remove(seedKey);

                container.update();

                LootTable table = Registries.LOOT_TABLES.get(tableId);
                if (table != null) {
                    Random random = new Random(seed);
                    LootContext.Builder builder = LootContext.builder(block.getLocation()).random(random);
                    if (looter != null) builder.looter(looter);

                    table.fill(container.getInventory(), builder.build());
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldInit(WorldInitEvent e) {
        WorldGenManager.inject(e.getWorld());
    }

    @SubscribeEvent
    public void onWorldLoad(WorldLoadEvent e) {
        WorldGenManager.inject(e.getWorld());
    }

    private static void reloadAdvancements() {
        List<AdvancementHolder> newAdvancements = new ArrayList<>();
        Map<AdvancementHolder, Advancement> customAdvancementMap = new HashMap<>();

        for (Advancement adv : Registries.ADVANCEMENTS.getAll().values()) {
            AdvancementHolder holder = adv.toNMSHolder();
            newAdvancements.add(holder);
            customAdvancementMap.put(holder, adv);

            ServerAdvancementManager manager = MinecraftServer.getServer().getAdvancements();
            Map<Identifier, AdvancementHolder> mutableAdvancements = new HashMap<>(manager.advancements);
            mutableAdvancements.put(holder.id(), holder);
            manager.advancements = mutableAdvancements;
        }

        applyAdvancementLayout(newAdvancements, customAdvancementMap);
    }

    public static void applyAdvancementLayout(List<AdvancementHolder> newAdvancements, Map<AdvancementHolder, Advancement> customAdvancementMap) {
        if (!newAdvancements.isEmpty()) {
            ServerAdvancementManager manager = MinecraftServer.getServer().getAdvancements();
            AdvancementTree tree = manager.tree();
            tree.addAll(newAdvancements);

            for (AdvancementNode root : tree.roots()) {
                if (root.advancement().display().isPresent()) {
                    TreeNodePosition.run(root);
                }
            }

            for (AdvancementHolder holder : newAdvancements) {
                Advancement customAdv = customAdvancementMap.get(holder);
                if (customAdv != null && customAdv.getDisplay() != null) {
                    float customX = customAdv.getDisplay().getX();
                    float customY = customAdv.getDisplay().getY();
                    if (!Float.isNaN(customX) && !Float.isNaN(customY)) {
                        holder.value().display().ifPresent(info -> info.setLocation(customX, customY));
                    }
                }
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                ServerPlayer sp = ((CraftPlayer) player).getHandle();
                sp.getAdvancements().reload(manager);
            }
        }
    }
}