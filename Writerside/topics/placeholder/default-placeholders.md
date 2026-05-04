# Default Placeholders
<link-summary>Reference guide for built-in AbyssalLib placeholders and expression operators</link-summary>

Placeholders dynamically resolve contextual data (such as player statistics or server information) into text. Below is a list of all default placeholders included in AbyssalLib and their required syntax.

### Data Placeholders
These placeholders retrieve specific values from the game, the player, or the server.

<table>
<tr>
<th>Placeholder ID</th>
<th>Syntax</th>
<th>Description</th>
</tr>

<tr>
<td><code>abyssallib:player_name</code></td>
<td><code>&lt;placeholder:abyssallib:player_name&gt;</code></td>
<td>Returns the player's username.</td>
</tr>
<tr>
<td><code>abyssallib:player_uuid</code></td>
<td><code>&lt;placeholder:abyssallib:player_uuid&gt;</code></td>
<td>Returns the player's UUID as a string.</td>
</tr>
<tr>
<td><code>abyssallib:player_world</code></td>
<td><code>&lt;placeholder:abyssallib:player_world&gt;</code></td>
<td>Returns the namespace key of the player's current world.</td>
</tr>
<tr>
<td><code>abyssallib:player_gamemode</code></td>
<td><code>&lt;placeholder:abyssallib:player_gamemode&gt;</code></td>
<td>Returns the name of the player's current gamemode.</td>
</tr>
<tr>
<td><code>abyssallib:player_locale</code></td>
<td><code>&lt;placeholder:abyssallib:player_locale&gt;</code></td>
<td>Returns the player's client language locale setting.</td>
</tr>
<tr>
<td><code>abyssallib:player_ping</code></td>
<td><code>&lt;placeholder:abyssallib:player_ping&gt;</code></td>
<td>Returns the player's current connection ping in milliseconds.</td>
</tr>
<tr>
<td><code>abyssallib:player_health</code></td>
<td><code>&lt;placeholder:abyssallib:player_health&gt;</code></td>
<td>Returns the player's current health.</td>
</tr>
<tr>
<td><code>abyssallib:player_max_health</code></td>
<td><code>&lt;placeholder:abyssallib:player_max_health&gt;</code></td>
<td>Returns the player's maximum health attribute value.</td>
</tr>
<tr>
<td><code>abyssallib:player_food</code></td>
<td><code>&lt;placeholder:abyssallib:player_food&gt;</code></td>
<td>Returns the player's current food/hunger level.</td>
</tr>
<tr>
<td><code>abyssallib:player_saturation</code></td>
<td><code>&lt;placeholder:abyssallib:player_saturation&gt;</code></td>
<td>Returns the player's current hidden saturation level.</td>
</tr>
<tr>
<td><code>abyssallib:player_level</code></td>
<td><code>&lt;placeholder:abyssallib:player_level&gt;</code></td>
<td>Returns the player's current experience level.</td>
</tr>
<tr>
<td><code>abyssallib:player_x</code></td>
<td><code>&lt;placeholder:abyssallib:player_x&gt;</code></td>
<td>Returns the player's exact X coordinate.</td>
</tr>
<tr>
<td><code>abyssallib:player_y</code></td>
<td><code>&lt;placeholder:abyssallib:player_y&gt;</code></td>
<td>Returns the player's exact Y coordinate.</td>
</tr>
<tr>
<td><code>abyssallib:player_z</code></td>
<td><code>&lt;placeholder:abyssallib:player_z&gt;</code></td>
<td>Returns the player's exact Z coordinate.</td>
</tr>

<tr>
<td><code>abyssallib:server_tps</code></td>
<td><code>&lt;placeholder:abyssallib:server_tps&gt;</code></td>
<td>Returns the server's current 1-minute TPS (Ticks Per Second).</td>
</tr>
<tr>
<td><code>abyssallib:server_online</code></td>
<td><code>&lt;placeholder:abyssallib:server_online&gt;</code></td>
<td>Returns the current total number of online players.</td>
</tr>
<tr>
<td><code>abyssallib:server_max_players</code></td>
<td><code>&lt;placeholder:abyssallib:server_max_players&gt;</code></td>
<td>Returns the server's configured maximum player capacity.</td>
</tr>

<tr>
<td><code>abyssallib:is_sneaking</code></td>
<td><code>&lt;placeholder:abyssallib:is_sneaking&gt;</code></td>
<td>Returns true if the player is currently sneaking.</td>
</tr>
<tr>
<td><code>abyssallib:is_sprinting</code></td>
<td><code>&lt;placeholder:abyssallib:is_sprinting&gt;</code></td>
<td>Returns true if the player is currently sprinting.</td>
</tr>
<tr>
<td><code>abyssallib:is_flying</code></td>
<td><code>&lt;placeholder:abyssallib:is_flying&gt;</code></td>
<td>Returns true if the player is currently flying.</td>
</tr>
<tr>
<td><code>abyssallib:is_op</code></td>
<td><code>&lt;placeholder:abyssallib:is_op&gt;</code></td>
<td>Returns true if the player is a server operator.</td>
</tr>

<tr>
<td><code>abyssallib:attribute</code></td>
<td><code>&lt;placeholder:abyssallib:attribute:&lt;id&gt;:[type]:[mod_key]&gt;</code></td>
<td>Returns the value of a vanilla entity attribute. <code>&lt;id&gt;</code> is required. <code>[type]</code> can be "base", "modifier", or omitted (defaults to final value). <code>[mod_key]</code> is only required if querying a specific modifier.</td>
</tr>
<tr>
<td><code>abyssallib:statistic</code></td>
<td><code>&lt;placeholder:abyssallib:statistic:&lt;id&gt;&gt;</code></td>
<td>Returns the integer value of a vanilla server statistic. <code>&lt;id&gt;</code> must match the Bukkit Statistic enum exact name.</td>
</tr>
<tr>
<td><code>abyssallib:custom_attribute</code></td>
<td><code>&lt;placeholder:abyssallib:custom_attribute:&lt;id&gt;:[type]:[mod_key]&gt;</code></td>
<td>Returns the value of a custom AbyssalLib entity attribute. Syntax rules map identically to the vanilla attribute placeholder.</td>
</tr>
<tr>
<td><code>abyssallib:custom_statistic</code></td>
<td><code>&lt;placeholder:abyssallib:custom_statistic:&lt;id&gt;&gt;</code></td>
<td>Returns the value of a custom AbyssalLib player statistic.</td>
</tr>
</table>

---

### Expression Operators
These placeholders act as mathematical and logical operators. They require a target `Expressionable` placeholder (such as `abyssallib:player_health`) to evaluate against.

Where `[argument]` is specified, it denotes the value you are applying against the target placeholder (e.g., `<placeholder:abyssallib:add:abyssallib:player_health:5>`).

<table>
<tr>
<th>Operator ID</th>
<th>Syntax</th>
<th>Description</th>
</tr>
<tr>
<td><code>abyssallib:add</code></td>
<td><code>&lt;placeholder:abyssallib:add:&lt;target&gt;:&lt;argument&gt;&gt;</code></td>
<td>Adds the argument to the target placeholder's value.</td>
</tr>
<tr>
<td><code>abyssallib:sub</code></td>
<td><code>&lt;placeholder:abyssallib:sub:&lt;target&gt;:&lt;argument&gt;&gt;</code></td>
<td>Subtracts the argument from the target placeholder's value.</td>
</tr>
<tr>
<td><code>abyssallib:mul</code></td>
<td><code>&lt;placeholder:abyssallib:mul:&lt;target&gt;:&lt;argument&gt;&gt;</code></td>
<td>Multiplies the target placeholder's value by the argument.</td>
</tr>
<tr>
<td><code>abyssallib:div</code></td>
<td><code>&lt;placeholder:abyssallib:div:&lt;target&gt;:&lt;argument&gt;&gt;</code></td>
<td>Divides the target placeholder's value by the argument.</td>
</tr>
<tr>
<td><code>abyssallib:mod</code></td>
<td><code>&lt;placeholder:abyssallib:mod:&lt;target&gt;:&lt;argument&gt;&gt;</code></td>
<td>Returns the remainder of dividing the target by the argument.</td>
</tr>
<tr>
<td><code>abyssallib:pow</code></td>
<td><code>&lt;placeholder:abyssallib:pow:&lt;target&gt;:&lt;argument&gt;&gt;</code></td>
<td>Raises the target placeholder's value to the power of the argument.</td>
</tr>
<tr>
<td><code>abyssallib:min</code></td>
<td><code>&lt;placeholder:abyssallib:min:&lt;target&gt;:&lt;argument&gt;&gt;</code></td>
<td>Returns the lesser value between the target and the argument.</td>
</tr>
<tr>
<td><code>abyssallib:max</code></td>
<td><code>&lt;placeholder:abyssallib:max:&lt;target&gt;:&lt;argument&gt;&gt;</code></td>
<td>Returns the greater value between the target and the argument.</td>
</tr>

<tr>
<td><code>abyssallib:sin</code></td>
<td><code>&lt;placeholder:abyssallib:sin:&lt;target&gt;&gt;</code></td>
<td>Returns the sine of the target placeholder's value.</td>
</tr>
<tr>
<td><code>abyssallib:cos</code></td>
<td><code>&lt;placeholder:abyssallib:cos:&lt;target&gt;&gt;</code></td>
<td>Returns the cosine of the target placeholder's value.</td>
</tr>
<tr>
<td><code>abyssallib:tan</code></td>
<td><code>&lt;placeholder:abyssallib:tan:&lt;target&gt;&gt;</code></td>
<td>Returns the tangent of the target placeholder's value.</td>
</tr>
<tr>
<td><code>abyssallib:asin</code></td>
<td><code>&lt;placeholder:abyssallib:asin:&lt;target&gt;&gt;</code></td>
<td>Returns the arc sine of the target placeholder's value.</td>
</tr>
<tr>
<td><code>abyssallib:acos</code></td>
<td><code>&lt;placeholder:abyssallib:acos:&lt;target&gt;&gt;</code></td>
<td>Returns the arc cosine of the target placeholder's value.</td>
</tr>
<tr>
<td><code>abyssallib:atan</code></td>
<td><code>&lt;placeholder:abyssallib:atan:&lt;target&gt;&gt;</code></td>
<td>Returns the arc tangent of the target placeholder's value.</td>
</tr>

<tr>
<td><code>abyssallib:round</code></td>
<td><code>&lt;placeholder:abyssallib:round:&lt;target&gt;&gt;</code></td>
<td>Rounds the target placeholder's value to the nearest whole number.</td>
</tr>
<tr>
<td><code>abyssallib:floor</code></td>
<td><code>&lt;placeholder:abyssallib:floor:&lt;target&gt;&gt;</code></td>
<td>Rounds the target placeholder's value down to the nearest whole number.</td>
</tr>
<tr>
<td><code>abyssallib:ceil</code></td>
<td><code>&lt;placeholder:abyssallib:ceil:&lt;target&gt;&gt;</code></td>
<td>Rounds the target placeholder's value up to the nearest whole number.</td>
</tr>
<tr>
<td><code>abyssallib:abs</code></td>
<td><code>&lt;placeholder:abyssallib:abs:&lt;target&gt;&gt;</code></td>
<td>Returns the absolute (positive) value of the target placeholder.</td>
</tr>

<tr>
<td><code>abyssallib:eq</code></td>
<td><code>&lt;placeholder:abyssallib:eq:&lt;target&gt;:&lt;argument&gt;&gt;</code></td>
<td>Returns <code>true</code> if the target equals the argument.</td>
</tr>
<tr>
<td><code>abyssallib:neq</code></td>
<td><code>&lt;placeholder:abyssallib:neq:&lt;target&gt;:&lt;argument&gt;&gt;</code></td>
<td>Returns <code>true</code> if the target does not equal the argument.</td>
</tr>
<tr>
<td><code>abyssallib:gt</code></td>
<td><code>&lt;placeholder:abyssallib:gt:&lt;target&gt;:&lt;argument&gt;&gt;</code></td>
<td>Returns <code>true</code> if the target is strictly greater than the argument.</td>
</tr>
<tr>
<td><code>abyssallib:lt</code></td>
<td><code>&lt;placeholder:abyssallib:lt:&lt;target&gt;:&lt;argument&gt;&gt;</code></td>
<td>Returns <code>true</code> if the target is strictly less than the argument.</td>
</tr>
<tr>
<td><code>abyssallib:gte</code></td>
<td><code>&lt;placeholder:abyssallib:gte:&lt;target&gt;:&lt;argument&gt;&gt;</code></td>
<td>Returns <code>true</code> if the target is greater than or equal to the argument.</td>
</tr>
<tr>
<td><code>abyssallib:lte</code></td>
<td><code>&lt;placeholder:abyssallib:lte:&lt;target&gt;:&lt;argument&gt;&gt;</code></td>
<td>Returns <code>true</code> if the target is less than or equal to the argument.</td>
</tr>

<tr>
<td><code>abyssallib:and</code></td>
<td><code>&lt;placeholder:abyssallib:and:&lt;target&gt;:&lt;argument&gt;&gt;</code></td>
<td>Returns <code>true</code> if both the target and the argument evaluate to true.</td>
</tr>
<tr>
<td><code>abyssallib:or</code></td>
<td><code>&lt;placeholder:abyssallib:or:&lt;target&gt;:&lt;argument&gt;&gt;</code></td>
<td>Returns <code>true</code> if either the target or the argument evaluate to true.</td>
</tr>
<tr>
<td><code>abyssallib:xor</code></td>
<td><code>&lt;placeholder:abyssallib:xor:&lt;target&gt;:&lt;argument&gt;&gt;</code></td>
<td>Returns <code>true</code> if exclusively one of the target or argument evaluates to true.</td>
</tr>
<tr>
<td><code>abyssallib:not</code></td>
<td><code>&lt;placeholder:abyssallib:not:&lt;target&gt;&gt;</code></td>
<td>Inverts the boolean state of the target placeholder.</td>
</tr>
</table>