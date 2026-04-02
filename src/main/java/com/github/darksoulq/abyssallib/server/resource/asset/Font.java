package com.github.darksoulq.abyssallib.server.resource.asset;

import com.google.gson.*;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.apache.fontbox.ttf.CmapSubtable;
import org.apache.fontbox.ttf.CmapTable;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Represents a Minecraft font resource supporting bitmap, space, TTF, and Unihex providers,
 * with full Unicode occupation tracking.
 */
public class Font implements Asset {

    /**
     * A thread-safe global registry mapping namespace-prefixed names to their respective visual components.
     */
    private static final Map<String, Component> NAMED_GLYPHS = new ConcurrentHashMap<>();

    /**
     * The namespace of this font, typically aligning with the plugin or resource pack namespace.
     */
    private final String namespace;

    /**
     * The unique identifier of this font, commonly corresponding to the file name excluding the extension.
     */
    private final String id;

    /**
     * An ordered list containing grouped glyphs, separated logically by their respective provider types.
     */
    private final List<LinkedList<Glyph>> glyphGroups = new LinkedList<>();

    /**
     * A tracking set storing all Unicode characters currently utilized to prevent inadvertent duplication.
     */
    private final Set<Character> occupied = new HashSet<>();

    /**
     * The starting Unicode code point utilized for allocating new characters within the Private Use Area (PUA).
     */
    private int unicodeBase = 0xE000;

    /**
     * The raw byte data representing pre-compiled JSON font files, bypassing dynamic generation when present.
     */
    private byte[] rawData = null;

    /**
     * Retrieves a mapped glyph component globally by its registered namespace and name.
     *
     * @param namespace The namespace the glyph was registered under.
     * @param name      The unique identifier name of the glyph.
     * @return The text component representation of the glyph, or null if absent.
     */
    public static @Nullable Component getGlyph(@NotNull String namespace, @NotNull String name) {
        return NAMED_GLYPHS.get(namespace + ":" + name);
    }

    /**
     * Constructs a new empty Font.
     *
     * @param namespace The font's namespace.
     * @param id        The font's identifier (usually filename without extension).
     */
    public Font(@NotNull String namespace, @NotNull String id) {
        this.namespace = namespace;
        this.id = id;
    }

    /**
     * Constructs a new Font from pre-compiled byte data.
     *
     * @param namespace The font's namespace.
     * @param id        The font's identifier (usually filename without extension).
     * @param data      The raw byte data representing the font JSON.
     */
    public Font(@NotNull String namespace, @NotNull String id, byte[] data) {
        this.namespace = namespace;
        this.id = id;
        this.rawData = data;
    }

    /**
     * Loads a font definition from plugin resources, supports only single-char bitmap providers.
     *
     * @param plugin    The plugin providing the resource.
     * @param namespace Namespace for this font.
     * @param id        Font identifier (without .json).
     * @throws IllegalStateException If the requested resource path cannot be located.
     * @throws RuntimeException      If an I/O error occurs during file reading or parsing.
     */
    public Font(@NotNull Plugin plugin, @NotNull String namespace, @NotNull String id) {
        this.namespace = namespace;
        this.id = id;
        String path = "resourcepack/" + namespace + "/font/" + id + ".json";
        try (InputStream in = plugin.getResource(path)) {
            if (in == null) throw new IllegalStateException("Font not found: " + path);
            JsonArray providers = JsonParser.parseReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8))
                .getAsJsonObject()
                .getAsJsonArray("providers");
            for (JsonElement el : providers) {
                JsonObject p = el.getAsJsonObject();
                if ("bitmap".equals(p.get("type").getAsString())) {
                    JsonArray chars = p.getAsJsonArray("chars");
                    if (chars.size() == 1 && chars.get(0).getAsString().length() == 1) {
                        String file = p.get("file").getAsString().replaceFirst("\\.png$", "");
                        int h = p.has("height") ? p.get("height").getAsInt() : 8;
                        int a = p.has("ascent") ? p.get("ascent").getAsInt() : h;
                        char c = chars.get(0).getAsString().charAt(0);
                        Texture tex = new Texture(plugin, namespace, file);
                        addTextureGlyph(tex, c, h, a);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates an offset glyph that affects spacing only.
     *
     * @param pixelOffset The horizontal offset in pixels.
     * @param unicode     The specific unicode character to map the offset to.
     * @return The created {@link OffsetGlyph}.
     */
    public @NotNull OffsetGlyph offset(int pixelOffset, char unicode) {
        OffsetGlyph g = new OffsetGlyph(Key.key(namespace, id), unicode, pixelOffset);
        LinkedList<Glyph> list = new LinkedList<>();
        list.add(g);
        glyphGroups.add(list);
        return g;
    }

    /**
     * Registers a TrueType font provider.
     *
     * @param ttfFile    The TTF file.
     * @param shiftX     X offset for rendering.
     * @param shiftY     Y offset for rendering.
     * @param size       Size of font in pixels.
     * @param oversample Oversample factor.
     * @throws IOException           If the file fails to load or parse.
     * @throws IllegalStateException If a detected unicode character is already occupied.
     */
    public void ttf(@NotNull File ttfFile, int shiftX, int shiftY, int size, int oversample) throws IOException {
        Set<Character> chars = readTtfUnicodes(ttfFile);
        for (char c : chars) ensureNotOccupied(c);
        occupied.addAll(chars);
        LinkedList<Glyph> gl = new LinkedList<>();
        gl.add(new TtfFont(Key.key(namespace, id), ttfFile.getPath(), shiftX, shiftY, size, oversample));
        glyphGroups.add(gl);
    }

    /**
     * Registers a Unihex ZIP provider.
     *
     * @param zipFile   ZIP file containing .hex glyphs.
     * @param overrides List of overrides for character sizing.
     * @throws IOException           If loading the ZIP fails.
     * @throws IllegalStateException If a detected unicode character is already occupied.
     */
    public void unihex(@NotNull File zipFile, @NotNull List<UnihexFont.Override> overrides) throws IOException {
        Set<Character> chars = readUnihexUnicodes(zipFile);
        for (char c : chars) ensureNotOccupied(c);
        occupied.addAll(chars);
        LinkedList<Glyph> gl = new LinkedList<>();
        gl.add(new UnihexFont(Key.key(namespace, id), zipFile.getPath(), overrides));
        glyphGroups.add(gl);
    }

    /**
     * Creates a named texture glyph (bitmap glyph) and registers it globally for tag resolution.
     *
     * @param name    The name of the glyph used for the tag resolver.
     * @param texture The texture used by the glyph.
     * @param height  The height of the image.
     * @param ascent  The ascent of the glyph.
     * @return The glyph that has been created.
     */
    public @NotNull TextureGlyph glyph(@NotNull String name, @NotNull Texture texture, int height, int ascent) {
        char c = nextUnicode();
        TextureGlyph g = new TextureGlyph(name, Key.key(namespace, id), texture, c, height, ascent);
        LinkedList<Glyph> gl = new LinkedList<>();
        gl.add(g);
        occupied.add(c);
        glyphGroups.add(gl);
        NAMED_GLYPHS.put(namespace + ":" + name, g.toComponent());
        return g;
    }

    /**
     * Creates a texture glyph (bitmap glyph) without registering a global tag resolver.
     *
     * @param texture The texture used by the glyph.
     * @param height  The height of the image.
     * @param ascent  The ascent of the glyph.
     * @return The glyph that has been created.
     */
    public @NotNull TextureGlyph glyph(@NotNull Texture texture, int height, int ascent) {
        char c = nextUnicode();
        TextureGlyph g = new TextureGlyph(null, Key.key(namespace, id), texture, c, height, ascent);
        LinkedList<Glyph> gl = new LinkedList<>();
        gl.add(g);
        occupied.add(c);
        glyphGroups.add(gl);
        return g;
    }

    /**
     * Converts a texture atlas into glyphs split into rows.
     *
     * @param texture Texture data.
     * @param spriteW Width of a single glyph.
     * @param spriteH Height of a single glyph.
     * @param height  Glyph visual height.
     * @param ascent  Glyph ascent.
     * @return Glyphs grouped by texture rows.
     */
    public @NotNull List<LinkedList<Glyph>> glyphs(
        @NotNull Texture texture,
        int spriteW,
        int spriteH,
        int height,
        int ascent
    ) {
        int[] size = getImageSize(texture.data());
        int textureWidth = size[0];
        int textureHeight = size[1];

        List<LinkedList<Glyph>> result = new LinkedList<>();
        int cols = textureWidth / spriteW;
        int rows = textureHeight / spriteH;

        for (int r = 0; r < rows; r++) {
            LinkedList<Glyph> line = new LinkedList<>();
            for (int c = 0; c < cols; c++) {
                char ch = nextUnicode();
                TextureGlyph tg = new TextureGlyph(null, Key.key(namespace, id), texture, ch, height, ascent);
                line.add(tg);
                occupied.add(ch);
            }
            result.add(line);
            glyphGroups.add(line);
        }

        return result;
    }

    /**
     * Emits this font to a JSON file format.
     *
     * @param files Output file map for resource pack building.
     */
    @Override
    public void emit(@NotNull Map<String, byte[]> files) {
        if (rawData != null) {
            files.put("assets/" + namespace + "/font/" + id + ".json", rawData);
            return;
        }
        JsonArray providers = new JsonArray();

        Map<BitmapKey, LinkedList<TextureGlyph>> bitmapGroups = new LinkedHashMap<>();
        LinkedList<OffsetGlyph> spaces = new LinkedList<>();

        for (LinkedList<Glyph> group : glyphGroups) {
            if (group.isEmpty()) continue;

            Glyph first = group.getFirst();
            if (first instanceof OffsetGlyph) {
                group.forEach(g -> spaces.add((OffsetGlyph) g));
            }
        }

        for (LinkedList<Glyph> group : glyphGroups) {
            if (group.isEmpty()) continue;

            Glyph first = group.getFirst();

            if (first instanceof TextureGlyph) {
                for (Glyph g : group) {
                    TextureGlyph t = (TextureGlyph) g;
                    BitmapKey key = new BitmapKey(t.texture(), t.height(), t.ascent());
                    bitmapGroups.computeIfAbsent(key, k -> new LinkedList<>()).add(t);
                }
            } else if (first instanceof TtfFont ttf) {
                providers.add(ttf.toJson());
            } else if (first instanceof UnihexFont unihex) {
                providers.add(unihex.toJson());
            }
        }

        if (!spaces.isEmpty()) providers.add(toSpaceProvider(spaces));
        bitmapGroups.forEach((key, glyphs) -> providers.add(toBitmapProvider(glyphs, key)));

        JsonObject root = new JsonObject();
        root.add("providers", providers);

        files.put("assets/" + namespace + "/font/" + id + ".json",
            new GsonBuilder().setPrettyPrinting().create()
                .toJson(root).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Builds a JSON bitmap provider from a list of {@link TextureGlyph}s and their associated key.
     *
     * @param list List of glyphs sharing the same texture, height, and ascent.
     * @param key  Composite key that holds the texture, height, and ascent.
     * @return JSON representation of a bitmap provider.
     */
    private JsonObject toBitmapProvider(List<TextureGlyph> list, BitmapKey key) {
        JsonObject provider = new JsonObject();
        provider.addProperty("type", "bitmap");
        provider.addProperty("file", key.texture().file() + ".png");
        provider.addProperty("height", key.height());
        provider.addProperty("ascent", key.ascent());

        JsonArray chars = new JsonArray();
        for (TextureGlyph glyph : list) {
            chars.add(String.valueOf(glyph.character()));
        }
        provider.add("chars", chars);

        return provider;
    }

    /**
     * Builds a JSON space provider from a list of {@link OffsetGlyph}s.
     *
     * @param list List of spacing glyphs with character and advance values.
     * @return JSON representation of a space provider.
     */
    private JsonObject toSpaceProvider(List<OffsetGlyph> list) {
        JsonObject p = new JsonObject();
        p.addProperty("type", "space");
        JsonObject adv = new JsonObject();
        list.forEach(o -> adv.addProperty(String.valueOf(o.character()), o.advance()));
        p.add("advances", adv);
        return p;
    }

    /**
     * Generates the next available Unicode character from the Private Use Area.
     *
     * @return A free Unicode character not already used by another glyph.
     * @throws IllegalStateException If the Private Use Area is exhausted.
     */
    private char nextUnicode() {
        while (unicodeBase <= 0xF8FF) {
            char c = (char) unicodeBase++;
            if (!occupied.contains(c)) return c;
        }
        throw new IllegalStateException("PUA exhausted");
    }

    /**
     * Adds a single {@link TextureGlyph} to the font using a specified character.
     *
     * @param texture The texture this glyph uses.
     * @param c       The Unicode character for this glyph.
     * @param h       The height of the glyph in pixels.
     * @param a       The ascent of the glyph in pixels.
     */
    private void addTextureGlyph(@NotNull Texture texture, char c, int h, int a) {
        TextureGlyph g = new TextureGlyph(String.valueOf(c), Key.key(namespace, id), texture, c, h, a);
        LinkedList<Glyph> list = new LinkedList<>();
        list.add(g);
        glyphGroups.add(list);
        occupied.add(c);
        NAMED_GLYPHS.put(namespace + ":" + String.valueOf(c), g.toComponent());
    }

    /**
     * Ensures the given Unicode character is not already in use.
     *
     * @param c The character to check.
     * @throws IllegalStateException If the character is already occupied.
     */
    private void ensureNotOccupied(char c) {
        if (occupied.contains(c)) throw new IllegalStateException(
            "Unicode U+" + Integer.toHexString(c).toUpperCase() + " already occupied");
    }

    /**
     * Determines the width and height of an image given its byte array.
     *
     * @param imageData Raw image data.
     * @return An array of two integers: [width, height].
     * @throws RuntimeException If the image is invalid or cannot be read.
     */
    private int[] getImageSize(byte[] imageData) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            if (image == null) throw new IllegalArgumentException("Invalid image data.");
            return new int[]{ image.getWidth(), image.getHeight() };
        } catch (Exception e) {
            throw new RuntimeException("Failed to read image dimensions", e);
        }
    }

    /**
     * Extracts all Unicode characters defined in a TTF file.
     *
     * @param file TTF file to analyze.
     * @return Set of all characters with associated glyphs in the font.
     * @throws IOException If the file cannot be read or parsed.
     */
    private Set<Character> readTtfUnicodes(File file) throws IOException {
        Set<Character> set = new HashSet<>();
        try (TrueTypeFont ttf = new TTFParser().parse(new RandomAccessReadBufferedFile(file))) {
            CmapTable cmapTable = ttf.getCmap();
            if (cmapTable != null) {
                for (CmapSubtable cmap : cmapTable.getCmaps()) {
                    for (int cp = 0; cp <= Character.MAX_VALUE; cp++) {
                        int gid = cmap.getGlyphId(cp);
                        if (gid > 0) {
                            set.add((char) cp);
                        }
                    }
                }
            }
        }
        return set;
    }

    /**
     * Scans a Unihex font ZIP archive for all character files.
     *
     * @param zip ZIP archive containing Unihex .hex files.
     * @return Set of Unicode characters declared in the file names.
     * @throws IOException If the archive fails to open or parse.
     */
    private Set<Character> readUnihexUnicodes(File zip) throws IOException {
        Set<Character> set = new HashSet<>();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zip))) {
            ZipEntry e;
            while ((e = zis.getNextEntry()) != null) {
                String name = new File(e.getName()).getName();
                if (name.matches("[0-9A-Fa-f]{4,6}\\.hex")) {
                    int cp = Integer.parseInt(name.replace(".hex", ""), 16);
                    set.add((char) cp);
                }
            }
        }
        return set;
    }

    /**
     * Represents a font glyph.
     */
    public sealed interface Glyph permits TextureGlyph, OffsetGlyph, TtfFont, UnihexFont {}

    /**
     * Represents a unique bitmap provider group.
     *
     * @param texture The texture associated with the group.
     * @param height  The pixel height applied to the group.
     * @param ascent  The pixel ascent applied to the group.
     */
    private record BitmapKey(Texture texture, int height, int ascent) {}

    /**
     * A glyph from a bitmap texture.
     *
     * @param name      The name of the glyph used for tag resolution, or null if unnamed.
     * @param fontId    The key identifier of the font.
     * @param texture   The texture used by the glyph.
     * @param character The unicode character.
     * @param height    The pixel height.
     * @param ascent    The pixel ascent.
     */
    public record TextureGlyph(@Nullable String name, Key fontId, @NotNull Texture texture, char character, int height, int ascent) implements Glyph {

        /**
         * Converts this glyph to a TextComponent.
         *
         * @return The representing TextComponent.
         */
        public TextComponent toComponent() {
            return Component.text(character).font(fontId);
        }

        /**
         * Converts this glyph to a MiniMessage string format.
         *
         * @return The formatted MiniMessage string.
         */
        public String toMiniMessageString() {
            return "<font:" + fontId.toString() + ">" + character + "</font>";
        }
    }

    /**
     * A spacing glyph that adjusts character advance.
     *
     * @param fontId    The key identifier of the font.
     * @param character The unicode character.
     * @param advance   The pixel advance distance.
     */
    public record OffsetGlyph(Key fontId, char character, int advance) implements Glyph {

        /**
         * Converts this offset glyph to a TextComponent.
         *
         * @return The representing TextComponent.
         */
        public TextComponent toComponent() {
            return Component.text(character).font(fontId);
        }

        /**
         * Converts this offset glyph to a MiniMessage string format.
         *
         * @return The formatted MiniMessage string.
         */
        public String toMiniMessageString() {
            return "<font:" + fontId.toString() + ">" + character + "</font>";
        }
    }

    /**
     * TTF font provider.
     *
     * @param fontId     The key identifier of the font.
     * @param file       The TTF file path.
     * @param shiftX     The X offset.
     * @param shiftY     The Y offset.
     * @param size       The font size.
     * @param oversample The oversample scaling.
     */
    public record TtfFont(Key fontId, String file, int shiftX, int shiftY, int size, int oversample) implements Glyph {

        /**
         * Serializes the provider into a JSON object representation.
         *
         * @return The generated JsonObject.
         */
        public JsonObject toJson() {
            JsonObject p = new JsonObject();
            p.addProperty("type", "ttf");
            p.addProperty("file", file);
            p.addProperty("size", size);
            p.addProperty("oversample", oversample);
            JsonArray shift = new JsonArray();
            shift.add(shiftX);
            shift.add(shiftY);
            p.add("shift", shift);
            return p;
        }
    }

    /**
     * Unihex font provider with size overrides.
     *
     * @param fontId        The key identifier of the font.
     * @param file          The Hex file path.
     * @param sizeOverrides List of spacing adjustments for subsets.
     */
    public record UnihexFont(Key fontId, String file, List<Override> sizeOverrides) implements Glyph {

        /**
         * Represents a dimensional spacing override within the Unihex font.
         *
         * @param from  Start unicode integer boundary.
         * @param to    End unicode integer boundary.
         * @param left  Left spacing pixel amount.
         * @param right Right spacing pixel amount.
         */
        public record Override(int from, int to, int left, int right) {}

        /**
         * Serializes the provider into a JSON object representation.
         *
         * @return The generated JsonObject.
         */
        public JsonObject toJson() {
            JsonObject p = new JsonObject();
            p.addProperty("type", "unihex");
            p.addProperty("hex_file", file);
            JsonArray arr = new JsonArray();
            for (Override o : sizeOverrides) {
                JsonObject j = new JsonObject();
                j.addProperty("from", o.from());
                j.addProperty("to", o.to());
                j.addProperty("left", o.left());
                j.addProperty("right", o.right());
                arr.add(j);
            }
            p.add("size_overrides", arr);
            return p;
        }
    }
}