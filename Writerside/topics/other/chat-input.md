# Chat Input Handler
<link-summary>Guide to capturing and handling asynchronous player chat input</link-summary>

The `ChatInputHandler` provides a simple, functional way to prompt players for text input. When a player is "awaiting" input, their next chat message is intercepted, hidden from public chat, and passed directly into a callback function.

This is highly useful for creating setup wizards, renaming items, or answering prompts without requiring complex command structures or anvil GUIs.

### Requesting Input
To request input from a player, use the `ChatInputHandler.await()` method. You provide the target player, the callback logic (a `Consumer<String>`), and an optional custom prompt to display.

```Java
public void askForGuildName(Player player) {
    Component prompt = TextUtil.parse("<aqua>Please enter your new Guild name in chat:</aqua>");

    ChatInputHandler.await(player, input -> {
        // This block executes when the player types their message.
        if (input.length() > 16) {
            player.sendMessage(TextUtil.parse("<red>That name is too long!</red>"));
            return;
        }

        player.sendMessage(TextUtil.parse("<green>Guild name set to: </green>" + input));
        // Proceed with creating the guild...
        
    }, prompt);
}
```

<note>
Even though chat events are fired asynchronously, the `ChatInputHandler` safely schedules your callback to run on the <strong>primary server thread</strong>. You can safely interact with the Bukkit API (e.g., modifying inventories or spawning entities) inside the callback.
</note>

### Using Timeouts
You can pass an additional `long` parameter representing server ticks to apply a timeout to the request. If the player does not type anything in chat before the timeout expires, the request is silently cancelled, and they are notified.

```Java
// Prompts the player, but expires after 200 ticks (10 seconds)
ChatInputHandler.await(player, input -> {
    player.sendMessage("You typed: " + input);
}, 200L);
```

### Cancelling Requests
If you need to manually abort an active input request (for example, if the player closes a menu or logs out), you can forcefully remove them from the waiting map.

```Java
boolean wasWaiting = ChatInputHandler.cancel(player);
if (wasWaiting) {
    player.sendMessage("Your pending chat prompt was cancelled.");
}
```

---

### Available Methods
Below is a summary of all static methods available in the `ChatInputHandler`.

<table>
<tr>
<th>Method</th>
<th>Description</th>
</tr>
<tr>
<td><code>await(Player, Consumer&lt;String&gt;)</code></td>
<td>Awaits input indefinitely, displaying a default AbyssalLib prompt to the player.</td>
</tr>
<tr>
<td><code>await(Player, Consumer&lt;String&gt;, Component)</code></td>
<td>Awaits input indefinitely, displaying your custom formatted <code>Component</code> prompt.</td>
</tr>
<tr>
<td><code>await(Player, Consumer&lt;String&gt;, long)</code></td>
<td>Awaits input with a default prompt, expiring after the specified number of server ticks.</td>
</tr>
<tr>
<td><code>await(Player, Consumer&lt;String&gt;, Component, long)</code></td>
<td>Awaits input with a custom prompt, expiring after the specified number of server ticks.</td>
</tr>
<tr>
<td><code>cancel(Player)</code></td>
<td>Manually aborts an active input request for the specified player. Returns <code>true</code> if a request was successfully cancelled.</td>
</tr>
</table>