package com.travelagency.gui;

import com.travelagency.dao.BookingDAO;
import com.travelagency.model.Booking;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookingsFrame extends JFrame {

    private final BookingDAO bookingDAO = new BookingDAO();

    private JTable table;
    private DefaultTableModel tableModel;

    private static final String[] STATUSES = {"PENDING", "CONFIRMED", "CANCELLED", "COMPLETED"};

    public BookingsFrame() {
        setTitle("Travel Agency Management System - Bookings");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(960, 560);
        setMinimumSize(new Dimension(760, 440));
        setLocationRelativeTo(null);

        buildUI();
        loadBookings();
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

        JLabel heading = new JLabel("\uD83D\uDCC5  Bookings");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setForeground(UIStyle.TEXT_DARK);
        topBar.add(heading, BorderLayout.WEST);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);

        UIStyle.RoundedButton addButton = new UIStyle.RoundedButton(
                "+ Add Booking", UIStyle.PRIMARY, UIStyle.PRIMARY_DARK);
        addButton.addActionListener(e -> openAddDialog());

        UIStyle.RoundedButton statusButton = new UIStyle.RoundedButton(
                "Update Status", new Color(0xF3F4F6), new Color(0xE5E7EB));
        statusButton.setForeground(UIStyle.TEXT_DARK);
        statusButton.addActionListener(e -> updateStatus());

        UIStyle.RoundedButton deleteButton = new UIStyle.RoundedButton(
                "Delete", new Color(0xFEF2F2), new Color(0xFEE2E2));
        deleteButton.setForeground(UIStyle.DANGER);
        deleteButton.addActionListener(e -> deleteSelected());

        buttonRow.add(statusButton);
        buttonRow.add(deleteButton);
        buttonRow.add(addButton);
        topBar.add(buttonRow, BorderLayout.EAST);

        return topBar;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyle.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 24, 24, 24));

        String[] columns = {"ID", "Booking Ref", "Customer", "Package", "Travel Date",
                "Travelers", "Status", "Total (PHP)"};
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
        table.getColumnModel().getColumn(5).setMaxWidth(80);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyle.BORDER, 1));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        List<Booking> bookings = bookingDAO.getAllBookings();
        for (Booking b : bookings) {
            tableModel.addRow(new Object[]{
                    b.getBookingId(), b.getBookingRef(), b.getCustomerName(), b.getPackageName(),
                    b.getTravelDate(), b.getNumTravelers(), b.getStatus(), b.getTotalAmount()
            });
        }
    }

    private int getSelectedBookingId() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking first.",
                    "No selection", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        return (int) tableModel.getValueAt(row, 0);
    }

    private void openAddDialog() {
        BookingFormDialog dialog = new BookingFormDialog(this);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadBookings();
        }
    }

    private void updateStatus() {
        int bookingId = getSelectedBookingId();
        if (bookingId == -1) return;

        String currentStatus = (String) tableModel.getValueAt(table.getSelectedRow(), 6);

        String newStatus = (String) JOptionPane.showInputDialog(this,
                "Select new status:", "Update Booking Status",
                JOptionPane.PLAIN_MESSAGE, null, STATUSES, currentStatus);

        if (newStatus != null && !newStatus.equals(currentStatus)) {
            boolean success = bookingDAO.updateBookingStatus(bookingId, newStatus);
            if (success) {
                loadBookings();
            } else {
                JOptionPane.showMessageDialog(this, "Could not update booking status.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelected() {
        int bookingId = getSelectedBookingId();
        if (bookingId == -1) return;

        String ref = (String) tableModel.getValueAt(table.getSelectedRow(), 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete booking \"" + ref + "\"? This will also remove any payments " +
                "linked to this booking.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = bookingDAO.deleteBooking(bookingId);
            if (success) {
                loadBookings();
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete this booking.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}