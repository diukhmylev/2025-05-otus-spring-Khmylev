package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Тесты репозитория авторов")
class AuthorRepositoryTest extends AbstractRepositoryTestData {

    @Autowired
    private AuthorRepository authorRepository;

    private List<Author> dbAuthors;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
    }

    @Test
    @DisplayName("должен загружать всех авторов")
    void shouldReturnAllAuthors() {
        var authors = authorRepository.findAll();
        assertThat(authors)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("books")
                .containsExactlyInAnyOrderElementsOf(dbAuthors);
    }
}
