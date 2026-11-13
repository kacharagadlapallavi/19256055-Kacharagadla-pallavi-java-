# Task 03: Engineer the hashCode–equals Contract (Event Attendance De-duplication)

## 1. Problem Context
A college fest records student attendance by scanning barcodes on student ID cards across multiple entry gates. Because students may enter, exit, and re-scan at different gates, a `HashSet<Student>` is used to filter duplicates and maintain an accurate count of unique attendees.

A junior developer implemented the following `Student` class:
```java
class Student {
    String regNo; String name;
    Student(String regNo, String name) { this.regNo = regNo; this.name = name; }
    public boolean equals(Object o) {
        if (!(o instanceof Student)) return false;
        return regNo.equals(((Student) o).regNo);
    }
    // hashCode() is NOT overridden
}
```
This investigation analyzes the catastrophic failure modes of violating the `hashCode–equals` contract, executes three deliberately designed test suites, resolves the "Complete the Code" challenge, and compares alternative de-duplication architectures (`TreeSet`, `HashMap`).

---

## 2. Complete the Code Verification

### 2.1 Code Before Completing TODO 1 (`Q3/AttendanceIncomplete.java`)
```java
// hashCode() is omitted; relies on inherited Object.hashCode()
```
- **Observed Output**:
  ```text
  Unique students: 3
  ```
- **Explanation**: Two separate instances representing Arun (`new Student("192311001", "Arun")`) were allocated on the heap at Gate 1 and Gate 3. Because `hashCode()` was not overridden, each instance inherited `Object.hashCode()` (based on `System.identityHashCode`), returning distinct values (`2124308362` vs `1482968390`). The two entries mapped to different hash buckets; `equals()` was never called, and both duplicate records were inserted.

### 2.2 Code After Completing TODO 1 (`Q3/Attendance.java`)
```java
@Override
public int hashCode() {
    return Objects.hashCode(regNo);
}
```
- **Observed Output**:
  ```text
  Unique students: 2
  ```
- **Explanation**: Both Arun instances now return the exact same hash code (`822376264`). They map to the exact same hash bucket. Upon collision, `HashSet` invokes `equals()`, which evaluates to `true` (matching `regNo`). The second insertion is identified as a duplicate and rejected.

---

## 3. Three Deliberately Designed Tests

| Test ID | Objective / Scenario | Predicted Set Size | Observed Set Size | Mechanism / Reason |
| :--- | :--- | :--- | :--- | :--- |
| **Test 1** | **Apparent Success (False Security)**<br>1. Scans of all-unique students.<br>2. Same object reference added twice. | Case 1: **3**<br>Case 2: **1** | Case 1: **3**<br>Case 2: **1** | **Case 1**: When no duplicates exist, the set size equals scan count.<br>**Case 2**: When the exact same reference `arun` is re-added, `HashSet` short-circuits on reference identity `(p.key == key)` before checking `equals()`. This creates a dangerous false impression that de-duplication is functional. |
| **Test 2** | **Duplicate Exposure**<br>Two distinct instances of Arun (`192311001`) scanned at Gate 1 & 3, plus Divya (`192311002`). | Uncorrected: **3**<br>Corrected: **2** | Uncorrected: **3**<br>Corrected: **2** | Default `Object.hashCode()` derives from JVM object identity. Different heap allocations yield distinct hash buckets. In the uncorrected code, `s1.hashCode() != s3.hashCode()`, bypassing `equals()`. Overriding `hashCode()` on `regNo` fixes this. |
| **Test 3** | **Mutation After Insertion & TreeSet Inconsistency**<br>**Part A**: Mutating `regNo` of an already-inserted student.<br>**Part B**: `TreeSet` with `Comparator` inconsistent with `equals()`. | Part A:<br>`contains(old)`: **false**<br>`contains(new)`: **false**<br>Size: **1**<br><br>Part B:<br>Size: **1** | Part A:<br>`contains(old)`: **false**<br>`contains(new)`: **false**<br>Size: **1**<br><br>Part B:<br>Size: **1** | **Part A**: Modifying `regNo` changes the object's hash code while it remains trapped in its original bucket. Searching for the old key fails `equals()`; searching for the new key checks a different bucket where the object does not exist. The object becomes permanently orphaned and un-removable.<br>**Part B**: `TreeSet` checks equality exclusively via `comparator.compare() == 0`, ignoring `equals()`. A comparator comparing name length considers Arun (4) and Divy (4) identical, dropping an entirely unique student! |

---

## 4. Deep Inquiry Answers

### 4.1 How `HashSet` Uses `hashCode()` and `equals()` to Locate an Element
Under the hood, `HashSet<E>` is a facade over `HashMap<E, Object>`. When `add(e)` or `contains(e)` is invoked:
1. **Hash Computation & Bit Spreading**:
   $$\text{hash} = \text{key.hashCode}() \oplus (\text{key.hashCode}() \gg 16)$$
   This spreads higher-order bits into lower-order positions to reduce collisions in small tables.
2. **Bucket Index Calculation**:
   $$\text{index} = (n - 1) \ \& \ \text{hash}$$
   Where $n$ is the table power-of-two capacity ($16, 32, 64, \dots$).
3. **Bucket Inspection**:
   - If the bucket table `tab[index]` is null, the element is inserted directly.
   - If occupied, the JVM traverses the collision chain (linked list or Red-Black Tree):
     ```java
     if (p.hash == hash && ((k = p.key) == key || (key != null && key.equals(k))))
     ```
4. **The Contract Dependency**:
   Notice that `key.equals(k)` is evaluated **ONLY IF** `p.hash == hash`. If `hashCode()` is not overridden, two objects that are logically equal under `equals()` will have different hash codes, mapping to different buckets or failing the fast integer check `p.hash == hash`. Consequently, `equals()` is never invoked!

### 4.2 What Happens When a Key Field is Mutated After Insertion?
If a mutable field used in `hashCode()` (e.g., `regNo`) is modified after insertion:
1. The entry remains stored in the bucket determined by its **original hash code**.
2. When `set.contains(modifiedObject)` is called, the JVM computes a **new bucket index** based on the updated field value and searches the wrong bucket. It returns `false`.
3. When searching using the old value, the JVM looks in the original bucket, but `equals()` fails because the object's field has changed.
4. Calling `set.remove(modifiedObject)` fails for the same reason. The object cannot be found, cannot be removed, yet continues to consume heap memory and is returned during iteration. This creates a severe **silent memory leak**.

### 4.3 How to Correct the `Student` Class and Verify the Correction
1. **Enforce Immutability**:
   Declare key identifying fields as `private final String regNo`. Immutability guarantees that hash codes remain invariant over the lifetime of the object.
2. **Override Both `equals()` and `hashCode()` Consistently**:
   ```java
   @Override
   public boolean equals(Object o) {
       if (this == o) return true;
       if (!(o instanceof Student)) return false;
       return Objects.equals(regNo, ((Student) o).regNo);
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(regNo);
   }
   ```
3. **Verification**:
   - Write automated unit tests verifying:
     - Reflexivity: `a.equals(a) == true`
     - Symmetry: `a.equals(b) == b.equals(a)`
     - Transitivity: `a.equals(b) && b.equals(c) => a.equals(c)`
     - Consistency: `a.hashCode() == b.hashCode()` whenever `a.equals(b)`

---

## 5. Alternative Concept & Complexity Analysis

If de-duplication were implemented using `TreeSet<Student>` or `HashMap<String, Student>`:

### 5.1 Architecture Comparison

1. **`TreeSet<Student>` with `Comparator.comparing(Student::getRegNo)`**:
   - Stores elements in a self-balancing Red-Black Tree.
   - De-duplication is determined solely by `comparator.compare(s1, s2) == 0`.
   - **Advantage**: Elements are automatically kept sorted by registration number at all times.
   - **Trade-off**: Requires $O(\log N)$ comparisons per scan rather than $O(1)$ hashing.
2. **`HashMap<String, Student>` Keyed by `regNo`**:
   - `map.putIfAbsent(student.getRegNo(), student)`.
   - Delegates hashing to Java's built-in `java.lang.String`, which has an immutable, cached, and thoroughly tested `hashCode()`.
   - **Advantage**: Completely isolates de-duplication correctness from user errors in domain model classes (`Student`).
   - **Trade-off**: Stores redundant key references in the map entry.

### 5.2 Theoretical Complexity & Resource Trade-Offs

| Data Structure | Time Complexity (Average) | Time Complexity (Worst-Case) | Memory Overhead per Unique Entry | Contract Dependency |
| :--- | :--- | :--- | :--- | :--- |
| **`HashSet<Student>`** | $\mathcal{O}(1)$ | $\mathcal{O}(\log N)$ (Treeified buckets) | $\approx 32\text{--}40$ bytes (`Node` with hash, key, value, next) | Requires strict consistency between `equals()` and `hashCode()`. |
| **`TreeSet<Student>`** | $\mathcal{O}(\log N)$ | $\mathcal{O}(\log N)$ | $\approx 48$ bytes (`Entry` with left, right, parent, color, key, val) | Requires `compareTo` / `Comparator` consistent with `equals()`. |
| **`HashMap<String, Student>`** | $\mathcal{O}(1)$ | $\mathcal{O}(\log N)$ | $\approx 40\text{--}48$ bytes (`Node` + `String` key reference) | Relies on `String`'s immutable hash code. Safest against junior developer error. |

---

## 6. Compilation and Execution

To compile and run from the repository root:
```bash
# Compile all Q3 classes
javac -d . Q3/AttendanceIncomplete.java Q3/Attendance.java Q3/ContractInvestigation.java

# 1. Run Complete the Code (Before TODO 1)
java -cp . Q3.AttendanceIncomplete

# 2. Run Complete the Code (After TODO 1)
java -cp . Q3.Attendance

# 3. Run Comprehensive Contract Investigation Suite
java -cp . Q3.ContractInvestigation
```
Execution logs are archived in `Q3/output_incomplete.txt`, `Q3/output_complete.txt`, and `Q3/output_investigation.txt`.

