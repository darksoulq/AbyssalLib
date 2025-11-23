# Commands

AbyssalLib comes with a few useful commands for admins:

<table>
<tr>
<td>
Command
</td>
<td>
Permission
</td>
<td>
Description
</td>
</tr>
<tr>
<td>
/abyssallib give $item:id $amount
</td>
<td>
abyssallib.admin.give
</td>
<td>
Gives the item by the provided id (amount is optional)
</td>
</tr>
<tr>
<td>
/abyssallib statistics view
</td>
<td>
abyssallib.player.statistic_self.menu
</td>
<td>
Opens a dialog that shows the stats of the sender
</td>
</tr>
<tr>
<td>
/abyssallib statistics view $player
</td>
<td>
abyssallib.admin.statistic_all.menu
</td>
<td>
Opens a dialog that shows the stats of the given player
</td>
</tr>
<tr>
<td>
/abyssallib statistics get
</td>
<td>
abyssallib.player.statistic_self
</td>
<td>
Shows the stats of the sender in chat
</td>
</tr>
<tr>
<td>
/abyssallib statistics get $player
</td>
<td>
abyssallib.admin.statistic_all
</td>
<td>
Shows the stats of the given player in chat
</td>
</tr>
<tr>
<td>
/abyssallib content items $plugin
</td>
<td>
None
</td>
<td>
Opens the main menu of the Item Menu if no $plugin is given (main menu shows all plugins that add items), incase `abyssallib.admin.give` has been granted to sender, they will be able to take out items (normal players are only able to view)
</td>
</tr>
<tr>
<td>
/abyssallib attribute get $entity_select $attribute:id
</td>
<td>
abyssallib.admin.attribute
</td>
<td>
Gets the attribute value of the given attribute for a given entity
</td>
</tr>
<tr>
<td>
/abyssallib reload commands
</td>
<td>
abyssallib.admin.reload
</td>
<td>
Reloads all Commands
</td>
</tr>
</table>