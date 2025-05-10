package io.github.darksoulq.abyssalLib.util;

import com.google.gson.JsonObject;
import io.github.darksoulq.abyssalLib.gui.AbstractGui;
import io.github.darksoulq.abyssalLib.gui.slot.Slot;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for serializing and deserializing various objects like {@link ItemStack} and {@link AbstractGui}.
 * It supports serialization to and from base64 encoding to store and retrieve data in a compressed, readable form.
 */
public class Serialization {

    /**
     * Serializes an {@link ItemStack} into a {@link JsonObject} with a base64-encoded string representing the item.
     *
     * @param stack the {@link ItemStack} to serialize
     * @return a {@link JsonObject} containing the serialized item stack
     */
    public static JsonObject serializeItemStack(ItemStack stack) {
        JsonObject json = new JsonObject();
        byte[] bytes = stack.serializeAsBytes();
        String encoded = Base64.getEncoder().encodeToString(bytes);
        json.addProperty("base64", encoded);
        return json;
    }

    /**
     * Deserializes an {@link ItemStack} from the given {@link JsonObject}.
     *
     * @param json the {@link JsonObject} containing the base64-encoded item data
     * @return the deserialized {@link ItemStack}
     */
    public static ItemStack deserializeItemStack(JsonObject json) {
        String base64 = json.get("base64").getAsString();
        byte[] bytes = Base64.getDecoder().decode(base64);
        return ItemStack.deserializeBytes(bytes);
    }

    /**
     * Serializes an {@link AbstractGui} object to a base64-encoded string, saving the GUI's item data.
     * This method serializes the items in the top row of the GUI into a byte array and encodes it.
     *
     * @param gui the {@link AbstractGui} to serialize
     * @return a base64-encoded string representing the serialized GUI
     */
    public static String serializeGui(AbstractGui gui) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(output)) {

            Map<Integer, Slot> slots = new HashMap<>();
            for (Slot slot : gui.slots.TOP) {
                slots.put(slot.index(), slot);
            }
            oos.writeInt(slots.size());

            for (Map.Entry<Integer, Slot> entry : slots.entrySet()) {
                if (!entry.getValue().doSerialize()) continue;
                int index = entry.getKey();
                ItemStack item = entry.getValue().item();

                oos.writeInt(index);
                oos.writeObject(item);
            }

            return Base64.getEncoder().encodeToString(output.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize GUI", e);
        }
    }

    /**
     * Deserializes a GUI from a base64-encoded string and updates the items in the provided {@link AbstractGui}.
     *
     * @param data the base64-encoded string containing the serialized GUI data
     * @param gui  the {@link AbstractGui} to update with the deserialized data
     */
    public static void deserializeGui(String data, AbstractGui gui) {
        try (ByteArrayInputStream input = new ByteArrayInputStream(Base64.getDecoder().decode(data));
             ObjectInputStream ois = new ObjectInputStream(input)) {

            int count = ois.readInt();
            for (int i = 0; i < count; i++) {
                int index = ois.readInt();
                ItemStack item = (ItemStack) ois.readObject();

                Slot slot = gui.slots.TOP.get(index);
                if (slot != null) {
                    slot.item(item);
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to deserialize GUI", e);
        }
    }
}
