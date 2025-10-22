package ru.otus.hw.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

@Component
@RequiredArgsConstructor
public class CommentHandler {
    private final CommentService commentService;

    public Mono<ServerResponse> getByBookId(ServerRequest request) {
        String bookId = request.pathVariable("bookId");
        Flux<CommentDto> result = commentService.findAllByBookId(bookId);
        if (result == null) {
            result = Flux.empty();
        }
        return result
                .collectList()
                .flatMap(comments -> ServerResponse.ok().bodyValue(comments));
    }


    public Mono<ServerResponse> getById(ServerRequest request) {
        String id = request.pathVariable("id");
        return commentService.findById(id)
                .flatMap(c -> ServerResponse.ok().bodyValue(c))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(CommentDto.class)
                .flatMap(commentService::save)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(CommentDto.class)
                .map(dto -> { dto.setId(id); return dto; })
                .flatMap(commentService::save)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        return commentService.deleteById(id)
                .then(ServerResponse.noContent().build());
    }
}
