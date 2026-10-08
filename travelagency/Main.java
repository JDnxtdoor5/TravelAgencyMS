package com.travelagency;

import com.formdev.flatlaf.FlatLightLaf;
import com.travelagency.db.DBConnection;
import com.travelagency.gui.LoginForm;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // FlatLaf: modern flat LIGHT theme for the whole Swing UI
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {
            // fall back to default look and feel if FlatLaf isn't on the classpath
        }

        // Make sure embedded Derby shuts down cleanly when the app closes,
        // so its lock file doesn't stay held if the window is just closed.
        Runtime.getRuntime().addShutdownHook(new Thread(DBConnection::closeConnection));

        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}