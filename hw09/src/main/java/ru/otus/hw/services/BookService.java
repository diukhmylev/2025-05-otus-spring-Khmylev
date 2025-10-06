package ru.otus.hw.services;

import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;
import java.util.List;
import java.util.Optional;

public interface BookService {
    List<BookDto> findAll();
    Optional<BookDto> findById(String id);
    BookDto save(BookDto dto);
    void deleteById(String id);
}

