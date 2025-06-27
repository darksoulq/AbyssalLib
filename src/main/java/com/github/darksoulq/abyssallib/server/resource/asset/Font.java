package com.github.darksoulq.abyssallib.server.resource.asset;

import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import com.google.gson.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.apache.fontbox.ttf.*;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Represents a Minecraft font resource supporting bitmap, space, TTF, and Unihex providers,
 * with full Unicode occupation tracking.
 */
@ApiStatus.Experimental
public class Font implements Asset {

    /** Namespace of this font (e.g., plugin or resource pack namespace). */
    private final String namespace;

    /** Identifier of this font (e.g., file name without extension). */
    private final String id;

    /** Ordered list of glyph groups, grouped by provider type. */
    private final List<LinkedList<Glyph>> glyphGroups = new LinkedList<>();

    /** Tracks used Unicode characters to prevent duplication. */
    private final Set<Character> occupied = new HashSet<>();

    /** Base Unicode code point for private-use area allocations. */
    private int unicodeBase = 0xE000;

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
     * Loads a font definition from plugin resources, supports only single-char bitmap providers.
     *
     * @param plugin    The plugin providing the resource.
     * @param namespace Namespace for this font.
     * @param id        Font identifier (without .json).
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
                        String file = p.get("file").getAsString();
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
     * @return The created {@link OffsetGlyph}.
     */
    public @NotNull OffsetGlyph offset(int pixelOffset) {
        char c = nextUnicode();
        OffsetGlyph g = new OffsetGlyph(Identifier.of(namespace, id), c, pixelOffset);
        LinkedList<Glyph> list = new LinkedList<>();
        list.add(g);
        glyphGroups.add(list);
        occupied.add(c);
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
     * @throws IOException If the file fails to load or parse.
     */
    public void ttf(@NotNull File ttfFile, int shiftX, int shiftY, int size, int oversample) throws IOException {
        Set<Character> chars = readTtfUnicodes(ttfFile);
        for (char c : chars) ensureNotOccupied(c);
        occupied.addAll(chars);
        LinkedList<Glyph> gl = new LinkedList<>();
        gl.add(new TtfFont(Identifier.of(namespace, id), ttfFile.getPath(), shiftX, shiftY, size, oversample));
        glyphGroups.add(gl);
    }

    /**
     * Registers a Unihex ZIP provider.
     *
     * @param zipFile   ZIP file containing .hex glyphs.
     * @param overrides List of overrides for character sizing.
     * @throws IOException If loading the ZIP fails.
     */
    public void unihex(@NotNull File zipFile, @NotNull List<UnihexFont.Override> overrides) throws IOException {
        Set<Character> chars = readUnihexUnicodes(zipFile);
        for (char c : chars) ensureNotOccupied(c);
        occupied.addAll(chars);
        LinkedList<Glyph> gl = new LinkedList<>();
        gl.add(new UnihexFont(Identifier.of(namespace, id), zipFile.getPath(), overrides));
        glyphGroups.add(gl);
    }

    /**
     * Converts a texture atlas into glyphs split into rows.
     *
     * @param texture  Texture data.
     * @param spriteW  Width of a single glyph.
     * @param spriteH  Height of a single glyph.
     * @param height   Glyph visual height.
     * @param ascent   Glyph ascent.
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

        List<LinkedList<Glyph>> result = new ArrayList<>();
        int cols = textureWidth / spriteW;
        int rows = textureHeight / spriteH;

        for (int r = 0; r < rows; r++) {
            LinkedList<Glyph> line = new LinkedList<>();
            for (int c = 0; c < cols; c++) {
                char ch = nextUnicode();
                TextureGlyph tg = new TextureGlyph(Identifier.of(namespace, id), texture, ch, height, ascent);
                line.add(tg);
                occupied.add(ch);
            }
            result.add(line);
            glyphGroups.add(line);
        }

        return result;
    }

    /**
     * Emits this font to a JSON file.
     *
     * @param files Output file map for resource pack building.
     */
    @Override
    public void emit(@NotNull Map<String, byte[]> files) {
        JsonArray providers = new JsonArray();
        Map<Texture, LinkedList<TextureGlyph>> bitmaps = new LinkedHashMap<>();
        LinkedList<OffsetGlyph> spaces = new LinkedList<>();

        for (LinkedList<Glyph> group : glyphGroups) {
            if (group.isEmpty()) continue;
            Glyph f = group.getFirst();
            if (f instanceof TextureGlyph) {
                group.forEach(g -> {
                    TextureGlyph t = (TextureGlyph) g;
                    bitmaps.computeIfAbsent(t.texture(), k -> new LinkedList<>()).add(t);
                });
            } else if (f instanceof OffsetGlyph) {
                group.forEach(g -> spaces.add((OffsetGlyph) g));
            } else if (f instanceof TtfFont t) {
                providers.add(t.toJson());
            } else if (f instanceof UnihexFont u) {
                providers.add(u.toJson());
            }
        }

        bitmaps.forEach((tex, list) -> providers.add(toBitmapProvider(list)));
        if (!spaces.isEmpty()) providers.add(toSpaceProvider(spaces));

        JsonObject root = new JsonObject();
        root.add("providers", providers);
        files.put("assets/" + namespace + "/font/" + id + ".json",
                new GsonBuilder().setPrettyPrinting().create()
                        .toJson(root).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Builds a JSON bitmap provider from a list of {@link TextureGlyph}s.
     *
     * @param list List of glyphs sharing the same texture, height, and ascent.
     * @return JSON representation of a bitmap provider.
     */
    private JsonObject toBitmapProvider(List<TextureGlyph> list) {
        TextureGlyph s = list.get(0);
        JsonObject p = new JsonObject();
        p.addProperty("type", "bitmap");
        p.addProperty("file", s.texture().file());
        p.addProperty("height", s.height());
        p.addProperty("ascent", s.ascent());
        JsonArray arr = new JsonArray();
        list.forEach(t -> arr.add(String.valueOf(t.character())));
        p.add("chars", arr);
        return p;
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
        TextureGlyph g = new TextureGlyph(Identifier.of(namespace, id), texture, c, h, a);
        LinkedList<Glyph> list = new LinkedList<>();
        list.add(g);
        glyphGroups.add(list);
        occupied.add(c);
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
     * A glyph from a bitmap texture.
     */
    public record TextureGlyph(Identifier fontId, @NotNull Texture texture, char character, int height, int ascent) implements Glyph {
        public TextComponent toComponent() {
            return Component.text(character).font(fontId.toNamespace());
        }
    }

    /**
     * A spacing glyph that adjusts character advance.
     */
    public record OffsetGlyph(Identifier fontId, char character, int advance) implements Glyph {
        public TextComponent toComponent() {
            return Component.text(character).font(fontId.toNamespace());
        }
    }

    /**
     * TTF font provider.
     */
    public record TtfFont(Identifier fontId, String file, int shiftX, int shiftY, int size, int oversample) implements Glyph {
        /**
         * @return the json representation.
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
     */
    public record UnihexFont(Identifier fontId, String file, List<Override> sizeOverrides) implements Glyph {
        public record Override(int from, int to, int left, int right) {}

        /**
         * @return the json representation.
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
