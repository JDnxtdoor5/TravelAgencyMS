package com.travelagency.gui;

import com.travelagency.dao.DestinationDAO;
import com.travelagency.model.Destination;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modal dialog used for both "Add Destination" and "Edit Destination".
 * Pass an existing Destination to edit it, or null to create a new one.
 */
public class DestinationFormDialog extends JDialog {

    private final DestinationDAO destinationDAO = new DestinationDAO();
    private final Destination editingDestination; // null when adding a new one

    private JTextField nameField;
    private JTextField countryField;
    private JTextField seasonField;
    private JTextArea descriptionArea;
    private JLabel errorLabel;

    private boolean saved = false; // true if the user successfully saved

    public DestinationFormDialog(Frame owner, Destination destinationToEdit) {
        super(owner, destinationToEdit == null ? "Add Destination" : "Edit Destination", true);
        this.editingDestination = destinationToEdit;

        setSize(420, 480);
        setLocationRelativeTo(owner);
        setResizable(true);
        setMinimumSize(new Dimension(380, 320));

        buildUI();
        if (destinationToEdit != null) {
            populateFields(destinationToEdit);
        }
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIStyle.CARD_BG);
        root.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel heading = new JLabel(editingDestination == null ? "Add Destination" : "Edit Destination");
        heading.setFont(UIStyle.FONT_TITLE);
        heading.setForeground(UIStyle.TEXT_DARK);
        heading.setBorder(new EmptyBorder(0, 0, 16, 0));
        root.add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        nameField = new JTextField();
        countryField = new JTextField();
        seasonField = new JTextField();
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        UIStyle.styleTextField(nameField);
        UIStyle.styleTextField(countryField);
        UIStyle.styleTextField(seasonField);
        descriptionArea.setFont(UIStyle.FONT_LABEL);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)));

        form.add(fieldBlock("Destination Name", nameField));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(fieldBlock("Country", countryField));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(fieldBlock("Best Season (e.g. Dec - Feb)", seasonField));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(fieldBlock("Description", new JScrollPane(descriptionArea)));

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

    private void populateFields(Destination d) {
        nameField.setText(d.getName());
        countryField.setText(d.getCountry());
        seasonField.setText(d.getBestSeason());
        descriptionArea.setText(d.getDescription());
    }

    private void handleSave() {
        String name = nameField.getText().trim();
        String country = countryField.getText().trim();
        String season = seasonField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (name.isEmpty() || country.isEmpty()) {
            errorLabel.setText("Destination name and country are required.");
            return;
        }

        boolean success;
        if (editingDestination == null) {
            Destination d = new Destination(0, name, country, description, season);
            success = destinationDAO.addDestination(d);
        } else {
            editingDestination.setName(name);
            editingDestination.setCountry(country);
            editingDestination.setDescription(description);
            editingDestination.setBestSeason(season);
            success = destinationDAO.updateDestination(editingDestination);
        }

        if (success) {
            saved = true;
            dispose();
        } else {
            errorLabel.setText("Could not save destination. Check your database connection.");
        }
    }

    public boolean isSaved() {
        return saved;
    }
}