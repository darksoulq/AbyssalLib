# Custom Food
<link-summary>Guide to creating a custom Food item using DataComponents</link-summary>

Creating custom food items in AbyssalLib is highly flexible thanks to the DataComponent system. In this guide, we will transform a standard item into something a player can eat to restore hunger and saturation.

To create a custom food, you need to utilize `Item#setData` and apply both the `Food` and `Consume` components.

### Adding the Food component
To make the item mathematically "edible" to the server (providing hunger/saturation), we will set the `Food` DataComponent.

```Java
item.setData(new Food(FoodProperties.food()
    .canAlwaysEat(false)
    .nutrition(3)
    .saturation(4.5f)
    .build()));
```

<note>
Nutrition dictates how much of the hunger bar is filled. 1 point of nutrition equals half a hunger drumstick (so a value of 3 restores 1.5 drumsticks).
</note>

<table>
<tr>
<th>Method</th>
<th>Information</th>
</tr>
<tr>
<td><code>canAlwaysEat</code></td>
<td>Whether or not the player can eat the item while their nutrition is already full.</td>
</tr>
<tr>
<td><code>nutrition</code></td>
<td>Amount of nutrition to restore.</td>
</tr>
<tr>
<td><code>saturation</code></td>
<td>Amount of saturation (hidden anti-hunger mechanic) to restore.</td>
</tr>
</table>

While this gives the item food properties, if you try to eat the item in-game now, it will not work because the item lacks the timing needed to be consumed.

### Adding the Consumable component
To make the item physically eaten by the player, we add the `Consume` component similarly to the `Food` DataComponent.

```Java
item.setData(new Consume(Consumable.consumable()
    .consumeSeconds(0.8f)
    .build()));
```

<table>
<tr>
<th>Method</th>
<th>Information</th>
</tr>
<tr>
<td><code>addEffect</code></td>
<td>Adds a single <code>ConsumeEffect</code> (like gaining a Potion Effect).</td>
</tr>
<tr>
<td><code>addEffects</code></td>
<td>Adds multiple <code>ConsumeEffect</code> instances.</td>
</tr>
<tr>
<td><code>animation</code></td>
<td>Sets the <code>ItemUseAnimation</code> to be used during consumption (e.g., eat, drink).</td>
</tr>
<tr>
<td><code>consumeSeconds</code></td>
<td>Sets the time (in seconds) it takes to finish eating the item.</td>
</tr>
<tr>
<td><code>effects</code></td>
<td>Sets the <code>ConsumeEffect</code> instances that should occur upon completion.</td>
</tr>
<tr>
<td><code>hasConsumeParticles</code></td>
<td>Sets whether crumb particles should spawn during consumption.</td>
</tr>
<tr>
<td><code>sound</code></td>
<td>Sets the sound that should play during consumption.</td>
</tr>
</table>

### Bringing it all together
With both components applied, our food item is fully functional. Here is how a complete registration might look:

```Java
public static final Item EDIBLE_PAPER = register("edible_paper", item -> {
    item.setData(new ItemModel(NamespacedKey.minecraft("paper")));
    
    // 1. Give it food properties
    item.setData(new Food(FoodProperties.food()
        .canAlwaysEat(true)
        .nutrition(3)
        .saturation(4.5f)
        .build()));
        
    // 2. Give it consume properties
    item.setData(new Consume(Consumable.consumable()
        .consumeSeconds(0.8f)
        .hasConsumeParticles(true)
        .build()));
});
```

<video src="eating_edible_paper.mp4" preview-src="eating_edible_paper.png"/>