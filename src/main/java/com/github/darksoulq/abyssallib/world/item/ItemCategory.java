package com.github.darksoulq.abyssallib.world.item;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Supplier;

/**
 * Represents a categorized collection of {@link Item} instances.
 * <p>
 * Categories are identified by a unique {@link Identifier} and include an icon
 * for display in graphical user interfaces.
 */
public class ItemCategory {

    /**
     * The unique identifier for this category.
     */
    private final Identifier id;

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
     * @param id    The category identifier.
     * @param icon  The icon supplier.
     * @param items The list of items to include.
     */
    private ItemCategory(Identifier id, Supplier<ItemStack> icon, List<Item> items) {
        this.id = id;
        this.icon = icon;
        this.items = new ArrayList<>(items);
    }

    /**
     * @return The {@link Identifier} of this category.
     */
    public Identifier getId() {
        return id;
    }

    /**
     * Gets the translatable title of this category.
     * <p>
     * The translation key follows the format: {@code category.item.[namespace].[path]}
     *
     * @return A {@link Component} representing the localized title.
     */
    public Component getTitle() {
        return Component.translatable("category.item." + id.getNamespace() + "." + id.getPath());
    }

    /**
     * @return A new {@link ItemStack} representing this category's icon.
     */
    public ItemStack getIcon() {
        return icon.get();
    }

    /**
     * @return An unmodifiable view of the {@link Item}s in this category.
     */
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Creates a new builder for an ItemCategory.
     *
     * @param id The {@link Identifier} for the new category.
     * @return A new {@link Builder} instance.
     */
    public static Builder builder(Identifier id) {
        return new Builder(id);
    }

    /**
     * A fluent builder for creating {@link ItemCategory} instances.
     */
    public static class Builder {

        /**
         * The identifier for the category being built.
         */
        private final Identifier id;

        /**
         * The supplier for the category icon.
         */
        private Supplier<ItemStack> icon;

        /**
         * The list of items being collected for the category.
         */
        private final List<Item> items = new ArrayList<>();

        /**
         * Constructs a builder with a mandatory identifier.
         *
         * @param id The {@link Identifier} to assign.
         */
        public Builder(Identifier id) {
            this.id = id;
        }

        /**
         * Sets the icon using a lazy supplier.
         *
         * @param icon A {@link Supplier} providing the {@link ItemStack}.
         * @return This builder for chaining.
         */
        public Builder icon(Supplier<ItemStack> icon) {
            this.icon = icon;
            return this;
        }

        /**
         * Sets the icon using a static ItemStack.
         *
         * @param icon The {@link ItemStack} to use as an icon.
         * @return This builder for chaining.
         */
        public Builder icon(ItemStack icon) {
            this.icon = () -> icon;
            return this;
        }

        /**
         * Sets the icon using an {@link Item} instance.
         *
         * @param icon The item to extract the stack from.
         * @return This builder for chaining.
         */
        public Builder icon(Item icon) {
            return icon(icon.getStack());
        }

        /**
         * Sets the icon using a {@link Holder} of an {@link Item}.
         *
         * @param icon The holder containing the icon item.
         * @return This builder for chaining.
         */
        public Builder icon(Holder<Item> icon) {
            return icon(icon.get());
        }

        /**
         * Adds a single item to the category.
         *
         * @param item The {@link Item} to add.
         * @return This builder for chaining.
         */
        public Builder add(Item item) {
            this.items.add(item);
            return this;
        }

        /**
         * Adds a single item to the category via its holder.
         *
         * @param holder The {@link Holder} of the item to add.
         * @return This builder for chaining.
         */
        public Builder add(Holder<Item> holder) {
            return add(holder.get());
        }

        /**
         * Adds multiple items to the category.
         *
         * @param items The {@link Item}s to add.
         * @return This builder for chaining.
         */
        public Builder add(Item... items) {
            this.items.addAll(Arrays.asList(items));
            return this;
        }

        /**
         * Adds multiple items to the category via their holders.
         *
         * @param items The {@link Holder}s of items to add.
         * @return This builder for chaining.
         */
        @SafeVarargs
        public final Builder add(Holder<Item>... items) {
            return addAllHolders(Arrays.asList(items));
        }

        /**
         * Adds a collection of items to the category.
         *
         * @param items The collection of {@link Item}s to add.
         * @return This builder for chaining.
         */
        public Builder addAll(Collection<Item> items) {
            this.items.addAll(items);
            return this;
        }

        /**
         * Adds a collection of item holders to the category.
         *
         * @param items The collection of {@link Holder}s to add.
         * @return This builder for chaining.
         */
        public Builder addAllHolders(Collection<Holder<Item>> items) {
            return addAll(items.stream().map(Holder::get).toList());
        }

        /**
         * Finalizes the creation of the ItemCategory.
         *
         * @return A new {@link ItemCategory} instance.
         * @throws NullPointerException if the icon has not been set.
         */
        public ItemCategory build() {
            Objects.requireNonNull(icon, "Category icon must be set before building");
            return new ItemCategory(id, icon, items);
        }
    }
}