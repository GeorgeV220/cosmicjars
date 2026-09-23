package com.georgev22.cosmicjars.gui;

import com.georgev22.cosmicjars.CosmicJars;
import com.georgev22.cosmicjars.pterodactyl.PterodactylSession;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public final class PterodactylSettingsPopup {

    private PterodactylSettingsPopup() {
    }

    public static void show(@Nullable Component parent) {
        PterodactylSession session = PterodactylSession.getInstance();

        JDialog dialog = new JDialog(
                parent instanceof Frame ? (Frame) parent : null,
                "Pterodactyl Settings",
                true
        );
        dialog.setSize(520, 320);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JTextField urlField = new JTextField(32);
        if (session.hasStoredCredentials()) {
            String stored = CosmicJars.getInstance().getConfig().getString("pterodactyl.url", "");
            urlField.setText(stored != null ? stored : "");
        }

        JPasswordField apiKeyField = new JPasswordField(32);
        JPasswordField passwordField = new JPasswordField(32);
        JPasswordField confirmField = new JPasswordField(32);

        JLabel hint = new JLabel("<html>Use a Client API key (ptlc_…). The key is encrypted with your password and never saved in plaintext.</html>");

        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        dialog.add(new JLabel("Panel URL:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        dialog.add(urlField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        dialog.add(new JLabel("API Key:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        dialog.add(apiKeyField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        dialog.add(new JLabel("Encryption Password:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        dialog.add(passwordField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        dialog.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        dialog.add(confirmField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        dialog.add(hint, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton clearButton = new JButton("Clear Saved");
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");
        buttons.add(clearButton);
        buttons.add(cancelButton);
        buttons.add(saveButton);

        row++;
        gbc.gridy = row;
        dialog.add(buttons, gbc);

        clearButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    dialog,
                    "Remove saved Pterodactyl URL and encrypted API key?",
                    "Clear Credentials",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                session.clearStoredCredentials();
                urlField.setText("");
                apiKeyField.setText("");
                passwordField.setText("");
                confirmField.setText("");
                JOptionPane.showMessageDialog(dialog, "Saved credentials cleared.");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        saveButton.addActionListener(e -> {
            String url = urlField.getText().trim();
            char[] apiKeyChars = apiKeyField.getPassword();
            char[] password = passwordField.getPassword();
            char[] confirm = confirmField.getPassword();
            try {
                if (url.isBlank()) {
                    JOptionPane.showMessageDialog(dialog, "Panel URL is required.");
                    return;
                }
                if (apiKeyChars.length == 0) {
                    JOptionPane.showMessageDialog(dialog, "API key is required.");
                    return;
                }
                if (password.length == 0) {
                    JOptionPane.showMessageDialog(dialog, "Encryption password is required.");
                    return;
                }
                if (!Arrays.equals(password, confirm)) {
                    JOptionPane.showMessageDialog(dialog, "Passwords do not match.");
                    return;
                }

                String apiKey = new String(apiKeyChars);
                session.saveAndUnlock(url, apiKey, password);
                JOptionPane.showMessageDialog(dialog, "Credentials saved and unlocked for this session.");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Failed to save credentials: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            } finally {
                Arrays.fill(apiKeyChars, '\0');
                Arrays.fill(password, '\0');
                Arrays.fill(confirm, '\0');
            }
        });

        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
