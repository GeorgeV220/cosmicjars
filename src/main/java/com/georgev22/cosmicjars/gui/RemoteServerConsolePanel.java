package com.georgev22.cosmicjars.gui;

import com.georgev22.cosmicjars.CosmicJars;
import com.georgev22.cosmicjars.pterodactyl.PterodactylClient;
import com.georgev22.cosmicjars.pterodactyl.PterodactylSession;
import com.georgev22.cosmicjars.pterodactyl.PterodactylWebSocket;
import com.georgev22.cosmicjars.pterodactyl.model.PterodactylServer;
import com.georgev22.cosmicjars.utilities.AnsiConsoleDocument;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Remote console tab for a single Pterodactyl server.
 */
public class RemoteServerConsolePanel extends JPanel {

    private final @NotNull PterodactylServer server;
    private final @NotNull JTextPane consoleTextPane;
    private final @NotNull JLabel statusLabel;
    private final @NotNull PterodactylClient client;
    private final @NotNull PterodactylWebSocket webSocket;
    private final @NotNull AnsiConsoleDocument ansiDocument;

    public RemoteServerConsolePanel(@NotNull PterodactylServer server) {
        super(new BorderLayout());
        this.server = server;
        this.client = new PterodactylClient(PterodactylSession.getInstance());
        this.ansiDocument = new AnsiConsoleDocument(Color.WHITE, Color.BLACK);

        JPanel top = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Status: connecting…");
        JPanel powerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");
        JButton restartButton = new JButton("Restart");
        JButton killButton = new JButton("Kill");
        powerPanel.add(startButton);
        powerPanel.add(stopButton);
        powerPanel.add(restartButton);
        powerPanel.add(killButton);
        top.add(statusLabel, BorderLayout.WEST);
        top.add(powerPanel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        consoleTextPane = new JTextPane();
        consoleTextPane.setEditable(false);
        consoleTextPane.setBackground(Color.BLACK);
        consoleTextPane.setForeground(Color.WHITE);
        consoleTextPane.setFont(new Font("Roboto Mono", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(consoleTextPane);
        new SmartScroller(scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        HistoryTextField commandField = new HistoryTextField();
        JButton sendButton = new JButton("Send");
        Action sendAction = new AbstractAction("send") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = commandField.getText();
                if (command == null || command.isBlank()) {
                    return;
                }
                webSocket.sendCommand(command);
                commandField.setText("");
            }
        };
        commandField.setAction(sendAction);
        sendButton.setAction(sendAction);
        sendButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "send");
        sendButton.getActionMap().put("send", sendAction);

        JPanel commandPanel = new JPanel(new BorderLayout());
        commandPanel.add(commandField, BorderLayout.CENTER);
        commandPanel.add(sendButton, BorderLayout.EAST);
        add(commandPanel, BorderLayout.SOUTH);

        webSocket = new PterodactylWebSocket(
                client,
                server.getIdentifier(),
                PterodactylSession.getInstance().getPanelUrl(),
                new PterodactylWebSocket.Listener() {
                    @Override
                    public void onConsoleOutput(@NotNull String line) {
                        appendConsole(line);
                    }

                    @Override
                    public void onStatus(@NotNull String status) {
                        SwingUtilities.invokeLater(() -> statusLabel.setText("Status: " + status));
                    }

                    @Override
                    public void onDaemonMessage(@NotNull String message) {
                        appendConsole("[daemon] " + message + (message.endsWith("\n") ? "" : "\n"));
                    }

                    @Override
                    public void onError(@NotNull String message) {
                        appendConsole("[error] " + message + "\n");
                        CosmicJars.getInstance().getLogger().warn("Pterodactyl [{}]: {}", server.getName(), message);
                    }

                    @Override
                    public void onClosed() {
                        SwingUtilities.invokeLater(() -> statusLabel.setText("Status: disconnected"));
                    }
                }
        );

        startButton.addActionListener(e -> sendPower("start"));
        stopButton.addActionListener(e -> sendPower("stop"));
        restartButton.addActionListener(e -> sendPower("restart"));
        killButton.addActionListener(e -> sendPower("kill"));

        if (server.getStatus() != null) {
            statusLabel.setText("Status: " + server.getStatus());
        }
        webSocket.connect();
    }

    public @NotNull String getTabTitle() {
        return server.getName();
    }

    public @NotNull String getServerIdentifier() {
        return server.getIdentifier();
    }

    public void disposePanel() {
        webSocket.close();
    }

    private void sendPower(@NotNull String signal) {
        webSocket.setState(signal);
        new Thread(() -> {
            try {
                client.sendPower(server.getIdentifier(), signal);
            } catch (Exception e) {
                appendConsole("[error] Power " + signal + " failed: " + e.getMessage() + "\n");
            }
        }, "ptero-power-" + server.getIdentifier()).start();
    }

    private void appendConsole(@NotNull String text) {
        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = consoleTextPane.getStyledDocument();
            try {
                ansiDocument.append(doc, text);
            } catch (BadLocationException e) {
                CosmicJars.getInstance().getLogger().error("Error writing remote console: {}", e.getMessage());
            }
        });
    }
}
