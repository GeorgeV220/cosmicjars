package com.georgev22.cosmicjars.gui;

import javax.swing.*;
import java.awt.*;

import com.georgev22.cosmicjars.CosmicJars;

public class ConfigPopup {

    private final static CosmicJars instance = CosmicJars.getInstance();

    public static void showConfigPopup() {
        JDialog configDialog = new JDialog((Frame) null, "Server Configuration", true);
        configDialog.setSize(500, 300);
        configDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel providerLabel = new JLabel("Provider:");
        String[] providers = {"Servers", "Modded", "Proxies", "MCJars"};
        JComboBox<String> providerDropdown = new JComboBox<>(providers);

        JLabel implementationLabel = new JLabel("Server Implementation:");
        String[] implementations = {"Arclight", "AsPaper", "Banner", "BungeeCord", "Canvas", "Fabric", "Folia", "Forge",
                "Leaves", "Legacy_Fabric", "Loohp_Limbo", "Mohist", "Nanolimbo", "NeoForge", "PandaSpigot", "Paper",
                "Pufferfish", "Purpur", "Quilt", "Spigot", "Sponge", "Vanilla", "Velocity", "Waterfall"};
        JComboBox<String> implementationDropdown = new JComboBox<>(implementations);
        JLabel versionLabel = new JLabel("Server Version:");
        JTextField versionField = new JTextField();

        JLabel jdkLabel = new JLabel("JDK Version:");
        JComboBox<String> jdkDropdown = new JComboBox<>(instance.getJDKUtilities().getOnlineJDKVersions());

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String selectedProvider = (String) providerDropdown.getSelectedItem();
            String selectedImplementation = (String) implementationDropdown.getSelectedItem();
            String serverVersion = versionField.getText();
            String selectedJDK = (String) jdkDropdown.getSelectedItem();

            if (selectedProvider == null || selectedImplementation == null || serverVersion == null) {
                JOptionPane.showMessageDialog(configDialog, "Please select a provider, implementation, and server version.");
                return;
            }

            instance.getConfig().set("server.type", selectedProvider.toLowerCase());
            instance.getConfig().set("server.implementation", selectedImplementation.toLowerCase());
            instance.getConfig().set("server.version", serverVersion);
            if (selectedJDK != null)
                instance.getConfig().set("server.jdkVersion", selectedJDK.split(" ")[0]);

            instance.saveConfig();
            instance.reloadConfig();

            instance.getLogger().info("Selected Provider: {}", selectedProvider);
            instance.getLogger().info("Selected Implementation: {}", selectedImplementation);
            instance.getLogger().info("Server Version: {}", serverVersion);
            instance.getLogger().info("JDK Version: {}", selectedJDK != null ? selectedJDK.split(" ")[0] : null);

            configDialog.dispose();
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        configDialog.add(providerLabel, gbc);
        gbc.gridx = 1;
        configDialog.add(providerDropdown, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        configDialog.add(implementationLabel, gbc);
        gbc.gridx = 1;
        configDialog.add(implementationDropdown, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        configDialog.add(versionLabel, gbc);
        gbc.gridx = 1;
        configDialog.add(versionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        configDialog.add(jdkLabel, gbc);
        gbc.gridx = 1;
        configDialog.add(jdkDropdown, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        configDialog.add(saveButton, gbc);

        configDialog.setVisible(true);
    }
}
