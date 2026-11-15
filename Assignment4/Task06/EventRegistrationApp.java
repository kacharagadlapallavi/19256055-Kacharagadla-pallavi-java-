package Task06;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Task 6 - Campus Event Registration Desktop Application
 * Multi-tier Architecture: Presentation (Swing) + Model/DAO + Concurrency (SwingWorker)
 * Student: Moksha Dude (192372374)
 */
public class EventRegistrationApp extends JFrame {

    // Domain Model
    public static class Registration {
        final int id;
        final String regNo;
        final String name;
        final String dept;
        final String event;

        public Registration(int id, String regNo, String name, String dept, String event) {
            this.id = id;
            this.regNo = regNo;
            this.name = name;
            this.dept = dept;
            this.event = event;
        }
    }

    // In-memory / Mock JDBC Store supporting unique constraint validation
    public static class RegistrationDAO {
        private final List<Registration> store = new ArrayList<>();
        private int idCounter = 1;

        public RegistrationDAO() {
            // Seed sample data
            store.add(new Registration(idCounter++, "192372374", "Moksha Dude", "Computer Science", "Hackathon 2026"));
            store.add(new Registration(idCounter++, "192311001", "Arun Kumar", "Information Technology", "RoboWars"));
            store.add(new Registration(idCounter++, "192311002", "Divya R", "Computer Science", "Coding Sprint"));
        }

        public synchronized Registration register(String regNo, String name, String dept, String event) throws Exception {
            // Check unique constraint: (regNo, event)
            for (Registration r : store) {
                if (r.regNo.equalsIgnoreCase(regNo) && r.event.equalsIgnoreCase(event)) {
                    throw new Exception("Duplicate Registration: Student " + regNo + " is already enrolled in " + event);
                }
            }
            Registration newReg = new Registration(idCounter++, regNo, name, dept, event);
            store.add(newReg);
            return newReg;
        }

        public synchronized List<Registration> getAll(String filterEvent) {
            if (filterEvent == null || filterEvent.equals("All Events")) {
                return new ArrayList<>(store);
            }
            List<Registration> filtered = new ArrayList<>();
            for (Registration r : store) {
                if (r.event.equalsIgnoreCase(filterEvent)) filtered.add(r);
            }
            return filtered;
        }
    }

    private final RegistrationDAO dao = new RegistrationDAO();

    // UI Components
    private final JTextField regNoField = new JTextField(12);
    private final JTextField nameField = new JTextField(15);
    private final JComboBox<String> deptField = new JComboBox<>(new String[]{"Computer Science", "Information Technology", "Electronics", "Mechanical"});
    private final JComboBox<String> eventField = new JComboBox<>(new String[]{"Hackathon 2026", "RoboWars", "Coding Sprint", "Web Design Expo"});

    private final JComboBox<String> filterCombo = new JComboBox<>(new String[]{"All Events", "Hackathon 2026", "RoboWars", "Coding Sprint", "Web Design Expo"});
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Register No", "Student Name", "Department", "Event"}, 0
    );
    private final JTable table = new JTable(tableModel);
    private final JLabel statusLabel = new JLabel("Status: Ready");

    public EventRegistrationApp() {
        super("Campus Event Registration Management System");
        setupMenuBar();
        setLayout(new BorderLayout(10, 10));

        // 1. Top Section: Registration Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("New Student Registration"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Register No:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(regNoField, gbc);

        gbc.gridx = 2; gbc.gridy = 0; formPanel.add(new JLabel("Student Name:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(deptField, gbc);

        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(new JLabel("Event:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; formPanel.add(eventField, gbc);

        JButton submitButton = new JButton("Register Student");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        formPanel.add(submitButton, gbc);

        add(formPanel, BorderLayout.NORTH);

        // 2. Middle Section: Table with Filter Bar
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Registered Participants"));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterBar.add(new JLabel("Filter by Event:"));
        filterBar.add(filterCombo);
        JButton refreshBtn = new JButton("Refresh");
        filterBar.add(refreshBtn);
        centerPanel.add(filterBar, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // 3. Bottom Status Bar
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        southPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        southPanel.add(statusLabel);
        add(southPanel, BorderLayout.SOUTH);

        // Event Handlers
        submitButton.addActionListener(e -> handleRegistration());
        filterCombo.addActionListener(e -> reloadTable());
        refreshBtn.addActionListener(e -> reloadTable());

        reloadTable();

        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void setupMenuBar() {
        JMenuBar mb = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem exportItem = new JMenuItem("Export to CSV");
        exportItem.addActionListener(e -> exportToCSV());
        fileMenu.add(exportItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Campus Event Registration System\nDeveloped by Moksha Dude (192372374)\nSIMATS Engineering",
                "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        mb.add(fileMenu);
        mb.add(helpMenu);
        setJMenuBar(mb);
    }

    private void handleRegistration() {
        String regNo = regNoField.getText().trim();
        String name = nameField.getText().trim();
        String dept = (String) deptField.getSelectedItem();
        String event = (String) eventField.getSelectedItem();

        if (regNo.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Register Number and Student Name.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Asynchronous DB submission via SwingWorker to keep UI responsive
        statusLabel.setText("Status: Saving registration...");
        new SwingWorker<Registration, Void>() {
            @Override
            protected Registration doInBackground() throws Exception {
                return dao.register(regNo, name, dept, event);
            }

            @Override
            protected void done() {
                try {
                    Registration r = get();
                    statusLabel.setText("Status: Successfully registered " + r.name + " for " + r.event);
                    regNoField.setText("");
                    nameField.setText("");
                    reloadTable();
                } catch (Exception ex) {
                    statusLabel.setText("Status: Registration failed.");
                    JOptionPane.showMessageDialog(EventRegistrationApp.this,
                            ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage(),
                            "Registration Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        }.execute();
    }

    private void reloadTable() {
        String selectedFilter = (String) filterCombo.getSelectedItem();
        tableModel.setRowCount(0);
        List<Registration> list = dao.getAll(selectedFilter);
        for (Registration r : list) {
            tableModel.addRow(new Object[]{r.id, r.regNo, r.name, r.dept, r.event});
        }
        statusLabel.setText("Status: Displaying " + list.size() + " registrations.");
    }

    private void exportToCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("registrations_export.csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                bw.write("RegistrationID,RegisterNo,StudentName,Department,Event\n");
                for (Registration r : dao.getAll("All Events")) {
                    bw.write(String.format("%d,%s,%s,%s,%s\n", r.id, r.regNo, r.name, r.dept, r.event));
                }
                JOptionPane.showMessageDialog(this, "Exported successfully to " + f.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "I/O Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EventRegistrationApp().setVisible(true));
    }
}
