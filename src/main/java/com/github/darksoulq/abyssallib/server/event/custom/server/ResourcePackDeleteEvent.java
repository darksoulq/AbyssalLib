package com.github.darksoulq.abyssallib.server.event.custom.server;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Called when a resource pack folder is being deleted by AbyssalLib.
 * <p>
 * This event is cancellable, meaning you can prevent the folder from being deleted.
 */
public class ResourcePackDeleteEvent extends Event implements Cancellable {

    /**
     * The handler list for this event.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The mod ID associated with the resource pack.
     */
    private final String modid;

    /**
     * The folder that is being deleted.
     */
    private final File folder;

    /**
     * The cause for this deletion.
     */
    private final Cause cause;

    /**
     * Whether or not the event is cancelled.
     */
    private boolean isCancelled;

    /**
     * Constructs a new ResourcePackDeleteEvent.
     *
     * @param modid  The mod ID associated with the resource pack.
     * @param file   The folder that is going to be deleted.
     * @param cause  The reason the resource pack is being deleted.
     */
    public ResourcePackDeleteEvent(String modid, File file, Cause cause) {
        this.modid = modid;
        this.folder = file;
        this.cause = cause;
    }

    /**
     * Gets the mod ID associated with this resource pack deletion.
     *
     * @return The mod ID as a string.
     */
    public String modid() {
        return modid;
    }

    /**
     * Gets the folder (file) that is being deleted.
     *
     * @return The file representing the folder to be deleted.
     */
    public File file() {
        return folder;
    }

    /**
     * Gets the cause of the resource pack deletion.
     *
     * @return The cause of this event.
     */
    public Cause cause() {
        return cause;
    }

    /**
     * Gets the list of handlers for this event.
     *
     * @return The static handler list.
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Gets the static handler list for this event.
     *
     * @return The static handler list.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Checks whether the event is cancelled.
     *
     * @return {@code true} if the event is cancelled, {@code false} otherwise.
     */
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Sets the cancellation state of this event.
     *
     * @param cancel {@code true} to cancel the event, {@code false} to allow it to proceed.
     */
    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    /**
     * Enum representing the possible causes for a resource pack deletion.
     */
    public enum Cause {
        /**
         * The resource pack was deleted due to regeneration logic.
         */
        GENERATE,

        /**
         * The resource pack was deleted due to glyph/font changes.
         */
        GLYPHS
    }
}
