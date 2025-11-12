package ru.otus.hw.command;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.domain.Board;
import ru.otus.hw.domain.Chair;
import ru.otus.hw.gateway.WorkshopGateway;

import org.springframework.shell.standard.ShellOption;

@ShellComponent
@RequiredArgsConstructor
public class WorkshopCommands {

    private final WorkshopGateway gateway;

    @ShellMethod(key = "build-chair", value = "Преобразует доску в готовый стул")
    public String buildChair(
            @ShellOption(defaultValue = "oak", help = "Тип дерева") String wood) {

        Board board = new Board(wood);
        Chair chair = gateway.processBoard(board);
        return "Готовый стул: " + chair;
    }
}