# Loading your glyphs

> Glyphs are useful for GUI textures, icons, hud elements and so on.

### Loading a glyph:
```Java
public class MyResourcePack {
    public static Font.TextureGlyph glyph;
    
    public static void load(Plugin plugin) {
        ResourcePack pack = new ResourcePack(plugin, "plugin_id");
        Namespace ns = pack.namespace("plugin_id");
        
        Texture texture = ns.texture("glyph/smiley_face");
        
        Font font = ns.font("glyphs");
        glyph = font.glyph(texture, height, ascent);
        
        pack.register(false);
    }
}
```

> This loads a simple BitMap glyph inside the font file "glyphs.json"
> you can utilise the glyph variable wherever you want to use said glyph using `TextureGlyph#toMiniMessageString` and `TextureGlyph#toComponent`

### Loading other font providers:

<tabs>
<tab title="Offset">
<code-block lang="Java">
public static Font.OffsetGlyph offset;

public static void load(Plugin plugin) {
    ResourcePack pack = new ResourcePack(plugin, "plugin_id");
    Namespace ns = pack.namespace("plugin_id");
        
    Font font = ns.font("glyphs");
    offset = font.offset(offset, unicode)

    pack.register(false);
}
</code-block>

> - offset is the offset to apply to the given unicode (float)
> - unicode is the char to apply the offset to

You can afterwards use the offset using `OffsetGlyph#toMiniMessageString` and `OffsetGlyph#toComponent`
</tab>
<tab title="TTF">
<code-block lang="Java">
public static void load(Plugin plugin) {
    ResourcePack pack = new ResourcePack(plugin, "plugin_id");
    Namespace ns = pack.namespace("plugin_id");
        
    Font font = ns.font("my_ttf_font");
    font.ttf(file, xShift, yShift, size, oversample)

    pack.register(false);
}
</code-block>

> - file is the File object of the TTF file
> - xShift and yShift are the shifts to apply to the characters
> - size is the scale of the font
> - oversample is just what it says

`Font#ttf` does NOT return anything, it simple loads given font
</tab>
<tab title="UniHex">
<code-block lang="Java">
public static void load(Plugin plugin) {
    ResourcePack pack = new ResourcePack(plugin, "plugin_id");
    Namespace ns = pack.namespace("plugin_id");

    Font font = ns.font("my_font");
    font.unihex(unihexZipFile, overrides)

    pack.register(false);
}
</code-block>

> - unihexZipFile is a File object of the zip containing the unihex
> - overrides is a List&lt;UnihexFont.Overrides&gt; object

`Font#unihex` does NOT return anything, it simply loads given font
</tab>
<tab title="Multi-Bitmap">
<code-block lang="Java">
public class MyResourcePack {
    public static List&lt;Font.TextureGlyph&gt; glyphs;

    public static void load(Plugin plugin) {
        ResourcePack pack = new ResourcePack(plugin, "plugin_id");
        Namespace ns = pack.namespace("plugin_id");
        
        Texture texture = ns.texture("glyph/smiley_face");
        
        Font font = ns.font("glyphs");
        glyph = font.glyphs(texture, spriteWidth, spriteHeight, height, ascent);
        
        pack.register(false);
    }
}
</code-block>

`Font#glyphs` returns all loaded glyphs in ORDER (row-order, left-to-right)
</tab>
</tabs>