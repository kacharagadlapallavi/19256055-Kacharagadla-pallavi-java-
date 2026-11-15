package Q1;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * Q1 - Handling Failure in a Bank ATM Simulation
 * CO3: Exception Hierarchy, checked vs unchecked, try-catch-finally, multi-catch
 * Student: Moksha Dude (192372374)
 */
public class BankAtmSimulation {

    // Custom domain exception
    public static class InsufficientFundsException extends Exception {
        public InsufficientFundsException(String message) {
            super(message);
        }
    }

    public static class CardReaderException extends IOException {
        public CardReaderException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private double balance = 10000.00;

    public void verifyCard(String cardPath) throws CardReaderException {
        try (FileReader fr = new FileReader(cardPath)) {
            // Read simulated smart card chip data
        } catch (FileNotFoundException e) {
            throw new CardReaderException("Card chip verification failed: card file missing", e);
        } catch (IOException e) {
            throw new CardReaderException("Card hardware read error", e);
        }
    }

    public void withdraw(String amountStr) throws InsufficientFundsException, IllegalArgumentException {
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input: amount must be a numeric value.", e);
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be strictly greater than 0.");
        }

        if (amount > balance) {
            throw new InsufficientFundsException(
                    String.format("Requested Rs. %.2f exceeds current balance of Rs. %.2f", amount, balance));
        }

        balance -= amount;
        System.out.printf("Dispensing Cash: Rs. %.2f | Remaining Balance: Rs. %.2f\n", amount, balance);
    }

    public static void testTransaction(BankAtmSimulation atm, String cardPath, String amountInput) {
        System.out.println("--------------------------------------------------");
        System.out.printf("Initiating Transaction: Card='%s', Amount='%s'\n", cardPath, amountInput);
        try {
            atm.verifyCard(cardPath);
            atm.withdraw(amountInput);
            System.out.println("Transaction Status: SUCCESS");
        } catch (IllegalArgumentException e) {
            System.out.println("Handled [Unchecked Exception]: " + e.getMessage());
        } catch (InsufficientFundsException e) {
            System.out.println("Handled [Checked Business Exception]: " + e.getMessage());
        } catch (CardReaderException e) {
            System.out.println("Handled [Checked I/O Exception]: " + e.getMessage() + " (Cause: " + e.getCause() + ")");
        } catch (Exception e) {
            // General catch-all fallback
            System.out.println("Handled [General Exception]: " + e.getMessage());
        } finally {
            System.out.println("Finally Block Executed: Session terminated, card ejected, audit log closed.");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Bank ATM Failure Handling Simulation ===\n");
        BankAtmSimulation atm = new BankAtmSimulation();

        // 1. Valid transaction (using a dummy file or handled)
        testTransaction(atm, "card_dummy.txt", "2000"); // card file might not exist

        // 2. Non-numeric input
        testTransaction(atm, "card_dummy.txt", "abc_invalid");

        // 3. Negative amount input
        testTransaction(atm, "card_dummy.txt", "-500");

        // 4. Overdraft / Insufficient funds
        testTransaction(atm, "card_dummy.txt", "25000");

        System.out.println("\n--- Catch Block Ordering & General Catch Discussion ---");
        System.out.println("1. Specific exceptions (subclasses) must be caught BEFORE general ones (Exception/Throwable).");
        System.out.println("   Placing catch(Exception e) first triggers a compiler unreachable code error.");
        System.out.println("2. Catching everything with `catch (Exception e)` obscures root causes and prevents");
        System.out.println("   differentiated recovery (e.g. asking for re-entry on typo vs alarming on hardware failure).");
    }
}
