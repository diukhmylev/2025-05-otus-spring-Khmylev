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
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.handler.BookHandler;
import ru.otus.hw.router.BookRouter;
import ru.otus.hw.services.BookService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(BookRouter.class)
@Import(BookHandler.class)
class BookRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookService bookService;

    @Test
    @DisplayName("Get all books returns book list")
    void getAllBooks() {
        BookDto dto = new BookDto("id1", "title", "authorId", List.of("genreId"), List.of("commentId"), List.of("text"));
        when(bookService.findAll()).thenReturn(Flux.just(dto));

        webTestClient.get().uri("/api/books")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class).hasSize(1).contains(dto);
    }

    @Test
    @DisplayName("Create new book")
    void addBook() {
        BookDto newBook = new BookDto(null, "title", "authorId", List.of("genreId"), List.of(), List.of());
        BookDto savedBook = new BookDto("id2", "title", "authorId", List.of("genreId"), List.of(), List.of());
        when(bookService.save(any())).thenReturn(Mono.just(savedBook));

        webTestClient.post().uri("/api/books")
                .bodyValue(newBook)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class).isEqualTo(savedBook);
    }

    @Test
    @DisplayName("Update book by id")
    void updateBook() {
        BookDto toUpdate = new BookDto("id1", "newTitle", "authorId", List.of(), List.of(), List.of());
        when(bookService.save(any())).thenReturn(Mono.just(toUpdate));

        webTestClient.put().uri("/api/books/id1")
                .bodyValue(toUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class).isEqualTo(toUpdate);
    }

    @Test
    @DisplayName("Delete book by id")
    void deleteBook() {
        when(bookService.deleteById("id1")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/books/id1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Get book by id")
    void getBookById() {
        BookDto dto = new BookDto("id1", "title", "authorId", List.of(), List.of(), List.of());
        when(bookService.findById("id1")).thenReturn(Mono.just(dto));

        webTestClient.get().uri("/api/books/id1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class).isEqualTo(dto);
    }
}
