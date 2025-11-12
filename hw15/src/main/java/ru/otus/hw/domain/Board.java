package ru.otus.hw.domain;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class Board {
    private final String woodType;

    public String getWoodType() {
        return woodType;
    }

    public List<Piece> cutIntoPieces() {
        return List.of(
                new Piece("leg", woodType),
                new Piece("leg", woodType),
                new Piece("leg", woodType),
                new Piece("leg", woodType),
                new Piece("seat", woodType),
                new Piece("back", woodType)
        );
    }
}
