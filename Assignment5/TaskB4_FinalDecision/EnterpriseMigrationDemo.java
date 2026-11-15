package TaskB4_FinalDecision;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Task B4 - Enterprise Migration Program & Final Persistence Decision
 * Student: Moksha Dude (192372374)
 */
public class EnterpriseMigrationDemo {

    public static class MigrationReport {
        public int totalRead = 0;
        public int validLoaded = 0;
        public final List<String> rejectedRecords = new ArrayList<>();
    }

    public static MigrationReport runMigration(String csvData) {
        MigrationReport report = new MigrationReport();
        try (BufferedReader reader = new BufferedReader(new StringReader(csvData))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                report.totalRead++;

                String[] fields = line.split(",");
                if (fields.length != 5) {
                    report.rejectedRecords.add("Malformed field count (" + fields.length + "): " + line);
                    continue;
                }

                try {
                    int route = Integer.parseInt(fields[3].trim());
                    double amount = Double.parseDouble(fields[4].trim());
                    if (amount < 0) {
                        report.rejectedRecords.add("Negative amount value: " + line);
                        continue;
                    }
                    report.validLoaded++;
                } catch (NumberFormatException e) {
                    report.rejectedRecords.add("Numeric parsing failure: " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return report;
    }

    public static void main(String[] args) {
        System.out.println("=== Task B4: Enterprise Migration & Production Architecture Decision ===\n");

        String simulatedFile =
                "P1001,192372374,Moksha Dude,14,4500.00\n" +
                "P1002,192311001,Arun Kumar,5,3800.00\n" +
                "P1003,INVALID_DAMAGED_RECORD\n" +
                "P1004,192311002,Divya R,8,NOT_NUMERIC\n" +
                "P1005,192311003,Karthik,12,-150.00\n" +
                "P1006,192311004,Suresh,10,4200.00\n";

        MigrationReport report = runMigration(simulatedFile);
        System.out.println("--- Migration Summary Report ---");
        System.out.println("Total Records Processed : " + report.totalRead);
        System.out.println("Successfully Validated  : " + report.validLoaded);
        System.out.println("Rejected / Failed Count : " + report.rejectedRecords.size());
        System.out.println("Rejection Audit Log:");
        for (String err : report.rejectedRecords) {
            System.out.println("  * " + err);
        }

        System.out.println("\n--- B4.2: DatabaseMetaData Utilities ---");
        System.out.println("Using DatabaseMetaData allows querying driver capabilities, transaction isolation levels,");
        System.out.println("table catalog schemas, and batch statement support programmatically at startup,");
        System.out.println("ensuring seamless environment portability across local development, staging, and cloud production.");

        System.out.println("\n--- B4.3: Production Architecture Decision (12 Campuses, 100x Scale) ---");
        System.out.println("DECISION: RELATIONAL DATABASE (RDBMS / MySQL / PostgreSQL) via Connection Pooling.");
        System.out.println("1. Concurrency: Multiple issuing counters require ACID row-level locking. Flat files or Java");
        System.out.println("   serialization require whole-file locking, causing catastrophic counter lockouts.");
        System.out.println("2. Query Flexibility: B-Tree database indexes allow instantaneous O(log N) searches across");
        System.out.println("   millions of records by pass ID, register number, or bus route.");
        System.out.println("3. Fault Recovery: Write-Ahead Logging (WAL) guarantees zero data loss during power failure.");
    }
}
