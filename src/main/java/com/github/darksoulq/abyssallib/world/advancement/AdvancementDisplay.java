package com.github.darksoulq.abyssallib.world.advancement;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

/**
 * Defines the visual representation of an advancement in the client menu.
 * This class encapsulates all metadata required by the client to render the
 * advancement's icon, title, description, and its relative position within the
 * advancement tab.
 */
public class AdvancementDisplay {

    /**
     * The codec responsible for serializing and deserializing advancement display data.
     * This allows the display information to be easily saved to or loaded from
     * various data formats like JSON or NBT.
     */
    public static final Codec<AdvancementDisplay> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.TEXT_COMPONENT.nullable().optionalFieldOf("title", null).forGetter(AdvancementDisplay.class, AdvancementDisplay::getTitle),
        Codecs.TEXT_COMPONENT.nullable().optionalFieldOf("description", null).forGetter(AdvancementDisplay.class, AdvancementDisplay::getDescription),
        Codecs.ITEM_STACK.nullable().optionalFieldOf("icon", null).forGetter(AdvancementDisplay.class, AdvancementDisplay::getIcon),
        Codecs.KEY.nullable().optionalFieldOf("background", null).forGetter(AdvancementDisplay.class, AdvancementDisplay::getBackground),
        AdvancementFrame.CODEC.optionalFieldOf("frame", AdvancementFrame.TASK).forGetter(AdvancementDisplay.class, AdvancementDisplay::getFrame),
        Codecs.BOOLEAN.optionalFieldOf("show_toast", true).forGetter(AdvancementDisplay.class, AdvancementDisplay::isShowToast),
        Codecs.BOOLEAN.optionalFieldOf("announce_to_chat", true).forGetter(AdvancementDisplay.class, AdvancementDisplay::isAnnounceToChat),
        Codecs.BOOLEAN.optionalFieldOf("hidden", false).forGetter(AdvancementDisplay.class, AdvancementDisplay::isHidden),
        Codecs.FLOAT.optionalFieldOf("x", Float.NaN).forGetter(AdvancementDisplay.class, AdvancementDisplay::getX),
        Codecs.FLOAT.optionalFieldOf("y", Float.NaN).forGetter(AdvancementDisplay.class, AdvancementDisplay::getY)
    ).apply(instance, AdvancementDisplay::new)).describe("AdvancementDisplay");

    /**
     * The display title component.
     */
    private final Component title;

    /**
     * The description component providing detail about the advancement.
     */
    private final Component description;

    /**
     * The ItemStack used as the visual icon in the GUI.
     */
    private final ItemStack icon;

    /**
     * The background texture key used if this is a root advancement.
     */
    private final Key background;

    /**
     * The visual border style (Task, Goal, or Challenge).
     */
    private final AdvancementFrame frame;

    /**
     * Flag determining if a toast notification appears on completion.
     */
    private final boolean showToast;

    /**
     * Flag determining if completion is broadcasted to the server chat.
     */
    private final boolean announceToChat;

    /**
     * Flag determining if the advancement is invisible until completed.
     */
    private final boolean hidden;

    /**
     * The horizontal position in the advancement tree GUI.
     */
    private float x;

    /**
     * The vertical position in the advancement tree GUI.
     */
    private float y;

    /**
     * Constructs a new AdvancementDisplay with full visual and positional data.
     *
     * @param title          The Adventure {@link Component} for the title.
     * @param description    The Adventure {@link Component} for the description.
     * @param icon           The {@link ItemStack} icon.
     * @param background     The resource {@link Key} for the background (nullable).
     * @param frame          The {@link AdvancementFrame} border style.
     * @param showToast      Whether to show a popup toast.
     * @param announceToChat Whether to broadcast completion to chat.
     * @param hidden         Whether the advancement is hidden until earned.
     * @param x              The horizontal position in the GUI tree.
     * @param y              The vertical position in the GUI tree.
     */
    public AdvancementDisplay(Component title, Component description, ItemStack icon, Key background, AdvancementFrame frame, boolean showToast, boolean announceToChat, boolean hidden, float x, float y) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.background = background;
        this.frame = frame;
        this.showToast = showToast;
        this.announceToChat = announceToChat;
        this.hidden = hidden;
        this.x = x;
        this.y = y;
    }

    /**
     * Retrieves the display title of the advancement.
     *
     * @return The title {@link Component}.
     */
    public Component getTitle() {
        return title;
    }

    /**
     * Retrieves the description component providing details about the advancement task.
     *
     * @return The description {@link Component}.
     */
    public Component getDescription() {
        return description;
    }

    /**
     * Retrieves the item stack assigned as the icon for this advancement.
     *
     * @return The {@link ItemStack} icon.
     */
    public ItemStack getIcon() {
        return icon;
    }

    /**
     * Retrieves the resource key for the background texture, used primarily for root nodes.
     *
     * @return The background texture {@link Key}, or null if not applicable.
     */
    public Key getBackground() {
        return background;
    }

    /**
     * Retrieves the border frame style used to categorize the advancement difficulty or type.
     *
     * @return The {@link AdvancementFrame} style.
     */
    public AdvancementFrame getFrame() {
        return frame;
    }

    /**
     * Checks if the completion of this advancement triggers a toast notification.
     *
     * @return True if a toast should be displayed.
     */
    public boolean isShowToast() {
        return showToast;
    }

    /**
     * Checks if the completion of this advancement should be announced to the global chat.
     *
     * @return True if the advancement should be announced.
     */
    public boolean isAnnounceToChat() {
        return announceToChat;
    }

    /**
     * Checks if the advancement is hidden from the advancement tree until it is achieved.
     *
     * @return True if the advancement is currently hidden.
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Retrieves the horizontal position of the advancement node within the GUI.
     *
     * @return The X coordinate as a float.
     */
    public float getX() {
        return x;
    }

    /**
     * Retrieves the vertical position of the advancement node within the GUI.
     *
     * @return The Y coordinate as a float.
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the visual coordinates for this advancement within the client tree GUI.
     *
     * @param x The new horizontal position.
     * @param y The new vertical position.
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a new builder instance for constructing AdvancementDisplay objects.
     *
     * @return A new {@link Builder} instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A fluent builder class for creating {@link AdvancementDisplay} instances.
     */
    public static class Builder {
        private Component title;
        private Component description;
        private ItemStack icon;
        private Key background;
        private AdvancementFrame frame = AdvancementFrame.TASK;
        private boolean showToast = true;
        private boolean announceToChat = true;
        private boolean hidden = false;
        private float x = Float.NaN;
        private float y = Float.NaN;

        /**
         * Sets the title for the advancement.
         *
         * @param title The Adventure {@link Component} title.
         * @return This builder instance for chaining.
         */
        public Builder title(Component title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the description for the advancement.
         *
         * @param description The Adventure {@link Component} description.
         * @return This builder instance for chaining.
         */
        public Builder description(Component description) {
            this.description = description;
            return this;
        }

        /**
         * Sets the icon for the advancement.
         *
         * @param icon The {@link ItemStack} to display.
         * @return This builder instance for chaining.
         */
        public Builder icon(ItemStack icon) {
            this.icon = icon;
            return this;
        }

        /**
         * Sets the background texture for the advancement.
         *
         * @param background The resource {@link Key} for the background.
         * @return This builder instance for chaining.
         */
        public Builder background(Key background) {
            this.background = background;
            return this;
        }

        /**
         * Sets the frame style for the advancement.
         *
         * @param frame The {@link AdvancementFrame} style.
         * @return This builder instance for chaining.
         */
        public Builder frame(AdvancementFrame frame) {
            this.frame = frame;
            return this;
        }

        /**
         * Sets whether to show a toast notification.
         *
         * @param showToast True to show toast.
         * @return This builder instance for chaining.
         */
        public Builder showToast(boolean showToast) {
            this.showToast = showToast;
            return this;
        }

        /**
         * Sets whether to announce completion to chat.
         *
         * @param announceToChat True to announce.
         * @return This builder instance for chaining.
         */
        public Builder announceToChat(boolean announceToChat) {
            this.announceToChat = announceToChat;
            return this;
        }

        /**
         * Sets whether the advancement is hidden.
         *
         * @param hidden True to hide.
         * @return This builder instance for chaining.
         */
        public Builder hidden(boolean hidden) {
            this.hidden = hidden;
            return this;
        }

        /**
         * Sets the horizontal position in the tree.
         *
         * @param x The X position.
         * @return This builder instance for chaining.
         */
        public Builder x(float x) {
            this.x = x;
            return this;
        }

        /**
         * Sets the vertical position in the tree.
         *
         * @param y The Y position.
         * @return This builder instance for chaining.
         */
        public Builder y(float y) {
            this.y = y;
            return this;
        }

        /**
         * Finalizes the creation of the AdvancementDisplay instance.
         *
         * @return A new {@link AdvancementDisplay} instance.
         */
        public AdvancementDisplay build() {
            return new AdvancementDisplay(title, description, icon, background, frame, showToast, announceToChat, hidden, x, y);
        }
    }
}