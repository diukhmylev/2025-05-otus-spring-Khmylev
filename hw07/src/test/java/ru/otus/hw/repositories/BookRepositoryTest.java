package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Тесты репозитория книг")
class BookRepositoryTest extends AbstractRepositoryTestData {

    @Autowired
    private BookRepository bookRepository;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        dbBooks = getDbBooks();
    }

    @ParameterizedTest
    @MethodSource("expectedBooks")
    @DisplayName("должен загружать книгу по id вместе с комментариями")
    void shouldReturnBookById(Book expectedBook) {
        var actualOpt = bookRepository.findById(expectedBook.getId());
        assertThat(actualOpt).isPresent();

        var actual = actualOpt.get();

        assertThat(actual.getTitle()).isEqualTo(expectedBook.getTitle());
        assertThat(actual.getAuthor().getId()).isEqualTo(expectedBook.getAuthor().getId());

        var expectedGenreIds = expectedBook.getGenres().stream().map(Genre::getId).toList();
        var actualGenreIds = actual.getGenres().stream().map(Genre::getId).toList();
        assertThat(actualGenreIds).containsExactlyInAnyOrderElementsOf(expectedGenreIds);

        var expectedCommentTexts = expectedBook.getComments().stream().map(Comment::getText).toList();
        var actualCommentTexts = actual.getComments().stream().map(Comment::getText).toList();
        assertThat(actualCommentTexts).containsExactlyInAnyOrderElementsOf(expectedCommentTexts);
    }

    static Stream<Book> expectedBooks() {
        return getDbBooks().stream();
    }

    @Test
    @DisplayName("должен загружать все книги")
    void shouldReturnAllBooks() {
        var books = bookRepository.findAll();
        assertThat(books).hasSize(dbBooks.size());
    }

    @Test
    @DisplayName("должен сохранять новую книгу")
    void shouldSaveNewBook() {
        var book = new Book(null, "New Book", getDbAuthors().get(0),
                List.of(getDbGenres().get(0), getDbGenres().get(1)), new ArrayList<>());

        var saved = bookRepository.save(book);

        assertThat(saved.getId()).isNotNull();
        assertThat(bookRepository.findById(saved.getId()))
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringFields("comments")
                .isEqualTo(saved);
    }

    @Test
    @DisplayName("должен удалять книгу по id")
    void shouldDeleteBook() {
        var id = dbBooks.get(0).getId();
        bookRepository.deleteById(id);
        assertThat(bookRepository.findById(id)).isEmpty();
    }
}
