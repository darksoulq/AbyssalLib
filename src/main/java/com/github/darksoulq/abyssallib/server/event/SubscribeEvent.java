package com.github.darksoulq.abyssallib.server.event;

import org.bukkit.event.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as an event listener for the custom EventBus system.
 * <p>
 * Methods annotated with this will be automatically registered by
 * {@code EventBus#register(Object)} and invoked when relevant events are posted.
 * </p>
 * <p>
 * The method must have exactly one parameter which extends {@link org.bukkit.event.Event}.
 * </p>
 *
 * <pre>
 * Example:
 * {@code
 * @SubscribeEvent
 * public void onCustomEvent(MyCustomEvent event) {
 *     // handle event
 * }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubscribeEvent {

    /**
     * Defines the priority at which the event listener should be called.
     * <p>
     * This affects the order in which listeners receive events.
     * Listeners with higher priority are called later.
     *
     * @return the priority for the event handler
     */
    EventPriority priority() default EventPriority.NORMAL;

    /**
     * Whether the event handler should be called for cancelled events.
     *
     * @return true to ignore cancelled events (default), false to receive them anyway
     */
    boolean ignoreCancelled() default true;
}
