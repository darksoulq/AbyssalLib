package com.github.darksoulq.abyssallib.server.event.custom.entity;

import com.github.darksoulq.abyssallib.world.entity.data.Attribute;
import com.github.darksoulq.abyssallib.world.entity.data.AttributeModifier;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event called when a new modifier is added to an entity's attribute.
 * This event is {@link Cancellable}, allowing listeners to prevent the modifier
 * from being applied or to swap the modifier for a different one.
 *
 * @param <T>
 * The numeric type of the attribute and modifier.
 */
public class EntityAttributeModifierAddEvent<T extends Number> extends Event implements Cancellable {

    /**
     * The handler list for this event. Required by Bukkit's event system.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The entity receiving the attribute modifier.
     */
    private final Entity entity;

    /**
     * The attribute to which the modifier is being added.
     */
    private final Attribute<T> attribute;

    /**
     * The unique identifier Key for the modifier.
     */
    private final Key modifierId;

    /**
     * The modifier object containing the value and operation type.
     */
    private AttributeModifier<T> modifier;

    /**
     * The current cancellation state of this event.
     */
    private boolean isCancelled;

    /**
     * Constructs a new EntityAttributeModifierAddEvent.
     *
     * @param entity
     * The {@link Entity} receiving the modifier.
     * @param attribute
     * The {@link Attribute} instance being modified.
     * @param modifierId
     * The unique {@link Key} identifying this specific modifier.
     * @param modifier
     * The {@link AttributeModifier} object to be added.
     */
    public EntityAttributeModifierAddEvent(@NotNull Entity entity, @NotNull Attribute<T> attribute, @NotNull Key modifierId, @NotNull AttributeModifier<T> modifier) {
        this.entity = entity;
        this.attribute = attribute;
        this.modifierId = modifierId;
        this.modifier = modifier;
    }

    /**
     * Retrieves the entity involved in this event.
     *
     * @return
     * The {@link Entity} receiving the modifier.
     */
    public @NotNull Entity getEntity() {
        return entity;
    }

    /**
     * Retrieves the specific attribute that the modifier is being applied to.
     *
     * @return
     * The {@link Attribute} definition.
     */
    public @NotNull Attribute<T> getAttribute() {
        return attribute;
    }

    /**
     * Retrieves the unique identifier Key for the incoming modifier.
     *
     * @return
     * The modifier's {@link Key}.
     */
    public @NotNull Key getModifierId() {
        return modifierId;
    }

    /**
     * Retrieves the current modifier object that is proposed to be added.
     *
     * @return
     * The {@link AttributeModifier} instance.
     */
    public @NotNull AttributeModifier<T> getModifier() {
        return modifier;
    }

    /**
     * Sets a new modifier object to be added instead of the original one.
     *
     * @param modifier
     * The replacement {@link AttributeModifier} instance.
     */
    public void setModifier(@NotNull AttributeModifier<T> modifier) {
        this.modifier = modifier;
    }

    /**
     * Checks if the addition of the modifier has been cancelled.
     *
     * @return
     * True if the event is cancelled.
     */
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Sets the cancellation state of the event.
     *
     * @param v
     * True to prevent the modifier from being added.
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