# Database

The library adds an easy to use api for database management for plugins, currently MySQL and SQLite are supported, more may be added later on.

YOU MUST HAVE SOME EXPERIENCE WITH DATABASES!

## Choose and create a Database instance:
<tabs>
<tab title="SQLite">
<code-block lang="Java">
File file = new File(getDataFolder(), "data.db");
Database db = new SqliteDatabase(file);
</code-block>
</tab>
<tab title="MySQL">
<code-block lang="Java">
Database db = new MysqlDatabase("host", port, "database", "user", "password");
</code-block>
</tab>
</tabs>

## Connecting and disconnecting:
```Java
db.connect(); // Opens the connection
db.disconnect(); // Safely closes the connection
```
you need to connect before running any queries, do this right after making an instance of the Database, and close it when your plugin disables in normal cases.

## Creating a table:
```Java
db.executor().table("users").create()
    .ifNotExists()
    .column("id", "INTEGER")
    .column("name", "TEXT")
    .primaryKey("id")
    .unique("name")
    .execute();
```
## Inserting Data:
```Java
db.executor().table("users")
    .insert()
    .value("id", 1)
    .value("name", "Steve")
    .execute();
```
This will insert (or replace) a row with <code>id = 1</code> and <code>name = "Steve"</code>.

## Updating data:
```Java
db.executor().table("users")
    .update()
    .value("name", "Alex")
    .where("id = ?", 1)
    .execute();
```
## Selecting data:
```Java
List<String> names = db.executor().table("users")
    .where("id > ?", 0)
    .select(rs -> rs.getString("name"));
```

### Notes:
<list>
<li>
SQLite will automatically create the database file if it doesn't exist.
</li>
<li>
MySQL requires a reachable host and valid credentials.
</li>
<li>
All operations are safe to chain fluently. (as long as you know what you are doing)
</li>
<li>
Default values, foreign keys, uniqueness, and constraints are supported through the builder.
</li>
</list>