package ru.otus.hw.batch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.documents.AuthorDocument;
import ru.otus.hw.models.documents.BookDocument;
import ru.otus.hw.models.documents.CommentDocument;
import ru.otus.hw.models.documents.GenreDocument;
import ru.otus.hw.models.jpa.Author;
import ru.otus.hw.models.jpa.Book;
import ru.otus.hw.models.jpa.Comment;
import ru.otus.hw.models.jpa.Genre;
import ru.otus.hw.repositories.jpa.AuthorRepository;
import ru.otus.hw.repositories.jpa.BookRepository;
import ru.otus.hw.repositories.jpa.CommentRepository;
import ru.otus.hw.repositories.jpa.GenreRepository;
import ru.otus.hw.repositories.mongo.AuthorMongoRepository;
import ru.otus.hw.repositories.mongo.BookMongoRepository;
import ru.otus.hw.repositories.mongo.CommentMongoRepository;
import ru.otus.hw.repositories.mongo.GenreMongoRepository;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@ActiveProfiles("test")
@SpringBootTest
class BatchMigrationIntegrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AuthorMongoRepository authorMongoRepository;

    @Autowired
    private BookMongoRepository bookMongoRepository;

    @Autowired
    private GenreMongoRepository genreMongoRepository;

    @Autowired
    private CommentMongoRepository commentMongoRepository;

    @BeforeEach
    public void setup() {
        jobRepositoryTestUtils.removeJobExecutions();

        commentRepository.deleteAll();
        bookRepository.deleteAll();
        genreRepository.deleteAll();
        authorRepository.deleteAll();

        commentMongoRepository.deleteAll();
        bookMongoRepository.deleteAll();
        genreMongoRepository.deleteAll();
        authorMongoRepository.deleteAll();

        Author author = new Author();
        author.setId(UUID.randomUUID());
        author.setFullName("Test Author");
        authorRepository.save(author);

        Genre genre = new Genre();
        genre.setId(UUID.randomUUID());
        genre.setName("Test Genre");
        genreRepository.save(genre);

        Book book = new Book();
        book.setId(UUID.randomUUID());
        book.setTitle("Test Book");
        book.setAuthor(author);
        Set<Genre> genres = new HashSet<>();
        genres.add(genre);
        book.setGenres(genres);
        bookRepository.save(book);

        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());
        comment.setText("Test Comment");
        comment.setBook(book);
        commentRepository.save(comment);

        Book persistedBook = bookRepository.findById(book.getId()).orElseThrow();
        Set<Comment> comments = new HashSet<>();
        comments.add(comment);
        persistedBook.setComments(comments);
        bookRepository.save(persistedBook);
    }


    @Test
    void testMigrationJob() throws Exception {
        Job job = jobLauncherTestUtils.getJob();
        assertThat(job).isNotNull()
                .extracting(Job::getName)
                .isEqualTo("migrationJob");

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(
                new JobParametersBuilder()
                        .addString("startAt", Instant.now().toString())
                        .toJobParameters()
        );

        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        List<AuthorDocument> authors = authorMongoRepository.findAll();
        assertThat(authors).hasSize(1);
        assertThat(authors.get(0).getFullName()).isEqualTo("Test Author");

        List<GenreDocument> genres = genreMongoRepository.findAll();
        assertThat(genres).hasSize(1);
        assertThat(genres.get(0).getName()).isEqualTo("Test Genre");

        List<BookDocument> books = bookMongoRepository.findAll();
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Test Book");

        List<CommentDocument> comments = commentMongoRepository.findAll();
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getText()).isEqualTo("Test Comment");
    }
}
