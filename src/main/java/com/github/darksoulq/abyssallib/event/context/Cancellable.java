package com.github.darksoulq.abyssallib.event.context;

/**
 * Represents an object that can be cancelled. Implementations of this interface
 * should define the behavior for cancelling the action or event.
 */
public interface Cancellable {

    /**
     * Cancels the associated action or event.
     */
    void cancel();
}
