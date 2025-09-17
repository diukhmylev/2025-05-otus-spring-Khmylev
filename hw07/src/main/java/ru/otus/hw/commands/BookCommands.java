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
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
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
    public String findBookById(long id) {
        return bookService.findById(id)
                .map(bookConverter::bookToStringWithComments)
                .orElse("Book with id %d not found".formatted(id));
    }

    @ShellMethod(value = "Insert new book", key = {"bins", "book-insert"})
    public String insertBook(String title, long authorId, Set<Long> genreIds) {
        var author = authorService.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Author with id %d not found".formatted(authorId)));

        var genres = genreService.findAllByIds(genreIds);
        if (genres.isEmpty()) {
            throw new IllegalArgumentException("No valid genres found for ids: " + genreIds);
        }

        var book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenres(new ArrayList<>(genres));

        var savedBook = bookService.save(book);
        return bookConverter.bookToString(savedBook);
    }

    @ShellMethod(value = "Update book", key = {"bupd", "book-update"})
    public String updateBook(long id, String title, long authorId, Set<Long> genreIds) {
        var existingBook = bookService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id " + id));

        var author = authorService.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Author with id %d not found".formatted(authorId)));

        var genres = genreService.findAllByIds(genreIds);
        if (genres.isEmpty()) {
            throw new IllegalArgumentException("No valid genres found for ids: " + genreIds);
        }

        existingBook.setTitle(title);
        existingBook.setAuthor(author);
        existingBook.setGenres(genres);

        var updatedBook = bookService.save(existingBook);
        return bookConverter.bookToString(updatedBook);
    }

    @ShellMethod(value = "Delete book by id", key = {"bdel", "book-delete"})
    public void deleteBook(long id) {
        bookService.deleteById(id);
    }
}
