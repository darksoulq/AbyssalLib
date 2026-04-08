package com.github.darksoulq.abyssallib.server.event.custom.entity;

import com.github.darksoulq.abyssallib.world.entity.data.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event called when an entity's base attribute value is changed.
 * This event is {@link Cancellable}, allowing listeners to prevent the change
 * or modify the new value before it is persisted to the database.
 *
 * @param <T>
 * The numeric type of the attribute being modified.
 */
public class EntityAttributeChangeEvent<T extends Number> extends Event implements Cancellable {

    /**
     * The handler list for this event. Required by Bukkit's event system.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The entity whose attribute is being modified.
     */
    private final Entity entity;

    /**
     * The attribute definition involved in this change.
     */
    private final Attribute<T> attribute;

    /**
     * The original base value before the change.
     */
    private final T oldValue;

    /**
     * The new base value to be applied.
     */
    private T newValue;

    /**
     * The current cancellation state of this event.
     */
    private boolean isCancelled;

    /**
     * Constructs a new EntityAttributeChangeEvent.
     *
     * @param entity
     * The {@link Entity} instance whose attribute is changing.
     * @param attribute
     * The {@link Attribute} definition being updated.
     * @param oldValue
     * The previous base value.
     * @param newValue
     * The proposed new base value.
     */
    public EntityAttributeChangeEvent(@NotNull Entity entity, @NotNull Attribute<T> attribute, T oldValue, T newValue) {
        this.entity = entity;
        this.attribute = attribute;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * Retrieves the entity involved in this attribute change.
     *
     * @return
     * The {@link Entity} instance.
     */
    public @NotNull Entity getEntity() {
        return entity;
    }

    /**
     * Retrieves the specific attribute that is being modified.
     *
     * @return
     * The {@link Attribute} definition.
     */
    public @NotNull Attribute<T> getAttribute() {
        return attribute;
    }

    /**
     * Retrieves the value of the attribute prior to this change event.
     *
     * @return
     * The old base value of type {@code T}.
     */
    public T getOldValue() {
        return oldValue;
    }

    /**
     * Retrieves the proposed new value for the attribute.
     *
     * @return
     * The proposed new base value of type {@code T}.
     */
    public T getNewValue() {
        return newValue;
    }

    /**
     * Overrides the proposed new value with a different one.
     *
     * @param newValue
     * The new base value to apply to the entity.
     */
    public void setNewValue(T newValue) {
        this.newValue = newValue;
    }

    /**
     * Checks if the attribute change has been cancelled.
     *
     * @return
     * True if the event is cancelled, preventing the value update.
     */
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Sets the cancellation state of the event.
     *
     * @param v
     * True to cancel the change, false to allow it to proceed.
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