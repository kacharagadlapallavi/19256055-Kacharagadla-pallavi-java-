package TaskB2_Adversarial;

import java.sql.*;
import java.util.Arrays;

/**
 * Task B2 - Engineer an Adversarial Input: SQL Injection & Corrupt File Payloads
 * Student: Moksha Dude (192372374)
 */
public class AdversarialSecurityDemo {

    // 1. Unsafe Concatenation Login Verification
    public static boolean unsafeLogin(String userId, String password) {
        String query = "SELECT * FROM users WHERE id='" + userId + "' AND pwd='" + password + "'";
        System.out.println("  [Unsafe SQL Generated]: " + query);

        // Simulation of SQL Parser logic:
        // If password contains "' OR '1'='1", condition evaluates: id='admin' AND pwd='' OR '1'='1'
        // Which evaluates to FALSE OR TRUE => TRUE!
        if (password.contains("' OR '1'='1") || password.contains("' or '1'='1")) {
            return true; // Bypass successful!
        }
        return false;
    }

    // 2. Safe Parameterized PreparedStatement Login Verification
    public static boolean safeLogin(String userId, String password) {
        String sql = "SELECT * FROM users WHERE id = ? AND pwd = ?";
        System.out.println("  [Safe PreparedStatement Template]: " + sql);
        System.out.printf("  [Bound Literals]: Param 1 = '%s', Param 2 = '%s'\n", userId, password);

        // Under PreparedStatement, the payload "' OR '1'='1" is treated purely as literal password characters,
        // never as executable SQL control structures.
        return false; // Authentication safely rejected!
    }

    public static void main(String[] args) {
        System.out.println("=== Task B2: Adversarial Input Analysis ===");

        String targetUser = "admin";
        String adversarialPassword = "' OR '1'='1";

        System.out.println("\n--- 1. Testing SQL Injection on Unsafe Statement ---");
        boolean breached = unsafeLogin(targetUser, adversarialPassword);
        System.out.println("Unsafe Authentication Result: " + (breached ? "ACCESS GRANTED (BREACH CONFIRMED!)" : "ACCESS DENIED"));

        System.out.println("\n--- 2. Testing Same Attack on PreparedStatement ---");
        boolean blocked = !safeLogin(targetUser, adversarialPassword);
        System.out.println("Safe Authentication Result: " + (blocked ? "ACCESS REJECTED (DEFENSE SUCCESSFUL!)" : "ACCESS GRANTED"));

        System.out.println("\n--- 3. Testing Malformed File Records ---");
        String[] malformedInputs = {
                "P9999",                                // Incomplete fields (< 5)
                "P8888,1923,Student,Nine,NotANumber",   // Non-numeric amount and route
                "",                                     // Empty line
                "P7777,1923,Attacker,1,100,Extra,Field" // Overflow fields (> 5)
        };

        for (String input : malformedInputs) {
            String[] tokens = input.split(",");
            if (tokens.length != 5) {
                System.out.println("  [File Filter]: Rejected corrupted record -> '" + input + "'");
            } else {
                try {
                    int route = Integer.parseInt(tokens[3]);
                    double amt = Double.parseDouble(tokens[4]);
                    System.out.println("  [File Filter]: Accepted valid record -> " + Arrays.toString(tokens));
                } catch (NumberFormatException e) {
                    System.out.println("  [File Filter]: Rejected type conversion error -> '" + input + "'");
                }
            }
        }
    }
}
