package Task05;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Task 5 - Comparative Evaluation of 4 Event-Handling Styles:
 * Style A: Container Implements ActionListener
 * Style B: Named Inner Class
 * Style C: Anonymous Inner Class
 * Style D: Lambda Expression (Single Abstract Method)
 */
public class ConverterStylesDemo {

    // Style B: Named Inner Class
    static class NamedConversionHandler implements ActionListener {
        private final Runnable action;
        NamedConversionHandler(Runnable action) { this.action = action; }
        @Override
        public void actionPerformed(ActionEvent e) { action.run(); }
    }

    public static void main(String[] args) {
        System.out.println("=== Task 5: Evaluation of 4 Event-Handling Styles ===");
        System.out.println("1. Style A (Frame implements ActionListener):");
        System.out.println("   - Coupling: Tight. Single actionPerformed method must use if-else chains on e.getSource().");
        System.out.println("   - Maintenance: Fragile as component count scales.");
        System.out.println("2. Style B (Named Inner Class):");
        System.out.println("   - Scope: Clean separation, reusable across multiple buttons/menus within same container.");
        System.out.println("   - Overhead: Moderate boilerplate.");
        System.out.println("3. Style C (Anonymous Inner Class):");
        System.out.println("   - Scope: Local to registration site.");
        System.out.println("   - Overhead: Generates separate .class file at compile-time (e.g. Converter$1.class).");
        System.out.println("4. Style D (Lambda Expression):");
        System.out.println("   - Conciseness: Highest readability. Leverages invokedynamic without extra class file overhead.");
        System.out.println("   - Best Practice: Ideal for modern Java UI development.");
    }
}
