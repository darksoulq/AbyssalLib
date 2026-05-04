# Toasts
<link-summary>Guide to sending transient UI toast pop-ups to players</link-summary>
<secondary-label ref="wip"/>

The Toast API leverages Minecraft's advancement packet system to display transient pop-up messages in the top-right corner of the player's screen.

### Creating and Sending a Toast
To create a toast notification, use `Toast.builder()`. You can configure the title, subtitle, icon, and the border frame style. Once built, simply call `send(Player)`.

```Java
public final class NotificationUtil {

    public static void sendLevelUpToast(Player player, int level) {
        Toast toast = Toast.builder()
            // The primary title
            .line1(TextUtil.parse("<green>Level Up!</green>"))
            // The secondary description beneath the title
            .line2(TextUtil.parse("<gray>You reached level " + level + ".</gray>"))
            // The visual item icon
            .icon(new ItemStack(Material.EXPERIENCE_BOTTLE))
            // The border style (TASK, GOAL, or CHALLENGE)
            .frame(AdvancementFrame.CHALLENGE)
            .build();

        // Send the packets to the player
        toast.send(player);
    }
}
```

[IMG?]

<note>
The API automatically schedules a cleanup packet 100 ticks (5 seconds) after the toast is sent to safely remove the virtual advancement from the player's client memory. You do not need to manually manage the cleanup.
</note>

### Builder Configuration Methods
The `Toast.Builder` provides a simple fluent interface for configuring the notification's appearance.

<table>
<tr>
<th>Method</th>
<th>Description</th>
</tr>
<tr>
<td><code>line1(Component)</code></td>
<td><strong>(Required)</strong> Sets the primary header text displayed in the toast.</td>
</tr>
<tr>
<td><code>line2(Component)</code></td>
<td><em>(Optional)</em> Sets the sub-header text displayed underneath the main title. If omitted, the toast will only display one line.</td>
</tr>
<tr>
<td><code>icon(ItemStack)</code></td>
<td><strong>(Required)</strong> Sets the item stack used as the visual icon on the left side of the notification.</td>
</tr>
<tr>
<td><code>frame(AdvancementFrame)</code></td>
<td>Sets the border frame style used for the notification. This dictates the background color and border shape (e.g., <code>TASK</code>, <code>GOAL</code>, <code>CHALLENGE</code>). Defaults to <code>TASK</code>.</td>
</tr>
<tr>
<td><code>build()</code></td>
<td>Finalizes the configuration and produces the <code>Toast</code> instance, ready to be sent.</td>
</tr>
</table>