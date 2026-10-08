package com.travelagency.gui;

import com.travelagency.dao.DestinationDAO;
import com.travelagency.model.Destination;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DestinationsFrame extends JFrame {

    private final DestinationDAO destinationDAO = new DestinationDAO();

    private JTable table;
    private DefaultTableModel tableModel;

    public DestinationsFrame() {
        setTitle("Travel Agency Management System - Destinations");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 540);
        setMinimumSize(new Dimension(640, 420));
        setLocationRelativeTo(null);

        buildUI();
        loadDestinations();
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

        JLabel heading = new JLabel("\uD83C\uDF0D  Destinations");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setForeground(UIStyle.TEXT_DARK);
        topBar.add(heading, BorderLayout.WEST);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);

        UIStyle.RoundedButton addButton = new UIStyle.RoundedButton(
                "+ Add Destination", UIStyle.PRIMARY, UIStyle.PRIMARY_DARK);
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

        String[] columns = {"ID", "Name", "Country", "Best Season", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only table; editing happens via the dialog
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
        table.getColumnModel().getColumn(0).setMaxWidth(60);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER, 1));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadDestinations() {
        tableModel.setRowCount(0);
        List<Destination> destinations = destinationDAO.getAllDestinations();
        for (Destination d : destinations) {
            tableModel.addRow(new Object[]{
                    d.getDestinationId(), d.getName(), d.getCountry(),
                    d.getBestSeason(), d.getDescription()
            });
        }
    }

    private Destination getSelectedDestination() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a destination first.",
                    "No selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        return destinationDAO.getDestinationById(id);
    }

    private void openAddDialog() {
        DestinationFormDialog dialog = new DestinationFormDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadDestinations();
        }
    }

    private void openEditDialog() {
        Destination selected = getSelectedDestination();
        if (selected == null) return;

        DestinationFormDialog dialog = new DestinationFormDialog(this, selected);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadDestinations();
        }
    }

    private void deleteSelected() {
        Destination selected = getSelectedDestination();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete \"" + selected.getName() + "\"? This will also remove any packages " +
                "linked to this destination.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = destinationDAO.deleteDestination(selected.getDestinationId());
            if (success) {
                loadDestinations();
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete this destination.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}