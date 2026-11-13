package Q4;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Q4Benchmark
 * Evaluates HashMap, TreeMap, LinkedHashMap, and Sorted Array with Binary Search
 * across 3 inventory sizes (1,000, 10,000, 100,000) for lookup latency,
 * report generation overhead, and ordering verification.
 */
public class Q4Benchmark {

    private static final int ITERATIONS = 5;
    private static final int LOOKUP_COUNT = 100000;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 2 - Q4: CHOOSING A MAP FOR PHARMACY INVENTORY                     ");
        System.out.println("   Comparing HashMap, TreeMap, LinkedHashMap & Sorted Array Binary Search       ");
        System.out.println("================================================================================\n");

        int[] sizes = {1000, 10000, 100000};
        List<BenchmarkEntry> results = new ArrayList<>();

        System.out.println("[*] Warming up JVM JIT compiler...");
        warmup();
        System.out.println("[*] Warmup complete.\n");

        for (int size : sizes) {
            System.out.printf(">>> Testing Pharmacy Inventory Size: %,d Medicines <<<%n", size);

            // Generate deterministic dataset with randomized code names to simulate real medicines
            List<Medicine> dataset = generateMedicines(size);
            String[] queryCodes = generateQueryKeys(dataset, LOOKUP_COUNT);

            // Test HashMap
            results.add(testMap(PharmacyInventory.MapType.HASH_MAP, dataset, queryCodes, size));
            // Test TreeMap
            results.add(testMap(PharmacyInventory.MapType.TREE_MAP, dataset, queryCodes, size));
            // Test LinkedHashMap
            results.add(testMap(PharmacyInventory.MapType.LINKED_HASH_MAP, dataset, queryCodes, size));
            // Test Alternative: Sorted Array with Binary Search
            results.add(testSortedArray(dataset, queryCodes, size));

            System.out.println();
        }

        printSummaryTable(results);
        exportRawData(results, "Q4/raw_benchmark_data.csv");

        // Verify and display ordering behavior explicitly
        demonstrateRawVsAdaptedOrdering(sizes[0]);
    }

    private static void warmup() {
        List<Medicine> dummy = generateMedicines(500);
        String[] q = generateQueryKeys(dummy, 10000);
        for (int i = 0; i < 3; i++) {
            testMap(PharmacyInventory.MapType.HASH_MAP, dummy, q, 500);
            testMap(PharmacyInventory.MapType.TREE_MAP, dummy, q, 500);
            testMap(PharmacyInventory.MapType.LINKED_HASH_MAP, dummy, q, 500);
        }
    }

    public static class BenchmarkEntry {
        public final String structure;
        public final int inventorySize;
        public final double populateTimeMs;
        public final double lookup100kTimeMs;
        public final double avgLookupLatencyNs;
        public final double report1CodeOrderTimeMs;
        public final boolean report1Matches;
        public final double report2InsertOrderTimeMs;
        public final boolean report2Matches;

        public BenchmarkEntry(String structure, int inventorySize, double populateTimeMs,
                              double lookup100kTimeMs, double avgLookupLatencyNs,
                              double report1CodeOrderTimeMs, boolean report1Matches,
                              double report2InsertOrderTimeMs, boolean report2Matches) {
            this.structure = structure;
            this.inventorySize = inventorySize;
            this.populateTimeMs = populateTimeMs;
            this.lookup100kTimeMs = lookup100kTimeMs;
            this.avgLookupLatencyNs = avgLookupLatencyNs;
            this.report1CodeOrderTimeMs = report1CodeOrderTimeMs;
            this.report1Matches = report1Matches;
            this.report2InsertOrderTimeMs = report2InsertOrderTimeMs;
            this.report2Matches = report2Matches;
        }
    }

    private static List<Medicine> generateMedicines(int n) {
        List<Medicine> list = new ArrayList<>(n);
        Random rnd = new Random(1337);
        // Ensure strictly unique medicine codes (as pharmacy codes are unique primary keys)
        Set<String> usedCodes = new HashSet<>(n);
        for (int i = 0; i < n; i++) {
            String code;
            do {
                code = String.format("MED-%06d", rnd.nextInt(900000) + 100000);
            } while (!usedCodes.add(code));

            String name = "PharmaDrug_" + i;
            String batch = "B" + (202600 + (i % 200));
            int stock = rnd.nextInt(500) + 10;
            double price = 5.0 + rnd.nextDouble() * 250.0;
            list.add(new Medicine(code, name, batch, stock, price, i));
        }
        return list;
    }

    private static String[] generateQueryKeys(List<Medicine> dataset, int queryCount) {
        Random rnd = new Random(42);
        String[] queries = new String[queryCount];
        int n = dataset.size();
        for (int i = 0; i < queryCount; i++) {
            // 90% hits, 10% misses
            if (rnd.nextDouble() < 0.90) {
                queries[i] = dataset.get(rnd.nextInt(n)).getCode();
            } else {
                queries[i] = "MED-MISS-" + rnd.nextInt(10000);
            }
        }
        return queries;
    }

    private static BenchmarkEntry testMap(PharmacyInventory.MapType type, List<Medicine> dataset,
                                          String[] queries, int size) {
        double popTotal = 0, lookupTotal = 0, rep1Total = 0, rep2Total = 0;
        boolean rep1Ok = false, rep2Ok = false;

        for (int it = 0; it < ITERATIONS; it++) {
            long s = System.nanoTime();
            PharmacyInventory inv = new PharmacyInventory(type);
            for (Medicine m : dataset) inv.putMedicine(m);
            popTotal += (System.nanoTime() - s) / 1_000_000.0;

            s = System.nanoTime();
            long sink = 0;
            for (String q : queries) {
                Medicine m = inv.getMedicine(q);
                if (m != null) sink += m.getStock();
            }
            lookupTotal += (System.nanoTime() - s) / 1_000_000.0;

            s = System.nanoTime();
            List<Medicine> rep1 = inv.generateCodeOrderReport();
            rep1Total += (System.nanoTime() - s) / 1_000_000.0;
            rep1Ok = verifyCodeOrder(rep1);

            s = System.nanoTime();
            List<Medicine> rep2 = inv.generateInsertionOrderReport();
            rep2Total += (System.nanoTime() - s) / 1_000_000.0;
            rep2Ok = verifyInsertionOrder(rep2);
        }

        double avgPop = popTotal / ITERATIONS;
        double avgLookup = lookupTotal / ITERATIONS;
        double avgLatencyNs = (avgLookup * 1_000_000.0) / queries.length;
        double avgRep1 = rep1Total / ITERATIONS;
        double avgRep2 = rep2Total / ITERATIONS;

        return new BenchmarkEntry(type.name(), size, avgPop, avgLookup, avgLatencyNs,
                avgRep1, rep1Ok, avgRep2, rep2Ok);
    }

    private static BenchmarkEntry testSortedArray(List<Medicine> dataset, String[] queries, int size) {
        double popTotal = 0, lookupTotal = 0, rep1Total = 0, rep2Total = 0;
        boolean rep1Ok = false, rep2Ok = false;

        for (int it = 0; it < ITERATIONS; it++) {
            long s = System.nanoTime();
            Medicine[] arr = dataset.toArray(new Medicine[0]);
            Arrays.sort(arr, Comparator.comparing(Medicine::getCode));
            popTotal += (System.nanoTime() - s) / 1_000_000.0;

            s = System.nanoTime();
            long sink = 0;
            Medicine dummy = new Medicine("", "", "", 0, 0, 0);
            for (String q : queries) {
                // Binary search requires dummy key object or custom comparator
                int idx = Arrays.binarySearch(arr, new Medicine(q, "", "", 0, 0, 0),
                        Comparator.comparing(Medicine::getCode));
                if (idx >= 0) sink += arr[idx].getStock();
            }
            lookupTotal += (System.nanoTime() - s) / 1_000_000.0;

            s = System.nanoTime();
            // Code report is instantaneous contiguous copy
            List<Medicine> rep1 = new ArrayList<>(Arrays.asList(arr));
            rep1Total += (System.nanoTime() - s) / 1_000_000.0;
            rep1Ok = verifyCodeOrder(rep1);

            s = System.nanoTime();
            // Insertion order report requires copying and sorting by insertionSeq
            List<Medicine> rep2 = new ArrayList<>(Arrays.asList(arr));
            rep2.sort(Comparator.comparingLong(Medicine::getInsertionSeq));
            rep2Total += (System.nanoTime() - s) / 1_000_000.0;
            rep2Ok = verifyInsertionOrder(rep2);
        }

        double avgPop = popTotal / ITERATIONS;
        double avgLookup = lookupTotal / ITERATIONS;
        double avgLatencyNs = (avgLookup * 1_000_000.0) / queries.length;
        double avgRep1 = rep1Total / ITERATIONS;
        double avgRep2 = rep2Total / ITERATIONS;

        return new BenchmarkEntry("SORTED_ARRAY", size, avgPop, avgLookup, avgLatencyNs,
                avgRep1, rep1Ok, avgRep2, rep2Ok);
    }

    private static boolean verifyCodeOrder(List<Medicine> list) {
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i - 1).getCode().compareTo(list.get(i).getCode()) > 0) return false;
        }
        return true;
    }

    private static boolean verifyInsertionOrder(List<Medicine> list) {
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i - 1).getInsertionSeq() > list.get(i).getInsertionSeq()) return false;
        }
        return true;
    }

    private static void demonstrateRawVsAdaptedOrdering(int sampleSize) {
        System.out.println("================================================================================");
        System.out.println("   NATURAL (RAW) ITERATION ORDER VS ADAPTED ORDER DEMONSTRATION                ");
        System.out.println("================================================================================");

        List<Medicine> sample = generateMedicines(sampleSize);
        PharmacyInventory hm = new PharmacyInventory(PharmacyInventory.MapType.HASH_MAP);
        PharmacyInventory tm = new PharmacyInventory(PharmacyInventory.MapType.TREE_MAP);
        PharmacyInventory lhm = new PharmacyInventory(PharmacyInventory.MapType.LINKED_HASH_MAP);

        for (Medicine m : sample) {
            hm.putMedicine(m);
            tm.putMedicine(m);
            lhm.putMedicine(m);
        }

        System.out.println("1. Raw HashMap.values() Order Check:");
        System.out.println("   - Is Code-Ordered? " + verifyCodeOrder(hm.getRawIterationList()) + " (FAILED without adaptation)");
        System.out.println("   - Is Insertion-Ordered? " + verifyInsertionOrder(hm.getRawIterationList()) + " (FAILED without fallback)");

        System.out.println("2. Raw TreeMap.values() Order Check:");
        System.out.println("   - Is Code-Ordered? " + verifyCodeOrder(tm.getRawIterationList()) + " (NATURAL MATCH)");
        System.out.println("   - Is Insertion-Ordered? " + verifyInsertionOrder(tm.getRawIterationList()) + " (FAILED without fallback)");

        System.out.println("3. Raw LinkedHashMap.values() Order Check:");
        System.out.println("   - Is Code-Ordered? " + verifyCodeOrder(lhm.getRawIterationList()) + " (FAILED without sort)");
        System.out.println("   - Is Insertion-Ordered? " + verifyInsertionOrder(lhm.getRawIterationList()) + " (NATURAL MATCH)");
        System.out.println("================================================================================\n");
    }

    private static void printSummaryTable(List<BenchmarkEntry> results) {
        System.out.println("==============================================================================================================================");
        System.out.println("                                         BENCHMARK RESULTS SUMMARY TABLE (Q4)                                                 ");
        System.out.println("==============================================================================================================================");
        System.out.printf("%-15s | %-8s | %-12s | %-16s | %-15s | %-16s | %-16s%n",
                "Data Structure", "Size", "Put Time(ms)", "100k Lookups(ms)", "Latency/Get(ns)", "Report 1-Code(ms)", "Report 2-Ins(ms)");
        System.out.println("------------------------------------------------------------------------------------------------------------------------------");
        for (BenchmarkEntry r : results) {
            System.out.printf("%-15s | %-8d | %-12.3f | %-16.3f | %-15.1f | %-12.3f (%s) | %-12.3f (%s)%n",
                    r.structure, r.inventorySize, r.populateTimeMs, r.lookup100kTimeMs, r.avgLookupLatencyNs,
                    r.report1CodeOrderTimeMs, r.report1Matches ? "OK" : "NO",
                    r.report2InsertOrderTimeMs, r.report2Matches ? "OK" : "NO");
        }
        System.out.println("==============================================================================================================================\n");
    }

    private static void exportRawData(List<BenchmarkEntry> results, String filePath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("Structure,Size,PopulateTimeMs,Lookup100kTimeMs,AvgLookupLatencyNs,Report1CodeTimeMs,Report1Matches,Report2InsertTimeMs,Report2Matches");
            for (BenchmarkEntry r : results) {
                pw.printf("%s,%d,%.4f,%.4f,%.2f,%.4f,%b,%.4f,%b%n",
                        r.structure, r.inventorySize, r.populateTimeMs, r.lookup100kTimeMs, r.avgLookupLatencyNs,
                        r.report1CodeOrderTimeMs, r.report1Matches, r.report2InsertOrderTimeMs, r.report2Matches);
            }
            System.out.println("[+] Raw benchmark data exported to: " + filePath);
        } catch (IOException e) {
            System.err.println("[-] Failed to write raw data: " + e.getMessage());
        }
    }
}
