package com.github.darksoulq.abyssallib.world.data.tag;

import net.kyori.adventure.key.Key;

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
 * @param <T> The type of entries stored within the tag.
 * @param <D> The type of input used for testing membership in the tag.
 */
public abstract class Tag<T, D> {
    protected final Key id;
    protected final Set<T> values = new HashSet<>();
    protected final Set<Tag<T, D>> included = new HashSet<>();

    /**
     * Constructs a new Tag instance.
     *
     * @param id The identifier for this tag.
     */
    public Tag(Key id) {
        this.id = id;
    }

    /**
     * Retrieves the specific type of this tag.
     *
     * @return The {@link TagType} defining this tag's logic and serialization.
     */
    public abstract TagType<T, D> getType();

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
     * Retrieves the local set of values directly assigned to this tag.
     *
     * @return A {@link Set} of directly added values.
     */
    public Set<T> getValues() {
        return values;
    }

    /**
     * Retrieves the set of tags directly included by this tag.
     *
     * @return A {@link Set} of included tags.
     */
    public Set<Tag<T, D>> getIncluded() {
        return included;
    }

    /**
     * Retrieves the unique identifier representing this tag.
     *
     * @return The {@link Key} identifier.
     */
    public Key getId() {
        return id;
    }
}