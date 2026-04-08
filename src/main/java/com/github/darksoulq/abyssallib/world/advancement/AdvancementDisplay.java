package com.github.darksoulq.abyssallib.world.advancement;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Try;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

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
    public static final Codec<AdvancementDisplay> CODEC = new Codec<>() {
        /**
         * Decodes an AdvancementDisplay from a serialized format.
         *
         * @param <D>
         * The type of the serialized data.
         * @param ops
         * The dynamic operations logic provider.
         * @param input
         * The raw serialized input data.
         * @return
         * The reconstructed AdvancementDisplay instance.
         * @throws CodecException
         * If the data is missing required fields or is malformed.
         */
        @Override
        public <D> AdvancementDisplay decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map for AdvancementDisplay"));
            Builder builder = builder();

            Try.of(() -> Codecs.TEXT_COMPONENT.decode(ops, map.get(ops.createString("title"))))
                .onSuccess(builder::title);
            Try.of(() -> Codecs.TEXT_COMPONENT.decode(ops, map.get(ops.createString("description"))))
                .onSuccess(builder::description);
            Try.of(() -> Codecs.ITEM_STACK.decode(ops, map.get(ops.createString("icon"))))
                .onSuccess(builder::icon);
            Try.of(() -> Codecs.KEY.decode(ops, map.get(ops.createString("background"))))
                .onSuccess(builder::background);
            Try.of(() -> AdvancementFrame.CODEC.decode(ops, map.get(ops.createString("frame"))))
                .onSuccess(builder::frame);
            Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("show_toast"))))
                .onSuccess(builder::showToast);
            Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("announce_to_chat"))))
                .onSuccess(builder::announceToChat);
            Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("hidden"))))
                .onSuccess(builder::hidden);
            Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("x"))))
                .onSuccess(builder::x);
            Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("y"))))
                .onSuccess(builder::y);

            return builder.build();
        }

        /**
         * Encodes an AdvancementDisplay into a serialized format.
         *
         * @param <D>
         * The target type for the serialized data.
         * @param ops
         * The dynamic operations logic provider.
         * @param value
         * The AdvancementDisplay instance to serialize.
         * @return
         * The serialized data representation.
         * @throws CodecException
         * If the internal components fail to encode.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, AdvancementDisplay value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("title"), Codecs.TEXT_COMPONENT.encode(ops, value.title));
            map.put(ops.createString("description"), Codecs.TEXT_COMPONENT.encode(ops, value.description));
            map.put(ops.createString("icon"), Codecs.ITEM_STACK.encode(ops, value.icon));

            if (value.background != null) {
                map.put(ops.createString("background"), Codecs.KEY.encode(ops, value.background));
            }

            map.put(ops.createString("frame"), AdvancementFrame.CODEC.encode(ops, value.frame));
            map.put(ops.createString("show_toast"), Codecs.BOOLEAN.encode(ops, value.showToast));
            map.put(ops.createString("announce_to_chat"), Codecs.BOOLEAN.encode(ops, value.announceToChat));
            map.put(ops.createString("hidden"), Codecs.BOOLEAN.encode(ops, value.hidden));
            map.put(ops.createString("x"), Codecs.FLOAT.encode(ops, value.x));
            map.put(ops.createString("y"), Codecs.FLOAT.encode(ops, value.y));

            return ops.createMap(map);
        }
    };

    /** The display title component. */
    private final Component title;

    /** The description component providing detail about the advancement. */
    private final Component description;

    /** The ItemStack used as the visual icon in the GUI. */
    private final ItemStack icon;

    /** The background texture key used if this is a root advancement. */
    private final Key background;

    /** The visual border style (Task, Goal, or Challenge). */
    private final AdvancementFrame frame;

    /** Flag determining if a toast notification appears on completion. */
    private final boolean showToast;

    /** Flag determining if completion is broadcasted to the server chat. */
    private final boolean announceToChat;

    /** Flag determining if the advancement is invisible until completed. */
    private final boolean hidden;

    /** The horizontal position in the advancement tree GUI. */
    private float x;

    /** The vertical position in the advancement tree GUI. */
    private float y;

    /**
     * Constructs a new AdvancementDisplay with full visual and positional data.
     *
     * @param title
     * The Adventure {@link Component} for the title.
     * @param description
     * The Adventure {@link Component} for the description.
     * @param icon
     * The {@link ItemStack} icon.
     * @param background
     * The resource {@link Key} for the background (nullable).
     * @param frame
     * The {@link AdvancementFrame} border style.
     * @param showToast
     * Whether to show a popup toast.
     * @param announceToChat
     * Whether to broadcast completion to chat.
     * @param hidden
     * Whether the advancement is hidden until earned.
     * @param x
     * The horizontal position in the GUI tree.
     * @param y
     * The vertical position in the GUI tree.
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
     * @return
     * The title {@link Component}.
     */
    public Component getTitle() {
        return title;
    }

    /**
     * Retrieves the description component providing details about the advancement task.
     *
     * @return
     * The description {@link Component}.
     */
    public Component getDescription() {
        return description;
    }

    /**
     * Retrieves the item stack assigned as the icon for this advancement.
     *
     * @return
     * The {@link ItemStack} icon.
     */
    public ItemStack getIcon() {
        return icon;
    }

    /**
     * Retrieves the resource key for the background texture, used primarily for root nodes.
     *
     * @return
     * The background texture {@link Key}, or null if not applicable.
     */
    public Key getBackground() {
        return background;
    }

    /**
     * Retrieves the border frame style used to categorize the advancement difficulty or type.
     *
     * @return
     * The {@link AdvancementFrame} style.
     */
    public AdvancementFrame getFrame() {
        return frame;
    }

    /**
     * Checks if the completion of this advancement triggers a toast notification.
     *
     * @return
     * True if a toast should be displayed.
     */
    public boolean isShowToast() {
        return showToast;
    }

    /**
     * Checks if the completion of this advancement should be announced to the global chat.
     *
     * @return
     * True if the advancement should be announced.
     */
    public boolean isAnnounceToChat() {
        return announceToChat;
    }

    /**
     * Checks if the advancement is hidden from the advancement tree until it is achieved.
     *
     * @return
     * True if the advancement is currently hidden.
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Retrieves the horizontal position of the advancement node within the GUI.
     *
     * @return
     * The X coordinate as a float.
     */
    public float getX() {
        return x;
    }

    /**
     * Retrieves the vertical position of the advancement node within the GUI.
     *
     * @return
     * The Y coordinate as a float.
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the visual coordinates for this advancement within the client tree GUI.
     *
     * @param x
     * The new horizontal position.
     * @param y
     * The new vertical position.
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a new builder instance for constructing AdvancementDisplay objects.
     *
     * @return
     * A new {@link Builder} instance.
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
         * @return
         * A new {@link AdvancementDisplay} instance.
         */
        public AdvancementDisplay build() {
            return new AdvancementDisplay(title, description, icon, background, frame, showToast, announceToChat, hidden, x, y);
        }
    }
}