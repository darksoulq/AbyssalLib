package dev.jsinco.brewery.bukkit.api.integration;

import dev.jsinco.brewery.api.integration.Integration;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ItemIntegration extends Integration {
    Optional<ItemStack> createItem(String id);
    boolean isIngredient(String id);
    Component displayName(String id);
    String getItemId(ItemStack stack);
    CompletableFuture<Void> initialized();
}