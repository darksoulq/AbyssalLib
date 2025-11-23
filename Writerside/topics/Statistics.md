# Statistics

Statistics allow minecraft-like statistics for players, in implementation it is very similar to Entity Attributes.

### Getting player Statistics and Modifying them
```Java
PlayerStatistics statistics = PlayerStatistics.of(Player);
FloatStatistic stat = (FloatStatistic) statistics.get(Identifier.of("my_plugin", "my_stat"));
stat.setValue(1.2f);
statistics.set(stat);
```

Below you will see what FloatStatistic is (and other types will be shown too).

### Adding custom statistics
```Java
public static MyStats {
    public static final DeferredRegistry<Statistic> STATS = DeferredRegistry.create(Registries.STATISTICS, "my_plugin");
    
    public static final Holder<Statistic> MY_STAT = STATS.register("my_stat", id -> Statistic.of(id, 1.2f));
}
```

Knowledge of DeferredRegistry and Holder is expected from topics before this.

> The ID passed in `statistic.of` is the one we registered our stat with, whereas the value you pass in the second parameter of `Statistic.of(id, val)` will be used as the default value of this sta (and determine type).

### Types of Statistics:

<table>
<tr>
<td>
Name
</td>
<td>
Created By
</td>
</tr>
<tr>
<td>
FloatStatistic
</td>
<td>
Created by passing a float value in Statistic.of
</td>
</tr>
<tr>
<td>
IntStatistic
</td>
<td>
Created by passing an int value in Statistic.of
</td>
</tr>
<tr>
<td>
BooleanStatistic
</td>
<td>
Created by passing a boolean value in Statistic.of
</td>
</tr>
</table>