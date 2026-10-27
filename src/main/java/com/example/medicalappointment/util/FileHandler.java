package com.example.medicalappointment.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * FileHandler Utility Class
 * Demonstrates:
 * - File Handling using Java I/O (BufferedReader, BufferedWriter, FileReader, FileWriter, Files, Path)
 * - Safe directory and file initialization
 * - Graceful handling of missing files, empty files, and I/O errors
 * - Reusable CRUD file operations (Read, Write, Append, Overwrite)
 */
public class FileHandler {

    private static final Object FILE_LOCK = new Object();

    /**
     * File Handling - Ensures data directory and file exist. Creates them if missing.
     *
     * @param relativeOrAbsolutePath File path (e.g., "data/patients.txt")
     * @return File object pointing to the file
     */
    public static File ensureFileExists(String relativeOrAbsolutePath) {
        synchronized (FILE_LOCK) {
            try {
                Path path = Paths.get(relativeOrAbsolutePath);
                if (path.getParent() != null && !Files.exists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }
                File file = path.toFile();
                if (!file.exists()) {
                    boolean created = file.createNewFile();
                    if (created) {
                        System.out.println("[FileHandler] Created missing file: " + file.getAbsolutePath());
                    }
                }
                return file;
            } catch (IOException e) {
                System.err.println("[FileHandler] Error creating file " + relativeOrAbsolutePath + ": " + e.getMessage());
                return new File(relativeOrAbsolutePath);
            }
        }
    }

    /**
     * File Handling - Read all lines from a file using BufferedReader and FileReader.
     *
     * @param filePath File path
     * @return List of non-empty, non-comment lines
     */
    public static List<String> readAllLines(String filePath) {
        synchronized (FILE_LOCK) {
            File file = ensureFileExists(filePath);
            List<String> lines = new ArrayList<>();

            if (!file.exists() || file.length() == 0) {
                return lines; // Gracefully return empty list for empty/missing files
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    // Skip blank lines and header comments starting with '#'
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        lines.add(line);
                    }
                }
            } catch (IOException e) {
                System.err.println("[FileHandler] Error reading from " + filePath + ": " + e.getMessage());
            }

            return lines;
        }
    }

    /**
     * File Handling - Overwrite file with new list of lines using BufferedWriter and FileWriter.
     *
     * @param filePath File path
     * @param lines    List of string lines to write
     * @return true if successful, false otherwise
     */
    public static boolean writeAllLines(String filePath, List<String> lines) {
        synchronized (FILE_LOCK) {
            File file = ensureFileExists(filePath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, false))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
                writer.flush();
                return true;
            } catch (IOException e) {
                System.err.println("[FileHandler] Error writing to " + filePath + ": " + e.getMessage());
                return false;
            }
        }
    }

    /**
     * File Handling - Append a single record line using BufferedWriter with append=true.
     *
     * @param filePath File path
     * @param line     Line to append
     * @return true if appended successfully, false otherwise
     */
    public static boolean appendLine(String filePath, String line) {
        synchronized (FILE_LOCK) {
            File file = ensureFileExists(filePath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, true))) {
                writer.write(line);
                writer.newLine();
                writer.flush();
                return true;
            } catch (IOException e) {
                System.err.println("[FileHandler] Error appending to " + filePath + ": " + e.getMessage());
                return false;
            }
        }
    }

    /**
     * Helper to safely split pipe-separated line into fields
     */
    public static String[] splitLine(String line) {
        if (line == null) {
            return new String[0];
        }
        return line.split("\\|", -1);
    }
}
