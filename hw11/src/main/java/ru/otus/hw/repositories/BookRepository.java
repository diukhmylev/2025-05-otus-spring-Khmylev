package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Book;

import java.util.List;

@Repository
public interface BookRepository extends ReactiveMongoRepository<Book, String>, CustomBookRepository {
}

