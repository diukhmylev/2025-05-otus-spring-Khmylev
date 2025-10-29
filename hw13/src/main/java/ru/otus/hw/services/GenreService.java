package ru.otus.hw.services;

import ru.otus.hw.dto.GenreDto;

import java.util.List;
import java.util.UUID;

public interface GenreService {
    List<GenreDto> findAll();
    GenreDto getById(UUID id);
    GenreDto create(GenreDto dto);
    GenreDto update(UUID id, GenreDto dto);
    void delete(UUID id);
}
