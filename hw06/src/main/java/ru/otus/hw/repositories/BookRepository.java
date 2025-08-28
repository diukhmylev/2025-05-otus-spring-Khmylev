package ru.otus.hw.repositories;

import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Book save(Book book);
    void deleteById(Long id);
    Optional<Book> findById(Long id);
    List<Book> findAll();
}
