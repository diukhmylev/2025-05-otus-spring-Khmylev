package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(
        path = "books",
        collectionResourceRel = "books"
)
public interface BookRepository extends JpaRepository<Book, UUID> {

    @Override
    @EntityGraph(attributePaths = {"author", "genres", "comments"})
    Optional<Book> findById(UUID id);

    @RestResource(path = "without-comments", rel = "withoutComments")
    @EntityGraph(attributePaths = {"author", "genres"})
    @Query("SELECT DISTINCT b FROM Book b")
    List<Book> findAllWithoutComments();
}
