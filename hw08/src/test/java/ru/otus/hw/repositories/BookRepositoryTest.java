package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("test")
@DisplayName("Интеграционные тесты репозитория книг")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    private Author author1;
    private Genre genre1;
    private Genre genre2;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();

        author1 = authorRepository.save(new Author("1", "author_1"));
        genre1 = genreRepository.save(new Genre("1", "genres_1"));
        genre2 = genreRepository.save(new Genre("2", "genres_2"));

        bookRepository.saveAll(List.of(
                new Book("1", "book_1", author1, List.of(genre1, genre2), null),
                new Book("2", "book_2", author1, List.of(genre1), null)
        ));
    }

    @Test
    @DisplayName("должен загружать все книги")
    void shouldReturnAllBooks() {
        List<Book> books = bookRepository.findAll();
        assertThat(books).hasSize(2);
    }

    @Test
    @DisplayName("должен загружать книгу по id")
    void shouldReturnBookById() {
        Book book = bookRepository.findById("1").orElseThrow();
        assertThat(book.getTitle()).isEqualTo("book_1");
        assertThat(book.getAuthor().getId()).isEqualTo("1");
    }

    @Test
    @DisplayName("должен сохранять книгу")
    void shouldSaveBook() {
        Book book = new Book("3", "book_3", author1, List.of(genre2), null);
        Book saved = bookRepository.save(book);
        assertThat(saved.getId()).isEqualTo("3");
        assertThat(bookRepository.findById("3")).isPresent();
    }

    @Test
    @DisplayName("должен удалять книгу")
    void shouldDeleteBook() {
        bookRepository.deleteById("1");
        assertThat(bookRepository.findById("1")).isEmpty();
    }
}