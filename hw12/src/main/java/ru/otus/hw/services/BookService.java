package ru.otus.hw.services;

import ru.otus.hw.dto.BookDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookService {
    List<BookDto> findAll();
    Optional<BookDto> findById(UUID id);
    BookDto save(BookDto dto);
    void deleteById(UUID id);
}

