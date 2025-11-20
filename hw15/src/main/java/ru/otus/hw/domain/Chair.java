package ru.otus.hw.domain;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class Chair {
    private final List<Piece> parts;
    private boolean finished;

    public void applyFinish() {
        this.finished = true;
    }

    public boolean isFinished() {
        return finished;
    }

    @Override
    public String toString() {
        return "Chair{" +
                "parts=" + parts +
                ", finished=" + finished +
                '}';
    }
}

