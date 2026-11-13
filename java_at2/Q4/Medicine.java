package Q4;

import java.util.Objects;

/**
 * Medicine
 * Domain model representing a pharmaceutical product in stock.
 */
public class Medicine implements Comparable<Medicine> {
    private final String code;          // e.g., "MED-04921"
    private final String name;          // e.g., "Amoxicillin 500mg"
    private final String batch;         // e.g., "BATCH-2026A"
    private int stock;                  // Current quantity in stock
    private final double unitPrice;     // Price per unit
    private final long insertionSeq;    // Sequential arrival counter

    public Medicine(String code, String name, String batch, int stock, double unitPrice, long insertionSeq) {
        this.code = code;
        this.name = name;
        this.batch = batch;
        this.stock = stock;
        this.unitPrice = unitPrice;
        this.insertionSeq = insertionSeq;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getBatch() { return batch; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public double getUnitPrice() { return unitPrice; }
    public long getInsertionSeq() { return insertionSeq; }

    @Override
    public int compareTo(Medicine other) {
        return this.code.compareTo(other.code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicine)) return false;
        Medicine medicine = (Medicine) o;
        return Objects.equals(code, medicine.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Stock: %d | Batch: %s", code, name, stock, batch);
    }
}

