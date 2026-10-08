package com.travelagency.gui;

import com.travelagency.dao.BookingDAO;
import com.travelagency.dao.CustomerDAO;
import com.travelagency.dao.PackageDAO;
import com.travelagency.model.Booking;
import com.travelagency.model.Customer;
import com.travelagency.model.TravelPackage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dialog for creating a new booking. Bookings are not edited field-by-field
 * after creation (only their status changes, handled from BookingsFrame) —
 * this dialog is "Add" only.
 */
public class BookingFormDialog extends JDialog {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final PackageDAO packageDAO = new PackageDAO();

    private JComboBox<Customer> customerCombo;
    private JComboBox<TravelPackage> packageCombo;
    private JTextField travelDateField;
    private JSpinner travelersSpinner;
    private JLabel totalAmountValue;
    private JLabel errorLabel;

    private boolean saved = false;

    public BookingFormDialog(Frame owner) {
        super(owner, "Add Booking", true);

        setSize(460, 560);
        setLocationRelativeTo(owner);
        setResizable(true);
        setMinimumSize(new Dimension(400, 360));

        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIStyle.CARD_BG);
        root.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel heading = new JLabel("Add Booking");
        heading.setFont(UIStyle.FONT_TITLE);
        heading.setForeground(UIStyle.TEXT_DARK);
        heading.setBorder(new EmptyBorder(0, 0, 16, 0));
        root.add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        List<Customer> customers = customerDAO.getAllCustomers();
        List<TravelPackage> packages = packageDAO.getActivePackages();

        customerCombo = new JComboBox<>(customers.toArray(new Customer[0]));
        customerCombo.setFont(UIStyle.FONT_LABEL);

        packageCombo = new JComboBox<>(packages.toArray(new TravelPackage[0]));
        packageCombo.setFont(UIStyle.FONT_LABEL);
        packageCombo.addActionListener(e -> recalculateTotal());

        travelDateField = new JTextField(LocalDate.now().plusDays(7)
                .format(DateTimeFormatter.ISO_LOCAL_DATE));
        UIStyle.styleTextField(travelDateField);

        travelersSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        travelersSpinner.setFont(UIStyle.FONT_LABEL);
        travelersSpinner.addChangeListener(e -> recalculateTotal());

        totalAmountValue = new JLabel("PHP 0.00");
        totalAmountValue.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalAmountValue.setForeground(UIStyle.PRIMARY);
        totalAmountValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(fieldBlock("Customer", customerCombo));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(fieldBlock("Package", packageCombo));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(fieldBlock("Travel Date (YYYY-MM-DD)", travelDateField));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(fieldBlock("Number of Travelers", travelersSpinner));
        form.add(Box.createRigidArea(new Dimension(0, 14)));
        form.add(fieldBlock("Total Amount", totalAmountValue));

        if (customers.isEmpty() || packages.isEmpty()) {
            JLabel warning = new JLabel("<html><div style='width:320px'>" +
                    (customers.isEmpty() ? "No customers found. " : "") +
                    (packages.isEmpty() ? "No active packages found. " : "") +
                    "Add them first before creating a booking.</div></html>");
            warning.setFont(UIStyle.FONT_SUBTITLE);
            warning.setForeground(UIStyle.DANGER);
            warning.setBorder(new EmptyBorder(10, 0, 0, 0));
            warning.setAlignmentX(Component.LEFT_ALIGNMENT);
            form.add(warning);
        }

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIStyle.FONT_SUBTITLE);
        errorLabel.setForeground(UIStyle.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        form.add(errorLabel);

        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setBorder(BorderFactory.createEmptyBorder());
        formScroll.getVerticalScrollBar().setUnitIncrement(12);
        root.add(formScroll, BorderLayout.CENTER);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setOpaque(false);
        buttonRow.setBorder(new EmptyBorder(20, 0, 0, 0));

        UIStyle.RoundedButton cancelButton = new UIStyle.RoundedButton(
                "Cancel", new Color(0xF3F4F6), new Color(0xE5E7EB));
        cancelButton.setForeground(UIStyle.TEXT_DARK);
        cancelButton.addActionListener(e -> dispose());

        UIStyle.RoundedButton saveButton = new UIStyle.RoundedButton(
                "Save", UIStyle.PRIMARY, UIStyle.PRIMARY_DARK);
        saveButton.addActionListener(e -> handleSave());
        saveButton.setEnabled(!customers.isEmpty() && !packages.isEmpty());

        buttonRow.add(cancelButton);
        buttonRow.add(saveButton);
        root.add(buttonRow, BorderLayout.SOUTH);

        setContentPane(root);
        recalculateTotal();
    }

    private JPanel fieldBlock(String labelText, JComponent field) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(UIStyle.FONT_LABEL);
        label.setForeground(UIStyle.TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 4, 0));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));

        block.add(label);
        block.add(field);
        return block;
    }

    private void recalculateTotal() {
        TravelPackage selectedPackage = (TravelPackage) packageCombo.getSelectedItem();
        int travelers = (int) travelersSpinner.getValue();
        if (selectedPackage != null) {
            BigDecimal total = selectedPackage.getPrice().multiply(BigDecimal.valueOf(travelers));
            totalAmountValue.setText("PHP " + total.toPlainString());
        } else {
            totalAmountValue.setText("PHP 0.00");
        }
    }

    private void handleSave() {
        Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
        TravelPackage selectedPackage = (TravelPackage) packageCombo.getSelectedItem();
        String dateText = travelDateField.getText().trim();
        int travelers = (int) travelersSpinner.getValue();

        if (selectedCustomer == null || selectedPackage == null || dateText.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        Date travelDate;
        try {
            travelDate = Date.valueOf(dateText); // expects exactly YYYY-MM-DD
        } catch (IllegalArgumentException e) {
            errorLabel.setText("Travel date must be in YYYY-MM-DD format (e.g. 2026-12-25).");
            return;
        }

        BigDecimal totalAmount = selectedPackage.getPrice().multiply(BigDecimal.valueOf(travelers));
        String bookingRef = bookingDAO.generateBookingRef();

        Booking booking = new Booking(0, bookingRef, selectedCustomer.getCustomerId(),
                selectedPackage.getPackageId(), travelDate, travelers, "PENDING", totalAmount);

        boolean success = bookingDAO.addBooking(booking);
        if (success) {
            saved = true;
            dispose();
        } else {
            errorLabel.setText("Could not save booking. Check your database connection.");
        }
    }

    public boolean isSaved() {
        return saved;
    }
}