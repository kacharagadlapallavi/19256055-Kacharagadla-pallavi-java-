package Q4;

/**
 * Q4 - Thread Creation and the Thread Life Cycle
 * Problem Context: Parallel Answer-Script Scanning (4 Scanner Stations)
 * Student: Moksha Dude (192372374)
 */
public class AnswerScriptScanner {

    // Approach 1: Extending Thread
    static class ScannerThread extends Thread {
        private final int stationId;
        private final int scriptCount;
        private int scanned = 0;

        ScannerThread(int stationId, int scriptCount) {
            super("ScannerStation-" + stationId);
            this.stationId = stationId;
            this.scriptCount = scriptCount;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < scriptCount; i++) {
                    Thread.sleep(10); // simulate optical scan
                    scanned++;
                }
                System.out.printf("[%s] Finished batch of %d scripts on Thread: %s\n",
                        getName(), scanned, Thread.currentThread().getName());
            } catch (InterruptedException e) {
                System.out.printf("[%s] Scanning interrupted.\n", getName());
                Thread.currentThread().interrupt();
            }
        }

        public int getScanned() { return scanned; }
    }

    // Approach 2: Implementing Runnable
    static class ScannerTask implements Runnable {
        private final int stationId;
        private final int scriptCount;

        ScannerTask(int stationId, int scriptCount) {
            this.stationId = stationId;
            this.scriptCount = scriptCount;
        }

        @Override
        public void run() {
            System.out.printf("[Runnable Station %d] Executing on Thread: %s\n",
                    stationId, Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Q4: Parallel Answer-Script Scanning Simulation ===");
        System.out.println("Main thread started: " + Thread.currentThread().getName());

        int stations = 4;
        ScannerThread[] workers = new ScannerThread[stations];

        // Thread creation & state: NEW
        for (int i = 0; i < stations; i++) {
            workers[i] = new ScannerThread(i + 1, 100);
            System.out.printf("Created Station %d: State = %s\n", i + 1, workers[i].getState());
        }

        // Start stations (State becomes RUNNABLE)
        for (ScannerThread worker : workers) {
            worker.start();
        }

        // Demonstrate isAlive() during execution
        Thread.sleep(15);
        for (ScannerThread worker : workers) {
            System.out.printf("Worker %s isAlive() = %b, State = %s\n",
                    worker.getName(), worker.isAlive(), worker.getState());
        }

        // Join to coordinate completion before printing total
        int totalScanned = 0;
        for (ScannerThread worker : workers) {
            worker.join(); // Main thread waits for each worker to reach TERMINATED state
            totalScanned += worker.getScanned();
            System.out.printf("Worker %s State after join() = %s\n", worker.getName(), worker.getState());
        }

        System.out.println("\nTotal Scripts Scanned Across All Stations: " + totalScanned);

        System.out.println("\n--- Inquiry: start() vs run() ---");
        System.out.println("Calling start() allocates a new native OS thread and invokes run() concurrently.");
        System.out.println("Calling run() directly is an ordinary synchronous method call executing on the CURRENT thread,");
        System.out.println("providing zero multithreading parallelism.");
    }
}
