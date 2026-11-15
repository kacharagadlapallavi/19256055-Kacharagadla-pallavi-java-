# Assignment 3 – CO3: Exception Handling & Multithreading

**Student Name:** Moksha Dude (dudemoksha)  
**Register Number:** 192372374  
**Course:** Programming in Java (CSA09)  
**Course Outcome:** CO3  
**Java Version:** OpenJDK 17.0.21.0.2 LTS  
**Mode:** Pair Work / Individual Execution Verification  

---

## 📌 Overview
This assignment explores robust, enterprise-grade fault tolerance and concurrent programming in Java. It covers the Throwable hierarchy, checked vs unchecked exceptions, custom chained exceptions, resource leakage prevention with try-with-resources, thread lifecycles, race condition synchronization, Coffman deadlock elimination, inter-thread wait/notify coordination, and the Executor framework with Callable/Future.

---

## 📁 Directory Structure

```
Assignment3/
├── Q1/   # ATM Failure Handling (try-catch-finally, checked vs unchecked)
│   └── BankAtmSimulation.java
├── Q2/   # Custom Exceptions & Chaining (Online Exam Portal)
│   └── StorageDemo.java
├── Q3/   # try-with-resources & Suppressed Exceptions (Daily Sales Reporter)
│   └── SalesReportGenerator.java
├── Q4/   # Thread Life Cycle & State Tracing (Answer-Script Scanner)
│   └── AnswerScriptScanner.java
├── Q5/   # Critical Section Synchronization (Cinema Seat Booking)
│   └── SeatBooking.java
├── Q6/   # Deadlock Prevention & Producer-Consumer (Token Counter)
│   ├── DeadlockDemo.java
│   └── TokenCounter.java
└── Q7/   # First-Principles Concurrent Fault-Tolerant Processor
    └── SemesterResultProcessor.java
```

---

## 🚀 Compilation and Execution Instructions

From the `Assignment3` directory:

```bash
# Q1: Bank ATM Exception Simulation
javac Q1/BankAtmSimulation.java
java Q1.BankAtmSimulation

# Q2: Online Exam Portal Storage Exception Chaining
javac Q2/StorageDemo.java
java Q2.StorageDemo

# Q3: Sales Report try-with-resources Safety
javac Q3/SalesReportGenerator.java
java Q3.SalesReportGenerator

# Q4: Multithreaded Scanner Stations
javac Q4/AnswerScriptScanner.java
java Q4.AnswerScriptScanner

# Q5: Cinema Seat Booking Race Condition
javac Q5/SeatBooking.java
java Q5.SeatBooking

# Q6: Hostel Wallet Transfer & Mess Token Counter
javac Q6/DeadlockDemo.java Q6/TokenCounter.java
java Q6.DeadlockDemo
java Q6.TokenCounter

# Q7: First-Principles Concurrent Result Processor
javac Q7/SemesterResultProcessor.java
java Q7.SemesterResultProcessor
```

---

## 📋 Individual Contribution & Assessment Summary

| Question | Assessed Area | Student A Responsibility | Student B Responsibility | Engineering Conclusion |
|---|---|---|---|---|
| **Q1** | ATM Exception Hierarchy | Exception Hierarchy & Catch Ordering | Test cases & Stack trace analysis | Catch narrow exceptions before broad ones; finally guarantees cleanup even under uncaught exceptions. |
| **Q2** | User-Defined Exceptions & Chaining | Exception Hierarchy & Propagation | Chaining evidence with `getCause()` | Wrapped `IOException` into `SubmissionStorageException` preserving underlying root cause. |
| **Q3** | Resource Safety & TWR | Debugging & Leak Detection | AutoCloseable design & Suppressed exceptions | `try-with-resources` closes all resources deterministically in reverse order of declaration. |
| **Q4** | Thread Life Cycle | Thread vs Runnable & Life cycle states | Scheduling experiments & priority tests | Calling `start()` spawns native OS threads; `run()` executes synchronously on the caller thread. |
| **Q5** | Race Conditions & Critical Sections | Race reproduction & Interleaving analysis | Synchronization benchmarks & Locking scope | Atomic `synchronized` booking prevents check-then-act race conditions that cause double-selling. |
| **Q6** | Deadlock & Producer-Consumer | Inter-thread communication (`wait`/`notifyAll`) | Deadlock reproduction & Global lock ordering | Strict resource ID ordering eliminates circular wait; `while` loop prevents spurious wakeups. |
| **Q7** | Concurrent Fault-Tolerant Processor | Architecture design (`ExecutorService`, `Future`) | Implementation, fault injection & benchmarking | FixedThreadPool with `Callable`/`Future` isolates individual task failures and delivers scalable speedup. |
