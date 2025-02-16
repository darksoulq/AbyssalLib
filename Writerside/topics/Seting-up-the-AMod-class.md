# Seting up the AMod class

setting up the AMod class is the first step to using AbyssalLib in your plugin, its wuere you will register items, blocks, entities and everything else!

<procedure title="Creating your AMod class" id="creating-your-amod-class">
<step>
Make a class in your project and extend `me.darksoul.abyssalLib.AMod`, implment the methods and pass your plugin instance to the super().

```Java
public class MyMod extends AMod {
    public MyMod() {
        super(YouPluginInstance);
    }
    
    @Override
    public void setRegistries() {
        // Here we register stuff.
    }
}
```
</step>
<step>
Now you can move on to registering stuff!
</step>
</procedure>
