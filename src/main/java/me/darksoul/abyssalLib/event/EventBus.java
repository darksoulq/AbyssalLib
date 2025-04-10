package me.darksoul.abyssalLib.event;

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

public class EventBus {
    private final Plugin plugin;
    private final Set<Object> registeredObjects = new HashSet<>();

    public EventBus(Plugin plugin) {
        this.plugin = plugin;
    }

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

}
