package com.georgev22.cosmicjars.utilities;

import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConsoleOutputHandler extends OutputStream implements Runnable {
    private final BlockingQueue<String> queue;
    private final JTextPane textPane;
    private final Logger logger;
    private final AnsiConsoleDocument ansiDocument;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private volatile boolean running = true;

    public ConsoleOutputHandler(JTextPane textPane, Logger logger) {
        this.textPane = textPane;
        this.logger = logger;
        this.queue = new LinkedBlockingQueue<>();
        this.ansiDocument = new AnsiConsoleDocument(
                textPane.getForeground() != null ? textPane.getForeground() : Color.WHITE,
                textPane.getBackground() != null ? textPane.getBackground() : Color.BLACK
        );
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
                SwingUtilities.invokeLater(() -> appendToConsole(output));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void appendToConsole(String text) {
        StyledDocument doc = textPane.getStyledDocument();
        try {
            ansiDocument.append(doc, text);
        } catch (BadLocationException e) {
            logger.error("Error writing to console: {}", e.getMessage());
        }
    }

    @Override
    public synchronized void write(int b) {
        buffer.write(b);
        if (b == '\n') {
            flushBuffer();
        }
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) {
        int end = off + len;
        for (int i = off; i < end; i++) {
            write(b[i] & 0xFF);
        }
    }

    @Override
    public synchronized void flush() {
        flushBuffer();
    }

    private void flushBuffer() {
        if (buffer.size() == 0) {
            return;
        }
        String text = buffer.toString(StandardCharsets.UTF_8);
        buffer.reset();
        addToQueue(text);
    }
}
