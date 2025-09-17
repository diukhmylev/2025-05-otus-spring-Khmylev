package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Тесты репозитория жанров")
class GenreRepositoryTest extends AbstractRepositoryTestData {

    @Autowired
    private GenreRepository genreRepository;

    private List<Genre> dbGenres;

    @BeforeEach
    void setUp() {
        dbGenres = getDbGenres();
    }

    @Test
    @DisplayName("должен загружать все жанры")
    void shouldReturnAllGenres() {
        var genres = genreRepository.findAll();
        assertThat(genres)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("books")
                .containsExactlyInAnyOrderElementsOf(dbGenres);
    }

    @Test
    @DisplayName("должен загружать жанры по id")
    void shouldReturnGenresByIds() {
        var ids = Set.of(1L, 3L, 5L);
        var genres = genreRepository.findAllByIds(ids);
        assertThat(genres).allMatch(g -> ids.contains(g.getId()));
    }
}
