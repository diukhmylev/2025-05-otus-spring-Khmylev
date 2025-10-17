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
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.handler.GenreHandler;
import ru.otus.hw.router.GenreRouter;
import ru.otus.hw.services.GenreService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(GenreRouter.class)
@Import(GenreHandler.class)
class GenreRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GenreService genreService;

    @Test
    @DisplayName("Get all genres returns genre list")
    void getAllGenres() {
        GenreDto dto = new GenreDto("genreId", "Genre Name");
        when(genreService.findAll()).thenReturn(Flux.just(dto));

        webTestClient.get().uri("/api/genres")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GenreDto.class).hasSize(1).contains(dto);
    }

    @Test
    @DisplayName("Create new genre")
    void addGenre() {
        GenreDto newGenre = new GenreDto(null, "Genre Name");
        GenreDto savedGenre = new GenreDto("newId", "Genre Name");
        when(genreService.save(any())).thenReturn(Mono.just(savedGenre));

        webTestClient.post().uri("/api/genres")
                .bodyValue(newGenre)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GenreDto.class).isEqualTo(savedGenre);
    }

    @Test
    @DisplayName("Update genre by id")
    void updateGenre() {
        GenreDto dto = new GenreDto("genreId", "New Genre");
        when(genreService.save(any())).thenReturn(Mono.just(dto));

        webTestClient.put().uri("/api/genres/genreId")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GenreDto.class).isEqualTo(dto);
    }

    @Test
    @DisplayName("Delete genre by id")
    void deleteGenre() {
        when(genreService.deleteById("genreId")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/genres/genreId")
                .exchange()
                .expectStatus().isNoContent();
    }
}
