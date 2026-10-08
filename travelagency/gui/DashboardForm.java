package com.travelagency.gui;

import com.travelagency.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardForm extends JFrame {

    private final User currentUser;

    public DashboardForm(User currentUser) {
        this.currentUser = currentUser;

        setTitle("Travel Agency Management System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 560);
        setMinimumSize(new Dimension(680, 460));
        setLocationRelativeTo(null);

        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIStyle.BACKGROUND);

        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildModuleGrid(), BorderLayout.CENTER);

        add(root);
    }

    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UIStyle.CARD_BG);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyle.BORDER),
                new EmptyBorder(18, 28, 18, 28)));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel appName = new JLabel("\u2708  Travel Agency MS");
        appName.setFont(new Font("SansSerif", Font.BOLD, 18));
        appName.setForeground(UIStyle.TEXT_DARK);

        JLabel welcome = new JLabel("Welcome back, " + currentUser.getFullName() +
                "  \u00B7  " + currentUser.getRole());
        welcome.setFont(UIStyle.FONT_SUBTITLE);
        welcome.setForeground(UIStyle.TEXT_MUTED);
        welcome.setBorder(new EmptyBorder(4, 0, 0, 0));

        left.add(appName);
        left.add(welcome);
        topBar.add(left, BorderLayout.WEST);

        UIStyle.RoundedButton logoutButton = new UIStyle.RoundedButton(
                "Log Out", new Color(0xF3F4F6), new Color(0xE5E7EB));
        logoutButton.setForeground(UIStyle.TEXT_DARK);
        logoutButton.addActionListener(e -> {
            new LoginForm().setVisible(true);
            this.dispose();
        });

        JPanel rightWrap = new JPanel(new GridBagLayout());
        rightWrap.setOpaque(false);
        rightWrap.add(logoutButton);
        topBar.add(rightWrap, BorderLayout.EAST);

        return topBar;
    }

    private JPanel buildModuleGrid() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UIStyle.BACKGROUND);

        JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));
        grid.setBackground(UIStyle.BACKGROUND);
        grid.setBorder(new EmptyBorder(30, 30, 30, 30));

        grid.add(createModuleCard("\uD83D\uDC65", "Customers", "Manage customer records", UIStyle.ACCENTS[0]));
        grid.add(createModuleCard("\uD83C\uDF0D", "Destinations", "Browse travel destinations", UIStyle.ACCENTS[1]));
        grid.add(createModuleCard("\uD83D\uDCE6", "Packages", "Manage travel packages", UIStyle.ACCENTS[2]));
        grid.add(createModuleCard("\uD83D\uDCC5", "Bookings", "Create and track bookings", UIStyle.ACCENTS[3]));
        grid.add(createModuleCard("\uD83D\uDCB3", "Payments", "Record and track payments", UIStyle.ACCENTS[4]));

        if (currentUser.isAdmin()) {
            grid.add(createModuleCard("\uD83D\uDCCA", "Reports", "Revenue & booking reports", UIStyle.ACCENTS[5]));
        }

        wrapper.add(grid);
        return wrapper;
    }

    private JComponent createModuleCard(String emoji, String title, String subtitle, Color accent) {
        UIStyle.RoundedCard card = new UIStyle.RoundedCard(16);
        card.setPreferredSize(new Dimension(220, 150));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 20, 18, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Colored icon badge
        JLabel iconBadge = new JLabel(emoji, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBadge.setOpaque(false);
        iconBadge.setFont(new Font("SansSerif", Font.PLAIN, 22));
        iconBadge.setPreferredSize(new Dimension(46, 46));
        iconBadge.setMaximumSize(new Dimension(46, 46));
        iconBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyle.FONT_CARD_TITLE);
        titleLabel.setForeground(UIStyle.TEXT_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(14, 0, 4, 0));

        JLabel subtitleLabel = new JLabel("<html><div style='width:150px'>" + subtitle + "</div></html>");
        subtitleLabel.setFont(UIStyle.FONT_CARD_SUB);
        subtitleLabel.setForeground(UIStyle.TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel textWrap = new JPanel();
        textWrap.setOpaque(false);
        textWrap.setLayout(new BoxLayout(textWrap, BoxLayout.Y_AXIS));
        textWrap.add(iconBadge);
        textWrap.add(titleLabel);
        textWrap.add(subtitleLabel);

        card.add(textWrap, BorderLayout.NORTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                openModule(title);
            }
        });

        return card;
    }

    /** Opens the screen for the clicked module, or a placeholder if it isn't built yet. */
    private void openModule(String title) {
        switch (title) {
            case "Destinations":
                new DestinationsFrame().setVisible(true);
                break;
            case "Packages":
                new PackagesFrame().setVisible(true);
                break;
            case "Customers":
                new CustomersFrame().setVisible(true);
                break;
            case "Bookings":
                new BookingsFrame().setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this,
                        title + " module coming next — this dashboard is the entry point " +
                        "each module screen will plug into.",
                        title, JOptionPane.INFORMATION_MESSAGE);
        }
    }
}