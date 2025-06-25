package com.github.darksoulq.abyssallib.server.event;

/**
 * Represents the result of an event or action handler.
 * <p>
 * This is used to indicate whether the default/original behavior should proceed
 * or be cancelled/interrupted.
 */
public enum ActionResult {

    /**
     * Indicates that the original/default logic should continue to run.
     * <p>
     * This does not block any following operations and lets the event propagate normally.
     */
    PASS,

    /**
     * Indicates that the action or event should be cancelled.
     * <p>
     * No further logic (including default behavior) should be executed.
     */
    CANCEL
}
