package com.github.darksoulq.abyssallib.server.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * A lightweight event bus for registering Bukkit event handlers using the {@link SubscribeEvent} format.
 * <p>
 * This class scans listener objects for annotated methods and registers them with Bukkit's event system.
 * It uses {@link MethodHandle} for fast invocation and supports event priority and cancel-ignoring behavior.
 */
public class EventBus {
    private final Plugin plugin;
    private final Set<Object> registeredObjects = new HashSet<>();

    /**
     * Constructs a new EventBus for the given plugin.
     *
     * @param plugin The plugin that owns this event bus and will be used as the event listener owner.
     */
    public EventBus(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers an object with the event bus. All methods annotated with {@link SubscribeEvent}
     * will be scanned and registered with Bukkit if they accept a single {@link Event} parameter.
     *
     * @param listenerObject The object containing annotated event handler methods.
     * @throws IllegalArgumentException if any annotated method is invalid (e.g., wrong parameter count or type).
     */
    public void register(Object listenerObject) {
        if (!registeredObjects.add(listenerObject)) return;

        Listener dynamicListener = new Listener() {}; // dynamic listener

        for (Method method : listenerObject.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(SubscribeEvent.class)) continue;

            if (method.getParameterCount() != 1) {
                throw new IllegalArgumentException("Method " + method + " must have exactly one parameter.");
            }

            Class<?> eventClass = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(eventClass)) {
                throw new IllegalArgumentException("Parameter must be a Bukkit event.");
            }

            SubscribeEvent annotation = method.getAnnotation(SubscribeEvent.class);
            EventPriority priority = annotation.priority();
            boolean ignoreCancelled = annotation.ignoreCancelled();

            method.setAccessible(true);

            MethodHandle handle;
            try {
                handle = MethodHandles.lookup().unreflect(method).bindTo(listenerObject);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Could not create method handle for " + method, e);
            }

            Bukkit.getPluginManager().registerEvent(
                    (Class<? extends Event>) eventClass,
                    dynamicListener,
                    priority,
                    (listener, event) -> {
                        if (eventClass.isAssignableFrom(event.getClass())) {
                            try {
                                handle.invokeWithArguments(event);
                            } catch (Throwable t) {
                                plugin.getLogger().severe("Failed to invoke event handler: " + t.getMessage());
                                t.printStackTrace();
                            }
                        }
                    },
                    plugin,
                    ignoreCancelled
            );
        }
    }

    /**
     * Posts a Bukkit event to all registered listeners.
     *
     * @param event The event to post.
     * @param <T>   The type of event being posted.
     * @return The same event instance after it has been processed by all listeners.
     */
    public static <T extends Event> T post(T event) {
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }
}
