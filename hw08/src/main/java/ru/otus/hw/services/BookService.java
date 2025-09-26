package ru.otus.hw.services;

import ru.otus.hw.models.Book;
import java.util.List;
import java.util.Optional;

public interface BookService {
    Book save(Book book);
    List<Book> findAll();
    Optional<Book> findById(String id);
    void deleteById(String id);
}