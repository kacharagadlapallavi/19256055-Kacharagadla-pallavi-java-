# CSA09 – Programming in Java: Assignment 2 (CO2)
**Course**: CSA09 – Programming in Java  
**Coverage**: CO2 – Strings and StringBuilder, Regular Expressions, Java Collection Framework (List, Queue, Deque, PriorityQueue, Set, Map), The `hashCode`–`equals` Contract, Generics, `Comparable` and `Comparator`, and `Collections` Utility Class (Sessions 15–26).  
**Nature of Assignment**: Individual  
**Total Marks**: 50  
**Java Version**: OpenJDK / Oracle Java 26 (Compatible with Java 17, 21, and 26+)  
**Repository Name**: `19256055-Kacharagadla-pallavi-java-`  
**Author**: Kacharagadla Pallavi (Register No: 19256055)

---

## 1. Executive Summary & Mark Distribution

| Task ID | Assessed Area | Bloom's Level | Marks | Key Engineering Result / Finding | Status |
| :---: | :--- | :---: | :---: | :--- | :---: |
| **[Q1](file:///c:/Users/arath/Downloads/java_at2/Q1/README.md)** | Building Text Under Load (`String`, `StringBuilder`, `StringBuffer`) | K4 – Analyse | 6 | `String (+)` exhibits $\mathcal{O}(n^2)$ character copying degradation under load (taking 955 ms and creating 5.98M intermediate objects at 20k tickets x 100 fields). `StringBuilder` operates in linear $\mathcal{O}(n)$ time (135 ms, 7.1x speedup) with single object creation. Defended thread-confined `StringBuilder` for tickets and asynchronous lock-free queue for shared logs. | **Complete** |
| **[Q2](file:///c:/Users/arath/Downloads/java_at2/Q2/README.md)** | Music Playlist Editor (`ArrayList`, `LinkedList`, `ArrayDeque`) | K4 – Analyse | 4 | Exploded the textbook linked list myth: `ArrayList` is **$98\times$ faster** than `LinkedList` on middle insertions (21 ms vs 2,089 ms at 100k) due to CPU cache locality and SIMD block memory moves. Established boundary: stop using `ArrayList` for high-frequency front insertions. For bidirectional "Up Next" queue, demonstrated that `ArrayDeque` is superior to both. | **Complete** |
| **[Q3](file:///c:/Users/arath/Downloads/java_at2/Q3/README.md)** | Event Attendance De-duplication (`hashCode`–`equals` Contract) | K4 – Analyse | 6 | Completed TODO 1 overriding `hashCode()`; demonstrated output change from `Unique students: 3` (failure) to `Unique students: 2` (expected). Implemented 3 designed tests: apparent success facade, duplicate exposure, and post-insertion mutation (orphaning). Evaluated `TreeSet` and `HashMap` alternatives. | **Complete** |
| **[Q4](file:///c:/Users/arath/Downloads/java_at2/Q4/README.md)** | Pharmacy Inventory Stock Lookup (`HashMap`, `TreeMap`, `LinkedHashMap`) | K4 – Analyse | 6 | Benchmarked 100k lookups across 1k, 10k, and 100k medicines. `LinkedHashMap` and `HashMap` delivered sub-microsecond latency (118 ns/query), $11\times$ faster than `TreeMap` (1,299 ns). Defended `LinkedHashMap` for production to optimize continuous daytime lookups while servicing once-daily evening reports. Analyzed why `Hashtable` is obsolete. | **Complete** |
| **[Q5](file:///c:/Users/arath/Downloads/java_at2/Q5/README.md)** | Generic Ranking Component from a Principle (`Leaderboard<T>`) | K4 – Analyse | 7 | Derived generic `Leaderboard<T>` utilizing `Comparator<? super T>`. Completed TODO 1 (defensive sorted copy) and TODO 2 (multi-field chained comparator). Verified expected output `[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]`. Exposed integer subtraction overflow and proved equivalence of 3-pass stable TimSort. | **Complete** |
| **[Q6](file:///c:/Users/arath/Downloads/java_at2/Q6/README.md)** | Emergency Hospital Triage (`PriorityQueue` & Fairness) | K5 – Evaluate | 8 | Completed TODO 1 using `PriorityQueue` with arrival sequence counter. Verified expected output `Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)`. Hand-verified 8-patient check. Explained why `queue.toString()` reflects binary heap array layout rather than extraction order. Designed dynamic aging rule preventing starvation and approved 5,000/day hospital deployment. | **Complete** |
| **[Q7](file:///c:/Users/arath/Downloads/java_at2/Q7/README.md)** | Day-4 Design Challenge (Diagnose, Redesign and Validate) | K5 – Evaluate | 13 | Diagnosed bottleneck in Q6 triage queue under aging ($\mathcal{O}(N)$ scan and $\mathcal{O}(N)$ heap rebuild). Redesigned into `MLFQFairTriageQueue` using an array of 5 `ArrayDeque` buffers and active bitmask (`tzcnt`). Validated a **$647.85\times$ speedup** at 100k ops, eliminating 64.5M redundant scans with $100\%$ behavioral equivalence. | **Complete** |
| **TOTAL** | **Comprehensive Evaluation (Q1–Q7)** | | **50** | **All 7 Tasks Fully Engineered, Benchmarked, Documented & Progressively Committed.** | **100%** |

---

## 2. Progressive Git Commit History

**Author**: Kacharagadla Pallavi (Register No: 19256055)
```text
be70e60 feat(Q7): Complete Day-4 Design Challenge - MLFQ triage queue bottleneck diagnosis, redesign, and 647x validation
09064eb feat(Q6): Complete emergency triage PriorityQueue, Complete-the-Code, fairness aging rule, and multi-bucket queue comparison
b375ec5 feat(Q5): Complete generic leaderboard ranking component, Complete-the-Code, and 3-pass stable sort analysis
9c12235 feat(Q4): Complete pharmacy inventory map benchmark, scalability inquiry, and sorted array analysis
1035330 feat(Q3): Complete hashCode-equals contract investigation, Complete-the-Code, and TreeSet/HashMap alternatives
c680bbb feat(Q2): Complete playlist editor benchmark, operating boundary analysis, and ArrayDeque evaluation
ca677a8 build: Add .gitignore for compiled class files
1a25e06 feat(Q1): Complete Railway e-Ticket generator benchmark, predictions, and analysis
```

---

## 3. Repository Structure

```text
19256055-Kacharagadla-pallavi-java-/
├── README.md                           # Master Project Documentation & Submission Summary
├── .gitignore                          # Build artifact exclusions (*.class)
├── run_all.bat                         # Automated execution script for all 7 tasks
│
├── Q1/                                 # Task 1: Building Text Under Load
│   ├── RailwayTicketGenerator.java     # String (+), StringBuilder, StringBuffer implementations
│   ├── Q1Benchmark.java                # Benchmark runner across 3 workloads
│   ├── raw_benchmark_data.csv          # Raw measured experimental data
│   ├── output.txt                      # Execution console transcript
│   └── README.md                       # In-depth Task 1 Analysis & Predictions
│
├── Q2/                                 # Task 2: When Is ArrayList an Appropriate Choice?
│   ├── Song.java                       # Domain model
│   ├── MusicPlaylistEditor.java        # Core playlist operations wrapper
│   ├── Q2Benchmark.java                # Multi-size list operations & Up Next queue benchmark
│   ├── raw_benchmark_data.csv          # Raw timing data
│   ├── output.txt                      # Execution console transcript
│   └── README.md                       # In-depth Task 2 Engineering Analysis & Cache Locality
│
├── Q3/                                 # Task 3: Engineer the hashCode-equals Contract
│   ├── Student.java                    # Corrected Student model
│   ├── AttendanceIncomplete.java       # Baseline "Complete the Code" (Before TODO 1)
│   ├── Attendance.java                 # Solved "Complete the Code" (After TODO 1)
│   ├── ContractInvestigation.java      # 3 designed test suites & alternative concepts
│   ├── output_incomplete.txt           # Output before TODO 1 ("Unique students: 3")
│   ├── output_complete.txt             # Output after TODO 1 ("Unique students: 2")
│   ├── output_investigation.txt        # Full contract investigation transcript
│   └── README.md                       # In-depth Task 3 Contract Analysis & Inquiry
│
├── Q4/                                 # Task 4: Choosing a Map for Pharmacy Inventory
│   ├── Medicine.java                   # Domain model
│   ├── PharmacyInventory.java          # HashMap, TreeMap, LinkedHashMap adapter
│   ├── Q4Benchmark.java                # 100k lookups & dual report generation benchmark
│   ├── raw_benchmark_data.csv          # Raw timing data
│   ├── output.txt                      # Execution console transcript
│   └── README.md                       # In-depth Task 4 Architectural Defense
│
├── Q5/                                 # Task 5: Generic Ranking Component from a Principle
│   ├── LeaderboardDemo.java            # Solved "Complete the Code" (TODO 1 & 2)
│   ├── GenericLeaderboardTest.java     # Multi-domain tests, faulty comparator, 3-pass sort
│   ├── output_demo.txt                 # Demo output: [Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]
│   ├── output_test.txt                 # Full test suite transcript
│   └── README.md                       # In-depth Task 5 Strict Weak Ordering & Generics
│
├── Q6/                                 # Task 6: Priority, Order and Fairness in a Queue
│   ├── Triage.java                     # Solved "Complete the Code" (TODO 1)
│   ├── TriageInvestigation.java        # 8-patient check, heap inspection, scaling, aging
│   ├── raw_benchmark_data.csv          # Growth scaling data
│   ├── output_triage.txt               # Demo output: Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)
│   ├── output_investigation.txt        # Full investigation transcript
│   └── README.md                       # In-depth Task 6 Queue Analysis & Deployment Modeling
│
└── Q7/                                 # Task 7: Day-4 Design Challenge (Diagnose, Redesign, Validate)
    ├── Patient.java                    # Shared domain model
    ├── OriginalTriageQueue.java        # Original PriorityQueue with heap rebuild aging
    ├── MLFQFairTriageQueue.java        # Redesigned Multi-Level Feedback Queue with Bitmask
    ├── Q7Benchmark.java                # Side-by-side comparative validation runner
    ├── raw_benchmark_data.csv          # Comparative experimental data
    ├── output.txt                      # Validation output transcript (647x speedup verified)
    └── README.md                       # Complete 6-Part Design Challenge Report (A-F)
```

---

## 4. Instructions to Compile and Execute

### Quick Start (Run All Tasks)
On Windows:
```cmd
run_all.bat
```

### Manual Compilation and Execution by Task

#### Task 01: Railway e-Ticket Generator
```bash
# Compile
javac -d . Q1/RailwayTicketGenerator.java Q1/Q1Benchmark.java

# Run Benchmark
java -cp . Q1.Q1Benchmark
```

#### Task 02: Music Playlist Editor
```bash
# Compile
javac -d . Q2/Song.java Q2/MusicPlaylistEditor.java Q2/Q2Benchmark.java

# Run Benchmark
java -cp . Q2.Q2Benchmark
```

#### Task 03: Event Attendance De-duplication
```bash
# Compile
javac -d . Q3/AttendanceIncomplete.java Q3/Attendance.java Q3/ContractInvestigation.java

# Run Complete the Code (Before TODO 1) -> Output: Unique students: 3
java -cp . Q3.AttendanceIncomplete

# Run Complete the Code (After TODO 1) -> Output: Unique students: 2
java -cp . Q3.Attendance

# Run Comprehensive Contract Investigation Suite
java -cp . Q3.ContractInvestigation
```

#### Task 04: Hospital Pharmacy Inventory Map
```bash
# Compile
javac -d . Q4/Medicine.java Q4/PharmacyInventory.java Q4/Q4Benchmark.java

# Run Benchmark
java -cp . Q4.Q4Benchmark
```

#### Task 05: Generic Sports Leaderboard Ranking Component
```bash
# Compile
javac -d . Q5/LeaderboardDemo.java Q5/GenericLeaderboardTest.java

# Run Complete the Code Demo -> Output: [Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]
java -cp . Q5.LeaderboardDemo

# Run Generic Leaderboard Validation Suite
java -cp . Q5.GenericLeaderboardTest
```

#### Task 06: Emergency Hospital Triage Queue
```bash
# Compile
javac -d . Q6/Triage.java Q6/TriageInvestigation.java

# Run Complete the Code Demo -> Output: Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)
java -cp . Q6.Triage

# Run Triage Investigation Suite
java -cp . Q6.TriageInvestigation
```

#### Task 07: Day-4 Design Challenge
```bash
# Compile
javac -d . Q7/Patient.java Q7/OriginalTriageQueue.java Q7/MLFQFairTriageQueue.java Q7/Q7Benchmark.java

# Run Redesign Validation Benchmark
java -cp . Q7.Q7Benchmark
```

---

## 5. Comprehensive Analysis & Findings by Task

### Q1 – Building Text Under Load: String, StringBuilder or StringBuffer?
- **Theoretical Basis**: Strings in Java are immutable (`byte[]` compact strings). Concatenation via `+` in a loop allocates a new string buffer on every iteration, copying $1 + 2 + \dots + n \approx \mathcal{O}(n^2)$ characters. `StringBuilder` uses a dynamic buffer with amortized $\mathcal{O}(1)$ appends, copying $\mathcal{O}(n)$ characters overall.
- **Empirical Findings**:
  - In Workload 3 (20,000 tickets $\times$ 100 fields), `String (+)` took **955.17 ms** and generated **5,980,000 intermediate objects** (577.5 MB heap churn).
  - `StringBuilder` completed in **135.12 ms** (a **$7.1\times$ speedup**) with only **20,000 final strings** created.
  - `String.format` was the slowest approach (188.9 ms for W2) due to regex tokenization and varargs array allocation.
- **Design Decision**:
  - **Per-ticket generation**: `StringBuilder` (thread-confined to worker stack; zero lock overhead).
  - **Shared booking log**: Thread-confined `StringBuilder` for log message assembly, submitted asynchronously to a lock-free queue (`ConcurrentLinkedQueue`), drained by a background I/O daemon. Defended against naive `StringBuffer` usage due to method-level interleaving race conditions and lock contention.

### Q2 – When Is ArrayList an Appropriate Engineering Choice?
- **Theoretical Basis**: `ArrayList` utilizes contiguous memory arrays; `LinkedList` uses doubly-linked `Node` objects.
- **Empirical Findings**:
  - At $100,000$ elements, random access (`get(k)`) on `ArrayList` took **0.812 ms** vs **6,153.47 ms** on `LinkedList` (**$7,578\times$ faster**).
  - On middle insertions (`add(size/2, item)`), `ArrayList` took **21.19 ms** vs **2,089.55 ms** on `LinkedList` (**$98\times$ faster**). Despite element shifting, `ArrayList` leverages CPU L1/L2 cache prefetching and native vectorized SIMD memory block moves (`System.arraycopy`), whereas `LinkedList` incurs $50,000$ pointer dereference cache misses per insertion.
  - Memory footprint: `LinkedList` consumes $28\text{--}32$ bytes per element ($6\text{--}8\times$ more memory than `ArrayList`'s 4 bytes/element).
- **Operating Boundary**: Stop recommending `ArrayList` only when workloads perform frequent insertions or removals at the **front (index 0)** in large lists.
- **Changed Condition ("Up Next" Queue)**:
  `ArrayDeque` is superior to both `ArrayList` and `LinkedList`. It implements a circular array buffer with bitwise index wrap-around, achieving $\mathcal{O}(1)$ amortized operations at both ends with zero node allocations and superior cache locality.

### Q3 – Engineer the hashCode–equals Contract
- **Contract Principle**: If `a.equals(b) == true`, then `a.hashCode() == b.hashCode()` must be strictly true.
- **The Junior Developer Bug**:
  The developer overrode `equals()` on `regNo` but omitted `hashCode()`. Each new `Student` instance inherited identity hash codes (`System.identityHashCode`), causing identical students (`s1` and `s3` with `regNo = "192311001"`) to map to different hash buckets. `equals()` was never called, resulting in **3 unique students** instead of 2.
- **Completed Code Output**: Overriding `hashCode()` to return `Objects.hashCode(regNo)` correctly routed duplicates to the same bucket, invoking `equals()` and producing the expected **`Unique students: 2`**.
- **Three Designed Tests**:
  1. *Apparent Success*: Adding distinct students or re-adding the exact same object reference yields correct counts, masking the underlying defect.
  2. *Duplicate Exposure*: Distinct object instances with identical `regNo` are improperly retained.
  3. *Field Mutation*: Mutating `regNo` after insertion orphans the object in the hash table (`contains` and `remove` return `false`, causing a silent memory leak). Demonstrates `TreeSet` inconsistency where a faulty comparator drops distinct students.

### Q4 – Choosing a Map for a Pharmacy Inventory
- **Requirements**: Hundreds of daytime lookups by code; evening Report 1 in code order; evening Report 2 in insertion order.
- **Empirical Findings**:
  - Lookups (100k queries at 100k size): `LinkedHashMap` (**11.82 ms** / 118 ns per lookup) and `HashMap` (**12.42 ms** / 124 ns) were **$11\times$ faster** than `TreeMap` (**129.93 ms** / 1,299 ns) and **$14\times$ faster** than Sorted Array Binary Search (**160.78 ms** / 1,607 ns).
  - Report 1 (Code order): `TreeMap` produces it in 13.2 ms naturally; `LinkedHashMap` takes 156.4 ms by sorting a copy.
  - Report 2 (Insertion order): `LinkedHashMap` produces it in **1.22 ms** naturally; `TreeMap` destroys insertion order and requires secondary logging.
- **Architectural Defense**:
  Deploy **`LinkedHashMap`**. Pharmacists perform lookups continually during active daytime care ($118\text{ ns}$ latency). Sacrificing daytime interactive speed by choosing `TreeMap` ($1,300\text{ ns}$) just to save 140 ms on a once-a-day midnight batch report violates workload-aligned systems design.
- **Why `Hashtable` is Obsolete**: Method-level synchronization serializes concurrent threads, nulls are prohibited, and legacy `Enumeration` lacks modern stream and fail-fast iterator support.

### Q5 – Derive a Generic Ranking Component from a Principle
- **Strict Weak Ordering**: Transitivity of order ($A > B \land B > C \implies A > C$) and transitivity of equivalence ($A \sim B \land B \sim C \implies A \sim C$).
- **Complete the Code Output**: `[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]`.
  - Bisons ranks above Panthers because both have 9 points and 4 goal difference, but `"Bisons".compareTo("Panthers") < 0` alphabetically.
- **Generic Design**: Implemented `Leaderboard<T>` with `Comparator<? super T>` (PECS) and defensive copying in `ranking()`. Reused across `Team`, `Player`, and `College`.
- **Faulty Comparator Testing**:
  Demonstrated that integer subtraction `(t2.points - t1.points)` overflows into negative territory when points differ widely (e.g. `Integer.MIN_VALUE`), reversing the comparison and crashing `TimSort` with `IllegalArgumentException`.
- **Alternative 3-Pass Stable Sort**: Proved that sorting 3 times using Java's stable `TimSort` (Name $\to$ GD descending $\to$ Points descending) produces the exact same ranking in $3 \times \mathcal{O}(N \log N)$ time.

### Q6 – Priority, Order and Fairness in a Queue
- **Design Foundations**:
  - Plain FIFO queues cannot prioritize critical Severity-1 emergencies over minor cases without an $\mathcal{O}(N)$ scan.
  - `PriorityQueue` with composite comparator sorts primarily by `severity` ascending.
  - To prevent non-deterministic reordering of equal-severity patients (heap instability), a monotonic sequence counter (`counter++`) enforces strict arrival order.
- **Internal Array vs Polling Order**:
  `PriorityQueue.toString()` outputs the backing binary heap array, which guarantees only parent $\le$ child ($A[i] \le A[2i+1], A[i] \le A[2i+2]$). It is **not sorted**. Only successive `poll()` calls extract elements in true treatment order.
- **Complete the Code Output**: `Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)`.
- **Starvation & Aging Fair Rule**: Designed dynamic aging policy:
  $$\text{EffectiveSeverity} = \max\left(1, \ \text{BaseSeverity} - \left\lfloor \frac{\text{WaitTime}}{\Delta T_{\text{aging}}} \right\rfloor\right)$$
  Guarantees an upper bound on wait time for Severity-5 patients.
- **City Hospital Deployment (5,000 patients/day)**: Verified that 5,000 patients/day requires $< 50\text{ ms}$ of total CPU time per day and $< 250\text{ KB}$ RAM, easily supported.

### Q7 – Day-4 Design Challenge: Diagnose, Redesign and Validate
- **Diagnose**: `PriorityQueue` suffers an $\mathcal{O}(N)$ scan and $\mathcal{O}(N)$ heap rebuild on every dynamic priority aging pass because heaps are not indexed and modifying element priorities breaks the heap invariant.
- **Redesign**: Created `MLFQFairTriageQueue` using:
  1. An array of 5 `ArrayDeque<Patient>` circular buffers.
  2. An active bucket bitmask resolved via CPU hardware instruction `Integer.numberOfTrailingZeros(activeMask)` in 1 clock cycle.
  3. **Monotonic Head-Inspection Aging**: Exploited FIFO monotonicity—the oldest waiting patient in any bucket is always at index 0 (`peekFirst()`). Aging inspects only bucket heads ($\mathcal{O}(1)$ per bucket), terminating immediately when a non-overdue head is seen.
- **Validation**:
  - At $100,000$ mixed operations, the redesign achieved a **$647.85\times$ speedup** (reducing execution time from **$10,046.95\text{ ms}$** down to **$15.51\text{ ms}$**).
  - Throughput rose from $9,953\text{ ops/sec}$ to **$6,448,177\text{ ops/sec}$**.
  - **$64,512,096$ redundant element scans were eliminated**.
  - Verified $100\%$ behavioral equivalence in patient extraction order.

---

## 6. How to Submit to Faculty
1. Rename the local repository / GitHub repository to match your register number:
   ```bash
   # Example:
   # git remote add origin https://github.com/<your-username>/19256055-Kacharagadla-pallavi-java-.git
   # git branch -M main
   # git push -u origin main
   ```
2. Replace `<RegisterNumber>` and your name in this `README.md` and cover page report.
3. Invite the faculty as a collaborator or share the public repository URL.


