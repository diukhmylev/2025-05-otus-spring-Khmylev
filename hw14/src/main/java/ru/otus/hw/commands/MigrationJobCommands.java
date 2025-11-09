package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.models.jpa.Author;
import ru.otus.hw.models.jpa.Book;
import ru.otus.hw.models.jpa.Comment;
import ru.otus.hw.models.jpa.Genre;
import ru.otus.hw.repositories.jpa.AuthorRepository;
import ru.otus.hw.repositories.jpa.BookRepository;
import ru.otus.hw.repositories.jpa.CommentRepository;
import ru.otus.hw.repositories.jpa.GenreRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ShellComponent
@RequiredArgsConstructor
public class MigrationJobCommands {

    private final JobLauncher jobLauncher;
    private final Job migrationJob;

    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;
    private final CommentRepository commentRepository;

    @ShellMethod(value = "Initialize test data in H2 database", key = "init-data")
    public String initData() {

        commentRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();

        List<Author> authors = IntStream.rangeClosed(1, 7)
                .mapToObj(i -> new Author(UUID.randomUUID(), "author_" + i))
                .collect(Collectors.toList());
        authorRepository.saveAll(authors);

        List<Genre> genres = IntStream.rangeClosed(1, 7)
                .mapToObj(i -> new Genre(UUID.randomUUID(), "genre_" + i))
                .collect(Collectors.toList());
        genreRepository.saveAll(genres);

        List<Book> books = IntStream.rangeClosed(1, 7)
                .mapToObj(i -> {
                    Book book = new Book();
                    book.setId(UUID.randomUUID());
                    book.setTitle("book_" + i);
                    book.setAuthor(authors.get(i - 1));
                    book.setGenres(Set.of(genres.get(i - 1)));
                    return book;
                })
                .collect(Collectors.toList());

        bookRepository.saveAll(books);

        List<Comment> comments = IntStream.rangeClosed(1, 7)
                .mapToObj(i -> new Comment(
                        UUID.randomUUID(),
                        "comment_" + i,
                        books.get(i - 1)
                ))
                .collect(Collectors.toList());
        commentRepository.saveAll(comments);

        return "H2 успешно инициализирована авторами, жанрами, книгами и комментариями.";
    }

    @ShellMethod(value = "Start migration job", key = "start-job")
    public String startMigrationJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLocalDateTime("startAt", LocalDateTime.now())
                .toJobParameters();
        jobLauncher.run(migrationJob, params);
        return "Миграция запущена.";
    }

    @ShellMethod(value = "Restart migration job", key = "restart-job")
    public String restartMigrationJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLocalDateTime("restartAt", LocalDateTime.now())
                .toJobParameters();
        jobLauncher.run(migrationJob, params);
        return "Миграция перезапущена.";
    }
}
