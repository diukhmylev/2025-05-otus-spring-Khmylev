package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("test")
@DisplayName("Интеграционные тесты репозитория жанров")
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    void setUp() {
        genreRepository.deleteAll();
        genreRepository.saveAll(List.of(
                new Genre("1", "genres_1"),
                new Genre("2", "genres_2"),
                new Genre("3", "genres_3")
        ));
    }

    @Test
    @DisplayName("должен загружать все жанры")
    void shouldReturnAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        assertThat(genres).hasSize(3);
    }

    @Test
    @DisplayName("должен загружать жанры по id")
    void shouldReturnGenresByIds() {
        Set<String> ids = Set.of("1", "3");
        List<Genre> genres = genreRepository.findAllById(ids);
        assertThat(genres).allMatch(g -> ids.contains(g.getId()));
    }
}