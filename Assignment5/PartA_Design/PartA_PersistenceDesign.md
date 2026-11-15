# Assignment 5 Part A: Persistence Design & Algorithmic Pseudocode

**Student Name:** Moksha Dude (dudemoksha)  
**Register Number:** 192372374  
**Course:** Programming in Java (CSA09)  
**Course Outcome:** CO5  

---

## TASK A1 – DESIGN A FILE FORMAT (Transport Office Bus-Pass Records)

### A1.1 Record Layout Representation

#### (a) Delimited Text File Layout
- **Delimiter:** Comma `,` with CSV standard RFC-4180 quotation for fields containing commas.
- **Field Order:** `PassID,RegisterNumber,StudentName,RouteNumber,BoardingStop,ValidityDate,AmountPaid`
- **Example Record:**
  ```csv
  P1001,192372374,"Dude, Moksha",14,Poonamallee,2027-05-31,4500.00
  ```
- **Handling Delimiters in Text:** If a student's name contains a comma (e.g. `"Dude, Moksha"`), the field is enclosed in double quotes. Any escaped quote inside is represented as `""`.

#### (b) Binary File Layout (`DataOutputStream`)
- Fixed-order binary serialization using primitive wrappers:
  1. `PassID`: `writeUTF(String)` (2-byte length prefix + modified UTF-8 bytes)
  2. `RegisterNumber`: `writeUTF(String)`
  3. `StudentName`: `writeUTF(String)`
  4. `RouteNumber`: `writeInt(int)` (4 bytes Big-Endian)
  5. `BoardingStop`: `writeUTF(String)`
  6. `ValidityDate`: `writeLong(long)` (8-byte epoch millis)
  7. `AmountPaid`: `writeDouble(double)` (8 bytes IEEE 754)

---

### A1.2 Design the Operations (Algorithmic Pseudocode)

```
ALGORITHM AddPass(passRecord, filePath)
    INPUT: passRecord object, filePath string
    OUTPUT: Boolean indicating success
BEGIN
    OPEN file AT filePath IN APPEND MODE USING BufferedWriter
    FORMAT line = passRecord.id + "," + passRecord.regNo + ",\"" + 
                  passRecord.name + "\"," + passRecord.route + "," + 
                  passRecord.stop + "," + passRecord.validity + "," + 
                  passRecord.amount
    WRITE line TO file
    WRITE NEWLINE TO file
    CLOSE file
    RETURN TRUE
END
```

```
ALGORITHM FindPassById(targetId, filePath)
    INPUT: targetId string, filePath string
    OUTPUT: passRecord object OR NULL
BEGIN
    IF NOT EXISTS(filePath) THEN
        THROW FileNotFoundException
    END IF

    OPEN file AT filePath IN READ MODE USING BufferedReader
    WHILE (line = READLINE(file)) IS NOT NULL DO
        fields = PARSE_CSV(line)
        IF LENGTH(fields) != 7 THEN
            CONTINUE  // Skip corrupt / damaged record
        END IF
        IF fields[0] == targetId THEN
            CLOSE file
            RETURN CREATE_RECORD(fields)
        END IF
    END WHILE
    CLOSE file
    RETURN NULL
END
```

```
ALGORITHM ListPassesByRoute(targetRoute, filePath)
    INPUT: targetRoute integer, filePath string
    OUTPUT: List of matching pass records
BEGIN
    INITIALIZE resultList AS EMPTY LIST
    OPEN file AT filePath USING BufferedReader
    WHILE (line = READLINE(file)) IS NOT NULL DO
        fields = PARSE_CSV(line)
        IF LENGTH(fields) == 7 AND PARSE_INT(fields[3]) == targetRoute THEN
            APPEND CREATE_RECORD(fields) TO resultList
        END IF
    END WHILE
    CLOSE file
    RETURN resultList
END
```

---

## TASK A2 – DON’T READ WHAT YOU DON’T NEED

### A2.1 Prediction of Single-Byte Read Cost
- When reading a 50 MB log byte-by-byte using `FileInputStream.read()`, the JVM makes **52,428,800 individual system calls** to the kernel `read()` function.
- Each invocation triggers a user-to-kernel mode context switch and CPU privilege transition, incurring massive latency (~100x slower).

### A2.2 Redesign Using Buffering
- Wrapping with `BufferedInputStream` allocates an internal 8 KB byte buffer (`byte[8192]`).
- The OS fetches 8,192 bytes in a single hardware block transfer. The subsequent 8,191 calls to `read()` are satisfied directly from high-speed CPU memory without kernel transitions.
- Total system calls plummet from 52.4 million to only **6,400**.

### A2.3 When Buffering Does Not Help
- Buffering provides negligible benefit when reading tiny files (e.g. < 512 bytes) or when performing truly random, non-sequential seeks (e.g. RandomAccessFile seeking across distant blocks).

---

## TASK A3 – DATA-ACCESS DESIGN PATTERN (DAO)

### Generic DAO Interface
```java
public interface Dao<T, K> {
    void insert(T entity) throws SQLException;
    T findById(K id) throws SQLException;
    List<T> findAll() throws SQLException;
    void update(T entity) throws SQLException;
    void delete(K id) throws SQLException;
}
```

### Mapping Student Entity to SQL Table
- `Student.regNo` (String) $\to$ `reg_no VARCHAR(20) PRIMARY KEY`
- `Student.name` (String) $\to$ `student_name VARCHAR(100) NOT NULL`
- `Student.dept` (String) $\to$ `department VARCHAR(50)`
- `Student.cgpa` (double) $\to$ `cgpa DECIMAL(3,2)`

---

## TASK A4 – WHAT SHOULD BE SERIALIZED?

### A4.1 In-Memory Persistence & `transient` Modifiers
- **Marked `transient`:**
  - `Connection dbConnection`: Database connections are OS-bound TCP sockets holding runtime socket file descriptors that cannot survive process shutdown.
  - `String password`: Plaintext passwords must not be persisted to disk to avoid credential exposure.
- **Object Graph Traversal:** Serializing the root `Hostel` object traverses references recursively to serialize all linked `Room` and `Resident` objects, creating a unified object graph snapshot.

### A4.2 Schema Evolution & `serialVersionUID`
- Declaring `private static final long serialVersionUID = 1L;` guarantees compatibility across JVM class changes. If a new field (`phoneNumber`) is added, reading an older serialized stream defaults the missing field to `null` instead of throwing an `InvalidClassException`.

---

## TASK A5 – DESIGN FOR CORRECTNESS AND SAFETY

### A5.1 Transaction Design: Wallet Transfer
```
ALGORITHM TransferWalletFunds(fromId, toId, amount, connection)
BEGIN
    SET connection.autoCommit = FALSE
    TRY
        // 1. Debit Source Wallet
        UPDATE wallets SET balance = balance - amount WHERE wallet_id = fromId AND balance >= amount
        IF ROWS_AFFECTED == 0 THEN
            THROW Exception("Insufficient balance or invalid source wallet")
        END IF

        // 2. Credit Destination Wallet
        UPDATE wallets SET balance = balance + amount WHERE wallet_id = toId
        IF ROWS_AFFECTED == 0 THEN
            THROW Exception("Invalid destination wallet")
        END IF

        COMMIT connection
    CATCH Exception e
        ROLLBACK connection
        RETHROW e
    FINALLY
        SET connection.autoCommit = TRUE
    END TRY
END
```

### A5.2 SQL Injection Analysis
- **Vulnerable Query:**
  ```sql
  SELECT * FROM users WHERE id='admin' AND pwd='' OR '1'='1'
  ```
- **Exploit:** Passing password `' OR '1'='1` creates a tautology where the `OR '1'='1'` condition always evaluates to TRUE, granting unauthorized administrator access.
- **Resolution:** `PreparedStatement` pre-compiles the SQL query template and treats all arguments strictly as literal data tokens, completely eliminating injection vectors.
