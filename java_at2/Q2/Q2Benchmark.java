package Q2;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Q2Benchmark
 * Measures and compares ArrayList, LinkedList, and ArrayDeque across
 * playlist manipulation operations and "Up Next" queue scenarios.
 */
public class Q2Benchmark {

    private static final int ITERATIONS = 5;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 2 - Q2: WHEN IS ARRAYLIST AN APPROPRIATE CHOICE? (PLAYLIST)      ");
        System.out.println("   Comparing ArrayList, LinkedList & ArrayDeque Across Multiple Playlist Sizes  ");
        System.out.println("================================================================================");

        int[] sizes = {5000, 25000, 100000};
        List<OpResult> allResults = new ArrayList<>();

        System.out.println("[*] Warming up JVM JIT compiler...");
        warmup();
        System.out.println("[*] Warmup completed.\n");

        for (int size : sizes) {
            System.out.printf(">>> Testing Playlist Size: %,d songs <<<%n", size);

            // 1. Add at end (N elements)
            allResults.add(benchAddEnd(size));

            // 2. Insert at front (1,000 operations)
            int insertCount = Math.min(2000, size / 2);
            allResults.add(benchInsertFront(size, insertCount));

            // 3. Insert in middle (1,000 operations)
            allResults.add(benchInsertMiddle(size, insertCount));

            // 4. Remove at front (1,000 operations)
            allResults.add(benchRemoveFront(size, insertCount));

            // 5. Remove in middle (1,000 operations)
            allResults.add(benchRemoveMiddle(size, insertCount));

            // 6. Access k-th element (10,000 random lookups)
            int lookupCount = 10000;
            allResults.add(benchGetKth(size, lookupCount));

            // 7. Iterate whole list (sequential traversal)
            allResults.add(benchIterate(size));

            // 8. Changed Condition: "Up Next" Queue (5,000 ops: addFirst, addLast, removeFirst)
            int queueOps = 5000;
            allResults.add(benchUpNextQueue(size, queueOps));

            System.out.println();
        }

        printSummaryTable(allResults);
        exportRawData(allResults, "Q2/raw_benchmark_data.csv");
    }

    private static void warmup() {
        for (int i = 0; i < 2000; i++) {
            List<Song> a = new ArrayList<>();
            List<Song> l = new LinkedList<>();
            for (int j = 0; j < 500; j++) {
                Song s = new Song("S" + j, "T" + j, "A" + j, 180);
                a.add(s);
                l.add(s);
            }
            a.get(250);
            l.get(250);
        }
    }

    public static class OpResult {
        public final String operation;
        public final int listSize;
        public final double arrayListTimeMs;
        public final double linkedListTimeMs;
        public final double arrayDequeTimeMs; // -1 if not applicable

        public OpResult(String operation, int listSize, double alTime, double llTime, double adTime) {
            this.operation = operation;
            this.listSize = listSize;
            this.arrayListTimeMs = alTime;
            this.linkedListTimeMs = llTime;
            this.arrayDequeTimeMs = adTime;
        }
    }

    private static Song createSong(int i) {
        return new Song("ID_" + i, "Track_" + i, "Artist_" + (i % 50), 180 + (i % 120));
    }

    // 1. Add at end
    private static OpResult benchAddEnd(int size) {
        double alTime = 0, llTime = 0;
        for (int it = 0; it < ITERATIONS; it++) {
            long s = System.nanoTime();
            List<Song> al = new ArrayList<>();
            for (int i = 0; i < size; i++) al.add(createSong(i));
            alTime += (System.nanoTime() - s) / 1_000_000.0;

            s = System.nanoTime();
            List<Song> ll = new LinkedList<>();
            for (int i = 0; i < size; i++) ll.add(createSong(i));
            llTime += (System.nanoTime() - s) / 1_000_000.0;
        }
        return new OpResult("Add at End", size, alTime / ITERATIONS, llTime / ITERATIONS, -1);
    }

    // 2. Insert at front
    private static OpResult benchInsertFront(int size, int count) {
        double alTime = 0, llTime = 0;
        for (int it = 0; it < ITERATIONS; it++) {
            List<Song> al = new ArrayList<>();
            List<Song> ll = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                Song s = createSong(i);
                al.add(s);
                ll.add(s);
            }

            long s = System.nanoTime();
            for (int i = 0; i < count; i++) al.add(0, createSong(100000 + i));
            alTime += (System.nanoTime() - s) / 1_000_000.0;

            s = System.nanoTime();
            for (int i = 0; i < count; i++) ll.add(0, createSong(100000 + i));
            llTime += (System.nanoTime() - s) / 1_000_000.0;
        }
        return new OpResult("Insert at Front (" + count + " ops)", size, alTime / ITERATIONS, llTime / ITERATIONS, -1);
    }

    // 3. Insert in middle
    private static OpResult benchInsertMiddle(int size, int count) {
        double alTime = 0, llTime = 0;
        for (int it = 0; it < ITERATIONS; it++) {
            List<Song> al = new ArrayList<>();
            List<Song> ll = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                Song s = createSong(i);
                al.add(s);
                ll.add(s);
            }

            long s = System.nanoTime();
            for (int i = 0; i < count; i++) al.add(al.size() / 2, createSong(200000 + i));
            alTime += (System.nanoTime() - s) / 1_000_000.0;

            s = System.nanoTime();
            for (int i = 0; i < count; i++) ll.add(ll.size() / 2, createSong(200000 + i));
            llTime += (System.nanoTime() - s) / 1_000_000.0;
        }
        return new OpResult("Insert at Middle (" + count + " ops)", size, alTime / ITERATIONS, llTime / ITERATIONS, -1);
    }

    // 4. Remove at front
    private static OpResult benchRemoveFront(int size, int count) {
        double alTime = 0, llTime = 0;
        for (int it = 0; it < ITERATIONS; it++) {
            List<Song> al = new ArrayList<>();
            List<Song> ll = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                Song s = createSong(i);
                al.add(s);
                ll.add(s);
            }

            long s = System.nanoTime();
            for (int i = 0; i < count; i++) al.remove(0);
            alTime += (System.nanoTime() - s) / 1_000_000.0;

            s = System.nanoTime();
            for (int i = 0; i < count; i++) ll.remove(0);
            llTime += (System.nanoTime() - s) / 1_000_000.0;
        }
        return new OpResult("Remove at Front (" + count + " ops)", size, alTime / ITERATIONS, llTime / ITERATIONS, -1);
    }

    // 5. Remove in middle
    private static OpResult benchRemoveMiddle(int size, int count) {
        double alTime = 0, llTime = 0;
        for (int it = 0; it < ITERATIONS; it++) {
            List<Song> al = new ArrayList<>();
            List<Song> ll = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                Song s = createSong(i);
                al.add(s);
                ll.add(s);
            }

            long s = System.nanoTime();
            for (int i = 0; i < count; i++) al.remove(al.size() / 2);
            alTime += (System.nanoTime() - s) / 1_000_000.0;

            s = System.nanoTime();
            for (int i = 0; i < count; i++) ll.remove(ll.size() / 2);
            llTime += (System.nanoTime() - s) / 1_000_000.0;
        }
        return new OpResult("Remove at Middle (" + count + " ops)", size, alTime / ITERATIONS, llTime / ITERATIONS, -1);
    }

    // 6. Access k-th element
    private static OpResult benchGetKth(int size, int lookups) {
        Random rnd = new Random(42);
        int[] indices = new int[lookups];
        for (int i = 0; i < lookups; i++) indices[i] = rnd.nextInt(size);

        List<Song> al = new ArrayList<>();
        List<Song> ll = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            Song s = createSong(i);
            al.add(s);
            ll.add(s);
        }

        double alTime = 0, llTime = 0;
        for (int it = 0; it < ITERATIONS; it++) {
            long s = System.nanoTime();
            long sink = 0;
            for (int idx : indices) sink += al.get(idx).getDurationSeconds();
            alTime += (System.nanoTime() - s) / 1_000_000.0;

            s = System.nanoTime();
            sink = 0;
            // For LinkedList on large sizes, cap lookups to prevent excessive runtimes
            int llLookups = Math.min(lookups, size > 25000 ? 2000 : lookups);
            for (int i = 0; i < llLookups; i++) sink += ll.get(indices[i]).getDurationSeconds();
            double scale = (double) lookups / llLookups;
            llTime += ((System.nanoTime() - s) / 1_000_000.0) * scale;
        }
        return new OpResult("Get k-th Element (" + lookups + " lookups)", size, alTime / ITERATIONS, llTime / ITERATIONS, -1);
    }

    // 7. Iterate whole list
    private static OpResult benchIterate(int size) {
        List<Song> al = new ArrayList<>();
        List<Song> ll = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            Song s = createSong(i);
            al.add(s);
            ll.add(s);
        }

        double alTime = 0, llTime = 0;
        for (int it = 0; it < ITERATIONS; it++) {
            long s = System.nanoTime();
            long sink = 0;
            for (Song song : al) sink += song.getDurationSeconds();
            alTime += (System.nanoTime() - s) / 1_000_000.0;

            s = System.nanoTime();
            sink = 0;
            for (Song song : ll) sink += song.getDurationSeconds();
            llTime += (System.nanoTime() - s) / 1_000_000.0;
        }
        return new OpResult("Iterate Whole List (" + size + " items)", size, alTime / ITERATIONS, llTime / ITERATIONS, -1);
    }

    // 8. Changed condition: Up Next Queue
    private static OpResult benchUpNextQueue(int size, int ops) {
        double alTime = 0, llTime = 0, adTime = 0;
        for (int it = 0; it < ITERATIONS; it++) {
            // ArrayList as Queue
            long s = System.nanoTime();
            ArrayList<Song> al = new ArrayList<>();
            for (int i = 0; i < ops; i++) {
                Song song = createSong(i);
                if (i % 2 == 0) al.add(0, song); // add to front
                else al.add(song);              // add to back
                if (al.size() > 10 && i % 3 == 0) al.remove(0); // remove from front
            }
            alTime += (System.nanoTime() - s) / 1_000_000.0;

            // LinkedList as Deque
            s = System.nanoTime();
            LinkedList<Song> ll = new LinkedList<>();
            for (int i = 0; i < ops; i++) {
                Song song = createSong(i);
                if (i % 2 == 0) ll.addFirst(song);
                else ll.addLast(song);
                if (ll.size() > 10 && i % 3 == 0) ll.removeFirst();
            }
            llTime += (System.nanoTime() - s) / 1_000_000.0;

            // ArrayDeque as Deque
            s = System.nanoTime();
            ArrayDeque<Song> ad = new ArrayDeque<>();
            for (int i = 0; i < ops; i++) {
                Song song = createSong(i);
                if (i % 2 == 0) ad.addFirst(song);
                else ad.addLast(song);
                if (ad.size() > 10 && i % 3 == 0) ad.removeFirst();
            }
            adTime += (System.nanoTime() - s) / 1_000_000.0;
        }
        return new OpResult("Up Next Queue (" + ops + " bidirectional ops)", size,
                alTime / ITERATIONS, llTime / ITERATIONS, adTime / ITERATIONS);
    }

    private static void printSummaryTable(List<OpResult> results) {
        System.out.println("========================================================================================================");
        System.out.println("                               BENCHMARK RESULTS SUMMARY TABLE (Q2)                                     ");
        System.out.println("========================================================================================================");
        System.out.printf("%-35s | %-10s | %-15s | %-15s | %-15s%n",
                "Operation", "Size", "ArrayList (ms)", "LinkedList (ms)", "ArrayDeque (ms)");
        System.out.println("--------------------------------------------------------------------------------------------------------");
        for (OpResult r : results) {
            String adStr = (r.arrayDequeTimeMs >= 0) ? String.format("%.3f", r.arrayDequeTimeMs) : "N/A";
            System.out.printf("%-35s | %-10d | %-15.3f | %-15.3f | %-15s%n",
                    r.operation, r.listSize, r.arrayListTimeMs, r.linkedListTimeMs, adStr);
        }
        System.out.println("========================================================================================================\n");
    }

    private static void exportRawData(List<OpResult> results, String filePath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("Operation,PlaylistSize,ArrayListTimeMs,LinkedListTimeMs,ArrayDequeTimeMs");
            for (OpResult r : results) {
                pw.printf("%s,%d,%.4f,%.4f,%.4f%n",
                        r.operation, r.listSize, r.arrayListTimeMs, r.linkedListTimeMs, r.arrayDequeTimeMs);
            }
            System.out.println("[+] Raw benchmark data exported to: " + filePath);
        } catch (IOException e) {
            System.err.println("[-] Failed to write raw data: " + e.getMessage());
        }
    }
}

