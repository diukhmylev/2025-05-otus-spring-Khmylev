package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Author;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("test")
@DisplayName("Интеграционные тесты репозитория авторов")
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    void setUp() {
        authorRepository.deleteAll();
        authorRepository.saveAll(List.of(
                new Author("1", "author_1"),
                new Author("2", "author_2"),
                new Author("3", "author_3")
        ));
    }

    @Test
    @DisplayName("должен загружать всех авторов")
    void shouldReturnAllAuthors() {
        List<Author> authors = authorRepository.findAll();
        assertThat(authors).hasSize(3);
    }

    @Test
    @DisplayName("должен загружать автора по id")
    void shouldReturnAuthorById() {
        Optional<Author> authorOpt = authorRepository.findById("1");
        assertThat(authorOpt).isPresent();
        assertThat(authorOpt.get().getFullName()).isEqualTo("author_1");
    }

    @Test
    @DisplayName("должен сохранять нового автора")
    void shouldSaveAuthor() {
        Author author = new Author("4", "author_4");
        Author saved = authorRepository.save(author);
        assertThat(saved.getId()).isEqualTo("4");
        assertThat(authorRepository.findById("4")).isPresent();
    }

    @Test
    @DisplayName("должен удалять автора по id")
    void shouldDeleteAuthor() {
        authorRepository.deleteById("1");
        assertThat(authorRepository.findById("1")).isEmpty();
    }
}