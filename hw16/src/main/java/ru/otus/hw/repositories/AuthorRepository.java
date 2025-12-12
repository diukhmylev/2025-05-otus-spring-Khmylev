package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.hw.models.Author;

import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(
        path = "authors",
        collectionResourceRel = "authors"
)
public interface AuthorRepository extends JpaRepository<Author, UUID> {

    @RestResource(path = "by-full-name", rel = "byFullName")
    Optional<Author> findByFullName(String fullName);
}
