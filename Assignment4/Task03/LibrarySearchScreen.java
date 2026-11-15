package Task03;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Task 3 - From Sketch to Layout
 * Campus Library Search Screen
 * Student: Moksha Dude (192372374)
 */
public class LibrarySearchScreen extends JFrame {

    private final JTextField searchField = new JTextField(20);
    private final JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"All Categories", "Computer Science", "Mathematics", "Physics", "Literature"});
    private final JButton searchButton = new JButton("Search");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[][]{
                    {"CS101", "Introduction to Java Programming", "Y. Daniel Liang", "Available"},
                    {"CS202", "Data Structures and Algorithm Analysis", "Mark Allen Weiss", "Available"},
                    {"CS305", "Operating System Concepts", "Silberschatz et al.", "Checked Out"},
                    {"MA110", "Discrete Mathematics and Applications", "Kenneth Rosen", "Available"}
            },
            new String[]{"Book ID", "Title", "Author", "Status"}
    );
    private final JTable resultsTable = new JTable(tableModel);

    private final JButton prevButton = new JButton("◀ Previous");
    private final JButton nextButton = new JButton("Next ▶");
    private final JButton reserveButton = new JButton("Reserve Book");
    private final JLabel statusLabel = new JLabel("Ready. 4 records found.");

    public LibrarySearchScreen() {
        super("Campus Library Catalog Search");
        setLayout(new BorderLayout(8, 8));

        // 1. Top Panel: Title Bar + Search Controls (BorderLayout)
        JPanel northPanel = new JPanel(new BorderLayout(5, 5));
        JLabel titleLabel = new JLabel("SIMATS University Central Library Catalog", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        northPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        searchBar.add(new JLabel("Keywords:"));
        searchBar.add(searchField);
        searchBar.add(categoryCombo);
        searchBar.add(searchButton);
        northPanel.add(searchBar, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // 2. Center Panel: Results Table inside JScrollPane (expands dynamically on resize)
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Search Results"));
        add(scrollPane, BorderLayout.CENTER);

        // 3. Bottom Panel: Navigation Buttons and Status Bar
        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        buttonBar.add(prevButton);
        buttonBar.add(nextButton);
        buttonBar.add(reserveButton);
        southPanel.add(buttonBar, BorderLayout.NORTH);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(statusLabel);
        southPanel.add(statusPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        // Event: Search Action
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            statusLabel.setText("Showing matches for: '" + query + "' in " + categoryCombo.getSelectedItem());
        });

        reserveButton.addActionListener(e -> {
            int row = resultsTable.getSelectedRow();
            if (row != -1) {
                String title = (String) resultsTable.getValueAt(row, 1);
                JOptionPane.showMessageDialog(this, "Reservation confirmed for: " + title);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book from the table first.");
            }
        });

        setSize(700, 450);
        setMinimumSize(new Dimension(500, 300));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibrarySearchScreen().setVisible(true));
    }
}
