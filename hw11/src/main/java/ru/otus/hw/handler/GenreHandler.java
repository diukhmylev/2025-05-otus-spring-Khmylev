package ru.otus.hw.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

@Component
@RequiredArgsConstructor
public class GenreHandler {
    private final GenreService genreService;

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok().body(genreService.findAll(), GenreDto.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        String id = request.pathVariable("id");
        return genreService.findById(id)
                .flatMap(g -> ServerResponse.ok().bodyValue(g))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(GenreDto.class)
                .flatMap(genreService::save)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(GenreDto.class)
                .map(dto -> { dto.setId(id); return dto; })
                .flatMap(genreService::save)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        return genreService.deleteById(id)
                .then(ServerResponse.noContent().build());
    }
}
