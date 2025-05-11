package com.github.darksoulq.abyssallib.util;

import com.google.gson.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for loading written books from JSON files within a plugin's resources.
 *
 * <p>JSON structure example:
 * <pre>{@code
 * {
 *   "title": "Example Book",
 *   "author": "John Doe",
 *   "pages": [
 *     ["<bold>Hello</bold>", "This is a test."],
 *     ["Another page."]
 *   ]
 * }
 * }</pre>
 */
public class BookLoader {
    private static final Gson GSON = new GsonBuilder().create();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /**
     * Loads a written book from a resource file located at {@code books/<path>.json} inside the plugin's jar.
     *
     * @param plugin the plugin instance used to access resources
     * @param id     the resource location for the book (used to construct the file path)
     * @return a Bukkit {@link ItemStack} representing the written book
     * @throws IllegalStateException if the resource cannot be found
     * @throws RuntimeException      if parsing or loading fails
     */
    public static ItemStack load(Plugin plugin, ResourceLocation id) {
        String path = "books/" + id.path() + ".json";
        InputStream in = plugin.getResource(path);
        if (in == null) throw new IllegalStateException("Book resource not found: " + path);

        try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            String title = root.get("title").getAsString();
            String author = root.get("author").getAsString();
            JsonArray pages = root.getAsJsonArray("pages");

            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta meta = (BookMeta) book.getItemMeta();
            meta.setTitle(title);
            meta.setAuthor(author);

            for (JsonElement pageElement : pages) {
                if (!pageElement.isJsonArray()) continue;

                JsonArray lines = pageElement.getAsJsonArray();
                StringBuilder pageText = new StringBuilder();

                for (JsonElement lineElement : lines) {
                    pageText.append(lineElement.getAsString()).append("\n");
                }

                Component pageComponent = MINI_MESSAGE.deserialize(pageText.toString().trim());
                meta.addPages(pageComponent);
            }

            book.setItemMeta(meta);
            return book;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load book: " + id, e);
        }
    }
}
