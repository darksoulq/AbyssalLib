package com.github.darksoulq.abyssallib.server.event.custom.entity;

import com.github.darksoulq.abyssallib.world.data.attribute.Attribute;
import com.github.darksoulq.abyssallib.world.data.attribute.AttributeModifier;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event called when an existing modifier is being removed from an entity's attribute.
 * This event is {@link Cancellable}, allowing listeners to prevent the removal of
 * the modifier.
 *
 * @param <T>
 * The numeric type of the attribute and the modifier being removed.
 */
public class EntityAttributeModifierRemoveEvent<T extends Number> extends Event implements Cancellable {

    /**
     * The handler list for this event. Required by Bukkit's event system.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The entity from which the modifier is being removed.
     */
    private final Entity entity;

    /**
     * The attribute that the modifier is currently attached to.
     */
    private final Attribute<T> attribute;

    /**
     * The unique identifier Key for the modifier being removed.
     */
    private final Key modifierId;

    /**
     * The specific modifier object that is slated for removal.
     */
    private final AttributeModifier<T> modifier;

    /**
     * The current cancellation state of this event.
     */
    private boolean isCancelled;

    /**
     * Constructs a new EntityAttributeModifierRemoveEvent.
     *
     * @param entity
     * The {@link Entity} losing the modifier.
     * @param attribute
     * The {@link Attribute} instance being updated.
     * @param modifierId
     * The unique {@link Key} identifying the modifier to remove.
     * @param modifier
     * The {@link AttributeModifier} instance that was found and is being removed.
     */
    public EntityAttributeModifierRemoveEvent(@NotNull Entity entity, @NotNull Attribute<T> attribute, @NotNull Key modifierId, @NotNull AttributeModifier<T> modifier) {
        this.entity = entity;
        this.attribute = attribute;
        this.modifierId = modifierId;
        this.modifier = modifier;
    }

    /**
     * Retrieves the entity involved in this event.
     *
     * @return
     * The {@link Entity} whose attribute is being updated.
     */
    public @NotNull Entity getEntity() {
        return entity;
    }

    /**
     * Retrieves the specific attribute that the modifier is currently influencing.
     *
     * @return
     * The {@link Attribute} definition.
     */
    public @NotNull Attribute<T> getAttribute() {
        return attribute;
    }

    /**
     * Retrieves the unique identifier Key for the modifier being removed.
     *
     * @return
     * The modifier's unique {@link Key}.
     */
    public @NotNull Key getModifierId() {
        return modifierId;
    }

    /**
     * Retrieves the modifier instance that is being removed.
     *
     * @return
     * The {@link AttributeModifier} instance.
     */
    public @NotNull AttributeModifier<T> getModifier() {
        return modifier;
    }

    /**
     * Checks whether the removal of this modifier has been cancelled.
     *
     * @return
     * True if the removal is prevented.
     */
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Sets the cancellation state of the event.
     *
     * @param v
     * True to cancel the removal, keeping the modifier active.
     */
    @Override
    public void setCancelled(boolean v) {
        this.isCancelled = v;
    }

    /**
     * Retrieves the list of handlers listening to this event instance.
     *
     * @return
     * The {@link HandlerList} for this event.
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Provides a static method to retrieve the handler list, required by Bukkit.
     *
     * @return
     * The static {@link HandlerList} for this event type.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}