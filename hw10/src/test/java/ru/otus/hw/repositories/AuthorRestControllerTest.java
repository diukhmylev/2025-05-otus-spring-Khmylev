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
import ru.otus.hw.controllers.AuthorRestController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("AuthorRestController")
@WebMvcTest(controllers = AuthorRestController.class)
class AuthorRestControllerTest {

    @Resource
    private MockMvc mvc;

    @Resource
    private ObjectMapper mapper;

    @MockBean
    private AuthorService authorService;

    @DisplayName("should return list of all authors")
    @Test
    void getAll_shouldReturnList() throws Exception {
        var a1 = new AuthorDto("1", "Author One");
        var a2 = new AuthorDto("2", "Author Two");

        Mockito.when(authorService.findAll()).thenReturn(List.of(a1, a2));

        mvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].fullName", is("Author One")));
    }

    @DisplayName("should return author by id if found")
    @Test
    void getById_found() throws Exception {
        var a = new AuthorDto("1", "Author One");
        Mockito.when(authorService.findById("1")).thenReturn(Optional.of(a));

        mvc.perform(get("/api/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.fullName", is("Author One")));
    }

    @DisplayName("should return 404 if author not found by id")
    @Test
    void getById_notFound() throws Exception {
        Mockito.when(authorService.findById("404")).thenReturn(Optional.empty());

        mvc.perform(get("/api/authors/404"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("should save and return new author")
    @Test
    void create_shouldReturnSaved() throws Exception {
        var req = new AuthorDto(null, "New Author");
        var saved = new AuthorDto("10", "New Author");

        Mockito.when(authorService.save(any(AuthorDto.class))).thenReturn(saved);

        mvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("10")))
                .andExpect(jsonPath("$.fullName", is("New Author")));
    }

    @DisplayName("should update and return author")
    @Test
    void update_shouldReturnUpdated() throws Exception {
        var req = new AuthorDto(null, "Updated");
        var saved = new AuthorDto("1", "Updated");

        Mockito.when(authorService.save(any(AuthorDto.class))).thenReturn(saved);

        mvc.perform(put("/api/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.fullName", is("Updated")));
    }

    @DisplayName("should delete author and return ok")
    @Test
    void delete_shouldReturnOk() throws Exception {
        mvc.perform(delete("/api/authors/1"))
                .andExpect(status().isNoContent());
        Mockito.verify(authorService).deleteById("1");
    }
}
