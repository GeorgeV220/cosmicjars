package com.georgev22.cosmicjars.utilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Appends text containing ANSI SGR escape sequences to a {@link StyledDocument}
 * with the corresponding Swing text attributes.
 */
public final class AnsiConsoleDocument {

    /**
     * Matches CSI sequences (colors, cursor, erase, etc.) and OSC sequences.
     */
    private static final Pattern ANSI_ESCAPE = Pattern.compile(
            "\u001B\\[[0-9;?]*[a-zA-Z]"
                    + "|\u001B\\].*?(?:\u0007|\u001B\\\\)"
                    + "|\u001B."
    );

    private static final Color[] STANDARD_COLORS = {
            new Color(0x00, 0x00, 0x00),
            new Color(0xCD, 0x31, 0x31),
            new Color(0x0D, 0xBC, 0x79),
            new Color(0xE5, 0xE5, 0x10),
            new Color(0x24, 0x72, 0xC8),
            new Color(0xBC, 0x3F, 0xBC),
            new Color(0x11, 0xA8, 0xCD),
            new Color(0xE5, 0xE5, 0xE5),
    };

    private static final Color[] BRIGHT_COLORS = {
            new Color(0x66, 0x66, 0x66),
            new Color(0xF1, 0x4C, 0x4C),
            new Color(0x23, 0xD1, 0x8B),
            new Color(0xF5, 0xF5, 0x43),
            new Color(0x3B, 0x8E, 0xE9),
            new Color(0xD6, 0x70, 0xD6),
            new Color(0x29, 0xB8, 0xDB),
            new Color(0xE5, 0xE5, 0xE5),
    };

    private final @NotNull Color defaultForeground;
    private final @NotNull Color defaultBackground;

    private @Nullable Color foreground;
    private @Nullable Color background;
    private boolean bold;
    private boolean italic;
    private boolean underline;

    public AnsiConsoleDocument(@NotNull Color defaultForeground, @NotNull Color defaultBackground) {
        this.defaultForeground = defaultForeground;
        this.defaultBackground = defaultBackground;
    }

    /**
     * Appends {@code text} to {@code document}, interpreting ANSI color codes.
     */
    public synchronized void append(@NotNull StyledDocument document, @NotNull String text) throws BadLocationException {
        Matcher matcher = ANSI_ESCAPE.matcher(text);
        int lastEnd = 0;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                insert(document, text.substring(lastEnd, matcher.start()));
            }
            String escape = matcher.group();
            if (escape.startsWith("\u001B[") && escape.endsWith("m")) {
                applySgr(escape);
            }
            lastEnd = matcher.end();
        }

        if (lastEnd < text.length()) {
            insert(document, text.substring(lastEnd));
        }
    }

    private void insert(@NotNull StyledDocument document, @NotNull String text) throws BadLocationException {
        if (text.isEmpty()) {
            return;
        }
        document.insertString(document.getLength(), text, currentAttributes());
    }

    private @NotNull SimpleAttributeSet currentAttributes() {
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs, foreground != null ? foreground : defaultForeground);
        StyleConstants.setBackground(attrs, background != null ? background : defaultBackground);
        StyleConstants.setBold(attrs, bold);
        StyleConstants.setItalic(attrs, italic);
        StyleConstants.setUnderline(attrs, underline);
        return attrs;
    }

    private void applySgr(@NotNull String escape) {
        String params = escape.substring(2, escape.length() - 1);
        if (params.isEmpty()) {
            reset();
            return;
        }

        List<Integer> codes = parseCodes(params);
        for (int i = 0; i < codes.size(); i++) {
            int code = codes.get(i);
            switch (code) {
                case 0 -> reset();
                case 1 -> bold = true;
                case 3 -> italic = true;
                case 4 -> underline = true;
                case 22 -> bold = false;
                case 23 -> italic = false;
                case 24 -> underline = false;
                case 39 -> foreground = null;
                case 49 -> background = null;
                case 38 -> i = applyExtendedColor(codes, i, true);
                case 48 -> i = applyExtendedColor(codes, i, false);
                default -> {
                    if (code >= 30 && code <= 37) {
                        foreground = STANDARD_COLORS[code - 30];
                    } else if (code >= 40 && code <= 47) {
                        background = STANDARD_COLORS[code - 40];
                    } else if (code >= 90 && code <= 97) {
                        foreground = BRIGHT_COLORS[code - 90];
                    } else if (code >= 100 && code <= 107) {
                        background = BRIGHT_COLORS[code - 100];
                    }
                }
            }
        }
    }

    private int applyExtendedColor(@NotNull List<Integer> codes, int index, boolean isForeground) {
        if (index + 1 >= codes.size()) {
            return index;
        }
        int mode = codes.get(index + 1);
        if (mode == 5 && index + 2 < codes.size()) {
            Color color = ansi256(codes.get(index + 2));
            if (isForeground) {
                foreground = color;
            } else {
                background = color;
            }
            return index + 2;
        }
        if (mode == 2 && index + 4 < codes.size()) {
            Color color = new Color(
                    clamp(codes.get(index + 2)),
                    clamp(codes.get(index + 3)),
                    clamp(codes.get(index + 4))
            );
            if (isForeground) {
                foreground = color;
            } else {
                background = color;
            }
            return index + 4;
        }
        return index;
    }

    private void reset() {
        foreground = null;
        background = null;
        bold = false;
        italic = false;
        underline = false;
    }

    private static @NotNull List<Integer> parseCodes(@NotNull String params) {
        List<Integer> codes = new ArrayList<>();
        for (String part : params.split(";")) {
            if (part.isEmpty()) {
                codes.add(0);
                continue;
            }
            try {
                codes.add(Integer.parseInt(part));
            } catch (NumberFormatException ignored) {
            }
        }
        return codes;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    /**
     * Converts an ANSI 256-color index to an RGB {@link Color}.
     */
    private static @NotNull Color ansi256(int index) {
        int i = clamp(index);
        if (i < 8) {
            return STANDARD_COLORS[i];
        }
        if (i < 16) {
            return BRIGHT_COLORS[i - 8];
        }
        if (i < 232) {
            int cube = i - 16;
            int r = cube / 36;
            int g = (cube % 36) / 6;
            int b = cube % 6;
            return new Color(cubeChannel(r), cubeChannel(g), cubeChannel(b));
        }
        int gray = 8 + (i - 232) * 10;
        return new Color(gray, gray, gray);
    }

    private static int cubeChannel(int value) {
        return value == 0 ? 0 : 55 + value * 40;
    }
}
