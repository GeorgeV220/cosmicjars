package com.georgev22.cosmicjars.gui;

import com.georgev22.cosmicjars.pterodactyl.PterodactylSession;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public final class UnlockSecretsPopup {

    private UnlockSecretsPopup() {
    }

    /**
     * @return true if unlocked successfully
     */
    public static boolean showUnlockDialog(@Nullable Component parent) {
        PterodactylSession session = PterodactylSession.getInstance();
        if (session.isUnlocked()) {
            return true;
        }
        if (!session.hasStoredCredentials()) {
            JOptionPane.showMessageDialog(
                    parent,
                    "No Pterodactyl credentials saved. Open Pterodactyl Settings first.",
                    "Unlock Pterodactyl",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return false;
        }

        JPasswordField passwordField = new JPasswordField(24);
        int result = JOptionPane.showConfirmDialog(
                parent,
                new Object[]{"Enter encryption password:", passwordField},
                "Unlock Pterodactyl",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) {
            return false;
        }

        char[] password = passwordField.getPassword();
        try {
            session.unlock(password);
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Failed to unlock credentials. Check your password.\n" + e.getMessage(),
                    "Unlock Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        } finally {
            Arrays.fill(password, '\0');
            passwordField.setText("");
        }
    }
}
