package com.github.darksoulq.abyssallib.bootstrap.compat;

import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.component.builtin.ItemName;
import dev.jsinco.brewery.bukkit.api.integration.ItemIntegration;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BrewingProjectProvider implements ItemIntegration {

    public final CompletableFuture<Void> initialized = new CompletableFuture<>();

    @Override
    public Optional<ItemStack> createItem(String id) {
        Item item = Registries.ITEMS.get(id);
        if (item == null) return Optional.empty();
        return Optional.of(item.getStack().clone());
    }

    @Override
    public boolean isIngredient(String id) {
        return Registries.ITEMS.contains(id);
    }

    @Override
    public @Nullable Component displayName(String id) {
        Item item = Registries.ITEMS.get(id);
        if (item == null) return null;
        return item.getData(ItemName.TYPE).getValue();
    }

    @Override
    public @Nullable String getItemId(ItemStack stack) {
        Item item = Item.resolve(stack);
        if (item == null) return null;
        return item.getId().asString();
    }

    @Override
    public @NonNull CompletableFuture<Void> initialized() {
        return initialized;
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
