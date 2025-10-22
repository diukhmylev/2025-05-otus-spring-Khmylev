package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.handler.CommentHandler;
import ru.otus.hw.router.CommentRouter;
import ru.otus.hw.services.CommentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(CommentRouter.class)
@Import(CommentHandler.class)
class CommentRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CommentService commentService;

    @Test
    @DisplayName("Get all comments by book id")
    void getAllCommentsByBookId() {
        CommentDto dto = new CommentDto("comm1", "Text", "book1");
        when(commentService.findAllByBookId("book1")).thenReturn(Flux.just(dto));

        webTestClient.get().uri("/api/comments/by-book/book1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CommentDto.class).hasSize(1).contains(dto);
    }

    @Test
    @DisplayName("Create new comment")
    void addComment() {
        CommentDto comment = new CommentDto(null, "Text", "book1");
        CommentDto savedComment = new CommentDto("comm2", "Text", "book1");
        when(commentService.save(any())).thenReturn(Mono.just(savedComment));

        webTestClient.post().uri("/api/comments")
                .bodyValue(comment)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CommentDto.class).isEqualTo(savedComment);
    }

    @Test
    @DisplayName("Update comment by id")
    void updateComment() {
        CommentDto updated = new CommentDto("comm3", "Updated", "book1");
        when(commentService.save(any())).thenReturn(Mono.just(updated));

        webTestClient.put().uri("/api/comments/comm3")
                .bodyValue(updated)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CommentDto.class).isEqualTo(updated);
    }

    @Test
    @DisplayName("Delete comment by id")
    void deleteComment() {
        when(commentService.deleteById("commId")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/comments/commId")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Get comment by id")
    void getCommentById() {
        CommentDto dto = new CommentDto("comm1", "Text", "book1");
        when(commentService.findById("comm1")).thenReturn(Mono.just(dto));

        webTestClient.get().uri("/api/comments/comm1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CommentDto.class).isEqualTo(dto);
    }
}
