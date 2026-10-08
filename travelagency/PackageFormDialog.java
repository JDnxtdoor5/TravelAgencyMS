package com.travelagency.gui;

import com.travelagency.dao.DestinationDAO;
import com.travelagency.dao.PackageDAO;
import com.travelagency.model.Destination;
import com.travelagency.model.TravelPackage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Modal dialog used for both "Add Package" and "Edit Package".
 * Pass an existing TravelPackage to edit it, or null to create a new one.
 */
public class PackageFormDialog extends JDialog {

    private final PackageDAO packageDAO = new PackageDAO();
    private final DestinationDAO destinationDAO = new DestinationDAO();
    private final TravelPackage editingPackage; // null when adding a new one

    private JTextField nameField;
    private JComboBox<Destination> destinationCombo;
    private JTextField durationField;
    private JTextField priceField;
    private JTextField inclusionsField;
    private JCheckBox activeCheckBox;
    private JLabel errorLabel;

    private boolean saved = false;

    public PackageFormDialog(Frame owner, TravelPackage packageToEdit) {
        super(owner, packageToEdit == null ? "Add Package" : "Edit Package", true);
        this.editingPackage = packageToEdit;

        setSize(440, 520);
        setLocationRelativeTo(owner);
        setResizable(true);
        setMinimumSize(new Dimension(380, 320));

        buildUI();
        if (packageToEdit != null) {
            populateFields(packageToEdit);
        }
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIStyle.CARD_BG);
        root.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel heading = new JLabel(editingPackage == null ? "Add Package" : "Edit Package");
        heading.setFont(UIStyle.FONT_TITLE);
        heading.setForeground(UIStyle.TEXT_DARK);
        heading.setBorder(new EmptyBorder(0, 0, 16, 0));
        root.add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        nameField = new JTextField();
        UIStyle.styleTextField(nameField);

        List<Destination> destinations = destinationDAO.getAllDestinations();
        destinationCombo = new JComboBox<>(destinations.toArray(new Destination[0]));
        destinationCombo.setFont(UIStyle.FONT_LABEL);

        durationField = new JTextField();
        UIStyle.styleTextField(durationField);

        priceField = new JTextField();
        UIStyle.styleTextField(priceField);

        inclusionsField = new JTextField();
        UIStyle.styleTextField(inclusionsField);

        activeCheckBox = new JCheckBox("Active (visible for new bookings)");
        activeCheckBox.setOpaque(false);
        activeCheckBox.setFont(UIStyle.FONT_LABEL);
        activeCheckBox.setSelected(true);
        activeCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(fieldBlock("Package Name", nameField));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(fieldBlock("Destination", destinationCombo));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(fieldBlock("Duration (days)", durationField));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(fieldBlock("Price (PHP)", priceField));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(fieldBlock("Inclusions (e.g. Hotel, Meals, Transport)", inclusionsField));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(activeCheckBox);

        if (destinations.isEmpty()) {
            JLabel noDestWarning = new JLabel(
                    "<html><div style='width:320px'>No destinations found. Add a destination first " +
                    "before creating a package.</div></html>");
            noDestWarning.setFont(UIStyle.FONT_SUBTITLE);
            noDestWarning.setForeground(UIStyle.DANGER);
            noDestWarning.setBorder(new EmptyBorder(10, 0, 0, 0));
            noDestWarning.setAlignmentX(Component.LEFT_ALIGNMENT);
            form.add(noDestWarning);
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
        saveButton.setEnabled(!destinations.isEmpty());

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

    private void populateFields(TravelPackage p) {
        nameField.setText(p.getPackageName());
        durationField.setText(String.valueOf(p.getDurationDays()));
        priceField.setText(p.getPrice().toPlainString());
        inclusionsField.setText(p.getInclusions());
        activeCheckBox.setSelected(p.isActive());

        // Select the matching destination in the dropdown
        for (int i = 0; i < destinationCombo.getItemCount(); i++) {
            Destination d = destinationCombo.getItemAt(i);
            if (d.getDestinationId() == p.getDestinationId()) {
                destinationCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void handleSave() {
        String name = nameField.getText().trim();
        String durationText = durationField.getText().trim();
        String priceText = priceField.getText().trim();
        String inclusions = inclusionsField.getText().trim();
        Destination selectedDestination = (Destination) destinationCombo.getSelectedItem();

        if (name.isEmpty() || durationText.isEmpty() || priceText.isEmpty() || selectedDestination == null) {
            errorLabel.setText("Package name, destination, duration, and price are required.");
            return;
        }

        int duration;
        BigDecimal price;
        try {
            duration = Integer.parseInt(durationText);
        } catch (NumberFormatException e) {
            errorLabel.setText("Duration must be a whole number of days.");
            return;
        }
        try {
            price = new BigDecimal(priceText);
        } catch (NumberFormatException e) {
            errorLabel.setText("Price must be a valid number (e.g. 15000 or 15000.50).");
            return;
        }

        boolean success;
        if (editingPackage == null) {
            TravelPackage p = new TravelPackage(0, name, selectedDestination.getDestinationId(),
                    duration, price, inclusions, activeCheckBox.isSelected());
            success = packageDAO.addPackage(p);
        } else {
            editingPackage.setPackageName(name);
            editingPackage.setDestinationId(selectedDestination.getDestinationId());
            editingPackage.setDurationDays(duration);
            editingPackage.setPrice(price);
            editingPackage.setInclusions(inclusions);
            editingPackage.setActive(activeCheckBox.isSelected());
            success = packageDAO.updatePackage(editingPackage);
        }

        if (success) {
            saved = true;
            dispose();
        } else {
            errorLabel.setText("Could not save package. Check your database connection.");
        }
    }

    public boolean isSaved() {
        return saved;
    }
}