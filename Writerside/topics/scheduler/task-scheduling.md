# Task Scheduling
<link-summary>Core API for scheduling delayed and repeating tasks</link-summary>

AbyssalLib provides a fluent, builder-based Scheduler API that makes writing delayed and repeating tasks incredibly clean. It supports both standard server ticks and real-time execution, with built-in conditions to automatically cancel loops.

To get started, instantiate the `Scheduler` in your plugin's main class:

```Java
public class MyPlugin extends JavaPlugin {
    private Scheduler scheduler;

    @Override
    public void onEnable() {
        this.scheduler = new Scheduler(this);
    }
    
    public Scheduler getScheduler() {
        return this.scheduler;
    }
}
```

---

### Basic Execution
Every task starts with `scheduler.schedule(Runnable)`. From there, you use the returned `TaskBuilder` to configure the delay and execution style.

To fire a task, you must terminate the builder chain with either `.once()` or `.repeatEvery()`.

<tabs>
<tab title="Run Immediately">

```Java
// Runs immediately on the next available tick
scheduler.schedule(() -> {
    Bukkit.broadcastMessage("Server is running!");
}).once();
```

</tab>
<tab title="Delayed Task">

Use `.after()` to set a delay.

```Java
// Runs once after a 5-second delay
scheduler.schedule(() -> {
    player.sendMessage("5 seconds have passed!");
}).after(5, TimeUnit.SECONDS, Clock.TICKS).once();
```

</tab>
</tabs>

---

### TimeUnits and Clocks
The API uses two distinct concepts for measuring time: `TimeUnit` and `Clock`.

* **`TimeUnit`**: The metric used to measure your numbers (`TICKS`, `MILLISECONDS`, `SECONDS`, `MINUTES`, `HOURS`, `DAYS`).
* **`Clock`**: The engine ticking the time.
    * `Clock.TICKS`: Tied to the Minecraft server loop (20 ticks per second, slows down if the server lags).
    * `Clock.REALTIME`: Tied to the physical system clock (Ignores server lag).

If you don't provide a `TimeUnit`, the API will default to the native unit of the `Clock` you select (`TICKS` for `Clock.TICKS`, `MILLISECONDS` for `Clock.REALTIME`).

```Java
// These do the exact same thing (5 seconds = 100 ticks):
.after(5, TimeUnit.SECONDS, Clock.TICKS)
.after(100, Clock.TICKS)
```

---

### Repeating Tasks
To create a looping task, terminate the builder with `.repeatEvery()` instead of `.once()`.

You can also dynamically control the lifecycle of a repeating task using `.repeatWhile()` or `.repeatUntil()`. The task will automatically evaluate the `BooleanSupplier` before every execution and cancel itself if the condition is met.

<tabs>
<tab title="Standard Loop">

```Java
// Runs immediately, then repeats every 10 minutes
scheduler.schedule(() -> {
    Bukkit.broadcastMessage("Don't forget to vote!");
}).repeatEvery(10, TimeUnit.MINUTES, Clock.TICKS);
```

</tab>
<tab title="Repeat Until">

```Java
// Spawns particles on the player every tick UNTIL they die
scheduler.schedule(() -> {
    player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 5);
})
.repeatUntil(player::isDead)
.repeatEvery(1, Clock.TICKS);
```

</tab>
<tab title="Repeat While">

```Java
// Heals the player every second WHILE they are blocking with a shield
scheduler.schedule(() -> {
    player.setHealth(Math.min(20.0, player.getHealth() + 1.0));
})
.repeatWhile(player::isBlocking)
.repeatEvery(1, TimeUnit.SECONDS, Clock.TICKS);
```

</tab>
</tabs>

---

### Task Management
Terminating the builder (via `.once()` or `.repeatEvery()`) returns a `ScheduledTask` object. You can store this object to monitor or cancel the task manually.

```Java
ScheduledTask task = scheduler.schedule(() -> {
    // some heavy processing
}).repeatEvery(1, TimeUnit.SECONDS, Clock.TICKS);

// Cancel the task manually
task.cancel();

// Check the task status
boolean running = task.isRunning();
boolean cancelled = task.isCancelled();

// Get the exact Instant of the next scheduled execution
Instant nextRun = task.nextExecution();

// Check if the task threw an error and crashed
task.failure().ifPresent(throwable -> {
    getLogger().severe("Task crashed: " + throwable.getMessage());
});
```