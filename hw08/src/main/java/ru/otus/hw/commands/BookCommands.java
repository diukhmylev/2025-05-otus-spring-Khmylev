package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class BookCommands {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final BookConverter bookConverter;

    @ShellMethod(value = "Find all books", key = {"ab", "all-books"})
    public String findAllBooks() {
        return bookService.findAll().stream()
                .map(bookConverter::bookToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(value = "Find book by id", key = {"bbid", "book-by-id"})
    public String findBookById(String id) {
        return bookService.findById(id)
                .map(bookConverter::bookToStringWithComments)
                .orElse("Book with id %s not found".formatted(id));
    }

    @ShellMethod(value = "Insert new book", key = {"bins", "book-insert"})
    public String insertBook(String title, String authorId, Set<String> genreIds) {
        var author = authorService.findById(authorId)
                .orElseGet(() -> new Author(authorId, "Unknown Author"));

        List<Genre> genres = new ArrayList<>();
        for (String gid : genreIds) {
            genres.add(genreService.findById(gid)
                    .orElseGet(() -> new Genre(gid, "Unknown Genre")));
        }

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenres(genres);

        Book saved = bookService.save(book);
        return bookConverter.bookToString(saved);
    }

    @ShellMethod(value = "Update book", key = {"bupd", "book-update"})
    public String updateBook(String id, String title, String authorId, Set<String> genreIds) {
        var book = bookService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id " + id));

        var author = authorService.findById(authorId)
                .orElseGet(() -> new Author(authorId, "Unknown Author"));

        List<Genre> genres = new ArrayList<>();
        for (String gid : genreIds) {
            genres.add(genreService.findById(gid)
                    .orElseGet(() -> new Genre(gid, "Unknown Genre")));
        }

        book.setTitle(title);
        book.setAuthor(author);
        book.setGenres(genres);

        Book updated = bookService.save(book);
        return bookConverter.bookToString(updated);
    }

    @ShellMethod(value = "Delete book by id", key = {"bdel", "book-delete"})
    public void deleteBook(String id) {
        bookService.deleteById(id);
    }
}