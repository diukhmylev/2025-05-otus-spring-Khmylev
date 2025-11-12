package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import ru.otus.hw.domain.Board;
import ru.otus.hw.domain.Chair;
import ru.otus.hw.domain.Piece;
import ru.otus.hw.services.FinishService;
import ru.otus.hw.services.WoodWorkerService;

import java.util.List;

@Configuration
public class IntegrationConfig {

    @Bean
    public IntegrationFlow processBoard(WoodWorkerService worker, FinishService finisher) {
        return flow -> flow
                .handle(Board.class, (board, headers) -> {
                    System.out.println("Доска для изготовления стула из " + board.getWoodType() + " взята и распилена ");
                    return board;
                })
                .split(Board.class, Board::cutIntoPieces)
                .<Piece, String>route(Piece::getType, route -> route
                        .subFlowMapping("leg", sf -> sf.handle(worker, "processLeg"))
                        .subFlowMapping("seat", sf -> sf.handle(worker, "processSeat"))
                        .subFlowMapping("back", sf -> sf.handle(worker, "processBack"))
                        .defaultOutputToParentFlow()
                )
                .aggregate()
                .<List<Piece>, Chair>transform(Chair::new)
                .handle(finisher, "applyFinish");
    }
}
