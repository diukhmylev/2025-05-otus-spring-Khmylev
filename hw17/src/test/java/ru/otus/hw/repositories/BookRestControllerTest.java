package ru.otus.hw.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.BookRestController;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("BookRestController")
@WebMvcTest(controllers = BookRestController.class)
class BookRestControllerTest {

    @Resource
    private MockMvc mvc;

    @Resource
    private ObjectMapper mapper;

    @MockBean
    private BookService bookService;

    @DisplayName("should return list of all books")
    @Test
    void getAll_shouldReturnList() throws Exception {
        var b1 = new BookDto();
        b1.setId("1"); b1.setTitle("Book 1"); b1.setAuthorId("a1"); b1.setGenreIds(List.of("g1","g2"));
        var b2 = new BookDto();
        b2.setId("2"); b2.setTitle("Book 2"); b2.setAuthorId("a2"); b2.setGenreIds(List.of("g2"));

        Mockito.when(bookService.findAll()).thenReturn(List.of(b1, b2));

        mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Book 1")));
    }

    @DisplayName("should return book by id if found")
    @Test
    void getById_found() throws Exception {
        var b = new BookDto();
        b.setId("1"); b.setTitle("Book 1"); b.setAuthorId("a1");

        Mockito.when(bookService.findById("1")).thenReturn(Optional.of(b));

        mvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.title", is("Book 1")));
    }

    @DisplayName("should return 404 if book not found by id")
    @Test
    void getById_notFound() throws Exception {
        Mockito.when(bookService.findById("404")).thenReturn(Optional.empty());

        mvc.perform(get("/api/books/404"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("should save and return new book")
    @Test
    void create_shouldReturnSaved() throws Exception {
        var req = new BookDto();
        req.setTitle("Created"); req.setAuthorId("a1"); req.setGenreIds(List.of("g1","g2"));

        var saved = new BookDto();
        saved.setId("10"); saved.setTitle("Created"); saved.setAuthorId("a1"); saved.setGenreIds(List.of("g1","g2"));

        Mockito.when(bookService.save(any(BookDto.class))).thenReturn(saved);

        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("10")))
                .andExpect(jsonPath("$.title", is("Created")));
    }

    @DisplayName("should update and return book")
    @Test
    void update_shouldReturnUpdated() throws Exception {
        var req = new BookDto();
        req.setTitle("Updated"); req.setAuthorId("a2"); req.setGenreIds(List.of("g3"));

        var saved = new BookDto();
        saved.setId("1"); saved.setTitle("Updated"); saved.setAuthorId("a2"); saved.setGenreIds(List.of("g3"));

        Mockito.when(bookService.save(any(BookDto.class))).thenReturn(saved);

        mvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.title", is("Updated")));
    }

    @DisplayName("should delete book and return ok")
    @Test
    void delete_shouldReturnOk() throws Exception {
        mvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
        Mockito.verify(bookService).deleteById("1");
    }
}
