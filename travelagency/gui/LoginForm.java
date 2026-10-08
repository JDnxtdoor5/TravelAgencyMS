package com.travelagency.gui;

import com.travelagency.dao.UserDAO;
import com.travelagency.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    private final UserDAO userDAO = new UserDAO();

    public LoginForm() {
        setTitle("Travel Agency Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 480);
        setMinimumSize(new Dimension(700, 420));
        setLocationRelativeTo(null);

        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new GridLayout(1, 2));

        root.add(buildBrandPanel());
        root.add(buildFormPanel());

        add(root);
    }

    /** Left side: solid brand color panel with app name and tagline. */
    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(
                        0, 0, UIStyle.PRIMARY,
                        getWidth(), getHeight(), UIStyle.PRIMARY_DARK);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panel.setLayout(new GridBagLayout());

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("\u2708");   // airplane symbol
        icon.setFont(new Font("SansSerif", Font.PLAIN, 48));
        icon.setForeground(Color.WHITE);
        icon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Travel Agency");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title2 = new JLabel("Management System");
        title2.setFont(new Font("SansSerif", Font.BOLD, 28));
        title2.setForeground(Color.WHITE);
        title2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("<html><div style='width:260px'>Manage customers, destinations, " +
                "packages, bookings and payments — all in one place.</div></html>");
        tagline.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tagline.setForeground(new Color(255, 255, 255, 220));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);
        tagline.setBorder(new EmptyBorder(16, 0, 0, 0));

        inner.add(icon);
        inner.add(Box.createRigidArea(new Dimension(0, 10)));
        inner.add(title);
        inner.add(title2);
        inner.add(tagline);

        panel.add(inner);
        return panel;
    }

    /** Right side: white panel with the actual login form. */
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIStyle.CARD_BG);
        panel.setBorder(new EmptyBorder(40, 50, 40, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        JLabel welcome = new JLabel("Welcome back");
        welcome.setFont(UIStyle.FONT_TITLE);
        welcome.setForeground(UIStyle.TEXT_DARK);

        JLabel subtitle = new JLabel("Log in to continue");
        subtitle.setFont(UIStyle.FONT_SUBTITLE);
        subtitle.setForeground(UIStyle.TEXT_MUTED);
        subtitle.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(UIStyle.FONT_LABEL);
        userLabel.setForeground(UIStyle.TEXT_DARK);

        usernameField = new JTextField();
        UIStyle.styleTextField(usernameField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(UIStyle.FONT_LABEL);
        passLabel.setForeground(UIStyle.TEXT_DARK);
        passLabel.setBorder(new EmptyBorder(10, 0, 0, 0));

        passwordField = new JPasswordField();
        UIStyle.styleTextField(passwordField);

        UIStyle.RoundedButton loginButton = new UIStyle.RoundedButton(
                "Log In", UIStyle.PRIMARY, UIStyle.PRIMARY_DARK);
        loginButton.addActionListener(this::handleLogin);
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIStyle.FONT_SUBTITLE);
        statusLabel.setForeground(UIStyle.DANGER);

        JLabel hint = new JLabel("Default login — admin / admin123");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(UIStyle.TEXT_MUTED);

        int y = 0;
        gbc.gridy = y++; panel.add(welcome, gbc);
        gbc.gridy = y++; panel.add(subtitle, gbc);
        gbc.gridy = y++; panel.add(userLabel, gbc);
        gbc.gridy = y++; panel.add(usernameField, gbc);
        gbc.gridy = y++; panel.add(passLabel, gbc);
        gbc.gridy = y++; panel.add(passwordField, gbc);

        gbc.gridy = y++;
        gbc.insets = new Insets(20, 0, 6, 0);
        panel.add(loginButton, gbc);

        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridy = y++; panel.add(statusLabel, gbc);
        gbc.gridy = y++; panel.add(hint, gbc);

        passwordField.addActionListener(this::handleLogin);

        return panel;
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password.");
            return;
        }

        User user = userDAO.login(username, password);
        if (user != null) {
            statusLabel.setText(" ");
            new DashboardForm(user).setVisible(true);
            this.dispose();
        } else {
            statusLabel.setText("Invalid username or password.");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}