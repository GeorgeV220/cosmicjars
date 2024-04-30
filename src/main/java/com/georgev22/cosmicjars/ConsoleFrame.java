package com.georgev22.cosmicjars;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ConsoleFrame extends JFrame {
    private final JTextPane consoleTextPane;
    private final JTextField commandTextField;
    private final JTextArea infoPanelTextArea;
    private static ConsoleFrame instance;

    public static ConsoleFrame getInstance() {
        return instance;
    }

    public ConsoleFrame(String[] args) {
        instance = this;
        setTitle("Console Program");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        // Main panel containing console, command input, and info panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Console panel
        JPanel consolePanel = new JPanel(new BorderLayout());
        consoleTextPane = new JTextPane();
        consoleTextPane.setEditable(false);
        consoleTextPane.setBackground(Color.BLACK);
        consoleTextPane.setForeground(Color.WHITE);
        JScrollPane consoleScrollPane = new JScrollPane(consoleTextPane);
        consolePanel.add(consoleScrollPane, BorderLayout.CENTER);
        mainPanel.add(consolePanel, BorderLayout.CENTER);

        // Command input panel
        JPanel commandPanel = new JPanel(new BorderLayout());
        commandTextField = new JTextField();
        JButton sendButton = new JButton("Send");
        Action action = new AbstractAction("send") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = commandTextField.getText();
                Main.getInstance().sendCommandToServer(command);
                commandTextField.setText("");
            }
        };
        commandTextField.setAction(action);
        sendButton.setAction(action);
        sendButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "send");
        sendButton.getActionMap().put("send", action);

        commandPanel.add(commandTextField, BorderLayout.CENTER);
        commandPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(commandPanel, BorderLayout.SOUTH);

        // Info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanelTextArea = new JTextArea();
        infoPanelTextArea.setEditable(false);
        JScrollPane infoScrollPane = new JScrollPane(infoPanelTextArea);
        infoPanel.add(infoScrollPane, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.EAST);

        getContentPane().add(mainPanel);

        // Redirect System.out and System.err to JTextAreas
        PrintStream consolePrintStream = new PrintStream(new ConsoleOutputStream(consoleTextPane));
        System.setOut(consolePrintStream);
        System.setErr(consolePrintStream);
    }

    public void printToConsole(String text) {
        consoleTextPane.setText(consoleTextPane.getText() + text + "\n");
    }

    public void addToInfoPanel(String text) {
        infoPanelTextArea.append(text + "\n");
    }

    // Custom OutputStream to redirect output to JTextArea
    private static class ConsoleOutputStream extends OutputStream {
        private final JTextPane textPane;

        public ConsoleOutputStream(JTextPane textPane) {
            this.textPane = textPane;
        }

        @Override
        public void write(int b) {
            StyledDocument doc = textPane.getStyledDocument();
            Style style = textPane.addStyle("Style", null);

            try {
                doc.insertString(doc.getLength(), String.valueOf((char) b), style);
            } catch (BadLocationException e) {
                Main.getInstance().getLogger().error("Error writing to console: {}", e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConsoleFrame frame = new ConsoleFrame(args);
            frame.setVisible(true);
            // Run Main.start(args)
            Main mainInstance = new Main(true, args);
            Main.setInstance(mainInstance);
            mainInstance.start();
        });
    }
}
