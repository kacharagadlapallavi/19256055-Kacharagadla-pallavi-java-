package Q3;

import java.util.HashSet;
import java.util.Set;

/**
 * AttendanceIncomplete
 * EXACT code as provided in Assignment 2 specification, prior to completing TODO 1.
 * Demonstrates failure of de-duplication when hashCode() is NOT overridden.
 */
class StudentIncomplete {
    private final String regNo;
    private final String name;

    StudentIncomplete(String regNo, String name) {
        this.regNo = regNo;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentIncomplete)) return false;
        return regNo.equals(((StudentIncomplete) o).regNo);
    }

    // TODO 1: Override hashCode() so that it is consistent with equals()
    // [LEFT UNCOMPLETED AS REQUIRED BY SPECIFICATION FOR BASELINE EXECUTION]

    @Override
    public String toString() {
        return regNo + " " + name;
    }
}

public class AttendanceIncomplete {
    public static void main(String[] args) {
        Set<StudentIncomplete> present = new HashSet<>();
        present.add(new StudentIncomplete("192311001", "Arun")); // scanned at Gate 1
        present.add(new StudentIncomplete("192311002", "Divya"));
        present.add(new StudentIncomplete("192311001", "Arun")); // scanned again at Gate 3
        System.out.println("Unique students: " + present.size());
    }
}

