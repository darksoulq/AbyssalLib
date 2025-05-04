package me.darksoul.abyssalLib.util;

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

public class BookLoader {
    private static final Gson GSON = new GsonBuilder().create();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

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
