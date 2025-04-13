package me.darksoul.abyssalLib.gui;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.Base64;
import java.util.Map;

public class GuiSerializer {

    public static String serialize(AbyssalGui gui) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(output)) {

            Map<Integer, Slot> slots = gui.slotMap();
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

    public static void deserialize(String data, AbyssalGui gui) {
        try (ByteArrayInputStream input = new ByteArrayInputStream(Base64.getDecoder().decode(data));
             ObjectInputStream ois = new ObjectInputStream(input)) {

            int count = ois.readInt();
            for (int i = 0; i < count; i++) {
                int index = ois.readInt();
                ItemStack item = (ItemStack) ois.readObject();

                Slot slot = gui.slots.get(index);
                if (slot != null) {
                    slot.item(item);
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to deserialize GUI", e);
        }
    }
}
