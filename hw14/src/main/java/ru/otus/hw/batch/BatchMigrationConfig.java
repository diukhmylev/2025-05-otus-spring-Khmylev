package ru.otus.hw.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.models.documents.AuthorDocument;
import ru.otus.hw.models.documents.BookDocument;
import ru.otus.hw.models.documents.CommentDocument;
import ru.otus.hw.models.documents.GenreDocument;
import ru.otus.hw.models.jpa.Author;
import ru.otus.hw.models.jpa.Book;
import ru.otus.hw.models.jpa.Comment;
import ru.otus.hw.models.jpa.Genre;
import ru.otus.hw.repositories.mongo.AuthorMongoRepository;
import ru.otus.hw.repositories.mongo.BookMongoRepository;
import ru.otus.hw.repositories.mongo.CommentMongoRepository;
import ru.otus.hw.repositories.mongo.GenreMongoRepository;

@Configuration
@RequiredArgsConstructor
public class BatchMigrationConfig {

    private static final int CHUNK = 20;

    private final EntityManagerFactory emf;
    private final AuthorMongoRepository authorMongoRepository;
    private final GenreMongoRepository genreMongoRepository;
    private final BookMongoRepository bookMongoRepository;
    private final CommentMongoRepository commentMongoRepository;

    @Bean
    @StepScope
    public JpaPagingItemReader<Author> authorReader() {
        return new JpaPagingItemReaderBuilder<Author>()
                .name("authorReader")
                .entityManagerFactory(emf)
                .queryString("select a from Author a")
                .pageSize(CHUNK)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Genre> genreReader() {
        return new JpaPagingItemReaderBuilder<Genre>()
                .name("genreReader")
                .entityManagerFactory(emf)
                .queryString("select g from Genre g")
                .pageSize(CHUNK)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Book> bookReader() {
        return new JpaPagingItemReaderBuilder<Book>()
                .name("bookReader")
                .entityManagerFactory(emf)
                .queryString("select b from Book b join fetch b.author left join fetch b.genres")
                .pageSize(CHUNK)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Comment> commentReader() {
        return new JpaPagingItemReaderBuilder<Comment>()
                .name("commentReader")
                .entityManagerFactory(emf)
                .queryString("select c from Comment c join fetch c.book")
                .pageSize(CHUNK)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Author, AuthorDocument> authorProcessor() {
        return a -> new AuthorDocument(a.getId().toString(), a.getFullName());
    }

    @Bean
    @StepScope
    public ItemProcessor<Genre, GenreDocument> genreProcessor() {
        return g -> new GenreDocument(g.getId().toString(), g.getName());
    }

    @Bean
    @StepScope
    public ItemProcessor<Book, BookDocument> bookProcessor() {
        return b -> new BookDocument(
                b.getId().toString(),
                b.getTitle(),
                b.getAuthor().getId().toString(),
                b.getGenres().stream().map(g -> g.getId().toString()).toList()
        );
    }

    @Bean
    @StepScope
    public ItemProcessor<Comment, CommentDocument> commentProcessor() {
        return c -> new CommentDocument(
                c.getId().toString(),
                c.getText(),
                c.getBook().getId().toString()
        );
    }

    @Bean
    @StepScope
    public ItemWriter<AuthorDocument> authorWriter() {
        return items -> authorMongoRepository.saveAll(items);
    }

    @Bean
    @StepScope
    public ItemWriter<GenreDocument> genreWriter() {
        return items -> genreMongoRepository.saveAll(items);
    }

    @Bean
    @StepScope
    public ItemWriter<BookDocument> bookWriter() {
        return items -> bookMongoRepository.saveAll(items);
    }

    @Bean
    @StepScope
    public ItemWriter<CommentDocument> commentWriter() {
        return items -> commentMongoRepository.saveAll(items);
    }

    @Bean
    public Step migrateAuthorsStep(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager,
                                   JpaPagingItemReader<Author> authorReader,
                                   ItemProcessor<Author, AuthorDocument> authorProcessor,
                                   ItemWriter<AuthorDocument> authorWriter) {
        return new StepBuilder("migrateAuthorsStep", jobRepository)
                .<Author, AuthorDocument>chunk(CHUNK, transactionManager)
                .reader(authorReader)
                .processor(authorProcessor)
                .writer(authorWriter)
                .build();
    }

    @Bean
    public Step migrateGenresStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  JpaPagingItemReader<Genre> genreReader,
                                  ItemProcessor<Genre, GenreDocument> genreProcessor,
                                  ItemWriter<GenreDocument> genreWriter) {
        return new StepBuilder("migrateGenresStep", jobRepository)
                .<Genre, GenreDocument>chunk(CHUNK, transactionManager)
                .reader(genreReader)
                .processor(genreProcessor)
                .writer(genreWriter)
                .build();
    }

    @Bean
    public Step migrateBooksStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 JpaPagingItemReader<Book> bookReader,
                                 ItemProcessor<Book, BookDocument> bookProcessor,
                                 ItemWriter<BookDocument> bookWriter) {
        return new StepBuilder("migrateBooksStep", jobRepository)
                .<Book, BookDocument>chunk(CHUNK, transactionManager)
                .reader(bookReader)
                .processor(bookProcessor)
                .writer(bookWriter)
                .build();
    }

    @Bean
    public Step migrateCommentsStep(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager,
                                    JpaPagingItemReader<Comment> commentReader,
                                    ItemProcessor<Comment, CommentDocument> commentProcessor,
                                    ItemWriter<CommentDocument> commentWriter) {
        return new StepBuilder("migrateCommentsStep", jobRepository)
                .<Comment, CommentDocument>chunk(CHUNK, transactionManager)
                .reader(commentReader)
                .processor(commentProcessor)
                .writer(commentWriter)
                .build();
    }

    @Bean
    public Job migrationJob(JobRepository jobRepository,
                            Step migrateAuthorsStep,
                            Step migrateGenresStep,
                            Step migrateBooksStep,
                            Step migrateCommentsStep) {
        return new JobBuilder("migrationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(migrateAuthorsStep)
                .next(migrateGenresStep)
                .next(migrateBooksStep)
                .next(migrateCommentsStep)
                .build();
    }
}