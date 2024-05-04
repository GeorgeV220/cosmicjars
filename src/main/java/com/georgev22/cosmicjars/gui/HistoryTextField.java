package com.georgev22.cosmicjars.gui;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryTextField extends JTextField {
    private final List<String> history = new ArrayList<>();
    private int historyIndex = -1;
    private boolean navigatingHistory = false;

    public HistoryTextField() {
        this(0);
    }

    public HistoryTextField(int columns) {
        super(columns);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }
        });
    }

    private void handleKeyPressed(@NotNull KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN) {
            if (history.isEmpty())
                return;

            navigatingHistory = true;
            if (keyCode == KeyEvent.VK_UP) {
                if (historyIndex == -1) {
                    historyIndex = history.size() - 1;
                } else {
                    historyIndex = Math.max(0, historyIndex - 1);
                }
            } else {
                historyIndex = Math.min(history.size() - 1, historyIndex + 1);
            }
            setText(history.get(historyIndex));
            e.consume();
        } else if (keyCode == KeyEvent.VK_ENTER) {
            navigatingHistory = false;
            historyIndex = -1;
            String text = getText().trim();
            if (!text.isEmpty()) {
                if (!history.contains(text)) {
                    history.add(text);
                }
            }
        }
    }


    @Override
    public void setText(String t) {
        super.setText(t);
        if (!navigatingHistory) {
            historyIndex = -1;
        }
    }
}