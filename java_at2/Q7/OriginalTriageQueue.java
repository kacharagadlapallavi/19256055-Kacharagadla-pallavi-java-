package Q7;

import java.util.*;

/**
 * OriginalTriageQueue
 * Original implementation based on Java's PriorityQueue.
 * Suffers severe limitations when dynamic aging / fairness is required:
 * - Admission and polling incur O(log N) comparisons.
 * - Updating priority for aging requires an O(N) scan and O(N) heap reconstruction.
 */
public class OriginalTriageQueue {

    private final PriorityQueue<Patient> heap;

    public OriginalTriageQueue() {
        this.heap = new PriorityQueue<>(
                Comparator.comparingInt(Patient::getSeverity)
                        .thenComparingLong(p -> p.arrivalTime)
        );
    }

    public void admit(Patient p) {
        heap.add(p);
    }

    public Patient pollNext() {
        return heap.poll();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public int size() {
        return heap.size();
    }

    /**
     * Aging mechanism in Original Solution:
     * Scans the entire heap (O(N)), elevates severity for overdue patients,
     * and rebuilds the heap to restore the binary min-heap invariant.
     */
    public int applyAging(long currentTime, long threshold) {
        if (heap.isEmpty()) return 0;

        int promotions = 0;
        List<Patient> temp = new ArrayList<>(heap.size());

        // Drain all elements: O(N) or O(N log N)
        while (!heap.isEmpty()) {
            Patient p = heap.poll();
            long wait = currentTime - p.arrivalTime;
            if (wait >= threshold && p.getSeverity() > 1) {
                p.setSeverity(p.getSeverity() - 1);
                promotions++;
            }
            temp.add(p);
        }

        // Re-insert / rebuild: O(N)
        heap.addAll(temp);
        return promotions;
    }
}

