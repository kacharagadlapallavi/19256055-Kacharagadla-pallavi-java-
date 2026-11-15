package TaskB1_FileStore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * Task B1 - Implement and Instrument the File Store
 * Complete the Code: PassStore.java
 * Student: Moksha Dude (192372374)
 */
public class PassStore {
    private final Path file;

    public PassStore(Path file) {
        this.file = file;
    }

    public void add(String passId, String regNo, String name, int route, double amount)
            throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(file,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write(String.join(",", passId, regNo, name,
                    String.valueOf(route), String.valueOf(amount)));
            w.newLine();
        }
    }

    /** Returns the five fields of the pass with the given ID, or null if not found. */
    public String[] find(String passId) throws IOException {
        // TODO 1: Read the file line by line with a BufferedReader inside try-with-
        //   resources; skip damaged lines (not exactly 5 fields); return the fields of
        //   the matching pass
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 5) {
                    continue; // Skip damaged line and proceed scanning
                }
                if (fields[0].trim().equals(passId)) {
                    return fields;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("=== Task B1: Instrumented Bus-Pass File Store ===");
        Path f = Files.createTempFile("passes", ".csv");
        PassStore store = new PassStore(f);

        store.add("P1001", "192311001", "Arun", 12, 4500);
        Files.writeString(f, "P1002,DAMAGED LINE\n", StandardOpenOption.APPEND);
        store.add("P1003", "192311003", "Divya", 7, 3800);

        System.out.println(Arrays.toString(store.find("P1003")));
        System.out.println(Arrays.toString(store.find("P1002")));

        System.out.println("\n--- Why Damaged Line Does Not Stop Search ---");
        System.out.println("The validation check `if (fields.length != 5) continue;` gracefully detects");
        System.out.println("malformed lines, bypassing them without raising an ArrayIndexOutOfBoundsException.");
        System.out.println("This achieves fault-tolerant stream parsing, allowing the search to successfully locate P1003.");

        Files.delete(f);
    }
}
