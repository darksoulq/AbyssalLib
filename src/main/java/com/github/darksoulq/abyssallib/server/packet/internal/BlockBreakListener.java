package com.github.darksoulq.abyssallib.server.packet.internal;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.packet.PacketEvents;
import com.github.darksoulq.abyssallib.server.packet.PacketPriority;
import com.github.darksoulq.abyssallib.world.level.block.Block;
import com.github.darksoulq.abyssallib.world.level.block.BlockProperties;
import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BlockBreakListener {

    private record BreakState(Location location, float progress, int stage, float speed) {}

    private static final Identifier MODIFIER_ID = Identifier.of("abyssallib", "break_modifier");

    private final Map<UUID, BreakState> activeBreaks = new HashMap<>();
    private final Map<UUID, Collection<AttributeModifier>> savedModifiers = new HashMap<>();

    public void register() {
        PacketEvents.listenIncoming(ServerboundPlayerActionPacket.class, PacketPriority.NORMAL, this::handlePacket, false);
    }

    private boolean handlePacket(ServerPlayer nmsPlayer, ServerboundPlayerActionPacket packet) {
        Player player = nmsPlayer.getBukkitEntity();
        UUID uuid = player.getUniqueId();
        Location loc = new Location(player.getWorld(), packet.getPos().getX(), packet.getPos().getY(), packet.getPos().getZ());

        Block custom = Block.from(loc.getBlock());
        if (custom == null) return false;

        BlockProperties props = custom.properties;
        ItemStack tool = player.getInventory().getItemInMainHand();
        List<Player> players = loc.getNearbyPlayers(Bukkit.getServer().getViewDistance() * 16).stream().toList();

        switch (packet.getAction()) {
            case START_DESTROY_BLOCK -> {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.breakBlock(loc.getBlock());
                    return true;
                }

                float speed = getBreakSpeed(player, props, tool);
                if (speed <= 0f) return true;

                AttributeInstance attr = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
                if (attr != null) applyModifier(attr, uuid);

                for (Player p : players) {
                    p.sendBlockDamage(loc, 0f);
                }
                activeBreaks.put(uuid, new BreakState(loc, 0f, 0, speed));

                new BreakProgress(player, players).runTaskTimer(AbyssalLib.getInstance(), 0, 1);
                return true;
            }

            case ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK -> {
                activeBreaks.remove(uuid);
                for (Player p : players) {
                    p.sendBlockDamage(loc, 0f);
                }
                restoreAttribute(player.getAttribute(Attribute.BLOCK_BREAK_SPEED), uuid);
                return true;
            }

            default -> {
                activeBreaks.remove(uuid);
                for (Player p : players) {
                    p.sendBlockDamage(loc, 0f);
                }
                restoreAttribute(player.getAttribute(Attribute.BLOCK_BREAK_SPEED), uuid);
            }
        }

        return false;
    }

    private float getBreakSpeed(Player player, BlockProperties props, ItemStack tool) {
        if (props.hardness < 0) return 0f;

        boolean correctTool = props.isPreferredTool(tool);
        float base = correctTool ? 1.0f : 0.2f;

        int eff = tool.getEnchantmentLevel(Enchantment.EFFICIENCY);
        if (eff > 0) base += eff * eff + 1;

        if (player.hasPotionEffect(PotionEffectType.MINING_FATIGUE)) {
            int level = player.getPotionEffect(PotionEffectType.MINING_FATIGUE).getAmplifier() + 1;
            float[] mult = {1.0f, 0.3f, 0.09f, 0.0027f, 0.00081f};
            base *= level < mult.length ? mult[level] : mult[4];
        }

        return base / props.hardness;
    }

    private void applyModifier(AttributeInstance attr, UUID uuid) {
        Collection<AttributeModifier> current = new ArrayList<>(attr.getModifiers());
        savedModifiers.put(uuid, current);

        for (AttributeModifier mod : current) attr.removeModifier(mod);

        double base = attr.getBaseValue();
        attr.addModifier(new AttributeModifier(
                MODIFIER_ID.toNamespace(), -base, AttributeModifier.Operation.ADD_NUMBER
        ));
    }

    private void restoreAttribute(AttributeInstance attr, UUID uuid) {
        if (attr == null) return;

        attr.removeModifier(MODIFIER_ID.toKey());
        Collection<AttributeModifier> saved = savedModifiers.remove(uuid);
        if (saved != null) for (AttributeModifier mod : saved) attr.addModifier(mod);
    }

    private class BreakProgress extends BukkitRunnable {
        private final Player player;
        private final UUID uuid;
        private final List<Player> players;

        BreakProgress(Player player, List<Player> players) {
            this.player = player;
            this.uuid = player.getUniqueId();
            this.players = players;
        }

        @Override
        public void run() {
            BreakState state = activeBreaks.get(uuid);
            if (state == null) {
                cancel();
                return;
            }

            float progress = state.progress + state.speed;
            if (progress >= 1.0f) {
                player.breakBlock(state.location.getBlock());
                activeBreaks.remove(uuid);
                restoreAttribute(player.getAttribute(Attribute.BLOCK_BREAK_SPEED), uuid);
                for (Player p : players) {
                    p.sendBlockDamage(state.location, 0f);
                }
                cancel();
            } else {
                progress = Math.min(progress, 1.0f);
                for (Player p : players) {
                    p.sendBlockDamage(state.location, progress);
                }
                activeBreaks.put(uuid, new BreakState(state.location, progress, 0, state.speed));
            }
        }
    }
}
