# Sidebars
<link-summary>Guide to creating and managing per-player, flicker-free scoreboards</link-summary>

The Sidebar API provides a robust, packet-based, and entirely flicker-free way to manage sideboards (scoreboards) for players. It supports dynamic per-player content, a priority-based overlapping system, and native support for hiding the red side numbers.

### Creating a Sidebar
To create a sidebar, instantiate the `Sidebar` class with a unique `Key` and a priority level.

The priority level determines which sidebar a player sees if they are added to multiple sidebars simultaneously. The sidebar with the highest priority integer will be displayed.

```Java
// Creates a sidebar with a priority of 10
Sidebar globalSidebar = new Sidebar(Key.key("abyssallib_example", "global_board"), 10);
```

### Setting Content
You can assign both static `Component`s and dynamic `Function<Player, Component>`s to the title and lines of the sidebar.

Using a `Function` allows you to use a single `Sidebar` instance globally, while still displaying player-specific data (like their individual ping, name, or stats). Sidebars support up to 15 lines (indexes `0` to `14`).

```Java
// Setting a static title
globalSidebar.setTitle(TextUtil.parse("<gold><b>My Server</b></gold>"));

// Setting a static line at index 0 (top line)
globalSidebar.setLine(0, TextUtil.parse("<gray>Welcome to the server!</gray>"));

// Setting a dynamic line at index 1 that calculates per-player
globalSidebar.setLine(1, player -> 
    TextUtil.parse("<white>Your Ping: <green>" + player.getPing() + "ms</green></white>")
);

// Setting a dynamic line at index 2
globalSidebar.setLine(2, player -> 
    TextUtil.parse("<white>Your Location: <aqua>" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ() + "</aqua></white>")
);
```

<tip>
The lines are rendered from top to bottom based on their index. Index <code>0</code> is the top-most line below the title, and index <code>14</code> is the bottom-most line.
</tip>

### Hiding the Score Numbers
In modern Minecraft versions, you can hide the red numbers that normally appear on the right side of the scoreboard. By default, AbyssalLib sidebars have these numbers hidden. You can toggle this behavior using `setShowNumbers()`.

```Java
// This will force the standard 1-15 red numbers to appear on the right side
globalSidebar.setShowNumbers(true);
```

### Managing Viewers and Priority
To show the sidebar to a player, simply add them as a viewer.

Because of the priority system, you can layer sidebars. For example, if a player is viewing a `GlobalBoard` (Priority 10) and you add them to a `CombatBoard` (Priority 50), the API will automatically transition their screen to the `CombatBoard`. When you remove them from the `CombatBoard`, it will seamlessly fall back to the `GlobalBoard`.

```Java
// Show the sidebar to the player
globalSidebar.addViewer(player);

// Remove the sidebar from the player
globalSidebar.removeViewer(player);
```

### Cleanup
If you are completely removing a sidebar feature from your plugin (e.g., during a minigame reset or plugin reload), you must destroy the sidebar to clear it from the internal memory and remove it from all players currently viewing it.

```Java
// Removes the sidebar globally from all viewers and memory
globalSidebar.destroy();

// Alternatively, you can close ALL sidebars across the entire server:
PlayerSidebarManager.closeAll();
```