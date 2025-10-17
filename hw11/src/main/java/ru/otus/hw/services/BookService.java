package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;

public interface BookService {
    Flux<BookDto> findAll();
    Mono<BookDto> findById(String id);
    Mono<BookDto> save(BookDto dto);
    Mono<Void> deleteById(String id);
}



