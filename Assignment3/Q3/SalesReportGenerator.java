package Q3;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Q3 - Resource Safety, Debugging and Robust Code
 * Problem Context: Daily Sales Report Generator ("Too many open files")
 * Student: Moksha Dude (192372374)
 */
public class SalesReportGenerator {

    // Simulated resource that tracks open handles
    static class SimulatedFileResource implements AutoCloseable {
        private static int activeHandles = 0;
        private final String filename;
        private boolean closed = false;

        SimulatedFileResource(String filename) {
            this.filename = filename;
            activeHandles++;
        }

        public void readData(boolean failOnRead) throws IOException {
            if (failOnRead) {
                throw new IOException("Corrupt packet in " + filename);
            }
        }

        @Override
        public void close() throws IOException {
            if (!closed) {
                closed = true;
                activeHandles--;
            }
        }

        public static int getActiveHandles() {
            return activeHandles;
        }
    }

    // Approach 1: Vulnerable manual close in try-finally with multiple resources
    public static void processFilesManual(List<String> files) {
        System.out.println("Processing with Manual try-finally...");
        for (String file : files) {
            SimulatedFileResource res = null;
            try {
                res = new SimulatedFileResource(file);
                // Trigger simulated failure on store_03
                res.readData(file.contains("03"));
                res.close(); // If exception above occurs, this close() is skipped!
            } catch (IOException e) {
                System.out.println("  [Manual] Caught error on " + file + ": " + e.getMessage());
                // In naive manual cleanup, close() in catch or poorly structured finally can leak
            }
        }
        System.out.println("  Active Leaked Handles remaining: " + SimulatedFileResource.getActiveHandles());
    }

    // Approach 2: Robust try-with-resources
    public static void processFilesTryWithResources(List<String> files) {
        System.out.println("Processing with try-with-resources...");
        for (String file : files) {
            try (SimulatedFileResource res = new SimulatedFileResource(file)) {
                res.readData(file.contains("03"));
            } catch (IOException e) {
                System.out.println("  [TWR] Caught error on " + file + ": " + e.getMessage());
            }
        }
        System.out.println("  Active Handles remaining after TWR: " + SimulatedFileResource.getActiveHandles());
    }

    public static void demonstrateSuppressedExceptions() {
        System.out.println("\n--- Demonstrating Suppressed Exceptions ---");
        class FaultyResource implements AutoCloseable {
            @Override
            public void close() throws Exception {
                throw new Exception("Secondary exception during close()");
            }
        }

        try (FaultyResource r = new FaultyResource()) {
            throw new Exception("Primary exception in try-block");
        } catch (Exception e) {
            System.out.println("Caught Primary Exception: " + e.getMessage());
            for (Throwable supp : e.getSuppressed()) {
                System.out.println("  -> Suppressed Exception: " + supp.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Q3: Resource Safety & try-with-resources Investigation ===\n");
        List<String> storeFiles = List.of("store_01.dat", "store_02.dat", "store_03.dat", "store_04.dat");

        processFilesManual(storeFiles);
        processFilesTryWithResources(storeFiles);
        demonstrateSuppressedExceptions();

        System.out.println("\n--- Engineering Conclusion ---");
        System.out.println("try-with-resources guarantees deterministic cleanup regardless of normal or abrupt termination.");
        System.out.println("It eliminates manual close nesting errors and preserves secondary exceptions via suppression.");
    }
}
