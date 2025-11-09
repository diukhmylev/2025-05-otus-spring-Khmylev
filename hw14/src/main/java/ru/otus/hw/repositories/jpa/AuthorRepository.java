package ru.otus.hw.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.jpa.Author;

import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
}