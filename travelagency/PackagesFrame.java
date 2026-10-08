package com.travelagency.gui;

import com.travelagency.dao.PackageDAO;
import com.travelagency.model.TravelPackage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PackagesFrame extends JFrame {

    private final PackageDAO packageDAO = new PackageDAO();

    private JTable table;
    private DefaultTableModel tableModel;

    public PackagesFrame() {
        setTitle("Travel Agency Management System - Packages");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(860, 540);
        setMinimumSize(new Dimension(700, 420));
        setLocationRelativeTo(null);

        buildUI();
        loadPackages();
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

        JLabel heading = new JLabel("\uD83D\uDCE6  Packages");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setForeground(UIStyle.TEXT_DARK);
        topBar.add(heading, BorderLayout.WEST);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);

        UIStyle.RoundedButton addButton = new UIStyle.RoundedButton(
                "+ Add Package", UIStyle.PRIMARY, UIStyle.PRIMARY_DARK);
        addButton.addActionListener(e -> openAddDialog());

        UIStyle.RoundedButton editButton = new UIStyle.RoundedButton(
                "Edit", new Color(0xF3F4F6), new Color(0xE5E7EB));
        editButton.setForeground(UIStyle.TEXT_DARK);
        editButton.addActionListener(e -> openEditDialog());

        UIStyle.RoundedButton deleteButton = new UIStyle.RoundedButton(
                "Delete", new Color(0xFEF2F2), new Color(0xFEE2E2));
        deleteButton.setForeground(UIStyle.DANGER);
        deleteButton.addActionListener(e -> deleteSelected());

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

        String[] columns = {"ID", "Package Name", "Destination", "Days", "Price (PHP)", "Inclusions", "Active"};
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
        table.getColumnModel().getColumn(3).setMaxWidth(60);
        table.getColumnModel().getColumn(6).setMaxWidth(60);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER, 1));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadPackages() {
        tableModel.setRowCount(0);
        List<TravelPackage> packages = packageDAO.getAllPackages();
        for (TravelPackage p : packages) {
            tableModel.addRow(new Object[]{
                    p.getPackageId(), p.getPackageName(), p.getDestinationName(),
                    p.getDurationDays(), p.getPrice(), p.getInclusions(),
                    p.isActive() ? "Yes" : "No"
            });
        }
    }

    private TravelPackage getSelectedPackage() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a package first.",
                    "No selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        return packageDAO.getPackageById(id);
    }

    private void openAddDialog() {
        PackageFormDialog dialog = new PackageFormDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadPackages();
        }
    }

    private void openEditDialog() {
        TravelPackage selected = getSelectedPackage();
        if (selected == null) return;

        PackageFormDialog dialog = new PackageFormDialog(this, selected);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadPackages();
        }
    }

    private void deleteSelected() {
        TravelPackage selected = getSelectedPackage();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete \"" + selected.getPackageName() + "\"? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = packageDAO.deletePackage(selected.getPackageId());
            if (success) {
                loadPackages();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not delete this package. It may already have bookings linked to it.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}