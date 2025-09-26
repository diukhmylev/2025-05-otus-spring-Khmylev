package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("test")
@DisplayName("Интеграционные тесты репозитория комментариев")
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    private Book book1;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();

        Author author = authorRepository.save(new Author("1", "author_1"));
        Genre genre = genreRepository.save(new Genre("1", "genres_1"));
        book1 = bookRepository.save(new Book("1", "book_1", author, List.of(genre), null));

        commentRepository.saveAll(List.of(
                new Comment("1", "comment_1", Instant.now(), book1),
                new Comment("2", "comment_2", Instant.now(), book1)
        ));
    }

    @Test
    @DisplayName("должен загружать все комментарии книги")
    void shouldReturnCommentsByBookId() {
        List<Comment> comments = commentRepository.findAllByBookId(book1.getId());
        assertThat(comments).hasSize(2);
        assertThat(comments).allMatch(c -> c.getBook().getId().equals(book1.getId()));
    }

    @Test
    @DisplayName("должен сохранять комментарий")
    void shouldSaveComment() {
        Comment comment = new Comment("3", "comment_3", Instant.now(), book1);
        Comment saved = commentRepository.save(comment);
        assertThat(saved.getId()).isEqualTo("3");
        assertThat(commentRepository.findById("3")).isPresent();
    }

    @Test
    @DisplayName("должен удалять комментарий")
    void shouldDeleteComment() {
        commentRepository.deleteById("1");
        assertThat(commentRepository.findById("1")).isEmpty();
    }
}