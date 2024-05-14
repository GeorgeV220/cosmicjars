package com.georgev22.cosmicjars.utilities;

import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConsoleOutputHandler extends OutputStream implements Runnable {
    private final BlockingQueue<String> queue;
    private final JTextPane textPane;
    private final Logger logger;
    private volatile boolean running = true;

    public ConsoleOutputHandler(JTextPane textPane, Logger logger) {
        this.textPane = textPane;
        this.logger = logger;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void addToQueue(String text) {
        //noinspection ResultOfMethodCallIgnored
        queue.offer(text);
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                String output = queue.take();
                appendToConsole(output);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void appendToConsole(String text) {
        StyledDocument doc = textPane.getStyledDocument();
        Style style = textPane.addStyle("Style", null);

        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            logger.error("Error writing to console: {}", e.getMessage());
        }
    }

    @Override
    public void write(int b) {
        this.appendToConsole(String.valueOf((char) b));
    }
}
