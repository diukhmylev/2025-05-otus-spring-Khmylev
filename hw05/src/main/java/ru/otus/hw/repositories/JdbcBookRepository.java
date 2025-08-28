package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<Book> findById(long id) {
        String sql = """
                SELECT 
                    b.id AS book_id, 
                    b.title, 
                    b.author_id,
                    a.id AS author_id, 
                    a.full_name,
                    g.id AS genre_id, 
                    g.name AS genre_name
                FROM books b
                    JOIN authors a ON b.author_id = a.id
                    LEFT JOIN books_genres bg ON b.id = bg.book_id
                    LEFT JOIN genres g ON bg.genre_id = g.id
                WHERE b.id = :id
                """;

        Map<String, Object> params = Map.of("id", id);

        return Optional.ofNullable(jdbcTemplate.query(sql, params, new BookResultSetExtractor()));

    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM books WHERE id = :id";
        int rows = jdbcTemplate.update(sql, Map.of("id", id));
        if (rows == 0) {
            throw new EntityNotFoundException("Book not found with id: " + id);
        }
    }

    private List<Book> getAllBooksWithoutGenres() {
        String sql = """
                SELECT 
                    b.id AS book_id, 
                    b.title, 
                    b.author_id, 
                    a.full_name
                FROM books b
                JOIN authors a ON b.author_id = a.id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Author author = new Author(rs.getLong("author_id"), rs.getString("full_name"));
            return new Book(rs.getLong("book_id"), rs.getString("title"), author, new ArrayList<>());
        });
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        String sql = "SELECT book_id, genre_id FROM books_genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new BookGenreRelation(rs.getLong("book_id"), rs.getLong("genre_id")));
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        Map<Long, Book> bookMap = booksWithoutGenres.stream()
                .collect(Collectors.toMap(Book::getId, b -> b));

        Map<Long, Genre> genreMap = genres.stream()
                .collect(Collectors.toMap(Genre::getId, g -> g));

        for (BookGenreRelation rel : relations) {
            Book book = bookMap.get(rel.bookId);
            Genre genre = genreMap.get(rel.genreId);
            if (book != null && genre != null) {
                book.getGenres().add(genre);
            }
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO books (title, author_id) VALUES (:title, :authorId)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("authorId", book.getAuthor().getId());

        jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
        Long generatedId = keyHolder.getKey().longValue();

        book.setId(generatedId);
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        String sql = "UPDATE books SET title = :title, author_id = :authorId WHERE id = :id";

        Map<String, Object> params = Map.of(
                "title", book.getTitle(),
                "authorId", book.getAuthor().getId(),
                "id", book.getId()
        );

        int updatedRows = jdbcTemplate.update(sql, params);
        if (updatedRows == 0) {
            throw new EntityNotFoundException("Book not found with id: " + book.getId());
        }
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        if (book.getGenres() == null || book.getGenres().isEmpty()) {
            return;
        }
        String sql = "INSERT INTO books_genres (book_id, genre_id) VALUES (:bookId, :genreId)";

        List<Map<String, Long>> batchValues = book.getGenres().stream()
                .map(genre -> Map.of("bookId", book.getId(), "genreId", genre.getId()))
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql, batchValues.toArray(new Map[0]));
    }

    private void removeGenresRelationsFor(Book book) {
        String sql = "DELETE FROM books_genres WHERE book_id = :bookId";
        jdbcTemplate.update(sql, Map.of("bookId", book.getId()));
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            return null;
        }
    }

    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            Book book = null;
            List<Genre> genres = new ArrayList<>();

            while (rs.next()) {
                if (book == null) {
                    long bookId = rs.getLong("book_id");
                    String title = rs.getString("title");
                    long authorId = rs.getLong("author_id");
                    String fullName = rs.getString("full_name");

                    Author author = new Author(authorId, fullName);
                    book = new Book(bookId, title, author, genres);
                }
                long genreId = rs.getLong("genre_id");
                String genreName = rs.getString("genre_name");

                if (genreId != 0) {
                    genres.add(new Genre(genreId, genreName));
                }
            }
            return book;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
