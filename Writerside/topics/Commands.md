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
abyssallib.items.give
</td>
<td>
Gives the item by the provided id (amount is optional)
</td>
</tr>
<tr>
<td>
/abyssallib summon $location $entity_id
</td>
<td>
abyssallib.entity.summon
</td>
<td>
Spawns a custom entity at the given location
</td>
</tr>
<tr>
<td>
/abyssallib statistics view
</td>
<td>
abyssallib.statistics.menu.self
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
abyssallib.statistics.menu.all
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
abyssallib.statistics.view.self
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
abyssallib.statistics.view.all
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
abyssallib.content.items.view
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
abyssallib.attribute.get
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
abyssallib.reload
</td>
<td>
Reloads all Commands
</td>
</tr>
<tr>
<td>
/abyssallib reload pack
</td>
<td>
abyssallib.reload
</td>
<td>
Reloads all packs from filesystem and resends to all online players.
</td>
</tr>
</table>