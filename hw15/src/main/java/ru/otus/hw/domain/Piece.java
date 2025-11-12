package ru.otus.hw.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Piece {
    private final String type;
    private final String woodType;
    private boolean processed = false;

    public String getType() {
        return type;
    }

    public String getWoodType() {
        return woodType;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    @Override
    public String toString() {
        return "Piece{" + "type='" + type + '\'' + ", processed=" + processed + '}';
    }
}
