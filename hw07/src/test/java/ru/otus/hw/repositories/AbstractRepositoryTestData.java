package ru.otus.hw.repositories;

import ru.otus.hw.models.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public abstract class AbstractRepositoryTestData {

    protected static List<Author> getDbAuthors() {
        return IntStream.range(1, 4)
                .mapToObj(i -> new Author((long) i, "Author_" + i))
                .toList();
    }

    protected static List<Genre> getDbGenres() {
        return IntStream.range(1, 7)
                .mapToObj(i -> new Genre((long) i, "Genre_" + i))
                .toList();
    }

    protected static List<Comment> getDbComments(List<Book> books) {
        return List.of(
                new Comment(1L, "Comment 1 for Book 1", Instant.now(), books.get(0)),
                new Comment(2L, "Comment 2 for Book 1", Instant.now(), books.get(0)),
                new Comment(3L, "Comment 1 for Book 2", Instant.now(), books.get(1)),
                new Comment(4L, "Comment 1 for Book 3", Instant.now(), books.get(2)),
                new Comment(5L, "Comment 2 for Book 3", Instant.now(), books.get(2))
        );
    }

    protected static List<Book> getDbBooks() {
        var authors = getDbAuthors();
        var genres = getDbGenres();

        var book1 = new Book(
                1L,
                "BookTitle_1",
                authors.get(0),
                genres.subList(0, 2),
                new ArrayList<>()
        );
        var book2 = new Book(
                2L,
                "BookTitle_2",
                authors.get(1),
                genres.subList(2, 4),
                new ArrayList<>()
        );
        var book3 = new Book(
                3L,
                "BookTitle_3",
                authors.get(2),
                genres.subList(4, 6),
                new ArrayList<>()
        );

        var comments = List.of(
                new Comment(1L, "Comment 1 for Book 1", Instant.now(), book1),
                new Comment(2L, "Comment 2 for Book 1", Instant.now(), book1),
                new Comment(3L, "Comment 1 for Book 2", Instant.now(), book2),
                new Comment(4L, "Comment 1 for Book 3", Instant.now(), book3),
                new Comment(5L, "Comment 2 for Book 3", Instant.now(), book3)
        );

        book1.getComments().addAll(comments.subList(0, 2));
        book2.getComments().add(comments.get(2));
        book3.getComments().addAll(comments.subList(3, 5));

        return List.of(book1, book2, book3);
    }
}