package ru.otus.hw.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class DataInitializerCommand {

    private final AuthorService authorService;
    private final GenreService genreService;
    private final BookService bookService;
    private final CommentService commentService;

    @ShellMethod(value = "Initialize database from JSON", key = "init-db")
    public String initializeDatabase() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new FileInputStream("src/main/resources/initial_data.json");
        JsonNode root = mapper.readTree(is);

        root.get("authors").forEach(a -> {
            authorService.save(new Author(a.get("id").asText(), a.get("fullName").asText()));
        });

        root.get("genres").forEach(g -> {
            genreService.save(new Genre(g.get("id").asText(), g.get("name").asText()));
        });

        root.get("books").forEach(b -> {
            Author author = authorService.findById(b.get("authorId").asText()).orElse(null);
            List<Genre> genres = new ArrayList<>();
            b.get("genreIds").forEach(gi -> genreService.findById(gi.asText()).ifPresent(genres::add));
            Book book = new Book(b.get("id").asText(), b.get("title").asText(), author, genres, new ArrayList<>());
            bookService.save(book);
        });

        root.get("comments").forEach(c -> {
            Book book = bookService.findById(c.get("bookId").asText()).orElse(null);
            commentService.save(new Comment(c.get("id").asText(), c.get("text").asText(), Instant.now(), book));
        });

        return "Database initialized from JSON!";
    }
}
