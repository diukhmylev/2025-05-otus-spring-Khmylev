package ru.otus.hw.repositories;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;

public interface CustomBookRepository {
    Flux<BookDto> findAllWithDetails();
    Mono<BookDto> findByIdWithDetails(String id);
}
