package ru.otus.hw.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.jpa.Book;

import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {

}