package me.darksoul.abyssallib.event.context;

import org.bukkit.event.Event;

/**
 * Represents a generic context for an event. This is an abstract base class
 * used for storing and working with events in a more structured way.
 *
 * @param <T> The type of the event this context holds.
 */
public abstract class Context<T extends Event> {

    /**
     * The event associated with this context.
     */
    public T event;

    /**
     * Constructs a new Context with the given event.
     *
     * @param event The event to associate with this context.
     */
    public Context(T event) {
        this.event = event;
    }
}
