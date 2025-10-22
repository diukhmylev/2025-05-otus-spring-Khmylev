package ru.otus.hw.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

@Component
@RequiredArgsConstructor
public class AuthorHandler {
    private final AuthorService authorService;

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok().body(authorService.findAll(), AuthorDto.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        String id = request.pathVariable("id");
        return authorService.findById(id)
                .flatMap(a -> ServerResponse.ok().bodyValue(a))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(AuthorDto.class)
                .flatMap(authorService::save)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(AuthorDto.class)
                .map(dto -> { dto.setId(id); return dto; })
                .flatMap(authorService::save)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        return authorService.deleteById(id)
                .then(ServerResponse.noContent().build());
    }
}
