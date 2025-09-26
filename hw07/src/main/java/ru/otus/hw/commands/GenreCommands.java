package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.services.GenreService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class GenreCommands {

    private final GenreService genreService;
    private final GenreConverter genreConverter;

    @ShellMethod(value = "Find all genres", key = {"ag", "all-genres"})
    public String findAllGenres() {
        var genres = genreService.findAll();

        if (genres.isEmpty()) {
            return "No genres found";
        }

        return genres.stream()
                .map(genreConverter::genreToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }
}

