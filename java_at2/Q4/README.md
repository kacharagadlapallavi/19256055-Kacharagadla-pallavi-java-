# Task 04: Choosing a Map for a Pharmacy Inventory (Hospital Stock Lookup)

## 1. Problem Context
A hospital pharmacy manages an extensive inventory of medications identified by unique alphanumeric medicine codes (e.g., `MED-049281`). The system handles two primary workloads:
1. **Daytime Intensive Operations**: Pharmacists perform hundreds to thousands of stock lookups per day during prescription fulfillment and emergency dispensing.
2. **Evening Batch Reporting**: Every evening, the system automatically generates two distinct administrative reports:
   - **Report 1 (Code Order)**: All medicines sorted alphanumerically by medicine code.
   - **Report 2 (Insertion Order)**: All medicines listed in the exact chronological order they were admitted to inventory.

This investigation evaluates `HashMap`, `TreeMap`, `LinkedHashMap`, and an alternative **Sorted Array with Binary Search** to determine the optimal production architecture.

---

## 2. Pre-Implementation Analysis & Theoretical Expectations

### 2.1 Theoretical Complexities of Map Implementations

| Metric / Operation | `HashMap` | `TreeMap` | `LinkedHashMap` |
| :--- | :--- | :--- | :--- |
| **`put(key, value)`** | $\mathcal{O}(1)$ average / $\mathcal{O}(\log N)$ worst | $\mathcal{O}(\log N)$ (Red-Black Tree insert + rebalance) | $\mathcal{O}(1)$ average (hash put + link append) |
| **`get(key)`** | $\mathcal{O}(1)$ average / $\mathcal{O}(\log N)$ worst | $\mathcal{O}(\log N)$ (tree traversal) | $\mathcal{O}(1)$ average |
| **Iteration Order** | Pseudo-random bucket table order (chaotic) | Natural key sort order (In-order traversal) | Strict insertion order (Doubly-linked entry chain) |
| **Natural Report 1 (Code Order)** | **No** (Order is scrambled) | **Yes** ($\mathcal{O}(N)$ in-order traversal) | **No** (Requires explicit sorting) |
| **Natural Report 2 (Insertion Order)** | **No** (Insertion order is lost) | **No** (Overridden by key comparison) | **Yes** ($\mathcal{O}(N)$ doubly-linked traversal) |

### 2.2 Extra Work When the "Wrong" Map is Selected
1. **If `HashMap` is Chosen**:
   - Neither report matches natural iteration order.
   - To produce Report 1, all entries must be copied to an `ArrayList` and sorted via `list.sort(Comparator.comparing(Medicine::getCode))` ($\mathcal{O}(N \log N)$ time, $\mathcal{O}(N)$ auxiliary space).
   - To produce Report 2, original arrival timestamps or sequence IDs must be explicitly stored with each entry and sorted at report time.
2. **If `TreeMap` is Chosen**:
   - Report 1 is free ($\mathcal{O}(N)$).
   - However, **Report 2 is irreparably destroyed** because `TreeMap` enforces key sorting on insert. A secondary list or sequence-indexed structure must be maintained.
   - Crucially, every daytime prescription lookup pays an $\mathcal{O}(\log N)$ tree traversal penalty ($10\text{--}17$ node pointer hops per query) instead of an instantaneous $\mathcal{O}(1)$ hash lookup.
3. **If `LinkedHashMap` is Chosen**:
   - Report 2 is free ($\mathcal{O}(N)$).
   - Report 1 requires copying and sorting ($\mathcal{O}(N \log N)$).
   - Daytime lookups operate at full $\mathcal{O}(1)$ hash speed.

---

## 3. Empirical Results & Benchmark Evidence

Tested across three inventory scales ($1,000$, $10,000$, and $100,000$ medicines) with $100,000$ random lookups ($90\%$ hits, $10\%$ misses) and evening report generation.

### 3.1 Benchmark Data Table

| Data Structure | Inventory Size | Put Time (ms) | 100k Lookups (ms) | Avg Latency / Get | Report 1 Code Order | Report 2 Insertion Order |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **`HashMap`** | 1,000 | 0.241 ms | 6.641 ms | **66.4 ns** | 1.184 ms (OK) | 0.023 ms (OK) |
| **`TreeMap`** | 1,000 | 0.650 ms | 32.980 ms | **329.8 ns** | 0.194 ms (OK) | 0.012 ms (OK) |
| **`LinkedHashMap`** | 1,000 | 0.287 ms | 13.840 ms | **138.4 ns** | 0.974 ms (OK) | 1.296 ms (OK) |
| **Sorted Array** | 1,000 | 2.736 ms | 80.961 ms | **809.6 ns** | 0.063 ms (OK) | 2.201 ms (OK) |
| | | | | | | |
| **`HashMap`** | 10,000 | 1.754 ms | 6.982 ms | **69.8 ns** | 17.520 ms (OK) | 0.046 ms (OK) |
| **`TreeMap`** | 10,000 | 8.096 ms | 61.611 ms | **616.1 ns** | 2.221 ms (OK) | 0.029 ms (OK) |
| **`LinkedHashMap`** | 10,000 | 1.459 ms | 6.644 ms | **66.4 ns** | 10.767 ms (OK) | 0.387 ms (OK) |
| **Sorted Array** | 10,000 | 18.833 ms | 77.153 ms | **771.5 ns** | 0.039 ms (OK) | 7.568 ms (OK) |
| | | | | | | |
| **`HashMap`** | 100,000 | 46.854 ms | 12.425 ms | **124.3 ns** | 109.608 ms (OK) | 0.170 ms (OK) |
| **`TreeMap`** | 100,000 | 154.126 ms | 129.930 ms | **1,299.3 ns** | 13.215 ms (OK) | 0.123 ms (OK) |
| **`LinkedHashMap`** | 100,000 | 32.078 ms | 11.820 ms | **118.2 ns** | 156.463 ms (OK) | 1.225 ms (OK) |
| **Sorted Array** | 100,000 | 190.859 ms | 160.787 ms | **1,607.9 ns** | 0.531 ms (OK) | 73.195 ms (OK) |

---

## 4. Scalability Inquiry & Architectural Defense

### 4.1 Definition of Acceptable Response Time
In clinical hospital operations (point-of-sale dispensing, emergency ward stat dosing, barcode medication verification):
- An individual lookup response time must be **strictly under $1.0\ \text{millisecond}$** ($1,000\ \mu\text{s}$) to prevent perceptible UI lag.
- In automated backend batch verification, sub-microsecond latency ($< 1.0\ \mu\text{s} = 1,000\ \text{ns}$) is required to sustain thousands of queries per second.
- **Observed**: `HashMap` ($124\ \text{ns}$) and `LinkedHashMap` ($118\ \text{ns}$) easily satisfy this with an **order-of-magnitude safety margin**. `TreeMap` ($1,299\ \text{ns}$) crosses the $1\ \mu\text{s}$ threshold at scale.

### 4.2 Architectural Map Deployment Decision
- **Final Recommendation**: **`LinkedHashMap<String, Medicine>`**.
- **Defense**:
  1. **Workload Priority Matching**:
     - Hospital lookups occur **continuously throughout the working day** during live patient care. `LinkedHashMap` services each query in **$118\text{ ns}$** ($\approx 10\times$ faster than `TreeMap`).
     - Reports are generated **once daily after hours**. Spending $156\text{ ms}$ once a day to sort the code order report is a completely negligible background cost compared to saving milliseconds on every live daytime lookup.
  2. **Inherent Insertion Tracking**:
     - `LinkedHashMap` naturally preserves insertion order with zero auxiliary bookkeeping data structures, satisfying Report 2 in **$1.225\text{ ms}$**.
  3. **Why Not `TreeMap`?**:
     - Choosing `TreeMap` sacrifices daytime lookup throughput by $10\times$ solely to optimize a report that runs once at midnight. This is a classic violation of workload-aligned systems design.

### 4.3 Origin of Remaining Costs
Even with $\mathcal{O}(1)$ hashing, latency grows from $66\text{ ns}$ at $1,000$ to $118\text{ ns}$ at $100,000$. This remaining cost originates from:
1. **CPU L3 Cache Misses**: At $100,000$ entries, the node table and medicine objects exceed CPU L1/L2 cache capacity ($\approx 32\text{--}512\text{ KB}$), forcing RAM bus fetches.
2. **String Hash & Equality Check**: Key resolution requires dereferencing the `String` object, reading its `byte[]` array, and verifying equality.

### 4.4 Why `Hashtable` is Obsolete in Modern Java
1. **Coarse-Grained Synchronization Bottleneck**: Every single method in `java.util.Hashtable` (`get`, `put`, `remove`) is declared `synchronized`. Multiple concurrent pharmacist terminals accessing the map would serialize, queuing for a single monitor lock.
2. **Null Rejection**: `Hashtable` throws `NullPointerException` on null keys or null values, lacking flexibility.
3. **Legacy Enumerator API**: Employs obsolete `Enumeration` interfaces instead of modern fail-fast `Iterator` and Stream APIs.
4. **Modern Alternatives**:
   - Single-threaded / thread-confined: `HashMap` / `LinkedHashMap`.
   - Multi-threaded concurrent reads/writes: `java.util.concurrent.ConcurrentHashMap`.

---

## 5. Alternative Concept and Complexity Analysis: Sorted Array with Binary Search

### 5.1 Architecture & Implementation
If the inventory were maintained as a contiguous sorted array `Medicine[] inventory`:
```java
// Lookup using binary search
int idx = Arrays.binarySearch(inventory, dummyKey, Comparator.comparing(Medicine::getCode));
Medicine found = (idx >= 0) ? inventory[idx] : null;
```

### 5.2 Comparative Evaluation

| Dimension | `HashMap` | `TreeMap` | `LinkedHashMap` | Sorted Array (`binarySearch`) |
| :--- | :--- | :--- | :--- | :--- |
| **Lookup Time** | $\mathcal{O}(1)$ | $\mathcal{O}(\log N)$ | $\mathcal{O}(1)$ | $\mathcal{O}(\log N)$ (Direct branch jumps) |
| **Insertion Time** | $\mathcal{O}(1)$ amortized | $\mathcal{O}(\log N)$ | $\mathcal{O}(1)$ amortized | $\mathcal{O}(N)$ (Requires right-shifting elements) |
| **Report 1 (Code Order)** | $\mathcal{O}(N \log N)$ (Sort copy) | $\mathcal{O}(N)$ (In-order walk) | $\mathcal{O}(N \log N)$ (Sort copy) | $\mathcal{O}(N)$ (Contiguous array copy: $0.53\text{ ms}$) |
| **Report 2 (Insert Order)** | Requires secondary list | Requires secondary list | $\mathcal{O}(N)$ (Natural chain) | $\mathcal{O}(N \log N)$ (Must re-sort copy by seq) |
| **Memory Footprint** | Moderate (Array + Nodes) | High (Tree node pointers) | Moderate-High (Hash + List pointers) | **Minimal** (Single contiguous reference array) |

- **Conclusion on Sorted Array**:
  While a sorted array delivers the fastest Report 1 generation (**$0.531\text{ ms}$**) and zero node pointer overhead, its **$\mathcal{O}(N)$ insertion time** makes it completely impractical for dynamic hospital inventories where new medicines, batches, and stock updates arrive continually.

---

## 6. Compilation and Execution

To compile and execute from the repository root:
```bash
# Compile all Q4 classes
javac -d . Q4/Medicine.java Q4/PharmacyInventory.java Q4/Q4Benchmark.java

# Run Benchmark
java -cp . Q4.Q4Benchmark
```
Raw benchmark measurements are saved in `Q4/raw_benchmark_data.csv` and console log is archived in `Q4/output.txt`.

