package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CommentRepositoryJpa.class)
@DisplayName("Тесты репозитория комментариев")
class CommentRepositoryJpaTest extends AbstractRepositoryTestData{

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepositoryJpa commentRepository;

    private List<Book> dbBooks;
    private List<Comment> dbComments;

    @BeforeEach
    void setUp() {
        dbBooks = getDbBooks();
        dbComments = getDbComments(dbBooks);
    }

    @Test
    @DisplayName("должен сохранять новый комментарий")
    void shouldSaveComment() {
        Book book = em.find(Book.class, 1L);
        Comment newComment = new Comment(null, "New comment", Instant.now(), book);

        var saved = commentRepository.save(newComment);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getText()).isEqualTo("New comment");
    }

    @Test
    @DisplayName("должен получать комментарий по id")
    void shouldReturnCommentById() {
        var expected = dbComments.get(0);
        var actual = commentRepository.findById(expected.getId());

        assertThat(actual).isPresent();
        assertThat(actual.get().getText()).isEqualTo(expected.getText());
    }

    @Test
    @DisplayName("должен удалять комментарий по id")
    void shouldDeleteComment() {
        var comment = dbComments.get(0);
        commentRepository.deleteById(comment.getId());
        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    @Test
    @DisplayName("должен получать все комментарии по id книги")
    void shouldReturnCommentsByBookId() {
        var book = dbBooks.get(0);

        var actualComments = commentRepository.findAllByBookId(book.getId());

        assertThat(actualComments).isNotEmpty();
        assertThat(actualComments)
                .allMatch(c -> c.getBook().getId().equals(book.getId()));

        var expectedTexts = dbComments.stream()
                .filter(c -> c.getBook().getId().equals(book.getId()))
                .map(Comment::getText)
                .toList();

        var actualTexts = actualComments.stream()
                .map(Comment::getText)
                .toList();

        assertThat(actualTexts)
                .containsExactlyInAnyOrderElementsOf(expectedTexts);
    }

}

