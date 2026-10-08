package com.travelagency.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Shared color palette, fonts, and small reusable UI components so every
 * screen in the app looks consistent.
 */
public final class UIStyle {

    // --- Palette (light theme) ---
    public static final Color PRIMARY = new Color(0x2F6FED);        // main brand blue
    public static final Color PRIMARY_DARK = new Color(0x1E4FBF);   // hover/pressed
    public static final Color BACKGROUND = new Color(0xF5F7FA);     // app background
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_DARK = new Color(0x1F2937);
    public static final Color TEXT_MUTED = new Color(0x6B7280);
    public static final Color BORDER = new Color(0xE5E7EB);
    public static final Color SUCCESS = new Color(0x16A34A);
    public static final Color DANGER = new Color(0xDC2626);

    public static final Color[] ACCENTS = {
            new Color(0x2F6FED), // blue
            new Color(0x0891B2), // teal
            new Color(0x9333EA), // purple
            new Color(0xEA580C), // orange
            new Color(0x16A34A), // green
            new Color(0xDB2777)  // pink
    };

    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_LABEL = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_CARD_TITLE = new Font("SansSerif", Font.BOLD, 15);
    public static final Font FONT_CARD_SUB = new Font("SansSerif", Font.PLAIN, 11);

    private UIStyle() {}

    /** A flat, rounded, colored button used for primary actions (e.g. Log In). */
    public static class RoundedButton extends JButton {
        private final Color base;
        private final Color hover;
        private final int arc;

        public RoundedButton(String text, Color base, Color hover) {
            this(text, base, hover, 10);
        }

        public RoundedButton(String text, Color base, Color hover, int arc) {
            super(text);
            this.base = base;
            this.hover = hover;
            this.arc = arc;
            setForeground(Color.WHITE);
            setFont(FONT_BUTTON);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = getModel().isRollover() ? hover : base;
            if (getModel().isPressed()) fill = hover.darker();
            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, arc, arc));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** A card-style panel with rounded corners and a subtle border, for the dashboard grid. */
    public static class RoundedCard extends JPanel {
        private final int arc;

        public RoundedCard(int arc) {
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CARD_BG);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, arc, arc));
            g2.setColor(BORDER);
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, arc, arc));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Rounded text field border for a softer, modern input look. */
    public static void styleTextField(JTextField field) {
        field.setFont(FONT_LABEL);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }
}