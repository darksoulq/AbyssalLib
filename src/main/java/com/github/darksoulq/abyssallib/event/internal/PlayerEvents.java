package com.github.darksoulq.abyssallib.event.internal;

import com.github.darksoulq.abyssallib.event.SubscribeEvent;
import com.github.darksoulq.abyssallib.event.custom.player.PlayerExitWaterEvent;
import com.github.darksoulq.abyssallib.event.custom.player.PlayerEnterWaterEvent;
import com.github.darksoulq.abyssallib.resource.glyph.Glyph;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class PlayerEvents {

    @SubscribeEvent
    public void onChat(AsyncChatEvent e) {
        Component result = e.message();
        for (String placeholder : Glyph.getChatMap().keySet()) {
            result = e.message().replaceText(TextReplacementConfig.builder()
                    .matchLiteral(placeholder)
                    .replacement(Glyph.getChatMap().get(placeholder).toString())
                    .build());
        }

        e.message(result);
    }

    @SubscribeEvent
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Block fromBlock = event.getFrom().getBlock();
        Block toBlock = event.getTo().getBlock();

        boolean wasInWater = isWaterBlock(fromBlock);
        boolean isInWater = isWaterBlock(toBlock);

        Vector velocity = player.getVelocity();

        if (!wasInWater && isInWater && velocity.getY() < -0.1) {
            PlayerEnterWaterEvent fallEvent = new PlayerEnterWaterEvent(player, toBlock.getLocation(), velocity);
            Bukkit.getServer().getPluginManager().callEvent(fallEvent);
        }

        if (wasInWater && !isInWater) {
            PlayerExitWaterEvent exitEvent = new PlayerExitWaterEvent(player, toBlock.getLocation());
            Bukkit.getServer().getPluginManager().callEvent(exitEvent);
        }
    }

    @SubscribeEvent
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().addCustomChatCompletions(Glyph.getChatMap().keySet().stream().sorted().toList());
    }

    /**
     * Checks if the block directly below the player's feet is solid.
     */
    private boolean isBlockSolidBelow(Player player) {
        Location loc = player.getLocation().clone().subtract(0, 0.1, 0);
        Block blockBelow = loc.getBlock();
        return blockBelow.getType().isSolid();
    }

    private boolean isWaterBlock(Block block) {
        Material type = block.getType();
        return type == Material.WATER || type == Material.KELP_PLANT || type == Material.SEAGRASS;
    }
}
