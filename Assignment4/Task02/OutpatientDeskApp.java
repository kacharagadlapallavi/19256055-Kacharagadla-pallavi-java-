package Task02;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Task 2 - From Requirement to Event-Driven Design
 * Hospital Outpatient Appointment Desk
 * Student: Moksha Dude (192372374)
 */
public class OutpatientDeskApp extends JFrame {

    // Department -> Doctors mapping
    private final Map<String, List<String>> doctorDirectory = new HashMap<>();

    private final JTextField patientIdField = new JTextField(12);
    private final JComboBox<String> deptCombo = new JComboBox<>(new String[]{"Cardiology", "Orthopedics", "Neurology", "Pediatrics"});
    private final JComboBox<String> doctorCombo = new JComboBox<>();
    private final JComboBox<String> timeSlotCombo = new JComboBox<>(new String[]{"09:00 AM", "10:30 AM", "02:00 PM", "04:30 PM"});
    private final JButton bookButton = new JButton("Book Appointment");
    private final JLabel statusLabel = new JLabel("Welcome. Please fill in details.");

    public OutpatientDeskApp() {
        super("Hospital Outpatient Appointment Desk");

        // Seed data
        doctorDirectory.put("Cardiology", List.of("Dr. Sharma (Senior Interventional)", "Dr. Rao (Clinical)"));
        doctorDirectory.put("Orthopedics", List.of("Dr. Verma (Joint Replacement)", "Dr. Kapoor (Sports Injury)"));
        doctorDirectory.put("Neurology", List.of("Dr. Iyer (Neurosurgeon)", "Dr. Menon (Consultant)"));
        doctorDirectory.put("Pediatrics", List.of("Dr. Ananya (Child Specialist)"));

        // Setup UI
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        formPanel.add(new JLabel("Patient ID:"));
        formPanel.add(patientIdField);

        formPanel.add(new JLabel("Department:"));
        formPanel.add(deptCombo);

        formPanel.add(new JLabel("Attending Doctor:"));
        formPanel.add(doctorCombo);

        formPanel.add(new JLabel("Time Slot:"));
        formPanel.add(timeSlotCombo);

        add(formPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        bottomPanel.add(bookButton, BorderLayout.NORTH);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        // Event Handling: Department Change -> Update Doctor List
        deptCombo.addActionListener(e -> updateDoctorList());
        updateDoctorList(); // Initial population

        // Event Handling: Book Button
        bookButton.addActionListener(e -> bookAppointment());

        // Event Handling: Window Closing confirmation
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(OutpatientDeskApp.this,
                        "Exit outpatient desk system?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void updateDoctorList() {
        String dept = (String) deptCombo.getSelectedItem();
        doctorCombo.removeAllItems();
        if (dept != null && doctorDirectory.containsKey(dept)) {
            for (String doc : doctorDirectory.get(dept)) {
                doctorCombo.addItem(doc);
            }
        }
    }

    private void bookAppointment() {
        String pid = patientIdField.getText().trim();
        if (pid.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Error: Patient ID is required.");
            return;
        }

        String dept = (String) deptCombo.getSelectedItem();
        String doc = (String) doctorCombo.getSelectedItem();
        String slot = (String) timeSlotCombo.getSelectedItem();

        statusLabel.setForeground(new Color(0, 128, 0));
        statusLabel.setText(String.format("Confirmed: %s with %s (%s at %s)", pid, doc, dept, slot));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OutpatientDeskApp().setVisible(true));
    }
}
