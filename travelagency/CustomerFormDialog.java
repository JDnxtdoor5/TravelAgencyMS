package com.travelagency.gui;

import com.travelagency.dao.CustomerDAO;
import com.travelagency.model.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modal dialog used for both "Add Customer" and "Edit Customer".
 * Pass an existing Customer to edit it, or null to create a new one.
 */
public class CustomerFormDialog extends JDialog {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final Customer editingCustomer; // null when adding a new one

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField idNumberField;
    private JLabel errorLabel;

    private boolean saved = false;

    public CustomerFormDialog(Frame owner, Customer customerToEdit) {
        super(owner, customerToEdit == null ? "Add Customer" : "Edit Customer", true);
        this.editingCustomer = customerToEdit;

        setSize(440, 540);
        setLocationRelativeTo(owner);
        setResizable(true);
        setMinimumSize(new Dimension(380, 320));

        buildUI();
        if (customerToEdit != null) {
            populateFields(customerToEdit);
        }
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIStyle.CARD_BG);
        root.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel heading = new JLabel(editingCustomer == null ? "Add Customer" : "Edit Customer");
        heading.setFont(UIStyle.FONT_TITLE);
        heading.setForeground(UIStyle.TEXT_DARK);
        heading.setBorder(new EmptyBorder(0, 0, 16, 0));
        root.add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        firstNameField = new JTextField();
        lastNameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        addressField = new JTextField();
        idNumberField = new JTextField();
        UIStyle.styleTextField(firstNameField);
        UIStyle.styleTextField(lastNameField);
        UIStyle.styleTextField(emailField);
        UIStyle.styleTextField(phoneField);
        UIStyle.styleTextField(addressField);
        UIStyle.styleTextField(idNumberField);

        form.add(fieldBlock("First Name", firstNameField));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(fieldBlock("Last Name", lastNameField));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(fieldBlock("Email", emailField));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(fieldBlock("Phone", phoneField));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(fieldBlock("Address", addressField));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(fieldBlock("ID / Passport Number", idNumberField));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIStyle.FONT_SUBTITLE);
        errorLabel.setForeground(UIStyle.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        form.add(errorLabel);

        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setBorder(BorderFactory.createEmptyBorder());
        formScroll.getVerticalScrollBar().setUnitIncrement(12);
        formScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
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

        buttonRow.add(cancelButton);
        buttonRow.add(saveButton);
        root.add(buttonRow, BorderLayout.SOUTH);

        setContentPane(root);
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

    private void populateFields(Customer c) {
        firstNameField.setText(c.getFirstName());
        lastNameField.setText(c.getLastName());
        emailField.setText(c.getEmail());
        phoneField.setText(c.getPhone());
        addressField.setText(c.getAddress());
        idNumberField.setText(c.getIdNumber());
    }

    private void handleSave() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String idNumber = idNumberField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            errorLabel.setText("First name and last name are required.");
            return;
        }

        boolean success;
        if (editingCustomer == null) {
            Customer c = new Customer(0, firstName, lastName, email, phone, address, idNumber);
            success = customerDAO.addCustomer(c);
        } else {
            editingCustomer.setFirstName(firstName);
            editingCustomer.setLastName(lastName);
            editingCustomer.setEmail(email);
            editingCustomer.setPhone(phone);
            editingCustomer.setAddress(address);
            editingCustomer.setIdNumber(idNumber);
            success = customerDAO.updateCustomer(editingCustomer);
        }

        if (success) {
            saved = true;
            dispose();
        } else {
            errorLabel.setText("Could not save customer. Check your database connection.");
        }
    }

    public boolean isSaved() {
        return saved;
    }
}