package com.github.darksoulq.abyssallib.world.item;

import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Supplier;

/**
 * Represents a categorized collection of {@link Item} instances.
 * Categories are identified by a unique {@link Key} and include an icon
 * used for display in graphical user interfaces or creative menus.
 */
public class ItemCategory {

    /**
     * The unique {@link Key} for this category.
     */
    private final Key id;

    /**
     * A supplier that provides the icon {@link ItemStack} for this category.
     */
    private final Supplier<ItemStack> icon;

    /**
     * The list of items contained within this category.
     */
    private final List<Item> items;

    /**
     * Private constructor for the builder.
     *
     * @param id
     * The category {@link Key}.
     * @param icon
     * The icon supplier.
     * @param items
     * The list of items to include.
     */
    private ItemCategory(Key id, Supplier<ItemStack> icon, List<Item> items) {
        this.id = id;
        this.icon = icon;
        this.items = new ArrayList<>(items);
    }

    /**
     * Retrieves the unique Key associated with this category.
     *
     * @return
     * The {@link Key} of this category.
     */
    public Key getId() {
        return id;
    }

    /**
     * Gets the translatable title of this category.
     * The translation key follows the format: {@code category.item.[namespace].[path]}
     *
     * @return
     * A {@link Component} representing the localized title.
     */
    public Component getTitle() {
        return Component.translatable("category.item." + id.namespace() + "." + id.value());
    }

    /**
     * Generates a new ItemStack to be used as the visual icon for this category.
     *
     * @return
     * A new {@link ItemStack} representing this category's icon.
     */
    public ItemStack getIcon() {
        return icon.get();
    }

    /**
     * Retrieves the list of all items registered to this category.
     *
     * @return
     * An unmodifiable view of the {@link Item}s in this category.
     */
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Creates a new builder for an ItemCategory.
     *
     * @param id
     * The {@link Key} for the new category.
     * @return
     * A new {@link Builder} instance.
     */
    public static Builder builder(Key id) {
        return new Builder(id);
    }

    /**
     * A fluent builder for creating {@link ItemCategory} instances.
     */
    public static class Builder {

        /**
         * The identifier for the category being built.
         */
        private final Key id;

        /**
         * The supplier for the category icon.
         */
        private Supplier<ItemStack> icon;

        /**
         * The list of items being collected for the category.
         */
        private final List<Item> items = new ArrayList<>();

        /**
         * Constructs a builder with a mandatory Key.
         *
         * @param id
         * The {@link Key} to assign to the category.
         */
        public Builder(Key id) {
            this.id = id;
        }

        /**
         * Sets the icon using a lazy supplier.
         *
         * @param icon
         * A {@link Supplier} providing the {@link ItemStack}.
         * @return
         * This builder for chaining.
         */
        public Builder icon(Supplier<ItemStack> icon) {
            this.icon = icon;
            return this;
        }

        /**
         * Sets the icon using a static ItemStack.
         *
         * @param icon
         * The {@link ItemStack} to use as an icon.
         * @return
         * This builder for chaining.
         */
        public Builder icon(ItemStack icon) {
            this.icon = () -> icon;
            return this;
        }

        /**
         * Sets the icon using a specific Item instance.
         *
         * @param icon
         * The {@link Item} to extract the stack from.
         * @return
         * This builder for chaining.
         */
        public Builder icon(Item icon) {
            return icon(icon.getStack());
        }

        /**
         * Adds a single item to the category.
         *
         * @param item
         * The {@link Item} to add.
         * @return
         * This builder for chaining.
         */
        public Builder add(Item item) {
            this.items.add(item);
            return this;
        }

        /**
         * Adds multiple items to the category.
         *
         * @param items
         * The array of {@link Item}s to add.
         * @return
         * This builder for chaining.
         */
        public Builder add(Item... items) {
            this.items.addAll(Arrays.asList(items));
            return this;
        }

        /**
         * Adds a collection of items to the category.
         *
         * @param items
         * The collection of {@link Item}s to add.
         * @return
         * This builder for chaining.
         */
        public Builder addAll(Collection<Item> items) {
            this.items.addAll(items);
            return this;
        }

        /**
         * Finalizes the creation of the ItemCategory.
         *
         * @return
         * A new {@link ItemCategory} instance.
         * @throws NullPointerException
         * If the icon has not been set prior to building.
         */
        public ItemCategory build() {
            Objects.requireNonNull(icon, "Category icon must be set before building");
            return new ItemCategory(id, icon, items);
        }
    }
}