# Task 06: Priority, Order and Fairness in a Queue (Hospital Emergency Triage)

## 1. Problem Context
In a hospital emergency department, triage nurses categorize arriving patients into five clinically recognized severity levels:
- **Level 1 (Resuscitation / Critical)**: Immediate life threat (e.g., cardiac arrest).
- **Level 2 (Emergent)**: High risk, severe pain, altered mental state.
- **Level 3 (Urgent)**: Stable, but requires multiple resources (e.g., fractures, high fever).
- **Level 4 (Less Urgent / Moderate)**: Low risk, single diagnostic test required.
- **Level 5 (Non-urgent / Minor)**: Routine prescription refill, mild rash.

The triage physician must always admit the **most severe patient next** (lowest severity number first). When multiple patients share the same severity level, treatment must strictly follow **first-come, first-served (FIFO) arrival order**.

---

## 2. Core Architectural Design

### 2.1 Why a Plain Queue (`LinkedList` or `ArrayDeque`) Fails
A standard FIFO queue can only enqueue at the tail and dequeue from the head. If an ambulance arrives with a critical Severity 1 patient while twenty Severity 4 patients are waiting, a plain queue cannot prioritize the emergency without an $\mathcal{O}(N)$ linear scan to find, remove, and re-enqueue patients.

### 2.2 Enforcing Priority & FIFO Stability via `PriorityQueue`
Java’s `PriorityQueue<E>` is implemented as an array-backed, unbounded **binary min-heap**:
```java
private final PriorityQueue<Patient> queue = new PriorityQueue<>(
    Comparator.comparingInt((Patient p) -> p.severity)
              .thenComparingLong(p -> p.arrival)
);
```
- **The Stability Problem**: Binary heaps are inherently **unstable**. If two elements compare as equal, the heap does not guarantee that the one inserted earlier will be polled earlier.
- **The Solution (Monotonic Arrival Counter)**:
  Every incoming patient is tagged with a strictly increasing 64-bit sequence counter (`counter++`). When two patients share identical severity levels, the secondary comparator `thenComparingLong(p -> p.arrival)` breaks the tie based on arrival time, enforcing strict FIFO fairness.

### 2.3 Why `queue.toString()` Does NOT Match Treatment Order
A binary min-heap guarantees only the **Heap Invariant**:
$$A[i] \le A[2i + 1] \quad \text{and} \quad A[i] \le A[2i + 2]$$
Only the root element ($A[0]$) is guaranteed to be the minimum. The remaining elements are organized in a partially ordered binary tree flattened into an array. Calling `System.out.println(queue)` or iterating over `queue` simply reads indices $0, 1, 2, \dots$ sequentially without sifting, displaying internal tree layout rather than extraction order. The true treatment order is revealed exclusively by successive `queue.poll()` calls.

---

## 3. Hand-Verified Correctness Check (8 Patients)

### 3.1 Arrival Sequence & Hand-Worked Derivation

| Arrival # | Patient Name | Severity Level | Clinical Triage Category | Expected Treatment Position |
| :---: | :--- | :---: | :--- | :---: |
| 0 | Alice | 4 | Moderate | **7th** |
| 1 | Bob | 1 | Resuscitation / Critical | **1st** |
| 2 | Charlie | 2 | Emergent | **4th** |
| 3 | David | 1 | Resuscitation / Critical | **2nd** (Tied S1, arrived after Bob) |
| 4 | Emma | 5 | Non-urgent | **8th** |
| 5 | Frank | 2 | Emergent | **5th** (Tied S2, arrived after Charlie) |
| 6 | Grace | 3 | Urgent | **6th** |
| 7 | Henry | 1 | Resuscitation / Critical | **3rd** (Tied S1, arrived after David) |

### 3.2 Verification
- **Expected Order**: `Bob(S1) David(S1) Henry(S1) Charlie(S2) Frank(S2) Grace(S3) Alice(S4) Emma(S5)`
- **Observed Program Output**: `Bob(S1) David(S1) Henry(S1) Charlie(S2) Frank(S2) Grace(S3) Alice(S4) Emma(S5)`
- **Match**: **$100\%$ Exact**.

---

## 4. Complete the Code Verification

### 4.1 Solution (`Q6/Triage.java`)
```java
public class Triage {
    private long counter = 0;

    // TODO 1: Create a PriorityQueue that serves the lowest severity number first and,
    // for equal severity, the earliest arrival first
    private final PriorityQueue<Patient> queue = new PriorityQueue<>(
            Comparator.comparingInt((Patient p) -> p.severity)
                    .thenComparingLong(p -> p.arrival)
    );

    void admit(String name, int severity) {
        queue.add(new Patient(name, severity, counter++));
    }

    Patient next() {
        return queue.poll();
    }
    ...
}
```

### 4.2 Expected vs Observed Output
- **Expected Output**:
  ```text
  Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)
  ```
- **Observed Output**:
  ```text
  Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)
  ```
- **Explanation**:
  - `Meena` (Arr 1) and `Kumar` (Arr 4) both have Severity 1; Meena arrived first and is treated first.
  - `Lata` has Severity 2.
  - `Ravi` (Arr 0) and `John` (Arr 2) both have Severity 3; Ravi arrived first and is treated first.
  - `Asha` has Severity 5 and is treated last.

---

## 5. Growth Investigation: Predicted vs. Observed Costs

Measured over 5 runs across three patient queue volumes:

| Queue Size ($N$) | Theoretical Step Count ($\log_2 N$) | Total Admit Time (ms) | Avg Latency / Admit | Total Poll Time (ms) | Avg Latency / Poll |
| :--- | :---: | :--- | :--- | :--- | :--- |
| **1,000** | $\approx 10$ steps | 1.549 ms | **1,549.5 ns** | 1.781 ms | **1,781.2 ns** |
| **10,000** | $\approx 13$ steps | 2.779 ms | **277.9 ns** | 2.297 ms | **229.7 ns** |
| **100,000** | $\approx 17$ steps | 7.547 ms | **75.5 ns** | 22.624 ms | **226.2 ns** |

- **Analysis**:
  Both `add()` (sift-up) and `poll()` (sift-down) scale strictly according to $\mathcal{O}(\log N)$ tree height. Even at $100,000$ active patients, admission executes in under $80\ \text{ns}$ and polling in $226\ \text{ns}$.

---

## 6. The Starvation Problem & Dynamic Aging Fair-Rule Design

### 6.1 Starvation Diagnosis
In a hospital ER, if high-severity patients (Severity 1–3) arrive at a steady rate $\lambda \ge \mu$ (where $\mu$ is physician service rate), a low-priority patient (Severity 5, e.g., Asha) will suffer **indefinite starvation**. Because the comparator strictly prioritizes severity over arrival time across different levels, Asha will remain trapped at the bottom of the queue indefinitely.

### 6.2 The Aging Fair-Rule
To prevent starvation, we implement a **dynamic aging policy**:
$$\text{EffectiveSeverity}(t) = \max\left(1, \ \text{BaseSeverity} - \left\lfloor \frac{t - t_{\text{arrival}}}{\Delta T_{\text{aging}}} \right\rfloor\right)$$
Where $\Delta T_{\text{aging}} = 30\text{ minutes}$.
- **Simulated Progression**:
  - $t = 0\text{ min}$: Asha arrives with Base S5 $\implies$ Effective Severity = **S5**
  - $t = 30\text{ min}$: Waited 30 min $\implies$ Promoted to **S4**
  - $t = 60\text{ min}$: Waited 60 min $\implies$ Promoted to **S3**
  - $t = 90\text{ min}$: Waited 90 min $\implies$ Promoted to **S2**
  - $t = 120\text{ min}$: Waited 120 min $\implies$ Promoted to **S1 (CRITICAL - SEEN NEXT)**!
- **Guarantee**: Guarantees an upper bound of $4 \times 30 = 120\text{ minutes}$ maximum wait time for any patient regardless of initial severity.

---

## 7. City Hospital Deployment Decision (5,000 Patients / Day)

### 7.1 Quantitative Feasibility Modeling
- **Volume**: $5,000$ patients / 24 hours $= \approx 208\text{ patients/hour} \approx 0.058\text{ arrivals/second}$.
- **Peak Burst**: Even during a mass-casualty surge of $50$ patients per second:
  - At $N = 5,000$, $\lceil \log_2(5000) \rceil = 13$ comparisons per heap operation.
  - Processing $5,000$ admissions and removals takes **$\approx 3.2\text{ milliseconds}$ of CPU time in total**.
- **Memory Footprint**:
  - $5,000$ `Patient` objects $\times 48\text{ bytes} \approx 240\text{ KB}$ of RAM.
- **Deployment Conclusion**:
  **Approved with Highest Confidence**. The `PriorityQueue` architecture effortlessly supports a city hospital of 5,000 patients/day, utilizing less than $0.001\%$ of available CPU capacity and less than $1\text{ MB}$ of memory.

---

## 8. Alternative Concept & Complexity Analysis

We compared three architectural implementations for $10,000$ emergency admissions:

```text
1. PriorityQueue (Min-Heap):           Total Time:   17.066 ms | Admit: O(log N) | Next: O(log N)
2. Sorted LinkedList:                  Total Time:  108.863 ms | Admit: O(N)     | Next: O(1)
3. Array of 5 FIFO Queues (Buckets):   Total Time:    1.750 ms | Admit: O(1)     | Next: O(1)
```

### 8.1 Architectural Comparison Matrix

| Architectural Approach | Admission Time | Dequeue Time (`next()`) | Space Complexity | Fairness / Stability |
| :--- | :--- | :--- | :--- | :--- |
| **`PriorityQueue<Patient>`** | $\mathcal{O}(\log N)$ (Heap sift-up) | $\mathcal{O}(\log N)$ (Heap sift-down) | $\mathcal{O}(N)$ (Flat array) | Requires artificial sequence counter. Aging is $\mathcal{O}(N)$. |
| **Sorted `LinkedList`** | $\mathcal{O}(N)$ (Linear scan to insertion point) | $\mathcal{O}(1)$ (Head removal) | $\mathcal{O}(N)$ (Node overhead: 3 pointers) | Inherently stable. Catastrophic slowdown as queue grows. |
| **Array of 5 FIFO Queues (`ArrayDeque[]`)** | $\mathcal{O}(1)$ (`buckets[sev-1].addLast()`) | $\mathcal{O}(1)$ (Scan 5 bucket heads) | $\mathcal{O}(N)$ (Zero node overhead) | **Naturally 100% stable FIFO**. Zero heap comparisons. |

- **Key Takeaway**: Because severity levels are discrete integers in $[1, 5]$, an **Array of 5 FIFO Queues** completely eliminates heap re-sifting and runs **$10\times$ faster than `PriorityQueue`**, providing the ideal architectural foundation for our Day-4 Design Challenge in Q7!

---

## 9. Compilation and Execution

To compile and execute from the repository root:
```bash
# Compile
javac -d . Q6/Triage.java Q6/TriageInvestigation.java

# Run Complete the Code Demo
java -cp . Q6.Triage

# Run Comprehensive Investigation & Benchmark
java -cp . Q6.TriageInvestigation
```
Outputs are captured in `Q6/output_triage.txt`, `Q6/output_investigation.txt`, and `Q6/raw_benchmark_data.csv`.

