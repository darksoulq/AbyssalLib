package com.github.darksoulq.abyssallib.server.event.internal;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.ClickType;
import com.github.darksoulq.abyssallib.server.event.InventoryClickType;
import com.github.darksoulq.abyssallib.server.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.server.event.context.item.AnvilContext;
import com.github.darksoulq.abyssallib.server.event.context.item.UseContext;
import com.github.darksoulq.abyssallib.server.event.custom.server.PacketSendEvent;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.component.builtin.EntitySpawner;
import com.github.darksoulq.abyssallib.world.item.internal.ItemTicker;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import io.papermc.paper.registry.PaperRegistries;
import io.papermc.paper.registry.PaperRegistryAccess;
import io.papermc.paper.registry.keys.DamageTypeKeys;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemEvents {
    private static final List<DamageType> MELEE_TYPES = new ArrayList<>();
    private static final List<DamageType> RANGED_TYPES = new ArrayList<>();

    static {
        Registry<DamageType> reg = PaperRegistryAccess.instance().getRegistry(PaperRegistries.registryFromNms(Registries.DAMAGE_TYPE));
        addMeleeDamageType(reg.get(DamageTypeKeys.MACE_SMASH));
        addMeleeDamageType(reg.get(DamageTypeKeys.MOB_ATTACK));
        addMeleeDamageType(reg.get(DamageTypeKeys.MOB_ATTACK_NO_AGGRO));
        addMeleeDamageType(reg.get(DamageTypeKeys.PLAYER_ATTACK));
        addMeleeDamageType(reg.get(DamageTypeKeys.PLAYER_EXPLOSION));
        addRangedDamageType(reg.get(DamageTypeKeys.ARROW));
        addRangedDamageType(reg.get(DamageTypeKeys.MOB_PROJECTILE));
        addRangedDamageType(reg.get(DamageTypeKeys.THROWN));
        addRangedDamageType(reg.get(DamageTypeKeys.WIND_CHARGE));
        addRangedDamageType(reg.get(DamageTypeKeys.TRIDENT));
    }

    public static void addMeleeDamageType(DamageType type) {
        MELEE_TYPES.add(type);
    }
    public static void addRangedDamageType(DamageType type) {
        RANGED_TYPES.add(type);
    }


    @SubscribeEvent(ignoreCancelled = false)
    public void onInventoryUpdate(PacketSendEvent event) {
        if (event.getPacket() instanceof ClientboundContainerSetSlotPacket
            || event.getPacket() instanceof ClientboundContainerSetContentPacket
            || event.getPacket() instanceof ClientboundSetPlayerInventoryPacket
            || event.getPlayer() instanceof ClientboundContainerSetDataPacket) return;
        ItemTicker.update(event.getPlayer());
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onPlayerJoin(PlayerJoinEvent event) {
        ItemTicker.update(event.getPlayer());
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onPlayerQuit(PlayerQuitEvent event) {
        ItemTicker.remove(event.getPlayer());
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onBlockMine(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Item item = Item.resolve(event.getPlayer().getActiveItem());
        if (item != null) {
            ActionResult result = item.postMine(player, block);
            if (result == ActionResult.CANCEL) {
                event.setCancelled(true);
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity source = event.getDamager();
        Entity target = event.getEntity();
        if (source instanceof LivingEntity lSource) {
            DamageType type = event.getDamageSource().getDamageType();
            Item item = null;
            if (MELEE_TYPES.contains(type)) {
                item = Item.resolve(lSource.getEquipment().getItemInMainHand());
            } else if (RANGED_TYPES.contains(type)) {
                ItemStack main = lSource.getEquipment().getItemInMainHand();
                ItemStack off = lSource.getEquipment().getItemInOffHand();
                if (main.hasData(DataComponentTypes.CHARGED_PROJECTILES) ||
                    main.getType().equals(Material.BOW)) {
                    item = Item.resolve(main);
                } else if (off.hasData(DataComponentTypes.CHARGED_PROJECTILES) ||
                    off.getType().equals(Material.BOW)) {
                    item = Item.resolve(off);
                }
            }
            if (item != null) {
                ActionResult result = item.postHit(lSource, target);
                if (result == ActionResult.CANCEL) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();
        Item item = Item.resolve(stack);
        if (item != null) {
            if (block != null && (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                UseContext ctx = new UseContext(
                    player,
                    block,
                    event.getBlockFace(),
                    null,
                    ClickType.of(event.getAction()),
                    event.getHand()
                );
                if (item.onUseOn(ctx) == ActionResult.CANCEL) {
                    event.setCancelled(true);
                    return;
                }
                if (item.hasData(EntitySpawner.class)) {
                    EntitySpawner spawner = (EntitySpawner) item.getData(EntitySpawner.class);
                    com.github.darksoulq.abyssallib.world.entity.Entity<?> entity =
                        com.github.darksoulq.abyssallib.server.registry.Registries.ENTITIES.get(spawner.getValue().toString());
                    if (entity == null) return;
                    Location loc = block.getLocation().clone().add(0, 1, 0);
                    entity.clone().spawn(loc);
                }
            }
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (item.onUse(player, event.getHand(), ClickType.of(event.getAction())) == ActionResult.CANCEL)
                    event.setCancelled(true);
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onClickInInventory(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof PlayerInventory pInv)) return;
        if (event.getClick().isKeyboardClick()) {
            int hotbarSlot = event.getHotbarButton();
            if (hotbarSlot >= 0 && hotbarSlot <= 8) {
                Item item = Item.resolve(event.getClickedInventory().getItem(hotbarSlot));
                if (item != null
                        && item.onClickInInventory((Player) event.getWhoClicked(), hotbarSlot, pInv, InventoryClickType.of(event.getClick())) == ActionResult.CANCEL) event.setCancelled(true);
            }
        }
        Item item = Item.resolve(event.getCurrentItem());
        if (item == null) return;
        if (item.onClickInInventory((Player) event.getWhoClicked(), event.getSlot(), pInv, InventoryClickType.of(event.getClick())) == ActionResult.CANCEL) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onChangeSlot(PlayerInventorySlotChangeEvent event) {
        Item item = Item.resolve(event.getNewItemStack());
        if (item == null) return;
        item.onSlotChange(event.getPlayer(), event.getSlot());
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onUseEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItem(event.getHand());
        Item item = Item.resolve(stack);
        if (item != null) {
            UseContext ctx = new UseContext(
                player,
                null,
                null,
                event.getRightClicked(),
                ClickType.RIGHT_CLICK,
                event.getHand()
            );
            ActionResult result = item.onUseOn(ctx);
            if (result == ActionResult.CANCEL) {
                event.setCancelled(true);
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onItemDrop(PlayerDropItemEvent event) {
        Item item = Item.resolve(event.getItemDrop().getItemStack());
        if (item == null) return;
        if (item.onDrop(event.getPlayer()) == ActionResult.CANCEL) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onItemPickup(PlayerAttemptPickupItemEvent event) {
        Item item = Item.resolve(event.getItem().getItemStack());
        if (item == null) return;
        if (item.onPickup(event.getPlayer()) == ActionResult.CANCEL) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onSwapItemHand(PlayerSwapHandItemsEvent event) {
        Item i1 = Item.resolve(event.getMainHandItem());
        Item i2 = Item.resolve(event.getOffHandItem());

        ActionResult r1 = null;
        ActionResult r2 = null;

        if (i1 != null) r1 = i1.onSwapHand(event.getPlayer(), EquipmentSlot.HAND);
        if (i2 != null) r2 = i2.onSwapHand(event.getPlayer(), EquipmentSlot.HAND);

        if (r1 == ActionResult.CANCEL || r2 == ActionResult.CANCEL) event.setCancelled(true);
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onAnvilCombine(PrepareAnvilEvent event) {
        ItemStack[] stacks = event.getInventory().getContents();
        for (ItemStack stack : stacks) {
            Item item = Item.resolve(stack);
            if (item != null) {
                ActionResult result = item.onAnvilPrepare(new AnvilContext(event));
                if (result == ActionResult.CANCEL) {
                    event.getInventory().setResult(null);
                }
                break;
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onCrafted(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        Item item = Item.resolve(event.getRecipe().getResult());
        if (item != null) {
            item.onCraftedBy(player);
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onSlotChange(PlayerInventorySlotChangeEvent event) {
        Item item = Item.resolve(event.getNewItemStack());
        if (item != null) {
            event.setShouldTriggerAdvancements(false);
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack[] contents = inventory.getContents();

        if (inventory.getRecipe() == null) return;
        boolean isCustom = true;
        Recipe recipe = event.getRecipe();
        if (recipe instanceof Keyed keyed && "minecraft".equals(keyed.getKey().getNamespace())) {
            isCustom = false;
        }
        if (isCustom) return;
        for (ItemStack item : contents) {
            if (Item.resolve(item) != null) {
                inventory.setResult(null);
                return;
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        SmithingInventory inventory = event.getInventory();
        ItemStack a = inventory.getInputTemplate();
        ItemStack b = inventory.getInputEquipment();
        ItemStack c = inventory.getInputMineral();

        if (inventory.getRecipe() == null) return;
        boolean isCustom = true;
        for (Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext(); ) {
            Recipe recipe = it.next();
            if (recipe instanceof Keyed keyed && "minecraft".equals(keyed.getKey().getNamespace())) {
                isCustom = false;
                break;
            }
        }
        if (isCustom) return;

        if (Item.resolve(a) != null || Item.resolve(b) != null || Item.resolve(c) != null) {
            inventory.setResult(null);
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        ItemStack source = event.getFuel();
        Block block = event.getBlock();

        BlockState furnace = block.getState();
        FurnaceInventory inv = (FurnaceInventory) ((Container) furnace).getInventory();
        ItemStack input = inv.getSmelting();

        if (Item.resolve(input) != null || Item.resolve(source) != null) {
            event.setCancelled(true);
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onCampfireCook(BlockCookEvent event) {
        ItemStack a = event.getSource();

        if (event.getRecipe() == null) return;
        boolean isCustom = true;
        for (Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext(); ) {
            Recipe recipe = it.next();
            if (recipe instanceof Keyed keyed && "minecraft".equals(keyed.getKey().getNamespace())) {
                isCustom = false;
                break;
            }
        }
        if (isCustom) return;

        if (Item.resolve(a) != null) {
            event.setCancelled(true);
        }
    }

    @SubscribeEvent(ignoreCancelled = false)
    public void onLoomPrepareEvent(PrepareResultEvent event) {
        InventoryView view = event.getView();
        if (view.getTopInventory().getType() != InventoryType.LOOM) return;
        ItemStack result = event.getResult();
        if (result == null) return;
        ;
        if (Item.resolve(result) != null) return;

        Inventory inv = view.getTopInventory();
        ItemStack base = inv.getItem(0);
        ItemStack dye = inv.getItem(1);
        ItemStack pattern = inv.getItem(2);

        if (Item.resolve(base) != null || Item.resolve(dye) != null || Item.resolve(pattern) != null) {
            event.setResult(null);
        }
    }
}
