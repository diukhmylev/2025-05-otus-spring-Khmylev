package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;
    private final CommentRepository commentRepository;

    @Override
    public void run(String... args) {
        if (authorRepository.count() > 0 || bookRepository.count() > 0) {
            return;
        }

        Author author1 = authorRepository.save(new Author(UUID.fromString("42d12a48-78fb-4fff-a6ae-e2b57b21ab14"), "Author One"));
        Author author2 = authorRepository.save(new Author(UUID.fromString("5532bfe3-88ec-4a2c-ac83-e2e32624016e"), "Author Two"));

        Genre genre1 = genreRepository.save(new Genre(UUID.randomUUID(), "Genre One"));
        Genre genre2 = genreRepository.save(new Genre(UUID.randomUUID(), "Genre Two"));

        Book book1 = new Book(UUID.randomUUID(), "Book_1", author1, Set.of(genre1), Set.of());
        Book book2 = new Book(UUID.randomUUID(), "Book_2", author2, Set.of(genre1, genre2), Set.of());

        book1 = bookRepository.save(book1);
        book2 = bookRepository.save(book2);

        Comment comment1 = commentRepository.save(new Comment(UUID.randomUUID(), "Comment_1", book1));
        Comment comment2 = commentRepository.save(new Comment(UUID.randomUUID(), "Comment_2", book1));
        Comment comment3 = commentRepository.save(new Comment(UUID.randomUUID(), "Comment_3", book2));

        book1.setComments(Set.of(comment1, comment2));
        book2.setComments(Set.of(comment3));

        bookRepository.save(book1);
        bookRepository.save(book2);
    }
}
