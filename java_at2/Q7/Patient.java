package Q7;

import java.util.Objects;

/**
 * Patient
 * Model representing an emergency patient with triage severity and arrival time.
 */
public class Patient {
    public final String id;
    public final String name;
    private int severity;       // 1 (Critical) to 5 (Non-urgent)
    public final long arrivalTime;

    public Patient(String id, String name, int severity, long arrivalTime) {
        this.id = id;
        this.name = name;
        this.severity = severity;
        this.arrivalTime = arrivalTime;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = Math.max(1, Math.min(5, severity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;
        Patient patient = (Patient) o;
        return Objects.equals(id, patient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("%s(S%d,Arr:%d)", name, severity, arrivalTime);
    }
}

