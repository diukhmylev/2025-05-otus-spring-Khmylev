package ru.otus.hw.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.domain.Board;
import ru.otus.hw.domain.Chair;

@MessagingGateway
public interface WorkshopGateway {
    @Gateway(requestChannel = "processBoard.input", replyTimeout = 5000)
    Chair processBoard(Board board);
}
