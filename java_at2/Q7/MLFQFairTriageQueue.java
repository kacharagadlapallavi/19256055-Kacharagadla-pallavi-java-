package Q7;

import java.util.ArrayDeque;

/**
 * MLFQFairTriageQueue
 * Redesigned Multi-Level Feedback Queue with Bitmask and Monotonic Head-Inspection Aging.
 *
 * Key Architectural Innovations:
 * 1. Array of 5 circular ArrayDeque buckets: O(1) admission and dequeue with natural FIFO stability.
 * 2. Active bucket bitmask: Bitwise Integer.numberOfTrailingZeros() finds the highest priority bucket in 1 CPU cycle.
 * 3. Monotonic FIFO Aging: Because patients in each bucket are ordered by arrival time,
 *    the oldest patient is ALWAYS at the head (peekFirst()). Aging checks require NO global scans!
 */
public class MLFQFairTriageQueue {

    public static final int LEVELS = 5;

    @SuppressWarnings("unchecked")
    private final ArrayDeque<Patient>[] buckets = new ArrayDeque[LEVELS];
    private int activeMask = 0; // bit i is set if buckets[i] is non-empty
    private int totalSize = 0;

    public MLFQFairTriageQueue() {
        for (int i = 0; i < LEVELS; i++) {
            buckets[i] = new ArrayDeque<>(64);
        }
    }

    /**
     * Strict O(1) Admission.
     * Inserts directly into the circular buffer of the patient's severity level.
     */
    public void admit(Patient p) {
        int idx = p.getSeverity() - 1;
        buckets[idx].addLast(p);
        activeMask |= (1 << idx);
        totalSize++;
    }

    /**
     * Strict O(1) Dequeue.
     * Uses bitwise hardware instruction (tzcnt) to locate the highest-priority non-empty bucket.
     */
    public Patient pollNext() {
        if (activeMask == 0) return null;

        // Bitwise find first set bit (0 = Severity 1, 1 = Severity 2, etc.)
        int highestPriorityIdx = Integer.numberOfTrailingZeros(activeMask);
        Patient p = buckets[highestPriorityIdx].pollFirst();
        totalSize--;

        if (buckets[highestPriorityIdx].isEmpty()) {
            activeMask &= ~(1 << highestPriorityIdx);
        }
        return p;
    }

    /**
     * Highly Optimized Aging:
     * Exploit Monotonicity: Since ArrayDeque maintains strict FIFO insertion,
     * the patient who has waited longest in any bucket is ALWAYS at index 0 (peekFirst()).
     * We inspect only bucket heads and stop as soon as a non-overdue head is encountered.
     * Complexity: O(k) where k is the number of promoted patients (O(1) per promotion),
     * completely eliminating the O(N) heap scan of the original design!
     */
    public int applyAging(long currentTime, long threshold) {
        if (activeMask == 0) return 0;
        int promotions = 0;

        // Only buckets 1 through 4 (Severities 2 through 5) can be promoted
        for (int b = 1; b < LEVELS; b++) {
            if ((activeMask & (1 << b)) == 0) continue;

            ArrayDeque<Patient> bucket = buckets[b];
            while (!bucket.isEmpty()) {
                Patient oldest = bucket.peekFirst();
                long wait = currentTime - oldest.arrivalTime;
                if (wait >= threshold) {
                    // Promote to higher priority bucket (b - 1)
                    bucket.pollFirst();
                    oldest.setSeverity(oldest.getSeverity() - 1);
                    buckets[b - 1].addLast(oldest);
                    activeMask |= (1 << (b - 1));
                    promotions++;
                } else {
                    // Because arrival times are strictly monotonically increasing in FIFO order,
                    // if the head has not waited >= threshold, NO subsequent patient in this bucket has!
                    break;
                }
            }

            if (bucket.isEmpty()) {
                activeMask &= ~(1 << b);
            }
        }
        return promotions;
    }

    public boolean isEmpty() {
        return activeMask == 0;
    }

    public int size() {
        return totalSize;
    }
}

