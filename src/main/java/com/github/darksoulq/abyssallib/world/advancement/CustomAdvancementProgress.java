package com.github.darksoulq.abyssallib.world.advancement;

import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Date;

/**
 * A wrapper for player-specific advancement progress.
 * This class links the custom {@link Advancement} logic with the underlying
 * Bukkit {@link AdvancementProgress}, handling reward granting upon completion.
 */
public class CustomAdvancementProgress {

    /** The custom advancement associated with this progress. */
    private final Advancement advancement;

    /** The underlying Bukkit progress tracker. */
    private final AdvancementProgress bukkitProgress;

    /** The player whom this progress belongs to. */
    private final Player player;

    /**
     * Constructs a new CustomAdvancementProgress wrapper.
     *
     * @param advancement
     * The custom {@link Advancement} instance.
     * @param player
     * The {@link Player} making progress.
     * @param bukkitProgress
     * The actual Bukkit progress object.
     */
    public CustomAdvancementProgress(Advancement advancement, Player player, AdvancementProgress bukkitProgress) {
        this.advancement = advancement;
        this.player = player;
        this.bukkitProgress = bukkitProgress;
    }

    /**
     * Retrieves the custom advancement instance.
     *
     * @return
     * The associated {@link Advancement}.
     */
    public Advancement getAdvancement() {
        return advancement;
    }

    /**
     * Retrieves the player whose progress is being tracked.
     *
     * @return
     * The {@link Player} instance.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Checks if all criteria for this advancement have been met.
     *
     * @return
     * True if the advancement is fully completed.
     */
    public boolean isDone() {
        return bukkitProgress.isDone();
    }

    /**
     * Retrieves a collection of all criteria identifiers already awarded to the player.
     *
     * @return
     * A {@link Collection} of awarded criterion names.
     */
    public Collection<String> getAwardedCriteria() {
        return bukkitProgress.getAwardedCriteria();
    }

    /**
     * Retrieves a collection of criteria identifiers still required for completion.
     *
     * @return
     * A {@link Collection} of remaining criterion names.
     */
    public Collection<String> getRemainingCriteria() {
        return bukkitProgress.getRemainingCriteria();
    }

    /**
     * Retrieves the date and time a specific criterion was awarded.
     *
     * @param criterion
     * The name of the criterion to check.
     * @return
     * The {@link Date} of award, or null if not yet awarded.
     */
    public Date getDateAwarded(String criterion) {
        return bukkitProgress.getDateAwarded(criterion);
    }

    /**
     * Awards a specific criterion to the player.
     * If this award completes the advancement, all registered rewards are granted automatically.
     *
     * @param criterion
     * The name of the criterion to award.
     */
    public void awardCriterion(String criterion) {
        bukkitProgress.awardCriteria(criterion);
        if (isDone()) {
            advancement.getRewards().forEach(reward -> reward.grant(player));
        }
    }

    /**
     * Revokes a specific criterion from the player, effectively resetting that part of the progress.
     *
     * @param criterion
     * The name of the criterion to revoke.
     */
    public void revokeCriterion(String criterion) {
        bukkitProgress.revokeCriteria(criterion);
    }
}