package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {

    @EntityGraph(attributePaths = {"author", "genres", "comments"})
    Optional<Book> findById(UUID id);

    @EntityGraph(attributePaths = {"author", "genres"})
    @Query("SELECT DISTINCT b FROM Book b")
    List<Book> findAllWithoutComments();

}