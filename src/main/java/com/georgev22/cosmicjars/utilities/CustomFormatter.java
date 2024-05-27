package com.georgev22.cosmicjars.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomFormatter extends Formatter {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Override
    public String format(LogRecord record) {
        return "[" +
                dateFormat.format(new Date(record.getMillis())) +
                "] [" +
                record.getLoggerName() +
                "/" +
                record.getLevel().getName() +
                "]: " +
                formatMessage(record) +
                "\n";
    }
}
