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

        Author author1 = authorRepository.save(new Author(null, "Author One"));
        Author author2 = authorRepository.save(new Author(null, "Author Two"));

        Genre genre1 = genreRepository.save(new Genre(null, "Genre One"));
        Genre genre2 = genreRepository.save(new Genre(null, "Genre Two"));

        Book book1 = new Book(null, "Book_1", author1, List.of(genre1), List.of());
        Book book2 = new Book(null, "Book_2", author2, List.of(genre1, genre2), List.of());

        book1 = bookRepository.save(book1);
        book2 = bookRepository.save(book2);

        Comment comment1 = commentRepository.save(new Comment(null, "Comment_1", null, book1));
        Comment comment2 = commentRepository.save(new Comment(null, "Comment_2", null, book1));
        Comment comment3 = commentRepository.save(new Comment(null, "Comment_3", null, book2));

        book1.setComments(List.of(comment1, comment2));
        book2.setComments(List.of(comment3));

        bookRepository.save(book1);
        bookRepository.save(book2);
    }
}
