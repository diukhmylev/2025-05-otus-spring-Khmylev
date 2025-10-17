package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;

public interface CommentService {
    Flux<CommentDto> findAllByBookId(String bookId);
    Mono<CommentDto> findById(String id);
    Mono<CommentDto> save(CommentDto dto);
    Mono<Void> deleteById(String id);
}




