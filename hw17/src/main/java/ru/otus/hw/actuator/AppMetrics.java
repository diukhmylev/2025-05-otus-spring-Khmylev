package ru.otus.hw.actuator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppMetrics {

    private final MeterRegistry meterRegistry;

    private Counter booksCreated;
    private Counter genresCreated;
    private Counter commentsCreated;
    private Counter authorCreated;

    @PostConstruct
    public void init() {
        booksCreated = meterRegistry.counter("app.books.created");
        genresCreated = meterRegistry.counter("app.genres.created");
        commentsCreated = meterRegistry.counter("app.comments.created");
        authorCreated = meterRegistry.counter("app.authors.created");
    }

    public void incrementBooksCreated() {
        booksCreated.increment();
    }

    public void incrementGenresCreated() {
        genresCreated.increment();
    }

    public void incrementCommentsCreated() {
        commentsCreated.increment();
    }

    public void incrementAuthorsCreated() {
        authorCreated.increment();
    }
}