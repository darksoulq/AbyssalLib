package com.github.darksoulq.abyssallib.bootstrap.compat;

import com.github.darksoulq.abyssallib.server.placeholder.CustomPlaceholderResolver;
import dev.jsinco.brewery.bukkit.api.integration.PlaceholderIntegration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.CompletableFuture;

public class BrewingPlaceholderProvider implements PlaceholderIntegration {

    public final CompletableFuture<Void> initialized = new CompletableFuture<>();

    @Override
    public TagResolver resolve(OfflinePlayer player) {
        if (player != null && player.isOnline() && player.getPlayer() != null) {
            return CustomPlaceholderResolver.resolve(player.getPlayer());
        }
        return CustomPlaceholderResolver.resolve();
    }

    @Override
    public String getId() {
        return "abyssallib";
    }

    @Override
    public boolean isEnabled() {
        return initialized.isDone();
    }
}