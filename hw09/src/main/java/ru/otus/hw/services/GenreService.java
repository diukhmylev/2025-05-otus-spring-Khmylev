package ru.otus.hw.services;

import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreService {
    List<GenreDto> findAll();
    Optional<GenreDto> findById(String id);
    GenreDto save(GenreDto dto);
    void deleteById(String id);
}
