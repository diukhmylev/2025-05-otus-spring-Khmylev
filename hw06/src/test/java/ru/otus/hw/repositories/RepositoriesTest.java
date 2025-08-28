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

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        BookRepositoryJpa.class,
        GenreRepositoryJpa.class,
        AuthorRepositoryJpa.class,
        CommentRepositoryJpa.class
})
@DisplayName("Тесты репозиториев и CRUD операций")
class RepositoriesTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private BookRepositoryJpa bookRepository;

    @Autowired
    private GenreRepositoryJpa genreRepository;

    @Autowired
    private AuthorRepositoryJpa authorRepository;

    @Autowired
    private CommentRepositoryJpa commentRepository;

    private List<Author> dbAuthors;
    private List<Genre> dbGenres;
    private List<Book> dbBooks;
    private List<Comment> dbComments;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
        dbComments = getDbComments(dbBooks);
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnBookById(Book expectedBook) {
        var actualOpt = bookRepository.findById(expectedBook.getId());
        assertThat(actualOpt).isPresent();

        var actual = actualOpt.get();

        assertThat(actual.getId()).isEqualTo(expectedBook.getId());
        assertThat(actual.getTitle()).isEqualTo(expectedBook.getTitle());

        assertThat(actual.getAuthor()).isNotNull();
        assertThat(actual.getAuthor().getId()).isEqualTo(expectedBook.getAuthor().getId());

        var expectedGenreIds = expectedBook.getGenres().stream()
                .map(g -> g.getId())
                .toList();
        var actualGenreIds = actual.getGenres().stream()
                .map(g -> g.getId())
                .toList();
        assertThat(actualGenreIds).containsExactlyInAnyOrderElementsOf(expectedGenreIds);
    }


    @DisplayName("должен загружать все книги")
    @Test
    void shouldReturnAllBooks() {
        var actualBooks = bookRepository.findAll();

        assertThat(actualBooks).hasSize(dbBooks.size());

        for (int i = 0; i < dbBooks.size(); i++) {
            var expected = dbBooks.get(i);
            var actual = actualBooks.get(i);

            assertThat(actual.getId()).isEqualTo(expected.getId());
            assertThat(actual.getTitle()).isEqualTo(expected.getTitle());

            assertThat(actual.getAuthor()).isNotNull();
            assertThat(actual.getAuthor().getId()).isEqualTo(expected.getAuthor().getId());

            var expectedGenreIds = expected.getGenres().stream()
                    .map(g -> g.getId())
                    .toList();
            var actualGenreIds = actual.getGenres().stream()
                    .map(g -> g.getId())
                    .toList();
            assertThat(actualGenreIds).containsExactlyInAnyOrderElementsOf(expectedGenreIds);
        }
    }


    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var book = new Book(null, "New Book", dbAuthors.get(0), List.of(dbGenres.get(0), dbGenres.get(1)));

        var saved = bookRepository.save(book);

        assertThat(saved.getId()).isNotNull();
        assertThat(bookRepository.findById(saved.getId())).isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(saved);
    }


    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteBook() {
        var id = dbBooks.get(0).getId();
        bookRepository.deleteById(id);
        assertThat(bookRepository.findById(id)).isEmpty();
    }

    @DisplayName("должен загружать все жанры")
    @Test
    void shouldReturnAllGenres() {
        var actual = genreRepository.findAll();
        assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(dbGenres);
    }

    @DisplayName("должен загружать жанры по id")
    @Test
    void shouldReturnGenresByIds() {
        var ids = Set.of(1L, 3L, 5L);
        var genres = genreRepository.findAllByIds(ids);
        assertThat(genres).allMatch(g -> ids.contains(g.getId()));
    }

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldSaveComment() {
        Book book = testEntityManager.find(Book.class, 1L);
        assertThat(book).isNotNull();

        Comment newComment = new Comment(null, "New comment", Instant.now(), book);

        Comment savedComment = commentRepository.save(newComment);

        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getText()).isEqualTo("New comment");
        assertThat(savedComment.getBook().getId()).isEqualTo(book.getId());
    }


    @DisplayName("должен получать комментарий по id")
    @Test
    void shouldReturnCommentById() {
        var expected = dbComments.get(0);
        var actualOpt = commentRepository.findById(expected.getId());

        assertThat(actualOpt).isPresent();
        var actual = actualOpt.get();

        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getText()).isEqualTo(expected.getText());

        assertThat(actual.getBook()).isNotNull();
        assertThat(actual.getBook().getId()).isEqualTo(expected.getBook().getId());
    }


    @DisplayName("должен получать все комментарии по книге")
    @Test
    void shouldReturnCommentsByBook() {
        var book = dbBooks.get(0);
        var comments = commentRepository.findAllByBookId(book.getId());
        assertThat(comments).allMatch(c -> c.getBook().getId().equals(book.getId()));
    }

    @DisplayName("должен удалять комментарий по id")
    @Test
    void shouldDeleteComment() {
        var comment = dbComments.get(0);
        commentRepository.deleteById(comment.getId());
        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4)
                .mapToObj(i -> new Author((long) i, "Author_" + i))
                .toList();
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7)
                .mapToObj(i -> new Genre((long) i, "Genre_" + i))
                .toList();
    }

    private static List<Book> getDbBooks(List<Author> authors, List<Genre> genres) {
        return IntStream.range(1, 4)
                .mapToObj(i -> new Book(
                        (long) i,
                        "BookTitle_" + i,
                        authors.get(i - 1),
                        genres.subList((i - 1) * 2, i * 2)
                ))
                .toList();
    }

    private static List<Comment> getDbComments(List<Book> books) {
        return List.of(
                new Comment(1L, "Comment 1 for Book 1", Instant.now(), books.get(0)),
                new Comment(2L, "Comment 2 for Book 1", Instant.now(), books.get(0)),
                new Comment(3L, "Comment 1 for Book 2", Instant.now(), books.get(1)),
                new Comment(4L, "Comment 1 for Book 3", Instant.now(), books.get(2)),
                new Comment(5L, "Comment 2 for Book 3", Instant.now(), books.get(2))
        );
    }

    private static List<Book> getDbBooks() {
        var authors = getDbAuthors();
        var genres = getDbGenres();
        return getDbBooks(authors, genres);
    }
}
