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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.handler.AuthorHandler;
import ru.otus.hw.router.AuthorRouter;
import ru.otus.hw.services.AuthorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(AuthorRouter.class)
@Import(AuthorHandler.class)
class AuthorRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AuthorService authorService;

    @Test
    @DisplayName("Get all authors returns author list")
    void getAllAuthors() {
        AuthorDto dto = new AuthorDto("authorId", "Author Name");
        when(authorService.findAll()).thenReturn(Flux.just(dto));

        webTestClient.get().uri("/api/authors")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuthorDto.class).hasSize(1).contains(dto);
    }

    @Test
    @DisplayName("Create new author")
    void addAuthor() {
        AuthorDto newAuthor = new AuthorDto(null, "Author Name");
        AuthorDto savedAuthor = new AuthorDto("newId", "Author Name");
        when(authorService.save(any())).thenReturn(Mono.just(savedAuthor));

        webTestClient.post().uri("/api/authors")
                .bodyValue(newAuthor)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class).isEqualTo(savedAuthor);
    }

    @Test
    @DisplayName("Update author by id")
    void updateAuthor() {
        AuthorDto dto = new AuthorDto("authorId", "New Name");
        when(authorService.save(any())).thenReturn(Mono.just(dto));

        webTestClient.put().uri("/api/authors/authorId")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class).isEqualTo(dto);
    }

    @Test
    @DisplayName("Delete author by id")
    void deleteAuthor() {
        when(authorService.deleteById("authorId")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/authors/authorId")
                .exchange()
                .expectStatus().isNoContent();
    }
}
