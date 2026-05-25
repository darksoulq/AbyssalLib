# Command API
<link-summary>Object-oriented Brigadier command wrapper</link-summary>

AbyssalLib provides a clean, object-oriented API for building and registering commands on top of Paper's Brigadier implementation. By extending `BaseCommand`, you can define requirements, default executors, complex argument syntaxes, and nested subcommands without dealing with messy `.then()` chains.

### Creating a Command
To create a command, create a class extending `BaseCommand`. The constructor requires the primary command name, followed by an optional list of aliases.

```Java
import com.github.darksoulq.abyssallib.server.command.BaseCommand;

public class HealCommand extends BaseCommand {
    public HealCommand() {
        // "heal" is the root, "restore" and "h" are aliases
        super("heal", "restore", "h");
    }
}
```

---

### Requirements & Permissions
You can restrict who can execute or see the command using `setRequirement()`. This automatically applies the requirement to the root command and all aliases simultaneously.

```Java
public HealCommand() {
    super("heal", "h");

    // Only players with this permission will see/use this command
    setRequirement(ctx -> ctx.getSender().hasPermission("myplugin.command.heal"));
}
```

---

### Execution & Results
Command executors use the `CommandExecutor` functional interface, which must return a `CommandResult`. This replaces standard Brigadier integers, making the success, failure, or error state explicitly clear.

* `CommandResult.success()`: Execution succeeded (Returns `1`).
* `CommandResult.success(int)`: Execution succeeded with a specific integer value.
* `CommandResult.failure()`: Execution failed silently (Returns `0`).
* `CommandResult.error(CommandSyntaxException)`: Execution failed and throws a specific syntax error to the sender.

#### Default Executor
The default executor handles the base command when no additional arguments are provided (e.g., just typing `/heal`).

```Java
public HealCommand() {
    super("heal");
    
    setDefaultExecutor(ctx -> {
        if (!(ctx.getSource().getSender() instanceof Player player)) {
            return CommandResult.failure();
        }

        player.setHealth(20.0);
        player.sendMessage("You have been healed!");
        return CommandResult.success();
    });
}
```

---

### Adding Syntaxes
Instead of chaining Brigadier's `.then()` method repeatedly, AbyssalLib uses `addSyntax()`.

You provide the `CommandExecutor` that should run for a specific path, followed by a varargs list of `ArgumentBuilder`s **in the exact order they should be typed**. Because the API internally clones these nodes, you can assign your arguments to variables and safely reuse them across different syntaxes without corrupting Brigadier's command tree.

```Java
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import com.mojang.brigadier.arguments.IntegerArgumentType;

public class HealCommand extends BaseCommand {
    public HealCommand() {
        super("heal");

        // Define arguments once
        var targetArg = Commands.argument("target", ArgumentTypes.player());
        var amountArg = Commands.argument("amount", IntegerArgumentType.integer(1, 20));

        // 1. Registers: /heal <target>
        addSyntax(this::healTarget, targetArg);

        // 2. Registers: /heal <target> <amount>
        addSyntax(this::healTargetAmount, targetArg, amountArg);
    }
    
    private CommandResult healTarget(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player target = ctx.getArgument("target", PlayerSelector.class).findSinglePlayer(ctx.getSource());

        target.setHealth(20.0);
        ctx.getSource().getSender().sendMessage("Healed " + target.getName());

        return CommandResult.success();
    }

    private CommandResult healTargetAmount(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player target = ctx.getArgument("target", PlayerSelector.class).findSinglePlayer(ctx.getSource());
        int amount = ctx.getArgument("amount", Integer.class);

        target.setHealth(amount);
        ctx.getSource().getSender().sendMessage("Set " + target.getName() + "'s health to " + amount);

        return CommandResult.success();
    }
}
```

---

### Subcommands
For modular and clean architecture, you should build subcommands as entirely separate classes extending `BaseCommand`.

You can then attach them to a parent command using `addSubcommand()`. This automatically hooks up the child's root and aliases to the parent's root and aliases, allowing infinite safe nesting.

<tabs>
<tab title="Main Command">

Create the root command and attach the separate subcommand classes.

```Java
public class AdminCommand extends BaseCommand {
    public AdminCommand() {
        super("admin", "admincmd");
        setRequirement(ctx -> ctx.getSender().hasPermission("myplugin.admin"));

        // Registers /admin heal [args]
        addSubcommand(new HealCommand());
        
        // Registers /admin feed [args]
        addSubcommand(new FeedCommand());
    }
}
```

</tab>
<tab title="Subcommand Class">

Design the subcommand exactly like a normal standalone command. Its root name naturally acts as the sub-argument literal.

```Java
public class FeedCommand extends BaseCommand {
    public FeedCommand() {
        super("feed", "f"); // "feed" and "f" become the subcommands

        // Triggers on exactly: /admin feed
        setDefaultExecutor(ctx -> {
            if (ctx.getSource().getSender() instanceof Player player) {
                player.setFoodLevel(20);
                return CommandResult.success();
            }
            return CommandResult.failure();
        });

        // You can still add deeper syntaxes: /admin feed <target>
        var targetArg = Commands.argument("target", ArgumentTypes.player());
        addSyntax(this::feedTarget, targetArg);
    }

    private CommandResult feedTarget(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player target = ctx.getArgument("target", PlayerSelector.class).findSinglePlayer(ctx.getSource());
        target.setFoodLevel(20);
        return CommandResult.success();
    }
}
```

</tab>
</tabs>

---

### Registration
Once your root command class is built, register it to the server using the `CommandBus`. Provide your plugin's ID and an instance of your root command.

```Java
public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Register the command
        CommandBus.register("myplugin", new AdminCommand());
    }
}
```
