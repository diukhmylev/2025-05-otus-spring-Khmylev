package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends ReactiveMongoRepository<Comment, String> {
    Flux<Comment> findAllByBookId(String bookId);
}