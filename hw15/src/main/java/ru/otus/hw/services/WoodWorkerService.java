package ru.otus.hw.services;

import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Piece;

@Service
public class WoodWorkerService {

    public Piece processLeg(Piece piece) {
        piece.setProcessed(true);
        System.out.println("Обработана ножка: " + piece);
        return piece;
    }

    public Piece processSeat(Piece piece) {
        piece.setProcessed(true);
        System.out.println("Обработано сиденье: " + piece);
        return piece;
    }

    public Piece processBack(Piece piece) {
        piece.setProcessed(true);
        System.out.println("Обработана спинка: " + piece);
        return piece;
    }
}
