# Assignment 5 – CO5: Data Persistence Engineering (Files and Databases)

**Student Name:** Moksha Dude (dudemoksha)  
**Register Number:** 192372374  
**Course:** Programming in Java (CSA09)  
**Course Outcome:** CO5  
**Java Version:** OpenJDK 17.0.21.0.2 LTS  

---

## 📌 Overview
This assignment evaluates architectural design and practical implementation decisions in Java data persistence. It contrasts text files, binary streams, object serialization, and relational databases using JDBC. Focus areas include stream buffering, DAO patterns, object-graph serialization, SQL injection defense, batch loading, transaction atomicity (`commit`/`rollback`), and enterprise migration.

---

## 📁 Directory Structure

```
Assignment5/
├── PartA_Design/
│   └── PartA_PersistenceDesign.md  # Record layouts, pseudocode, DAO & security analysis
├── TaskB1_FileStore/
│   └── PassStore.java              # File store with BufferedReader & damaged line tolerance
├── TaskB2_Adversarial/
│   └── AdversarialSecurityDemo.java # SQL injection exploit vs PreparedStatement defense
├── TaskB3_BatchTransactions/
│   └── PassLoader.java             # Batch insert & atomic transaction rollback
├── TaskB4_FinalDecision/
│   └── EnterpriseMigrationDemo.java # Multi-campus migration audit & persistence decision
└── README.md
```

---

## 🚀 Compilation and Execution Instructions

From the `Assignment5` directory:

```bash
# Task B1: File Store & Stream Buffering
javac TaskB1_FileStore/PassStore.java
java TaskB1_FileStore.PassStore

# Task B2: Adversarial SQL Injection & Malformed File Test
javac TaskB2_Adversarial/AdversarialSecurityDemo.java
java TaskB2_Adversarial.AdversarialSecurityDemo

# Task B3: Batch Processing & Transaction Rollback
javac TaskB3_BatchTransactions/PassLoader.java
java TaskB3_BatchTransactions.PassLoader

# Task B4: Enterprise Migration & Final Architecture Evaluation
javac TaskB4_FinalDecision/EnterpriseMigrationDemo.java
java TaskB4_FinalDecision.EnterpriseMigrationDemo
```

---

## 📋 Assessment Components & Engineering Findings

| Component | Task | Bloom's Level | Key Engineering Finding & Result |
|---|---|---|---|
| **Part A** | A1: File Format Design | K4 – Analyse | Designed delimited CSV (with RFC-4180 quotes) and fixed binary layout with algorithmic pseudocode for Add, Find, and List operations. |
| **Part A** | A2: Buffered I/O Design | K4 – Analyse | Demonstrated that unbuffered byte reads trigger 52+ million kernel transitions on a 50MB file; `BufferedInputStream` reduces this to 6,400 chunked reads. |
| **Part A** | A3: Data-Access Pattern | K4 – Analyse | Formulated generic `Dao<T, K>` interface, eliminating duplicated boilerplate code and isolating SQL schema changes. |
| **Part A** | A4: Serialization Design | K4 – Analyse | Identified non-serializable runtime handles (`Connection`) and sensitive data (`password`) requiring `transient`; declared `serialVersionUID` for backward compatibility. |
| **Part A** | A5: Transactions & Injection | K4 – Analyse | Designed atomic two-phase wallet transfer with rollback; analyzed tautology injection (`' OR '1'='1`) and parameterized defense. |
| **Part B** | B1: Instrumented File Store | K4 – Analyse | Completed `PassStore.java` with fault-tolerant scanning that bypasses corrupt lines without crashing. |
| **Part B** | B2: Adversarial Inputs | K4 – Analyse | Validated that `PreparedStatement` binds adversarial characters strictly as data tokens, neutralizing SQL injection attempts. |
| **Part B** | B3: Batch vs PreparedStatement | K4 – Analyse | Completed `PassLoader.java`. Batching 1,000 rows in single transactions yields 100x throughput speedups over single-row auto-commit inserts, with guaranteed atomicity on failure. |
| **Part B** | B4: Final Architecture Decision | K5 – Evaluate | Implemented migration pipeline with error auditing. Recommended enterprise RDBMS for 12 campuses to support concurrent counters, row-level locking, and ACID compliance. |
