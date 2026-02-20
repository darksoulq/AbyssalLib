package com.github.darksoulq.abyssallib.common.util

import java.util.NoSuchElementException
import java.util.Optional

/**
 * A sealed class used to represent the outcome of a computation that may result
 * in either a value [Success] or an exception [Failure].
 *
 * This utility facilitates a "Railway Oriented Programming" style, allowing
 * for safe error handling without nested try-catch blocks.
 *
 * @param T The type of the successful result.
 */
sealed class KTry<out T> {

    /** Indicates if the operation was successful. */
    abstract val isSuccess: Boolean

    /** * Returns the successful value.
     * @return The value of type [T].
     * @throws RuntimeException Wrapping the internal exception if this is a [Failure].
     */
    abstract fun get(): T

    /** * Returns the exception that caused the failure.
     * @return The [Throwable] instance.
     * @throws NoSuchElementException If called on a [Success] instance.
     */
    abstract fun getException(): Throwable

    /**
     * Maps the successful value to a new value using the provided function.
     *
     * @param U The type of the new result.
     * @param mapper The function to transform the successful value.
     * @return A [Success] with the new value, or a [Failure] if an error occurred during mapping.
     */
    abstract fun <U> map(mapper: (T) -> U): KTry<U>

    /**
     * Flat-maps the successful value into another [KTry].
     *
     * @param U The type of the result inside the new [KTry].
     * @param mapper The function returning a new [KTry].
     * @return The result of the mapper, or the original [Failure].
     */
    abstract fun <U> flatMap(mapper: (T) -> KTry<U>): KTry<U>

    /**
     * Executes the given action if this is a [Failure].
     *
     * @param action Logic to run with the caught [Throwable].
     * @return This instance for chaining.
     */
    abstract fun onFailure(action: (Throwable) -> Unit): KTry<T>

    /**
     * Executes the given action if this is a [Success].
     *
     * @param action Logic to run with the successful value.
     * @return This instance for chaining.
     */
    abstract fun onSuccess(action: (T) -> Unit): KTry<T>

    /**
     * Returns the successful value, or the provided default if this is a failure.
     *
     * @param other The default value.
     * @return The result or [other].
     */
    abstract fun orElse(other: @UnsafeVariance T): T

    /**
     * Returns the successful value, or computes a default if this is a failure.
     *
     * @param other The supplier of the default value.
     * @return The result or the supplied value.
     */
    abstract fun orElseGet(other: () -> @UnsafeVariance T): T

    /**
     * Returns the value or throws a custom exception.
     *
     * @param X The type of exception to throw.
     * @param exceptionProvider Logic to transform the internal error into [X].
     * @return The successful value.
     * @throws X
     */
    abstract fun <X : Throwable> orElseThrow(exceptionProvider: (Throwable) -> X): T

    /**
     * Converts this instance into a standard Java [Optional].
     *
     * @return An [Optional] containing the value, or empty if failure.
     */
    abstract fun toOptional(): Optional<@UnsafeVariance T>

    companion object {
        /**
         * Wraps a supplier in a [KTry].
         *
         * @param T The type of the returned value.
         * @param supplier The block of code to run.
         * @return [Success] or [Failure].
         */
        inline fun <T> of(supplier: () -> T): KTry<T> {
            return try {
                Success(supplier())
            } catch (t: Throwable) {
                Failure(t)
            }
        }

        /**
         * Wraps a side-effecting runnable in a [KTry].
         *
         * @param runnable The block of code to execute.
         * @return [Success] of Unit or [Failure].
         */
        inline fun run(runnable: () -> Unit): KTry<Unit> {
            return try {
                runnable()
                Success(Unit)
            } catch (t: Throwable) {
                Failure(t)
            }
        }
    }

    /**
     * Represents the successful state of a [KTry].
     * @param value The resulting data.
     */
    class Success<out T>(private val value: T) : KTry<T>() {
        override val isSuccess: Boolean get() = true
        override fun get(): T = value
        override fun getException(): Throwable = throw NoSuchElementException("Success does not contain an exception")

        override fun <U> map(mapper: (T) -> U): KTry<U> {
            return try {
                Success(mapper(value))
            } catch (t: Throwable) {
                Failure(t)
            }
        }

        override fun <U> flatMap(mapper: (T) -> KTry<U>): KTry<U> {
            return try {
                mapper(value)
            } catch (t: Throwable) {
                Failure(t)
            }
        }

        override fun onFailure(action: (Throwable) -> Unit): KTry<T> = this
        override fun onSuccess(action: (T) -> Unit): KTry<T> { action(value); return this }
        override fun orElse(other: @UnsafeVariance T): T = value
        override fun orElseGet(other: () -> @UnsafeVariance T): T = value
        override fun <X : Throwable> orElseThrow(exceptionProvider: (Throwable) -> X): T = value
        @Suppress("UNCHECKED_CAST")
        override fun toOptional(): Optional<@UnsafeVariance T> = Optional.ofNullable(value as Any?) as Optional<T>
    }

    /**
     * Represents the failed state of a [KTry].
     * @param exception The error encountered.
     */
    class Failure<out T>(private val exception: Throwable) : KTry<T>() {
        override val isSuccess: Boolean get() = false
        override fun get(): T = throw RuntimeException(exception)
        override fun getException(): Throwable = exception

        @Suppress("UNCHECKED_CAST")
        override fun <U> map(mapper: (T) -> U): KTry<U> = this as KTry<U>
        @Suppress("UNCHECKED_CAST")
        override fun <U> flatMap(mapper: (T) -> KTry<U>): KTry<U> = this as KTry<U>

        override fun onFailure(action: (Throwable) -> Unit): KTry<T> { action(exception); return this }
        override fun onSuccess(action: (T) -> Unit): KTry<T> = this
        override fun orElse(other: @UnsafeVariance T): T = other
        override fun orElseGet(other: () -> @UnsafeVariance T): T = other()
        override fun <X : Throwable> orElseThrow(exceptionProvider: (Throwable) -> X): T = throw exceptionProvider(exception)
        @Suppress("UNCHECKED_CAST")
        override fun toOptional(): Optional<@UnsafeVariance T> = Optional.empty<Any>() as Optional<T>
    }
}