package com.github.darksoulq.abyssallib.world.data.tag;

import com.github.darksoulq.abyssallib.common.util.Identifier;

import java.util.HashSet;
import java.util.Set;

/**
 * An abstract representation of a data tag, which functions as a named collection of values.
 * <p>
 * Tags are used to group objects (like items or blocks) together under a single identifier,
 * allowing for easier logic checks and data-driven configurations. Tags can also include
 * other tags of the same type to form hierarchical relationships.
 * </p>
 *
 * @param <T> The type of entries stored within the tag (e.g., Predicates or Strings).
 * @param <D> The type of input used for testing membership in the tag (e.g., ItemStack or Block).
 */
public abstract class Tag<T, D> {
    /** The unique identifier representing this tag. */
    public final Identifier id;

    /** The set of direct values contained within this tag. */
    protected final Set<T> values = new HashSet<>();

    /** A set of other tags whose values are considered part of this tag. */
    protected final Set<Tag<T, D>> included = new HashSet<>();

    /**
     * Constructs a new Tag instance.
     *
     * @param id The {@link Identifier} for this tag.
     */
    public Tag(Identifier id) {
        this.id = id;
    }

    /**
     * Adds a direct entry to this tag.
     *
     * @param value The value of type {@code T} to add.
     */
    public void add(T value) {
        this.values.add(value);
    }

    /**
     * Includes another tag's contents into this tag.
     *
     * @param tag The {@link Tag} to include.
     */
    public void include(Tag<T, D> tag) {
        this.included.add(tag);
    }

    /**
     * Checks if the given input is considered a member of this tag.
     *
     * @param input The input of type {@code D} to test.
     * @return {@code true} if the input matches any value in this tag or its included tags.
     */
    public abstract boolean contains(D input);

    /**
     * Retrieves a flattened set of all entries in this tag and all inherited tags.
     *
     * @return A {@link Set} containing all recursive entries of type {@code T}.
     */
    public abstract Set<T> getAll();

    /**
     * @return The local {@link Set} of values directly assigned to this tag.
     */
    public Set<T> getValues() {
        return values;
    }

    /**
     * @return The {@link Set} of tags directly included by this tag.
     */
    public Set<Tag<T, D>> getIncluded() {
        return included;
    }
}