package ru.otus.hw.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.services.BookService;

@Component
@RequiredArgsConstructor
public class BookHandler {
    private final BookService bookService;

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok().body(bookService.findAll(), BookDto.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        String id = request.pathVariable("id");
        return bookService.findById(id)
                .flatMap(b -> ServerResponse.ok().bodyValue(b))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(BookDto.class)
                .flatMap(bookService::save)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(BookDto.class)
                .map(dto -> { dto.setId(id); return dto; })
                .flatMap(bookService::save)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        return bookService.deleteById(id)
                .then(ServerResponse.noContent().build());
    }
}
