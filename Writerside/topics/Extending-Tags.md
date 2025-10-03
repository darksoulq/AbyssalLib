# Extending Tags

> You should only extend Tag<?> if what you are trying to group ISN'T handled in a provided Tag.

```Java
public class MyObjectTag extends Tag<MyObject> {
    public MyObjectTag(Identifier id) {
        super(id);
    }

    @Override
    public void add(MyObject value) {
        values.add(...);
    }

    @Override
    public boolean contains(MyObject value) {
        if (values.contains(...)) return true;
        for (Tag<MyObject> tag : included) {
            if (!tag.getValues().contains(...)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Set<MyObject> getAll() {
        Set<MyObject> all = new HashSet<>(values.stream().map(...).toList());
        included.forEach(t ->
                all.addAll(t.getValues().stream().map(...).toList()));
        return all;
    }
}
```

> MyObject is the type of object this tag can contain.

You would for the most part only have to replace the parts marked as `...` to complete your Tag impl.