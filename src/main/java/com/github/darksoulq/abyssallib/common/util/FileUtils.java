package com.github.darksoulq.abyssallib.common.util;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility methods for common file operations such as saving,
 * deleting, zipping, and hashing files or directories.
 */
public class FileUtils {
    /**
     * Saves the contents of the specified {@link InputStream} to the given {@link File}.
     *
     * @param inputStream the input stream to read data from
     * @param file        the target file to write data to
     * @throws IOException if an I/O error occurs while reading or writing
     */
    public static void saveFile(InputStream inputStream, File file) throws IOException {
        OutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
    }

    /**
     * Recursively deletes a directory and all its contents.
     *
     * @param folder the path to the directory
     * @throws IOException if a file or directory could not be deleted
     */
    public static void deleteFolder(Path folder) throws IOException {
        if (!Files.exists(folder)) return;

        Files.walkFileTree(folder, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public @NotNull FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Creates a directory and any nonexistent parent directories.
     * Logs an error to {@code System.err} if creation fails.
     *
     * @param dir the directory to create
     */
    public static void createDirectories(File dir) {
        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println("Failed to create directory: " + dir.getPath());
        }
    }

    /**
     * Recursively adds a file or directory to a {@link ZipOutputStream}.
     *
     * @param file     the file or directory to add
     * @param basePath the base path to calculate relative paths inside the zip
     * @param zos      the {@link ZipOutputStream} to write to
     * @throws IOException if an I/O error occurs during zipping
     */
    public static void addFileToZip(File file, String basePath, ZipOutputStream zos) throws IOException {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File subFile : children) {
                    addFileToZip(subFile, basePath, zos);
                }
            }
        } else {
            try (FileInputStream fis = new FileInputStream(file)) {
                String relativePath = file.getAbsolutePath().substring(basePath.length() + 1);
                zos.putNextEntry(new ZipEntry(relativePath.replace("\\", "/")));

                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
            }
        }
    }

    /**
     * Retrieves a list of resource file paths from a plugin's JAR.
     *
     * @param plugin   the plugin to read from
     * @param basePath the base path inside the JAR to search (e.g. {@code "assets/"})
     * @return a list of relative file paths within the JAR
     */
    public static List<String> getFilePathList(Plugin plugin, String basePath) {
        List<String> files = new ArrayList<>();
        String resourcePath = basePath.isEmpty() ? "" : (basePath.endsWith("/") ? basePath : basePath + "/");

        try {
            URL jarUrl = plugin.getClass().getProtectionDomain().getCodeSource().getLocation();
            if (jarUrl == null) {
                return files;
            }

            File jarFile = new File(jarUrl.toURI());
            try (JarFile jar = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    String entryName = entries.nextElement().getName();
                    if (entryName.startsWith(resourcePath) && !entryName.endsWith("/")) {
                        files.add(entryName);
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to read plugin resources: " + e.getMessage());
        }

        return files;
    }

    /**
     * Zips an entire directory into a single zip file.
     *
     * @param folder  the folder to zip
     * @param zipFile the resulting zip file
     */
    public static void zipFolder(File folder, File zipFile) {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            String basePath = folder.getAbsolutePath();
            addFileToZip(folder, basePath, zos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Computes the SHA-1 hash of a file.
     *
     * @param path the file path
     * @return the SHA-1 hash as a lowercase hex string
     * @throws RuntimeException if the file cannot be read or the algorithm is unavailable
     */
    public static String sha1(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile())) {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");

            byte[] buffer = new byte[8192];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }

            byte[] hashBytes = digest.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to compute hash for " + path, e);
        }
    }
}
