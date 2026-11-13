package Q7;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Q7Benchmark
 * Rigorous side-by-side benchmark comparing OriginalTriageQueue (PriorityQueue)
 * versus MLFQFairTriageQueue (Redesigned Multi-Level Feedback Queue).
 */
public class Q7Benchmark {

    private static final int ITERATIONS = 5;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 2 - Q7: DAY-4 DESIGN CHALLENGE - DIAGNOSE, REDESIGN & VALIDATE    ");
        System.out.println("   Emergency Triage: PriorityQueue (Min-Heap) vs MLFQ Multi-Bucket Bitmask      ");
        System.out.println("================================================================================\n");

        int[] opCounts = {5000, 25000, 100000};
        List<ComparisonRecord> records = new ArrayList<>();

        System.out.println("[*] Warming up JIT compiler...");
        warmup();
        System.out.println("[*] Warmup complete.\n");

        for (int ops : opCounts) {
            System.out.printf(">>> Executing Workload: %,d Emergency Department Operations <<<%n", ops);
            ComparisonRecord rec = runComparison(ops);
            records.add(rec);
            System.out.println();
        }

        printSummary(records);
        exportRawData(records, "Q7/raw_benchmark_data.csv");
        verifyBehavioralEquivalence();
    }

    private static void warmup() {
        for (int i = 0; i < 3; i++) {
            runComparison(2000);
        }
    }

    public static class ComparisonRecord {
        public final int operations;
        public final double originalTimeMs;
        public final double redesignedTimeMs;
        public final double speedup;
        public final double origThroughputOpsSec;
        public final double redThroughputOpsSec;
        public final long origFullScans;
        public final long redFullScans;
        public final long origMemoryDeltaBytes;
        public final long redMemoryDeltaBytes;

        public ComparisonRecord(int ops, double origMs, double redMs, double speedup,
                                double origTput, double redTput,
                                long origScans, long redScans,
                                long origMem, long redMem) {
            this.operations = ops;
            this.originalTimeMs = origMs;
            this.redesignedTimeMs = redMs;
            this.speedup = speedup;
            this.origThroughputOpsSec = origTput;
            this.redThroughputOpsSec = redTput;
            this.origFullScans = origScans;
            this.redFullScans = redScans;
            this.origMemoryDeltaBytes = origMem;
            this.redMemoryDeltaBytes = redMem;
        }
    }

    private static ComparisonRecord runComparison(int totalOps) {
        Random rnd = new Random(42);
        // Pre-generate operations: 0 = Admit, 1 = Poll, 2 = Age
        int[] opTypes = new int[totalOps];
        int[] severities = new int[totalOps];
        for (int i = 0; i < totalOps; i++) {
            double roll = rnd.nextDouble();
            if (roll < 0.60) {
                opTypes[i] = 0; // Admit
                severities[i] = rnd.nextInt(5) + 1;
            } else if (roll < 0.95) {
                opTypes[i] = 1; // Poll
            } else {
                opTypes[i] = 2; // Age
            }
        }

        double origTotalTime = 0, redTotalTime = 0;
        long origTotalMem = 0, redTotalMem = 0;
        long origScans = 0, redScans = 0;

        // 1. Benchmark Original Solution (PriorityQueue + Heap Rebuild Aging)
        for (int it = 0; it < ITERATIONS; it++) {
            System.gc();
            try { Thread.sleep(20); } catch (InterruptedException ignored) {}

            long memBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            OriginalTriageQueue orig = new OriginalTriageQueue();
            long s = System.nanoTime();

            long clock = 0;
            long scans = 0;
            for (int i = 0; i < totalOps; i++) {
                clock++;
                if (opTypes[i] == 0) {
                    orig.admit(new Patient("P" + i, "Patient_" + i, severities[i], clock));
                } else if (opTypes[i] == 1) {
                    orig.pollNext();
                } else { // Age
                    scans += orig.size(); // Original scans entire heap
                    orig.applyAging(clock, 30);
                }
            }

            origTotalTime += (System.nanoTime() - s) / 1_000_000.0;
            long memAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            origTotalMem += Math.max(0, memAfter - memBefore);
            origScans = scans;
        }

        // 2. Benchmark Redesigned Solution (MLFQ Multi-Bucket Bitmask Queue)
        for (int it = 0; it < ITERATIONS; it++) {
            System.gc();
            try { Thread.sleep(20); } catch (InterruptedException ignored) {}

            long memBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            MLFQFairTriageQueue red = new MLFQFairTriageQueue();
            long s = System.nanoTime();

            long clock = 0;
            for (int i = 0; i < totalOps; i++) {
                clock++;
                if (opTypes[i] == 0) {
                    red.admit(new Patient("P" + i, "Patient_" + i, severities[i], clock));
                } else if (opTypes[i] == 1) {
                    red.pollNext();
                } else { // Age
                    red.applyAging(clock, 30); // Inspects only bucket heads (O(1) per bucket)
                }
            }

            redTotalTime += (System.nanoTime() - s) / 1_000_000.0;
            long memAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            redTotalMem += Math.max(0, memAfter - memBefore);
            redScans = 0; // Zero global scans!
        }

        double avgOrigMs = origTotalTime / ITERATIONS;
        double avgRedMs = redTotalTime / ITERATIONS;
        double speedup = avgOrigMs / avgRedMs;
        double origTput = (totalOps / (avgOrigMs / 1000.0));
        double redTput = (totalOps / (avgRedMs / 1000.0));
        long avgOrigMem = origTotalMem / ITERATIONS;
        long avgRedMem = redTotalMem / ITERATIONS;

        return new ComparisonRecord(totalOps, avgOrigMs, avgRedMs, speedup,
                origTput, redTput, origScans, redScans, avgOrigMem, avgRedMem);
    }

    private static void verifyBehavioralEquivalence() {
        System.out.println("================================================================================");
        System.out.println("   BEHAVIORAL EQUIVALENCE & ORDER VERIFICATION                                  ");
        System.out.println("================================================================================");
        OriginalTriageQueue orig = new OriginalTriageQueue();
        MLFQFairTriageQueue red = new MLFQFairTriageQueue();

        Patient[] patients = {
                new Patient("1", "Ravi", 3, 10),
                new Patient("2", "Meena", 1, 12),
                new Patient("3", "John", 3, 15),
                new Patient("4", "Asha", 5, 20),
                new Patient("5", "Kumar", 1, 25),
                new Patient("6", "Lata", 2, 30)
        };

        for (Patient p : patients) {
            orig.admit(new Patient(p.id, p.name, p.getSeverity(), p.arrivalTime));
            red.admit(new Patient(p.id, p.name, p.getSeverity(), p.arrivalTime));
        }

        List<String> origOrder = new ArrayList<>();
        List<String> redOrder = new ArrayList<>();
        while (!orig.isEmpty()) origOrder.add(orig.pollNext().name);
        while (!red.isEmpty()) redOrder.add(red.pollNext().name);

        System.out.println("Original PriorityQueue Treatment Order: " + origOrder);
        System.out.println("Redesigned MLFQ Triage Treatment Order:  " + redOrder);
        boolean identical = origOrder.equals(redOrder);
        System.out.println("Are Extracted Treatment Sequences Identical? " + identical);
        System.out.println("================================================================================\n");
    }

    private static void printSummary(List<ComparisonRecord> records) {
        System.out.println("=====================================================================================================================================");
        System.out.println("                                      DAY-4 DESIGN CHALLENGE VALIDATION SUMMARY (Q7)                                                 ");
        System.out.println("=====================================================================================================================================");
        System.out.printf("%-10s | %-16s | %-16s | %-10s | %-18s | %-18s | %-16s%n",
                "Operations", "Original PQ (ms)", "Redesigned (ms)", "Speedup", "Orig Tput(ops/s)", "Red Tput(ops/s)", "Global Scans Eliminated");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------");
        for (ComparisonRecord r : records) {
            System.out.printf("%-10d | %-16.3f | %-16.3f | %-9.2fx | %-18.0f | %-18.0f | %,d scans%n",
                    r.operations, r.originalTimeMs, r.redesignedTimeMs, r.speedup,
                    r.origThroughputOpsSec, r.redThroughputOpsSec, r.origFullScans);
        }
        System.out.println("=====================================================================================================================================\n");
    }

    private static void exportRawData(List<ComparisonRecord> records, String filePath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("Operations,OriginalTimeMs,RedesignedTimeMs,SpeedupFactor,OriginalThroughputOpsSec,RedesignedThroughputOpsSec,ScansEliminated,OriginalMemBytes,RedesignedMemBytes");
            for (ComparisonRecord r : records) {
                pw.printf("%d,%.4f,%.4f,%.2f,%.2f,%.2f,%d,%d,%d%n",
                        r.operations, r.originalTimeMs, r.redesignedTimeMs, r.speedup,
                        r.origThroughputOpsSec, r.redThroughputOpsSec, r.origFullScans,
                        r.origMemoryDeltaBytes, r.redMemoryDeltaBytes);
            }
            System.out.println("[+] Raw benchmark data exported to: " + filePath);
        } catch (IOException e) {
            System.err.println("[-] Failed to write raw data: " + e.getMessage());
        }
    }
}

