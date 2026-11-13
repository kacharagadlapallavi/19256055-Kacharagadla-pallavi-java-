# Task 05: Derive a Generic Ranking Component from a Principle (Sports Leaderboard)

## 1. Problem Context & The Foundational Principle
An inter-collegiate athletic meet requires a robust ranking system for team standings. Teams are ranked by:
1. **Total Points** (higher ranks first).
2. **Goal Difference** (higher ranks first) to break point ties.
3. **Team Name** (alphabetical / lexicographical $A\to Z$) as the definitive tie-breaker.

The ranking component must be completely reusable across future tournaments for ranking **Players**, **Colleges**, and **Academic Quiz Teams**.

### The Principle
> *"A ranking is correct only if the comparison used is consistent: if A ranks above B and B ranks above C, then A must rank above C, and equal items must compare as equal."*

This principle is the mathematical definition of a **Strict Weak Ordering**, which is the foundational precondition for Java's sorting algorithms (`TimSort`, `DualPivotQuicksort`, `PriorityQueue`, `TreeSet`).

---

## 2. Deriving the Architectural Design

### 2.1 `Comparable<T>` vs. Separate `Comparator<T>`
- **Why `Comparable<T>` is Insufficient**:
  - `Comparable<T>` imposes an intrusive, single "natural ordering" directly inside the class definition.
  - Violates the **Single Responsibility Principle** and **Open/Closed Principle**: A domain class like `Team` or `Player` should not be tightly coupled to a specific tournament ranking policy. Different competitions may rank teams by Head-to-Head record, Fair Play disciplinary points, or Win Percentage.
- **Why Separate `Comparator<T>` is Superior**:
  - Decouples ranking logic from entity data.
  - Allows constructing composable, interchangeable ranking strategies that can be passed dynamically into `Leaderboard<T>`.

### 2.2 Expressing Multi-Field Comparisons
Using Java 8+ fluent factory methods:
```java
Comparator<Team> byRank = Comparator
    .comparingInt((Team t) -> t.points).reversed()
    .thenComparing(Comparator.comparingInt((Team t) -> t.goalDiff).reversed())
    .thenComparing(t -> t.name);
```
- `comparingInt(...).reversed()`: Primary sort descending.
- `thenComparing(...)`: Secondary and tertiary sort criteria evaluated only when prior comparisons yield $0$.

### 2.3 Generics & Wildcard Variance (`Comparator<? super T>`)
The leaderboard class is declared as:
```java
class Leaderboard<T> {
    private final List<T> items = new ArrayList<>();
    private final Comparator<? super T> order;
    ...
}
```
- **The PECS Principle (Producer Extends, Consumer Super)**:
  - The comparator is a **consumer** of objects of type `T` (it reads two `T` instances and returns an integer).
  - Using `Comparator<? super T>` allows a comparator defined for a base class or interface (e.g., `Comparator<NamedEntity>` comparing `getName()`) to rank any subclass (`Leaderboard<Player>`, `Leaderboard<College>`) without requiring casts or adapter code.

---

## 3. Complete the Code Verification

### 3.1 Solution Implementation (`Q5/LeaderboardDemo.java`)
```java
class Leaderboard<T> {
    private final List<T> items = new ArrayList<>();
    private final Comparator<? super T> order;

    Leaderboard(Comparator<? super T> order) { this.order = order; }
    void add(T item) { items.add(item); }

    List<T> ranking() {
        // TODO 1: Return a new sorted copy of items using order, without modifying items
        List<T> sortedCopy = new ArrayList<>(items);
        sortedCopy.sort(order);
        return sortedCopy;
    }
}

public class LeaderboardDemo {
    public static void main(String[] args) {
        // TODO 2: Build the comparator: points (high first), then goal difference (high
        // first), then name (A–Z)
        Comparator<Team> byRank = Comparator
                .comparingInt((Team t) -> t.points).reversed()
                .thenComparing(Comparator.comparingInt((Team t) -> t.goalDiff).reversed())
                .thenComparing(t -> t.name);

        Leaderboard<Team> board = new Leaderboard<>(byRank);
        board.add(new Team("Panthers", 9, 4));
        board.add(new Team("Eagles", 9, 6));
        board.add(new Team("Cobras", 6, 1));
        board.add(new Team("Bisons", 9, 4));
        System.out.println(board.ranking());
    }
}
```

### 3.2 Expected vs Observed Output
- **Expected Output**:
  ```text
  [Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]
  ```
- **Observed Output**:
  ```text
  [Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]
  ```
- **Why Bisons is Ranked Above Panthers**:
  1. **Points Check**: `Eagles` has 9 pts, `Bisons` has 9 pts, `Panthers` has 9 pts, `Cobras` has 6 pts. Cobras is ranked lowest.
  2. **Goal Difference Check**: Among the 9-point teams, `Eagles` has $\text{GD}=6$, so Eagles ranks #1. Both `Bisons` and `Panthers` have $\text{GD}=4$, resulting in a tie.
  3. **Name Check**: The comparator falls through to the tertiary tie-breaker `thenComparing(t -> t.name)`. `"Bisons".compareTo("Panthers")` returns a negative integer because `'B' < 'P'` in ASCII/Unicode. Therefore, Bisons is definitively placed above Panthers.

---

## 4. Test Design & Correctness Inquiry

### 4.1 Proof that the Comparator Satisfies the Principle
The principle requires consistency:
1. **Irreflexivity**: `compare(A, A) == 0`. Guaranteed because `Integer.compare(p, p) == 0`, `Integer.compare(gd, gd) == 0`, and `name.compareTo(name) == 0`.
2. **Antisymmetry**: `sgn(compare(A, B)) == -sgn(compare(B, A))`. Each chained comparator invokes `Integer.compare` or `String.compareTo`, which strictly adhere to mathematical sign inversion.
3. **Transitivity**: If $A > B$ and $B > C$, then $A > C$. Lexicographical composite comparison is proven transitive by mathematical induction: at whichever level the first non-zero difference occurs, transitivity of integers/strings dictates the overall relation.
4. **Transitivity of Equivalence**: If $A \sim B$ and $B \sim C$, then $A \sim C$. If all three fields match, both objects are identically equal.

### 4.2 Deliberately Designed Tests Breaking Faulty Comparators

#### Flaw A: Integer Subtraction Overflow
- **Faulty Code**: `(t1, t2) -> (t2.points - t1.points)`
- **Vulnerability**: If `t2.points` is a large positive integer and `t1.points` is negative (or points differ by $> 2^{31}-1$), integer subtraction overflows.
- **Observed Evidence**:
  - Comparing `PenaltyTeam (-2,147,483,643 pts)` vs `SuperTeam (2,147,483,642 pts)`.
  - Subtraction yields `-11` (positive overflow in 32-bit arithmetic), falsely asserting that `PenaltyTeam` ranks above `SuperTeam`!
  - Under `List.sort()`, this overflow triggers `java.lang.IllegalArgumentException: Comparison method violates its general contract!`.
  - **Resolution**: `Comparator.comparingInt()` uses `Integer.compare(x, y)`, which performs sign comparisons without subtraction overflow.

#### Flaw B: Missing Tie-Breaker
- **Faulty Code**: Comparing only `points` and `goalDiff`, omitting `name`.
- **Observed Evidence**:
  - `Alpha(10,5)` and `Zulu(10,5)` evaluate to `compare() == 0`, yet `equals()` is false.
  - If stored in a `TreeSet<Team>`, `Zulu` is permanently dropped as a duplicate!

---

## 5. Domain Reusability & Alternative Concept Analysis

### 5.1 Multi-Domain Reusability Demonstration
In `Q5/GenericLeaderboardTest.java`, the exact same `Leaderboard<T>` class was deployed without modification for:
- **Players**: Goals (high), Assists (high), Name ($A\to Z$).
  - Result: `[Ronaldo[G:28,A:6], Neymar[G:25,A:20], Messi[G:25,A:18]]`
- **Colleges**: Gold Medals (high), Silver Medals (high), Name ($A\to Z$).
  - Result: `[IISc Bangalore(Au:18,Ag:5), NIT Trichy(Au:15,Ag:12), IIT Madras(Au:15,Ag:10)]`

### 5.2 Alternative Concept: 3-Pass Stable Sort
Instead of a single composite comparator, sort the list three consecutive times using **stable sorts** from least significant field to most significant field:
```java
// Pass 1: Name (A-Z)
teams.sort(Comparator.comparing(t -> t.name));
// Pass 2: Goal Difference (High to Low)
teams.sort(Comparator.comparingInt((Team t) -> t.goalDiff).reversed());
// Pass 3: Points (High to Low)
teams.sort(Comparator.comparingInt((Team t) -> t.points).reversed());
```
- **Correctness**:
  Java's `List.sort()` is implemented as **TimSort** (an adaptive merge sort variant), which is mathematically guaranteed to be **stable** (it never reorders elements with equal keys).
  Because Pass 3 is stable, ties in points preserve the order established in Pass 2. Because Pass 2 is stable, ties in goal difference preserve the alphabetical order established in Pass 1.
  - **Result**: Exactly matches `[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]`.
- **Complexity Comparison**:

| Approach | Time Complexity | Extra Space | Code Maintainability / Reusability |
| :--- | :--- | :--- | :--- |
| **Composite Comparator Chain** | $1 \times \mathcal{O}(N \log N)$ | $\mathcal{O}(N)$ (for sorted copy) | **Highest**: Declarative, single pass, easily encapsulated in a factory method. |
| **3-Pass Stable Sort** | $3 \times \mathcal{O}(N \log N)$ | $\mathcal{O}(N)$ (in-place on copy) | **Lower**: Requires $3\times$ sort passes; error-prone if an un-stable sort (e.g. Dual-Pivot Quicksort) is used. |
| **`Comparable<Team>`** | $1 \times \mathcal{O}(N \log N)$ | $\mathcal{O}(N)$ | **Lowest**: Rigid, cannot support players or colleges without duplicating ranking logic. |

---

## 6. Compilation and Execution

To compile and execute from the repository root:
```bash
# Compile
javac -d . Q5/LeaderboardDemo.java Q5/GenericLeaderboardTest.java

# Run Complete the Code Demo
java -cp . Q5.LeaderboardDemo

# Run Comprehensive Validation & Faulty Comparator Test Suite
java -cp . Q5.GenericLeaderboardTest
```
Outputs are captured in `Q5/output_demo.txt` and `Q5/output_test.txt`.

