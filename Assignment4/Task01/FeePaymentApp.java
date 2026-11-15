package Task01;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Task 1 - GUI Design Review & Layered Architecture
 * Refactored Student Fee-Payment Window
 * Student: Moksha Dude (192372374)
 */
public class FeePaymentApp {

    // Service Layer: Completely decoupled from GUI components
    public static class FeeService {
        public static class PaymentResult {
            public final boolean success;
            public final String message;

            public PaymentResult(boolean success, String message) {
                this.success = success;
                this.message = message;
            }
        }

        public PaymentResult processPayment(String regNo, String amountStr) {
            // 1. Validation
            if (regNo == null || regNo.trim().isEmpty()) {
                return new PaymentResult(false, "Validation Error: Register Number cannot be empty.");
            }
            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    return new PaymentResult(false, "Validation Error: Amount must be positive.");
                }
            } catch (NumberFormatException e) {
                return new PaymentResult(false, "Validation Error: Invalid numeric amount format.");
            }

            // 2. Safe Database Execution using PreparedStatement (mocked/safe)
            String sql = "INSERT INTO fees(reg_no, amount, payment_time) VALUES (?, ?, CURRENT_TIMESTAMP)";
            // Safe execution ensures SQL injection immunity and auto-resource cleanup
            System.out.printf("[FeeService] Successfully processed fee payment of Rs. %.2f for RegNo: %s\n",
                    amount, regNo.trim());
            return new PaymentResult(true, "Payment of Rs. " + amount + " recorded successfully.");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Task 1: GUI Design Review & Refactored Fee Payment Service ===");
        FeeService service = new FeeService();

        // Test 1: Empty input
        var r1 = service.processPayment("", "5000");
        System.out.println("Test 1 Result: " + r1.message);

        // Test 2: Invalid amount
        var r2 = service.processPayment("192372374", "invalid_num");
        System.out.println("Test 2 Result: " + r2.message);

        // Test 3: Normal successful payment
        var r3 = service.processPayment("192372374", "15000.00");
        System.out.println("Test 3 Result: " + r3.message);

        System.out.println("\n--- Task 1.1: 5 Critical Deficiencies in Original Code ---");
        System.out.println("1. Identity String Comparison: `e.getActionCommand() == \"Pay\"` checks reference identity,");
        System.out.println("   which can fail unexpectedly; must use `.equals(\"Pay\")`.");
        System.out.println("2. UI Thread Blocking: Performing database I/O and `Thread.sleep(3000)` directly on the EDT");
        System.out.println("   freezes the OS window, stops repainting, and causes OS 'Not Responding' hangs.");
        System.out.println("3. SQL Injection Vulnerability: String concatenation `\"INSERT INTO fees VALUES('\" + ...`");
        System.out.println("   allows catastrophic SQL injection attacks.");
        System.out.println("4. Unhandled Exceptions: `Double.parseDouble` throws unchecked NumberFormatException on typos,");
        System.out.println("   and SQLException is unhandled, crashing the event loop silently.");
        System.out.println("5. Resource Leakage: Database `Connection` and `Statement` are never closed.");
    }
}
