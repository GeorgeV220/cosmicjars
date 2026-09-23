package com.georgev22.cosmicjars.gui;

import com.georgev22.cosmicjars.CosmicJars;
import com.georgev22.cosmicjars.pterodactyl.PterodactylClient;
import com.georgev22.cosmicjars.pterodactyl.PterodactylSession;
import com.georgev22.cosmicjars.pterodactyl.model.PterodactylServer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Lists Pterodactyl servers and opens remote console tabs.
 */
public class PterodactylServersPanel extends JPanel {

    private final @NotNull DefaultTableModel tableModel;
    private final @NotNull JTable table;
    private final @NotNull Consumer<PterodactylServer> openConsoleConsumer;
    private final @NotNull JLabel messageLabel;

    public PterodactylServersPanel(@NotNull Consumer<PterodactylServer> openConsoleConsumer) {
        super(new BorderLayout(8, 8));
        this.openConsoleConsumer = openConsoleConsumer;

        messageLabel = new JLabel("Unlock Pterodactyl credentials to list servers.");
        add(messageLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Name", "Short ID", "Status", "Suspended"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(22);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Refresh");
        JButton openButton = new JButton("Open Console");
        JButton unlockButton = new JButton("Unlock");
        JButton settingsButton = new JButton("Settings");
        buttons.add(refreshButton);
        buttons.add(openButton);
        buttons.add(unlockButton);
        buttons.add(settingsButton);
        add(buttons, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> refreshServers());
        openButton.addActionListener(e -> openSelected());
        unlockButton.addActionListener(e -> {
            if (UnlockSecretsPopup.showUnlockDialog(this)) {
                refreshServers();
            }
        });
        settingsButton.addActionListener(e -> {
            PterodactylSettingsPopup.show(SwingUtilities.getWindowAncestor(this));
            if (PterodactylSession.getInstance().isUnlocked()) {
                refreshServers();
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelected();
                }
            }
        });
    }

    public void onShown() {
        PterodactylSession session = PterodactylSession.getInstance();
        if (!session.hasStoredCredentials()) {
            messageLabel.setText("No credentials saved. Click Settings to add your panel URL and API key.");
            return;
        }
        if (!session.isUnlocked()) {
            messageLabel.setText("Credentials are locked. Click Unlock and enter your encryption password.");
            return;
        }
        refreshServers();
    }

    private void refreshServers() {
        PterodactylSession session = PterodactylSession.getInstance();
        if (!session.isUnlocked()) {
            if (!UnlockSecretsPopup.showUnlockDialog(this)) {
                return;
            }
        }

        messageLabel.setText("Loading servers…");
        tableModel.setRowCount(0);
        new Thread(() -> {
            try {
                PterodactylClient client = new PterodactylClient(PterodactylSession.getInstance());
                List<PterodactylServer> servers = client.listServers();
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    for (PterodactylServer server : servers) {
                        tableModel.addRow(new Object[]{
                                server.getName(),
                                server.getUuidShort(),
                                server.getStatus() != null ? server.getStatus() : "unknown",
                                server.isSuspended() ? "yes" : "no"
                        });
                    }
                    messageLabel.setText("Loaded " + servers.size() + " server(s). Double-click or use Open Console.");
                    table.putClientProperty("ptero.servers", servers);
                });
            } catch (Exception e) {
                CosmicJars.getInstance().getLogger().error("Failed to list Pterodactyl servers", e);
                SwingUtilities.invokeLater(() ->
                        messageLabel.setText("Failed to list servers: " + e.getMessage()));
            }
        }, "ptero-list-servers").start();
    }

    @SuppressWarnings("unchecked")
    private void openSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a server first.");
            return;
        }
        Object stored = table.getClientProperty("ptero.servers");
        if (!(stored instanceof List<?> list) || row >= list.size()) {
            JOptionPane.showMessageDialog(this, "Refresh the server list and try again.");
            return;
        }
        PterodactylServer server = ((List<PterodactylServer>) list).get(row);
        openConsoleConsumer.accept(server);
    }
}
