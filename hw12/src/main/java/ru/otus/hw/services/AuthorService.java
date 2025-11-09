package ru.otus.hw.services;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthorService {
    List<AuthorDto> findAll();
    AuthorDto getById(UUID id);
    AuthorDto create(AuthorDto dto);
    AuthorDto update(UUID id, AuthorDto dto);
    void delete(UUID id);
}


