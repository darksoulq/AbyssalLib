# Task Sequences
<link-summary>Chaining delayed tasks seamlessly without callback hell</link-summary>

When building animations, spell effects, minigame countdowns, or cutscenes, you often need to execute a series of actions separated by delays. Traditionally, this results in deeply nested "callback hell" using standard schedulers.

AbyssalLib solves this with the `SequenceBuilder`, allowing you to queue up linear steps of execution and delays sequentially.

### Creating a Sequence
To create a sequence, call `scheduler.sequence()`. This provides a `SequenceBuilder` that you can use to chain `.run()` and `.wait()` commands.

Once your configuration block finishes, the scheduler automatically calls `.start()` and begins executing the queue in order.

```Java
scheduler.sequence(seq -> {
    seq.run(() -> Bukkit.broadcastMessage("Starting sequence..."));
});
```

---

### Chaining Runs and Delays
The sequence processes steps one by one. When it hits a `.run()` block, it executes the code immediately and instantly moves to the next step. When it hits a `.wait()` block, it pauses the queue for the specified time before moving to the next step.

<tabs>
<tab title="Countdown Example">

This is a classic example of a 3-second countdown. Without the sequence API, this would require 4 deeply nested scheduler callbacks or a complex repeating task state machine.

```Java
scheduler.sequence(seq -> {
    seq.run(() -> player.sendMessage("<red>3...</red>"))
       .wait(1, TimeUnit.SECONDS, Clock.TICKS)
       
       .run(() -> player.sendMessage("<gold>2...</gold>"))
       .wait(1, TimeUnit.SECONDS, Clock.TICKS)
       
       .run(() -> player.sendMessage("<yellow>1...</yellow>"))
       .wait(1, TimeUnit.SECONDS, Clock.TICKS)
       
       .run(() -> {
           player.sendMessage("<green>GO!</green>");
           player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
       });
});
```

</tab>
<tab title="Spell Animation">

Sequences are perfect for staggering visual effects.

```Java
scheduler.sequence(seq -> {
    Location loc = player.getLocation();

    seq.run(() -> player.getWorld().spawnParticle(Particle.PORTAL, loc, 50))
       .wait(10, Clock.TICKS) // Wait half a second

       .run(() -> player.getWorld().strikeLightningEffect(loc))
       .wait(5, Clock.TICKS) // Wait a quarter second

       .run(() -> {
           player.teleport(newLocation);
           player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, newLocation, 1);
       });
});
```

</tab>
</tabs>

---

### Sequence TimeUnits and Clocks
The `.wait()` method utilizes the exact same `TimeUnit` and `Clock` parameters as the standard `TaskBuilder`.

You can use the shorthand `.wait(time, Clock)` if you want to default to the clock's native unit, or explicitly define it via `.wait(time, TimeUnit, Clock)`.

```Java
scheduler.sequence(seq -> {
    seq.run(() -> doSomething())
       .wait(50, Clock.TICKS) // Waits 50 server ticks
       .run(() -> doSomethingElse())
       .wait(2, TimeUnit.SECONDS, Clock.REALTIME) // Waits 2 real-world seconds
       .run(() -> finish());
});
```