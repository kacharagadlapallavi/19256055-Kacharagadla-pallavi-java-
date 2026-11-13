# Programming in Java (CSA09) — Assignments Repository

**Student Name**: Kacharagadla Pallavi  
**Register Number**: 19256055  
**Course**: CSA09 – Programming in Java  
**Repository**: `19256055-Kacharagadla-pallavi-java-`  

---

## Repository Structure

```text
19256055-Kacharagadla-pallavi-java-/
├── README.md                           # Master repository documentation
├── .gitignore                          # Global build artifact exclusions (*.class)
│
├── java_at1/                           # Assignment 1 (CO1: OOP Foundations, Classes, Inheritance, Packages)
│   ├── Task1.java                      # Vehicle Model & Parking Entry
│   ├── Task2.java                      # Parking Fee & Time Calculation
│   ├── Task5.java                      # Parking Slot Allocation & Status
│   ├── Task6.java                      # Floor Capacity Management
│   ├── Task7.java                      # Floor and Facility Management
│   ├── Task9.java                      # Fee Calculation Interface
│   ├── Task11.java                     # Dynamic Hourly Rate & Overstay Surcharge
│   ├── Task12.java                     # Vehicle Inheritance Hierarchy (Car, Bike, Truck)
│   ├── task3/                          # FastFacility & ParkingFacility
│   │   ├── FastFacility.java
│   │   └── ParkingFacility.java
│   ├── task4/                          # Task4 Parking Ticket
│   │   └── Task4.java
│   ├── task 8/                         # Polymorphism & Dynamic Dispatch
│   │   ├── FeeDemo.java
│   │   └── Task8Poly.java
│   └── task 10/                        # Multi-Package Parking Application
│       ├── Main.java
│       ├── ParkingService.java
│       └── ParkingSlot.java
│
└── java_at2/                           # Assignment 2 (CO2: Collections Framework, Generics, Queues, Day-4 Challenge)
    ├── README.md                       # Comprehensive Assignment 2 Technical Report & Mark Distribution (50 Marks)
    ├── run_all.bat                     # Automated script to compile & run all Q1-Q7 tasks
    ├── Q1/                             # Task 1: Building Text Under Load (String vs StringBuilder vs StringBuffer)
    ├── Q2/                             # Task 2: When Is ArrayList an Appropriate Choice? (ArrayList vs LinkedList vs ArrayDeque)
    ├── Q3/                             # Task 3: Engineer the hashCode-equals Contract (Event Attendance De-duplication)
    ├── Q4/                             # Task 4: Choosing a Map for Pharmacy Inventory (HashMap vs TreeMap vs LinkedHashMap)
    ├── Q5/                             # Task 5: Generic Sports Leaderboard Ranking Component from a Principle
    ├── Q6/                             # Task 6: Emergency Hospital Triage (PriorityQueue, FIFO Fairness & Dynamic Aging)
    └── Q7/                             # Task 7: Day-4 Design Challenge (MLFQ Multi-Bucket Bitmask Queue Redesign - 647x Speedup)
```

---

## Assignment Summaries

### 1. `java_at1` — Assignment 1 (CO1)
Contains Tasks 01 through 12 covering foundational Object-Oriented Programming (OOP) concepts in Java:
- Class design, encapsulation, constructors, and field accessibility.
- Single, multi-level, and hierarchical inheritance (`Vehicle`, `Car`, `EBike`, `Truck`).
- Polymorphic dynamic dispatch and interface implementations (`Chargeable`).
- Package modularization (`parking.app`, `parking.service`, `parking.model`).

### 2. `java_at2` — Assignment 2 (CO2)
Contains Tasks Q1 through Q7 covering advanced Core Java topics:
- **Strings & Text Building Under Load (Q1)**: Comparative analysis proving $\mathcal{O}(n^2)$ character copying degradation of `String (+)` under load vs $\mathcal{O}(n)$ efficiency of `StringBuilder`.
- **Lists & Cache Locality (Q2)**: Benchmarked `ArrayList` vs `LinkedList` vs `ArrayDeque`. Demonstrated that `ArrayList` is **$98\times$ faster** than `LinkedList` on middle insertions due to CPU cache locality and SIMD vector memory block moves (`System.arraycopy`).
- **The hashCode–equals Contract (Q3)**: Diagnosed duplicate attendance bug in `HashSet<Student>`. Completed TODO 1 overriding `hashCode()`, demonstrating output change from `Unique students: 3` to `Unique students: 2`. Implemented 3 designed test suites including mutation orphaning.
- **Maps for Pharmacy Stock (Q4)**: Evaluated `HashMap`, `TreeMap`, and `LinkedHashMap` over 100,000 lookups. Defended `LinkedHashMap` for production to sustain $118\text{ ns}$ daytime lookups while servicing once-daily evening reports.
- **Generic Leaderboard Ranking Component (Q5)**: Converted the Strict Weak Ordering principle into a generic `Leaderboard<T>` component with `Comparator<? super T>`. Completed TODO 1 and 2. Tested integer subtraction overflow and proved equivalence of 3-pass stable `TimSort`.
- **Priority, Order and Fairness in Queues (Q6)**: Hospital emergency triage implementing arrival sequence counter for FIFO stability. Designed dynamic priority aging to prevent patient starvation.
- **Day-4 Design Challenge (Q7)**: Diagnosed bottleneck in `PriorityQueue` under aging ($\mathcal{O}(N)$ scan and $\mathcal{O}(N)$ heap rebuild). Redesigned into `MLFQFairTriageQueue` using 5 `ArrayDeque` buckets and CPU bitmask hardware instruction (`tzcnt`), achieving a **$647.85\times$ speedup** at 100,000 operations with zero redundant scans.

For detailed compile and run steps, empirical benchmark tables, and mathematical derivations for Assignment 2, please consult [`java_at2/README.md`](java_at2/README.md).

