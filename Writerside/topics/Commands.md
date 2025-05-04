# Commands

For the most part, commands are basically written as would be in brigadier format, however registration is made much easier with a simpler setup process.

---

## Step 1: annotate a function with @Command(name)

To start with registering your command, you will need to annotate a method with @Command, like this:

```Java
@Command(name="mycommand")
public void mycommand(LiteralArgumentBuilder<CommandSourceStack> root) {
    // make the command as you would in brigadier, do not call .build on root.
}
```

- The function MUST only take in `LiteralArgumentBuilder<CommandSourceStack>`
- "root" is your top-level argument `/root`, root will have the name you declared in the annotation, so in-game it would be `/name`.
- From here, you can structure the command as you would in Brigadier

## Step 2: Registering the command

To register the command, simply call the CommandBus instances .register method, so in your `onEnable()` (or any other method):

```Java
CommandBus.INSTANCE.register(MODID, new MyCommandClass());
```

(assuming MyCommandClass is where the mycommand function is)

That's it! you have successfully created your first command. (For help with Brigadier, read the Paper docs!)