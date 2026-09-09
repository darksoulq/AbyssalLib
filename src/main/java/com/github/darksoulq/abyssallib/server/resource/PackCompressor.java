package com.github.darksoulq.abyssallib.server.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class PackCompressor {
    private static final Gson GSON = new GsonBuilder().create();

    public static byte[] minifyJson(byte[] data) {
        try {
            String json = new String(data, StandardCharsets.UTF_8);
            return GSON.toJson(JsonParser.parseString(json)).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            return data;
        }
    }

    public static void processExternalPack(Path originalPath) {
        try {
            boolean alreadyCompressed = false;
            try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(originalPath))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (entry.getName().equals("compression.manifest")) {
                        alreadyCompressed = true;
                        break;
                    }
                }
            }
            if (alreadyCompressed) return;

            Path backupDir = Paths.get("backup_packs");
            if (!Files.exists(backupDir)) Files.createDirectories(backupDir);
            Path backupPath = backupDir.resolve(originalPath.getFileName().toString());
            Files.copy(originalPath, backupPath, StandardCopyOption.REPLACE_EXISTING);

            Path tempFile = Files.createTempFile("abyssal_pack", ".zip");
            try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(originalPath));
                 ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(tempFile))) {
                zos.setLevel(Deflater.BEST_COMPRESSION);

                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (entry.getName().equals("compression.manifest")) continue;

                    ZipEntry newEntry = new ZipEntry(entry.getName());
                    zos.putNextEntry(newEntry);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = zis.read(buffer)) > 0) baos.write(buffer, 0, len);

                    byte[] data = baos.toByteArray();
                    if (!entry.isDirectory() && (entry.getName().endsWith(".json") || entry.getName().endsWith(".mcmeta"))) {
                        data = minifyJson(data);
                    }
                    zos.write(data);
                    zos.closeEntry();
                }
                zos.putNextEntry(new ZipEntry("compression.manifest"));
                zos.write("true".getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }
            Files.move(tempFile, originalPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}