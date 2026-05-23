# Execution Contexts & Folia
<link-summary>Managing threading and region-based scheduling for Folia compatibility</link-summary>

AbyssalLib’s scheduler is built from the ground up to seamlessly support both standard Bukkit/Paper and Folia. You write your scheduling logic once, and the `TaskDispatcher` automatically routes it to the correct underlying platform.

---

### Sync vs Async
By default, all tasks scheduled using `Clock.TICKS` run synchronously on the main server thread (or the region thread in Folia).

To run heavy calculations, database queries, or network requests off the main thread, append `.async()` to the builder.

```Java
// Runs on an asynchronous thread
scheduler.schedule(() -> {
    database.savePlayerData(player);
})
.async()
.after(10, TimeUnit.SECONDS, Clock.TICKS)
.once();
```

> **Note about `REALTIME`:**
> If you schedule a task using `Clock.REALTIME`, the scheduler **automatically forces the task to be asynchronous**. Minecraft's internal schedulers operate exclusively on ticks (which lag). To guarantee accurate real-time execution, the task must run independently of the server's tick loop.

---

### Folia Support & Contexts
Folia fundamentally changes server architecture by replacing the single main thread with multiple independent "Region Threads." If you modify a block or an entity, you **must** be executing on the thread that currently owns that region.

AbyssalLib provides context methods on the `TaskBuilder` to handle this routing for you. If you are not running Folia, these methods simply default back to the standard Bukkit main thread, ensuring your plugin works flawlessly on both platforms.

<tabs>
<tab title="Entity Context">

Use `.entity(Entity)` when your task modifies or tracks a specific entity. The task will execute on the thread currently ticking that entity, and will safely suspend or migrate if the entity teleports across region boundaries.

```Java
// Safely heals the player, even if they cross into a new region thread
scheduler.schedule(() -> {
    player.setHealth(20.0);
})
.entity(player)
.after(5, TimeUnit.SECONDS, Clock.TICKS)
.once();
```

</tab>
<tab title="Region Context">

Use `.region(Location)` when your task modifies blocks or spawns entities at a specific coordinate. The task will route to the thread responsible for that chunk.

```Java
// Safely places a block at the target location
scheduler.schedule(() -> {
    location.getBlock().setType(Material.DIAMOND_BLOCK);
})
.region(location)
.after(3, TimeUnit.SECONDS, Clock.TICKS)
.once();
```

</tab>
<tab title="Global Context">

`.global()` is the default behavior if no context is specified. It is meant for tasks that do not interact with the world, blocks, or entities (e.g., Discord bot updates, database syncing, or global server broadcasts).

```Java
// Runs on the global region thread
scheduler.schedule(() -> {
    Bukkit.broadcastMessage("The server is restarting in 5 minutes!");
})
.global() // Optional, as this is the default
.after(1, TimeUnit.MINUTES, Clock.TICKS)
.once();
```

</tab>
</tabs>

---

### Mixing Async and Folia
You can combine `.async()` with Folia contexts. However, if you flag an entity or region task as `.async()`, it simply runs on a detached async thread pool, completely ignoring the region threading.

Only do this if you need the entity's data *read-only* for a background process, and ensure you do not call Bukkit API methods that modify the world from that async task!