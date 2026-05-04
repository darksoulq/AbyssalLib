# Reflection API
<link-summary>Reference guide and comparison for the AbyssalLib Reflection API</link-summary>

The AbyssalLib Reflection API is a high-performance, cached wrapper around standard Java Reflection and `MethodHandles`. It abstracts away accessibility checks (`setAccessible(true)`) and uses a `Result<T>` wrapper to eliminate standard checked exceptions.

### The Result Wrapper
All operations that can fail (such as finding a method or getting a field value) return a `Result<T>` instead of throwing checked exceptions.

<table>
<tr>
<th>Method</th>
<th>Information</th>
</tr>
<tr>
<td><code>get()</code></td>
<td>Returns the value, or throws a <code>RuntimeException</code> if the operation failed.</td>
</tr>
<tr>
<td><code>getOrNull()</code></td>
<td>Returns the value, or <code>null</code> if the operation failed.</td>
</tr>
<tr>
<td><code>getOrElse(T fallback)</code></td>
<td>Returns the value, or the provided fallback if it failed.</td>
</tr>
<tr>
<td><code>ifSuccess(Consumer&lt;T&gt;)</code></td>
<td>Executes the consumer block only if the operation was successful.</td>
</tr>
<tr>
<td><code>ifFailure(Consumer&lt;Throwable&gt;)</code></td>
<td>Executes the consumer block only if the operation failed, passing the exception.</td>
</tr>
<tr>
<td><code>map(Function)</code> / <code>flatMap(Function)</code></td>
<td>Transforms the result functionally, chaining reflection calls together safely.</td>
</tr>
</table>

---

### Accessing Classes
The entry point for the API is the `Reflect` class. Internal caching ensures that repeated lookups for the same class or member do not incur a performance penalty.

<tabs>
<tab title="AbyssalLib">

```Java
// From a known class (Never fails, returns directly)
ReflectClass<Player> refPlayer = Reflect.of(Player.class);

// From a string (Returns a Result)
Result<ReflectClass<?>> refEntity = Reflect.of("net.minecraft.world.entity.Entity");
```

</tab>
<tab title="Vanilla">

```Java
try {
    Class<?> clazz = Class.forName("net.minecraft.world.entity.Entity");
} catch (ClassNotFoundException e) {
    e.printStackTrace();
}
```

</tab>
</tabs>

---

### Fields
You can retrieve fields by their exact string name, or dynamically search the class hierarchy for the first field matching a specific `Class` type using `fieldByType`.

<tabs>
<tab title="AbyssalLib">

```Java
ReflectClass<?> clazz = Reflect.of(target.getClass());

// Get
String value = (String) clazz.field("privateString")
    .flatMap(f -> f.get(target))
    .getOrNull();

// Set
clazz.field("privateString").ifSuccess(f -> f.set(target, "New Value"));

// Look up by Type instead of name
clazz.fieldByType(String.class).ifSuccess(f -> f.set(target, "New Value"));
```

</tab>
<tab title="Vanilla">

```Java
try {
    Field field = target.getClass().getDeclaredField("privateString");
    field.setAccessible(true);
    
    // Get
    String value = (String) field.get(target);
    
    // Set
    field.set(target, "New Value");
} catch (NoSuchFieldException | IllegalAccessException e) {
    e.printStackTrace();
}
```

</tab>
</tabs>

---

### Methods
Methods are looked up using their name and their exact parameter types.

<tabs>
<tab title="AbyssalLib">

```Java
Reflect.of(target.getClass())
    .method("doSomething", int.class, String.class)
    .ifSuccess(m -> {
        boolean result = (boolean) m.invoke(target, 5, "Hello").getOrElse(false);
    });
```

</tab>
<tab title="Vanilla">

```Java
try {
    Method method = target.getClass().getDeclaredMethod("doSomething", int.class, String.class);
    method.setAccessible(true);
    
    boolean result = (boolean) method.invoke(target, 5, "Hello");
} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
    e.printStackTrace();
}
```

</tab>
</tabs>

---

### Constructors
Constructors are fetched using their exact parameter types and can be instantiated safely.

<tabs>
<tab title="AbyssalLib">

```Java
ItemStack stack = Reflect.of(ItemStack.class)
    .constructor(Material.class, int.class)
    .flatMap(c -> c.newInstance(Material.DIAMOND, 64))
    .getOrNull();
```

</tab>
<tab title="Vanilla">

```Java
try {
    Constructor<ItemStack> constructor = ItemStack.class.getDeclaredConstructor(Material.class, int.class);
    constructor.setAccessible(true);
    
    ItemStack stack = constructor.newInstance(Material.DIAMOND, 64);
} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
    e.printStackTrace();
}
```

</tab>
</tabs>

---

### Arrays (`ReflectArray`)
Provides a safe wrapper over `java.lang.reflect.Array` to instantiate, read, and write arrays dynamically without manual casting or try-catch blocks.

<tabs>
<tab title="AbyssalLib">

```Java
ReflectArray.newInstance(String.class, 10).ifSuccess(array -> {
    ReflectArray.set(array, 0, "Hello");
    String value = (String) ReflectArray.get(array, 0).getOrNull();
    int length = ReflectArray.getLength(array).getOrElse(0);
});
```

</tab>
<tab title="Vanilla">

```Java
try {
    Object array = Array.newInstance(String.class, 10);
    Array.set(array, 0, "Hello");
    String value = (String) Array.get(array, 0);
    int length = Array.getLength(array);
} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
    e.printStackTrace();
}
```

</tab>
</tabs>

---

### Generics and Types (`ReflectType`)
`ReflectType` wraps standard Java `Type` objects to easily resolve generic parameters, wildcards, and array components without manual `instanceof` checks.

```Java
Reflect.of(MyClass.class).field("stringList").ifSuccess(field -> {
    // Fetches the 'String' class from inside a List<String> field
    ReflectType genericType = field.getGenericType();
    
    if (genericType.isParameterized()) {
        genericType.getTypeArguments().forEach(typeArg -> {
            Class<?> rawClass = typeArg.getRawType().getOrNull().getUnderlyingClass();
        });
    }
});
```

---

### Annotations (`ReflectAnnotation`)
Allows safe reading of annotation attributes at runtime without directly invoking the annotation's underlying interface methods.

<tabs>
<tab title="AbyssalLib">

```Java
Reflect.of(targetClass)
    .getReflectAnnotation(MyAnnotation.class)
    .ifSuccess(ann -> {
        // Safe dynamic retrieval of the default "value()" attribute
        String value = (String) ann.value().getOrNull();
        
        // Safe dynamic retrieval of a custom attribute
        int priority = (int) ann.getValue("priority").getOrElse(0);
    });
```

</tab>
<tab title="Vanilla">

```Java
try {
    MyAnnotation ann = targetClass.getAnnotation(MyAnnotation.class);
    if (ann != null) {
        Method valueMethod = ann.annotationType().getDeclaredMethod("value");
        String value = (String) valueMethod.invoke(ann);
    }
} catch (Exception e) {
    e.printStackTrace();
}
```

</tab>
</tabs>