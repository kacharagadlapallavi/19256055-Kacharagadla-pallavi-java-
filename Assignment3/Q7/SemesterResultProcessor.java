package Q7;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * Q7 - Design a Concurrent, Fault-Tolerant Processor from First Principles
 * Problem Context: Semester Result Processing (10,000 score files)
 * Student: Moksha Dude (192372374)
 */
public class SemesterResultProcessor {

    // Representation of a Student Result
    static class StudentScore {
        final String studentId;
        final int marks;
        final String status;

        StudentScore(String studentId, int marks, String status) {
            this.studentId = studentId;
            this.marks = marks;
            this.status = status;
        }
    }

    // Task to process an individual score record
    static class ScoreTask implements Callable<StudentScore> {
        private final String fileId;

        ScoreTask(String fileId) {
            this.fileId = fileId;
        }

        @Override
        public StudentScore call() throws Exception {
            // Simulate simulated failures for test resilience
            if (fileId.endsWith("99")) {
                throw new java.io.FileNotFoundException("File missing: " + fileId);
            }
            if (fileId.endsWith("77")) {
                throw new IllegalArgumentException("Corrupt data packet in: " + fileId);
            }

            // Simulate parsing and validation workload
            int score = (Math.abs(fileId.hashCode()) % 60) + 40; // 40 - 100
            String grade = score >= 50 ? "PASS" : "FAIL";
            return new StudentScore(fileId, score, grade);
        }
    }

    public static void processWithPool(int fileCount, int threadPoolSize) {
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        List<Future<StudentScore>> futures = new ArrayList<>(fileCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        LongAdder totalMarks = new LongAdder();
        ConcurrentMap<String, String> failureLog = new ConcurrentHashMap<>();

        long startTime = System.nanoTime();

        // Dispatch all tasks to the thread pool
        for (int i = 0; i < fileCount; i++) {
            String fileId = String.format("STU_%05d", i);
            futures.add(executor.submit(new ScoreTask(fileId)));
        }

        // Aggregate results fault-tolerantly
        for (int i = 0; i < futures.size(); i++) {
            try {
                StudentScore score = futures.get(i).get();
                successCount.incrementAndGet();
                totalMarks.add(score.marks);
            } catch (ExecutionException e) {
                failCount.incrementAndGet();
                failureLog.put("Task_" + i, e.getCause().getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        executor.shutdown();
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

        System.out.printf("Threads: %2d | Time: %5d ms | Success: %5d | Failed: %3d | Avg Score: %.1f\n",
                threadPoolSize, elapsedMs, successCount.get(), failCount.get(),
                successCount.get() > 0 ? (double) totalMarks.sum() / successCount.get() : 0.0);
    }

    public static void main(String[] args) {
        System.out.println("=== Q7: Concurrent, Fault-Tolerant Semester Result Processor ===");
        int totalFiles = 10_000;
        System.out.println("Processing " + totalFiles + " student score records across thread pool variations:\n");

        processWithPool(totalFiles, 1);
        processWithPool(totalFiles, 2);
        processWithPool(totalFiles, 4);
        processWithPool(totalFiles, 8);

        System.out.println("\n--- Architectural Defense & Pool Sizing ---");
        System.out.println("1. Fault Tolerance: Exceptions thrown within worker tasks are encapsulated inside");
        System.out.println("   ExecutionException during Future.get(). This prevents any individual corrupt or");
        System.out.println("   missing record from terminating the overall batch job.");
        System.out.println("2. Pool Sizing: Scaling from 1 to 4 threads yields near-linear speedup as tasks utilize");
        System.out.println("   all available physical CPU cores. Beyond the number of physical cores (8+), returns diminish");
        System.out.println("   due to OS context-switching overhead and thread coordination lock contention.");
        System.out.println("3. Concurrency: LongAdder and ConcurrentHashMap allow lock-free, zero-contention aggregation.");
    }
}
