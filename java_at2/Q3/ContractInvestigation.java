package Q3;

import java.util.*;

/**
 * ContractInvestigation
 * Conducts the three deliberately designed tests, mutability edge cases,
 * and alternative concepts (TreeSet, HashMap) for Q3.
 */
public class ContractInvestigation {

    // Mutable Student class to expose mutation bugs
    static class MutableStudent {
        String regNo;
        String name;

        MutableStudent(String regNo, String name) {
            this.regNo = regNo;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MutableStudent)) return false;
            MutableStudent that = (MutableStudent) o;
            return Objects.equals(regNo, that.regNo);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(regNo);
        }

        @Override
        public String toString() {
            return regNo + ":" + name;
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   ASSIGNMENT 2 - Q3: HASHCODE-EQUALS CONTRACT INVESTIGATION                   ");
        System.out.println("================================================================================\n");

        runTest1_ApparentSuccess();
        runTest2_ExposeDuplicates();
        runTest3_FieldMutationAndTreeSetInconsistency();
        runAlternativeConcepts();
    }

    /**
     * Test 1: One set of scans for which the HashSet appears to work correctly.
     * Case A: All scanned students are strictly unique across gates.
     * Case B: The exact same instance reference is added twice (reference equality bypasses hashCode in some contexts).
     */
    private static void runTest1_ApparentSuccess() {
        System.out.println("--- TEST 1: Apparent Success (Unbroken Facade) ---");
        System.out.println("Context: Scans where junior developer's StudentIncomplete class appears to function.");
        System.out.println("Hypothesis/Prediction: If 3 distinct students are scanned, size will be 3.");
        System.out.println("Also if the EXACT SAME object reference is re-added, size remains 1 (hash and identity match).");

        Set<StudentIncomplete> setUnique = new HashSet<>();
        setUnique.add(new StudentIncomplete("192311001", "Arun"));
        setUnique.add(new StudentIncomplete("192311002", "Divya"));
        setUnique.add(new StudentIncomplete("192311003", "Kiran"));
        System.out.println("[Observed] 3 unique students added -> Set size: " + setUnique.size() + " (Predicted: 3)");

        Set<StudentIncomplete> setSameRef = new HashSet<>();
        StudentIncomplete arun = new StudentIncomplete("192311001", "Arun");
        setSameRef.add(arun);
        setSameRef.add(arun); // Re-adding identical heap reference
        System.out.println("[Observed] Same object reference added twice -> Set size: " + setSameRef.size() + " (Predicted: 1)");
        System.out.println("Verdict: Test 1 creates a dangerous illusion of correctness.\n");
    }

    /**
     * Test 2: Deliberately designed to expose duplicate entries.
     */
    private static void runTest2_ExposeDuplicates() {
        System.out.println("--- TEST 2: Exposing Duplicate Entries ---");
        System.out.println("Context: Arun is scanned at Gate 1, Divya at Gate 2, Arun again at Gate 3 as separate instances.");
        System.out.println("Prediction: Without hashCode() overridden, default Object.hashCode() uses System.identityHashCode.");
        System.out.println("Predicted Set Size without fix: 3 (Duplicate Arun retained)");
        System.out.println("Predicted Set Size with fix: 2 (Duplicate Arun deduplicated)");

        // Uncorrected
        Set<StudentIncomplete> badSet = new HashSet<>();
        StudentIncomplete s1 = new StudentIncomplete("192311001", "Arun");
        StudentIncomplete s2 = new StudentIncomplete("192311002", "Divya");
        StudentIncomplete s3 = new StudentIncomplete("192311001", "Arun"); // New instance, same regNo

        badSet.add(s1);
        badSet.add(s2);
        badSet.add(s3);
        System.out.printf("[Observed Uncorrected] Set size: %d | Elements: %s%n", badSet.size(), badSet);
        System.out.printf("  s1.hashCode(): %d | s3.hashCode(): %d (Different buckets!)%n",
                s1.hashCode(), s3.hashCode());
        System.out.printf("  s1.equals(s3): %b (equals() is true, but contract is broken)%n", s1.equals(s3));

        // Corrected
        Set<Student> goodSet = new HashSet<>();
        Student g1 = new Student("192311001", "Arun");
        Student g2 = new Student("192311002", "Divya");
        Student g3 = new Student("192311001", "Arun");

        goodSet.add(g1);
        goodSet.add(g2);
        goodSet.add(g3);
        System.out.printf("[Observed Corrected]   Set size: %d | Elements: %s%n", goodSet.size(), goodSet);
        System.out.printf("  g1.hashCode(): %d | g3.hashCode(): %d (Same bucket!)%n",
                g1.hashCode(), g3.hashCode());
        System.out.println("Verdict: Test 2 successfully demonstrates contract enforcement.\n");
    }

    /**
     * Test 3: Changing key field after insertion & TreeSet inconsistency.
     */
    private static void runTest3_FieldMutationAndTreeSetInconsistency() {
        System.out.println("--- TEST 3: Mutation After Insertion & TreeSet Inconsistency ---");

        // Part A: Hash mutation corruption
        System.out.println("[Part A: Hash Mutation]");
        Set<MutableStudent> mutableSet = new HashSet<>();
        MutableStudent ms = new MutableStudent("192311010", "Priya");
        mutableSet.add(ms);
        System.out.println("Initial State: " + ms + " added. Set size: " + mutableSet.size());
        System.out.println("Contains original? " + mutableSet.contains(new MutableStudent("192311010", "Priya")));

        // Mutate regNo after addition
        System.out.println("--> Mutating regNo to 192311099...");
        ms.regNo = "192311099";

        // Query with old regNo
        boolean containsOld = mutableSet.contains(new MutableStudent("192311010", "Priya"));
        // Query with new regNo
        boolean containsNew = mutableSet.contains(new MutableStudent("192311099", "Priya"));
        // Query with ms reference itself
        boolean containsRef = mutableSet.contains(ms);

        System.out.println("[Observed] contains(old key): " + containsOld + " (Searches old bucket, but key inside has changed)");
        System.out.println("[Observed] contains(new key): " + containsNew + " (Calculates new bucket, but item is in old bucket!)");
        System.out.println("[Observed] contains(same reference): " + containsRef);
        System.out.println("[Observed] Set size(): " + mutableSet.size() + " | Actual Set content: " + mutableSet);
        System.out.println("--> Attempting to remove object: " + mutableSet.remove(ms));
        System.out.println("--> Set size after remove attempt: " + mutableSet.size() + " (Object is orphaned/unreachable!)");

        // Part B: TreeSet inconsistent with equals
        System.out.println("\n[Part B: TreeSet compareTo Inconsistent with equals]");
        // Comparator compares only by name length!
        Comparator<Student> faultyComparator = Comparator.comparingInt(s -> s.getName().length());
        TreeSet<Student> faultyTreeSet = new TreeSet<>(faultyComparator);

        Student stA = new Student("192311001", "Arun");   // name length = 4
        Student stB = new Student("192311002", "Divy");   // name length = 4, but DIFFERENT regNo and DIFFERENT person!

        faultyTreeSet.add(stA);
        boolean addedStB = faultyTreeSet.add(stB);

        System.out.println("Added Arun (len 4): true");
        System.out.println("Attempting to add Divy (len 4, different regNo): " + addedStB);
        System.out.println("[Observed] TreeSet size: " + faultyTreeSet.size() + " | Elements: " + faultyTreeSet);
        System.out.println("Verdict: TreeSet rejected a distinct student because comparator returned 0 (deemed duplicate) even though equals() is false!\n");
    }

    /**
     * Alternative Concepts:
     * 1. TreeSet<Student> with Comparator on regNo
     * 2. HashMap<String, Student> keyed by regNo
     */
    private static void runAlternativeConcepts() {
        System.out.println("--- ALTERNATIVE CONCEPTS EVALUATION ---");

        // Alternative 1: TreeSet<Student> with Comparator on regNo
        TreeSet<Student> treeSet = new TreeSet<>(Comparator.comparing(Student::getRegNo));
        treeSet.add(new Student("192311001", "Arun"));
        treeSet.add(new Student("192311002", "Divya"));
        treeSet.add(new Student("192311001", "Arun Duplicate"));

        System.out.println("1. TreeSet<Student> (sorted by regNo):");
        System.out.println("   Size: " + treeSet.size() + " | Contents: " + treeSet);
        System.out.println("   Complexity: O(log N) per scan | Space: Red-Black Tree (parent, left, right, color)");

        // Alternative 2: HashMap<String, Student> keyed by regNo
        Map<String, Student> studentMap = new HashMap<>();
        studentMap.putIfAbsent("192311001", new Student("192311001", "Arun"));
        studentMap.putIfAbsent("192311002", new Student("192311002", "Divya"));
        studentMap.putIfAbsent("192311001", new Student("192311001", "Arun Duplicate"));

        System.out.println("2. HashMap<String, Student> (keyed by String regNo):");
        System.out.println("   Size: " + studentMap.size() + " | Map Values: " + studentMap.values());
        System.out.println("   Complexity: O(1) average per scan | Relies on String's built-in robust hashCode/equals.");
        System.out.println("================================================================================\n");
    }
}

