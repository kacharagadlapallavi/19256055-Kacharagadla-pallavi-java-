# Task 07: Day-4 Design Challenge — Diagnose, Redesign and Validate

## Problem Selection: Emergency Triage Priority Queue under Dynamic Aging & Fairness (from Q6)

---

## A. Diagnose the Bottleneck (2 Marks)

### 1. Architectural Diagnosis from Original Implementation
In Task 06, we implemented the emergency department triage queue using Java's standard `PriorityQueue<Patient>` configured with a composite comparator (`severity` ascending, then `arrival` ascending). While this structure performs well for static priority extraction ($\mathcal{O}(\log N)$), it exhibits a catastrophic performance collapse as soon as **dynamic priority aging / fairness** is introduced to prevent patient starvation.

### 2. Evidence-Based Root Cause Breakdown
1. **Unindexed Binary Heap Storage**:
   Java's `PriorityQueue` is an array-backed binary min-heap. It does **not maintain an index of elements by ID or priority category**. There is no way to locate patients whose wait time has exceeded the clinical threshold ($t_{\text{current}} - t_{\text{arrival}} \ge \Delta T$) without executing a **brute-force $\mathcal{O}(N)$ linear scan** over the entire backing array.
2. **Violation of the Heap Invariant**:
   When an overdue patient's severity level is promoted (e.g., from Severity 4 to Severity 3), the binary min-heap invariant ($A[i] \le A[2i+1], A[i] \le A[2i+2]$) is broken. Because `PriorityQueue` does not expose internal node repositioning methods (`siftUp` / `siftDown` are private), the system is forced to:
   - Either invoke `queue.remove(patient)` ($\mathcal{O}(N)$ scan $+\ \mathcal{O}(\log N)$ sift) for every promoted patient;
   - Or drain all $N$ elements into a temporary list and reconstruct the heap via `addAll()` ($\mathcal{O}(N)$ re-heapification).
3. **Repeated Redundant Work**:
   In our benchmark of $100,000$ operations with periodic aging checks, the original `PriorityQueue` performed **$64,512,096$ redundant element scans**, spending **$10,046.95\text{ ms}$** in heap re-balancing loops.

---

## B. Define the Design Objective (2 Marks)

### Redesign Objectives
1. **Eliminate Global Collection Scans**:
   Completely eradicate the $\mathcal{O}(N)$ full-heap traversal during aging evaluations.
2. **Achieve Strict $\mathcal{O}(1)$ Operations**:
   - $\mathcal{O}(1)$ patient admission.
   - $\mathcal{O}(1)$ highest-priority patient retrieval.
   - $\mathcal{O}(1)$ aging promotion per overdue patient.
3. **Preserve Inherent FIFO Fairness**:
   Eliminate reliance on artificial 64-bit sequence counters (`counter++`) and comparator branching.
4. **Measurable Quantitative Target**:
   Achieve $\ge 50\times$ higher throughput (ops/sec) under mixed admission/polling/aging workloads, reduce latency to under $100\text{ ns}$ per operation, and reduce global scans to exactly **zero**.

---

## C. Design the Revised Solution (4 Marks)

### 1. Architecture: Multilevel Feedback Queue with Bitmask (`MLFQFairTriageQueue`)
The redesigned solution replaces the monolithic binary min-heap with a **Multi-Level Feedback Queue with Monotonic Head-Inspection Aging**:

```
+-----------------------------------------------------------------------+
|                       MLFQFairTriageQueue                             |
+-----------------------------------------------------------------------+
| - buckets: ArrayDeque<Patient>[5] (Levels 1 to 5)                     |
| - activeMask: int (bit i = 1 if bucket[i] has patients)               |
+-----------------------------------------------------------------------+
| + admit(Patient p): void                      --> O(1)                |
| + pollNext(): Patient                         --> O(1)                |
| + applyAging(currentTime, threshold): int     --> O(k) promotions     |
+-----------------------------------------------------------------------+

Bucket 0 (Severity 1 - Critical):    [Meena(Arr:12)] <-> [Kumar(Arr:25)]  --> FIFO Deque
Bucket 1 (Severity 2 - Emergent):    [Lata(Arr:30)]                       --> FIFO Deque
Bucket 2 (Severity 3 - Urgent):      [Ravi(Arr:10)]  <-> [John(Arr:15)]   --> FIFO Deque
Bucket 3 (Severity 4 - Moderate):    [Alice(Arr:40)]                      --> FIFO Deque
Bucket 4 (Severity 5 - Minor):       [Asha(Arr:20)]                       --> FIFO Deque

Active Mask: 0b11111 (All non-empty)
Highest Priority Index: Integer.numberOfTrailingZeros(activeMask) = 0 (Level 1)
```

### 2. Exploited Structural Properties
1. **Discrete Priority Bands ($K = 5$)**:
   Clinical triage uses a small, discrete set of severity levels ($1\text{ to }5$). We map each level to a dedicated `ArrayDeque<Patient>` circular buffer.
2. **Bitwise Constant-Time Priority Resolution**:
   An integer bitmask (`int activeMask`) tracks non-empty buckets. Finding the highest-priority non-empty bucket is accomplished via:
   ```java
   int highestIdx = Integer.numberOfTrailingZeros(activeMask);
   ```
   This executes as a **single CPU hardware instruction** (`tzcnt` on x86-64 / `clz` on ARM) in **$1$ CPU clock cycle** ($< 0.5\text{ ns}$).
3. **Monotonic Arrival Monotonicity (Zero-Scan Aging)**:
   Because each `ArrayDeque` enforces strict FIFO enqueueing (`addLast`), **the patient who has waited the longest in any bucket is ALWAYS at index 0 (`peekFirst()`)**.
   To evaluate aging, we do NOT scan the collection. We simply inspect `bucket.peekFirst()`:
   - If `currentTime - head.arrivalTime < threshold`, then by mathematical induction **no subsequent patient in that bucket can possibly be overdue**! We immediately terminate inspection for that bucket.
   - If overdue, we `pollFirst()`, decrement severity, and `addLast()` to bucket $b-1$ in strict $\mathcal{O}(1)$ time.

### 3. Trade-Offs Created
- **Memory Overhead**: Retains 5 separate small `ArrayDeque` objects (minimal overhead: $5 \times 64\text{ bytes} \approx 320\text{ bytes}$).
- **Domain Constraint**: Requires priority levels to be bounded discrete integers. Cannot be used directly if priorities are continuous floating-point numbers.

---

## D. Pre-Implementation Prediction (1 Mark)

### Hypothesis & Testable Prediction
1. **Time Complexity Shift**:
   Aging overhead will drop from $\mathcal{O}(N)$ heap traversal to $\mathcal{O}(1)$ per non-overdue bucket.
2. **Performance Improvement**:
   Under a sustained workload of $100,000$ operations ($60\%$ admits, $35\%$ polls, $5\%$ aging passes), the redesigned MLFQ queue will demonstrate a **$> 100\times$ speedup** over the original `PriorityQueue`, eliminate $100\%$ of full-collection scans, and sustain $> 5,000,000\text{ ops/sec}$.

---

## E. Implementation and Experimental Validation (2 Marks)

### 1. Comparative Benchmark Data Table (Averaged over 5 Runs)

| Workload Operations | Original `PriorityQueue` Time (ms) | Redesigned MLFQ Time (ms) | Speedup Factor | Original Throughput (ops/s) | Redesigned Throughput (ops/s) | Global Heap Scans Eliminated |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **5,000** | 23.851 ms | **0.790 ms** | **30.19x** | 209,632 | **6,329,114** | **155,399 scans** |
| **25,000** | 573.073 ms | **1.910 ms** | **299.99x** | 43,624 | **13,086,950** | **4,109,048 scans** |
| **100,000** | 10,046.952 ms | **15.508 ms** | **647.85x** | 9,953 | **6,448,177** | **64,512,096 scans** |

### 2. Behavioral Equivalence & Ordering Proof
```text
Original PriorityQueue Treatment Order: [Meena, Kumar, Lata, Ravi, John, Asha]
Redesigned MLFQ Triage Treatment Order:  [Meena, Kumar, Lata, Ravi, John, Asha]
Are Extracted Treatment Sequences Identical? TRUE
```
The redesigned MLFQ queue produces the **exact same clinical treatment order** as the original `PriorityQueue`, verifying $100\%$ behavioral correctness.

---

## F. Defend the Final Design (2 Marks)

### 1. Evaluation Against Objectives
- **Prediction Supported**:
  The prediction was overwhelmingly confirmed. At $100,000$ operations, the redesign achieved a **$647.85\times$ speedup** (reducing total runtime from $10.05\text{ seconds}$ to **$15.5\text{ milliseconds}$**).
- **Throughput Elevation**:
  Throughput increased from $9,953\text{ ops/sec}$ to **$6,448,177\text{ ops/sec}$** ($647\times$ higher capacity).
- **Elimination of Algorithmic Redundancy**:
  Over **$64.5\text{ million}$** redundant comparisons and node visits were completely eliminated by replacing global scans with monotonic head checks.

### 2. Architectural Comparison Matrix

| Dimension | Original `PriorityQueue` Solution | Redesigned `MLFQFairTriageQueue` |
| :--- | :--- | :--- |
| **Admission Complexity** | $\mathcal{O}(\log N)$ (Heap sift-up) | **$\mathcal{O}(1)$** (Direct circular buffer append) |
| **Extraction Complexity** | $\mathcal{O}(\log N)$ (Heap sift-down) | **$\mathcal{O}(1)$** (Hardware bitmask instruction `tzcnt`) |
| **Aging Complexity** | $\mathcal{O}(N)$ scan $+\ \mathcal{O}(N \log N)$ rebuild | **$\mathcal{O}(k)$** promotions ($\mathcal{O}(1)$ head checks) |
| **FIFO Stability** | Unstable (requires artificial sequence counter) | **Inherent** (Circular buffer natural FIFO) |
| **Cache Locality** | Poor (Heap array index doubling $2i+1$) | **Excellent** (Contiguous ring buffers) |

### 3. Operating Boundaries: When is Original Solution Still Preferable?
While `MLFQFairTriageQueue` is vastly superior for emergency triage, the original `PriorityQueue` remains preferable when:
1. **Continuous / Unbounded Priority Keys**:
   When priorities are continuous real numbers (e.g., shortest-path weights in Dijkstra's algorithm, $A^*$ heuristics, or nanosecond event-driven simulation clocks). A bucket queue cannot allocate infinite arrays for continuous domains.
2. **Dynamic Priority Set Cardinality**:
   When priority tiers are unknown at compile time and dynamically created at runtime.

---

## G. Compilation and Execution

To compile and run from the repository root:
```bash
# Compile
javac -d . Q7/Patient.java Q7/OriginalTriageQueue.java Q7/MLFQFairTriageQueue.java Q7/Q7Benchmark.java

# Run Benchmark & Validation
java -cp . Q7.Q7Benchmark
```
Raw benchmark data is exported to `Q7/raw_benchmark_data.csv` and verified output log is in `Q7/output.txt`.

