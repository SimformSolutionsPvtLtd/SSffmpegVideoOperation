package com.simform.videooperations;

import com.arthenica.mobileffmpeg.Level;

public class LogMessage {

    private final long executionId;
    private final Level level;
    private final String text;

    public LogMessage(final long executionId, final Level level, final String text) {
        this.executionId = executionId;
        this.level = level;
        this.text = text;
    }

    public long getExecutionId() {
        return executionId;
    }

    public Level getLevel() {
        return level;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("LogMessage{");
        stringBuilder.append("executionId=");
        stringBuilder.append(executionId);
        stringBuilder.append(", level=");
        stringBuilder.append(level);
        stringBuilder.append(", text=");
        stringBuilder.append("\'");
        stringBuilder.append(text);
        stringBuilder.append('\'');
        stringBuilder.append('}');

        return stringBuilder.toString();
    }
}
