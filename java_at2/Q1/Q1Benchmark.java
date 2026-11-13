package Q1;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Q1Benchmark
 * Benchmarks String (+), StringBuilder, StringBuffer, and alternatives
 * across three deliberately designed workloads with repeated averaging,
 * memory tracking, and intermediate object estimation.
 */
public class Q1Benchmark {

    private static final int ITERATIONS = 5;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 2 - Q1: BUILDING TEXT UNDER LOAD (RAILWAY E-TICKET GENERATION)   ");
        System.out.println("   Comparing String (+), StringBuilder, StringBuffer & Alternative Concepts     ");
        System.out.println("================================================================================");

        // Pre-warmup JIT compiler
        System.out.println("[*] Warming up JVM JIT compiler...");
        warmup();
        System.out.println("[*] Warmup complete.\n");

        // Define Workloads
        // Workload 1: Single short ticket (10 fields) - evaluated 10,000 times to obtain reliable micro-timings
        // Workload 2: Enterprise batch (10,000 tickets, 50 fields each)
        // Workload 3: Heavy stress burst (20,000 tickets, 100 fields each)

        BenchmarkResult w1Plus = runBenchmark("String (+)", 1, 10, 10000, 1);
        BenchmarkResult w1Sb   = runBenchmark("StringBuilder", 1, 10, 10000, 2);
        BenchmarkResult w1Sbuf = runBenchmark("StringBuffer", 1, 10, 10000, 3);

        BenchmarkResult w2Plus = runBenchmark("String (+)", 10000, 50, 1, 1);
        BenchmarkResult w2Sb   = runBenchmark("StringBuilder", 10000, 50, 1, 2);
        BenchmarkResult w2Sbuf = runBenchmark("StringBuffer", 10000, 50, 1, 3);
        BenchmarkResult w2Join = runBenchmark("StringJoiner", 10000, 50, 1, 4);
        BenchmarkResult w2Fmt  = runBenchmark("String.format", 10000, 50, 1, 5);

        BenchmarkResult w3Plus = runBenchmark("String (+)", 20000, 100, 1, 1);
        BenchmarkResult w3Sb   = runBenchmark("StringBuilder", 20000, 100, 1, 2);
        BenchmarkResult w3Sbuf = runBenchmark("StringBuffer", 20000, 100, 1, 3);

        printSummaryTable(Arrays.asList(
            w1Plus, w1Sb, w1Sbuf,
            w2Plus, w2Sb, w2Sbuf, w2Join, w2Fmt,
            w3Plus, w3Sb, w3Sbuf
        ));

        exportRawData(Arrays.asList(
            w1Plus, w1Sb, w1Sbuf,
            w2Plus, w2Sb, w2Sbuf, w2Join, w2Fmt,
            w3Plus, w3Sb, w3Sbuf
        ), "Q1/raw_benchmark_data.csv");
    }

    private static void warmup() {
        List<RailwayTicketGenerator.TicketField> sample = RailwayTicketGenerator.generateTicketFields(30, 0);
        for (int i = 0; i < 5000; i++) {
            RailwayTicketGenerator.generateWithPlusOperator(sample);
            RailwayTicketGenerator.generateWithStringBuilder(sample);
            RailwayTicketGenerator.generateWithStringBuffer(sample);
            RailwayTicketGenerator.generateWithStringJoiner(sample);
            RailwayTicketGenerator.generateWithStringFormat(sample);
        }
    }

    public static class BenchmarkResult {
        public final String approach;
        public final int tickets;
        public final int fields;
        public final int repetitions;
        public final double avgTimeMs;
        public final long estimatedIntermediateStrings;
        public final long memoryUsedBytes;

        public BenchmarkResult(String approach, int tickets, int fields, int repetitions,
                               double avgTimeMs, long estimatedIntermediateStrings, long memoryUsedBytes) {
            this.approach = approach;
            this.tickets = tickets;
            this.fields = fields;
            this.repetitions = repetitions;
            this.avgTimeMs = avgTimeMs;
            this.estimatedIntermediateStrings = estimatedIntermediateStrings;
            this.memoryUsedBytes = memoryUsedBytes;
        }
    }

    /**
     * approachId: 1 = String (+), 2 = StringBuilder, 3 = StringBuffer, 4 = StringJoiner, 5 = String.format
     */
    private static BenchmarkResult runBenchmark(String name, int ticketCount, int fieldCount, int batchReps, int approachId) {
        System.out.printf("Running: %s | Workload: %d tickets x %d fields (reps=%d)...%n",
                name, ticketCount, fieldCount, batchReps);

        List<List<RailwayTicketGenerator.TicketField>> dataset = new ArrayList<>(ticketCount);
        for (int i = 0; i < ticketCount; i++) {
            dataset.add(RailwayTicketGenerator.generateTicketFields(fieldCount, i));
        }

        // Theoretical intermediate String objects calculation:
        // In String (+): in a loop of n fields, each step creates:
        //   - intermediate string for "[" + key + ":" + value + "] " -> ~3 temporary string fragments
        //   - intermediate accumulator string (result + next) -> (n - 1) objects
        // Total intermediate objects per ticket for String (+) = (fieldCount - 1) + 2 * fieldCount = 3 * fieldCount - 1.
        // For StringBuilder / StringBuffer: 0 intermediate accumulator strings; only 1 final String per ticket upon toString().
        long intermediateObjects;
        if (approachId == 1) { // String (+)
            intermediateObjects = (long) ticketCount * batchReps * (fieldCount * 3L - 1);
        } else if (approachId == 4) { // StringJoiner
            intermediateObjects = (long) ticketCount * batchReps * (fieldCount); // for concatenation inside add
        } else if (approachId == 5) { // String.format
            intermediateObjects = (long) ticketCount * batchReps * (fieldCount + 1);
        } else { // StringBuilder, StringBuffer
            intermediateObjects = (long) ticketCount * batchReps; // exactly 1 final String per ticket
        }

        double totalTime = 0;
        long totalMemDelta = 0;

        for (int r = 0; r < ITERATIONS; r++) {
            System.gc();
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}

            long memBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long start = System.nanoTime();

            for (int b = 0; b < batchReps; b++) {
                for (int i = 0; i < ticketCount; i++) {
                    List<RailwayTicketGenerator.TicketField> f = dataset.get(i);
                    switch (approachId) {
                        case 1: RailwayTicketGenerator.generateWithPlusOperator(f); break;
                        case 2: RailwayTicketGenerator.generateWithStringBuilder(f); break;
                        case 3: RailwayTicketGenerator.generateWithStringBuffer(f); break;
                        case 4: RailwayTicketGenerator.generateWithStringJoiner(f); break;
                        case 5: RailwayTicketGenerator.generateWithStringFormat(f); break;
                    }
                }
            }

            long elapsed = System.nanoTime() - start;
            long memAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            totalTime += (elapsed / 1_000_000.0);
            totalMemDelta += Math.max(0, memAfter - memBefore);
        }

        double avgTimeMs = totalTime / ITERATIONS;
        long avgMemBytes = totalMemDelta / ITERATIONS;

        return new BenchmarkResult(name, ticketCount, fieldCount, batchReps, avgTimeMs, intermediateObjects, avgMemBytes);
    }

    private static void printSummaryTable(List<BenchmarkResult> results) {
        System.out.println("\n==================================================================================================================");
        System.out.println("                                      BENCHMARK RESULTS SUMMARY TABLE (Q1)                                        ");
        System.out.println("==================================================================================================================");
        System.out.printf("%-18s | %-12s | %-8s | %-8s | %-15s | %-20s | %-14s%n",
                "Approach", "Workload", "Tickets", "Fields", "Avg Time (ms)", "Est. Interm. Strings", "Heap Delta (KB)");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        for (BenchmarkResult r : results) {
            String workloadName;
            if (r.tickets == 1 && r.fields == 10) workloadName = "W1: Micro (10k reps)";
            else if (r.tickets == 10000 && r.fields == 50) workloadName = "W2: Enterprise";
            else workloadName = "W3: Heavy Stress";

            System.out.printf("%-18s | %-12s | %-8d | %-8d | %-15.3f | %-20s | %-14.1f%n",
                    r.approach, workloadName, r.tickets, r.fields, r.avgTimeMs,
                    String.format("%,d", r.estimatedIntermediateStrings),
                    (r.memoryUsedBytes / 1024.0));
        }
        System.out.println("==================================================================================================================\n");
    }

    private static void exportRawData(List<BenchmarkResult> results, String filePath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("Approach,WorkloadTickets,FieldsPerTicket,Repetitions,AvgTimeMs,EstimatedIntermediateStrings,HeapDeltaBytes");
            for (BenchmarkResult r : results) {
                pw.printf("%s,%d,%d,%d,%.4f,%d,%d%n",
                        r.approach, r.tickets, r.fields, r.repetitions, r.avgTimeMs, r.estimatedIntermediateStrings, r.memoryUsedBytes);
            }
            System.out.println("[+] Raw benchmark data exported to: " + filePath);
        } catch (IOException e) {
            System.err.println("[-] Failed to write raw data: " + e.getMessage());
        }
    }
}

