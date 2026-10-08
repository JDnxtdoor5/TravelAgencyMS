package com.travelagency.gui;

import com.travelagency.dao.CustomerDAO;
import com.travelagency.model.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomersFrame extends JFrame {

    private final CustomerDAO customerDAO = new CustomerDAO();

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public CustomersFrame() {
        setTitle("Travel Agency Management System - Customers");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 560);
        setMinimumSize(new Dimension(720, 440));
        setLocationRelativeTo(null);

        buildUI();
        loadCustomers();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIStyle.BACKGROUND);

        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildTablePanel(), BorderLayout.CENTER);

        add(root);
    }

    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UIStyle.CARD_BG);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyle.BORDER),
                new EmptyBorder(16, 24, 16, 24)));

        JLabel heading = new JLabel("\uD83D\uDC65  Customers");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setForeground(UIStyle.TEXT_DARK);
        topBar.add(heading, BorderLayout.WEST);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);

        searchField = new JTextField(16);
        UIStyle.styleTextField(searchField);
        searchField.addActionListener(e -> handleSearch());

        UIStyle.RoundedButton searchButton = new UIStyle.RoundedButton(
                "Search", new Color(0xF3F4F6), new Color(0xE5E7EB));
        searchButton.setForeground(UIStyle.TEXT_DARK);
        searchButton.addActionListener(e -> handleSearch());

        UIStyle.RoundedButton clearButton = new UIStyle.RoundedButton(
                "Clear", new Color(0xF3F4F6), new Color(0xE5E7EB));
        clearButton.setForeground(UIStyle.TEXT_DARK);
        clearButton.addActionListener(e -> {
            searchField.setText("");
            loadCustomers();
        });

        UIStyle.RoundedButton addButton = new UIStyle.RoundedButton(
                "+ Add Customer", UIStyle.PRIMARY, UIStyle.PRIMARY_DARK);
        addButton.addActionListener(e -> openAddDialog());

        UIStyle.RoundedButton editButton = new UIStyle.RoundedButton(
                "Edit", new Color(0xF3F4F6), new Color(0xE5E7EB));
        editButton.setForeground(UIStyle.TEXT_DARK);
        editButton.addActionListener(e -> openEditDialog());

        UIStyle.RoundedButton deleteButton = new UIStyle.RoundedButton(
                "Delete", new Color(0xFEF2F2), new Color(0xFEE2E2));
        deleteButton.setForeground(UIStyle.DANGER);
        deleteButton.addActionListener(e -> deleteSelected());

        buttonRow.add(searchField);
        buttonRow.add(searchButton);
        buttonRow.add(clearButton);
        buttonRow.add(editButton);
        buttonRow.add(deleteButton);
        buttonRow.add(addButton);
        topBar.add(buttonRow, BorderLayout.EAST);

        return topBar;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyle.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 24, 24, 24));

        String[] columns = {"ID", "First Name", "Last Name", "Email", "Phone", "Address", "ID Number"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(UIStyle.FONT_LABEL);
        table.setSelectionBackground(new Color(0xE8EEFD));
        table.setSelectionForeground(UIStyle.TEXT_DARK);
        table.setGridColor(UIStyle.BORDER);
        table.getTableHeader().setFont(UIStyle.FONT_CARD_TITLE);
        table.getTableHeader().setBackground(UIStyle.CARD_BG);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER, 1));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadCustomers() {
        populateTable(customerDAO.getAllCustomers());
    }

    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadCustomers();
        } else {
            populateTable(customerDAO.searchCustomers(keyword));
        }
    }

    private void populateTable(List<Customer> customers) {
        tableModel.setRowCount(0);
        for (Customer c : customers) {
            tableModel.addRow(new Object[]{
                    c.getCustomerId(), c.getFirstName(), c.getLastName(),
                    c.getEmail(), c.getPhone(), c.getAddress(), c.getIdNumber()
            });
        }
    }

    private Customer getSelectedCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer first.",
                    "No selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        return customerDAO.getCustomerById(id);
    }

    private void openAddDialog() {
        CustomerFormDialog dialog = new CustomerFormDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadCustomers();
        }
    }

    private void openEditDialog() {
        Customer selected = getSelectedCustomer();
        if (selected == null) return;

        CustomerFormDialog dialog = new CustomerFormDialog(this, selected);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadCustomers();
        }
    }

    private void deleteSelected() {
        Customer selected = getSelectedCustomer();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete \"" + selected.getFullName() + "\"? This will also remove any bookings " +
                "linked to this customer.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = customerDAO.deleteCustomer(selected.getCustomerId());
            if (success) {
                loadCustomers();
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete this customer.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}