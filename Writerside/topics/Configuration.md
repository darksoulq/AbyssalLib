# Configuration
<link-summary>Complete guide to configuring AbyssalLib, including resource pack hosting and permissions</link-summary>

This page details all available configuration options for AbyssalLib. The configuration file allows you to tweak entity spawning limits, set up the built-in resource pack host, and configure the database for the permissions system.

The configuration file is generated upon the first server startup and is located at <path>serverfolder/config/abyssallib/config.yml</path>.

<tip>
Changes to the configuration usually require a full server restart to take effect safely, especially when altering database credentials or the resource pack host ports!
</tip>

### Available config options

<table>
<tr>
<th>Setting</th>
<th>Description</th>
<th>Default</th>
</tr>
<tr>
<td>metrics</td>
<td>Whether to enable BStats for anonymous usage tracking.</td>
<td><code>true</code></td>
</tr>
<tr>
<td>spawn_limits.monster</td>
<td>Max custom MONSTER type entities that may be spawned naturally.</td>
<td><code>70</code></td>
</tr>
<tr>
<td>spawn_limits.creature</td>
<td>Max custom CREATURE type entities that may be spawned naturally.</td>
<td><code>10</code></td>
</tr>
<tr>
<td>spawn_limits.ambient</td>
<td>Max custom AMBIENT type entities that may be spawned naturally.</td>
<td><code>5</code></td>
</tr>
<tr>
<td>spawn_limits.water_creature</td>
<td>Max custom WATER_CREATURE type entities that may be spawned naturally.</td>
<td><code>10</code></td>
</tr>
<tr>
<td>spawn_limits.water_ambient</td>
<td>Max custom WATER_AMBIENT type entities that may be spawned naturally.</td>
<td><code>5</code></td>
</tr>
<tr>
<td>resource-pack.enabled</td>
<td>Whether to enable the built-in pack hosting. Disable this if you are using RSPM or other dedicated resource pack managers.</td>
<td><code>false</code></td>
</tr>
<tr>
<td>resource-pack.protocol</td>
<td>Which protocol to use for the host (http:// or https://).</td>
<td><code>"http"</code></td>
</tr>
<tr>
<td>resource-pack.ip</td>
<td>The numerical IP to use for the RP host.</td>
<td><code>"127.0.0.1"</code></td>
</tr>
<tr>
<td>resource-pack.port</td>
<td>The port to use for the host. Ensure this is port-forwarded if hosting externally.</td>
<td><code>8080</code></td>
</tr>
<tr>
<td>resource-pack.external_packs</td>
<td>All other Non-AbyssalLib packs that should be sent by the hoster (path is relative to <path>serverfolder/</path>).</td>
<td><code>[]</code></td>
</tr>
<tr>
<td>permissions.storage_type</td>
<td>The <tooltip term="storage_type">Storage Type</tooltip> that the permission system should use (e.g., sqlite, h2, mysql, mongodb, redis).</td>
<td><code>sqlite</code></td>
</tr>
<tr>
<td>permissions.sql.host</td>
<td>May be a numerical IP or a domain.</td>
<td><code>"127.0.0.1"</code></td>
</tr>
<tr>
<td>permissions.sql.port</td>
<td>Not needed if using a domain.</td>
<td><code>3306</code></td>
</tr>
<tr>
<td>permissions.sql.database</td>
<td>The name of the database.</td>
<td><code>"abyssallib"</code></td>
</tr>
<tr>
<td>permissions.sql.username</td>
<td>The SQL user.</td>
<td><code>"root"</code></td>
</tr>
<tr>
<td>permissions.sql.password</td>
<td>The SQL password.</td>
<td><code>"password"</code></td>
</tr>
<tr>
<td>permissions.nosql.uri</td>
<td>MongoDB or Redis URI connection string.</td>
<td><code>"mongodb://localhost:27017"</code></td>
</tr>
<tr>
<td>permissions.local.file</td>
<td>Local SQLite or H2 database file name.</td>
<td><code>"permissions.db"</code></td>
</tr>
<tr>
<td>permissions.web.enabled</td>
<td>Whether to enable the built-in Permissions Web Editor.</td>
<td><code>false</code></td>
</tr>
<tr>
<td>permissions.web.protocol</td>
<td>Which protocol to use for the web editor host (http:// or https://).</td>
<td><code>"http"</code></td>
</tr>
<tr>
<td>permissions.web.ip</td>
<td>The numerical IP to use for the web editor host.</td>
<td><code>"127.0.0.1"</code></td>
</tr>
<tr>
<td>permissions.web.port</td>
<td>The port to use for the web editor host.</td>
<td><code>8081</code></td>
</tr>
</table>