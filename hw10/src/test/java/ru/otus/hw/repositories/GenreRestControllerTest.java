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
import ru.otus.hw.controllers.GenreRestController;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("GenreRestController")
@WebMvcTest(controllers = GenreRestController.class)
class GenreRestControllerTest {

    @Resource
    private MockMvc mvc;

    @Resource
    private ObjectMapper mapper;

    @MockBean
    private GenreService genreService;

    @DisplayName("should return list of all genres")
    @Test
    void getAll_shouldReturnList() throws Exception {
        var g1 = new GenreDto("1", "Genre One");
        var g2 = new GenreDto("2", "Genre Two");

        Mockito.when(genreService.findAll()).thenReturn(List.of(g1, g2));

        mvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].name", is("Genre Two")));
    }

    @DisplayName("should return genre by id if found")
    @Test
    void getById_found() throws Exception {
        var g = new GenreDto("1", "Genre One");
        Mockito.when(genreService.findById("1")).thenReturn(Optional.of(g));

        mvc.perform(get("/api/genres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.name", is("Genre One")));
    }

    @DisplayName("should return 404 if genre not found by id")
    @Test
    void getById_notFound() throws Exception {
        Mockito.when(genreService.findById("404")).thenReturn(Optional.empty());

        mvc.perform(get("/api/genres/404"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("should save and return new genre")
    @Test
    void create_shouldReturnSaved() throws Exception {
        var req = new GenreDto(null, "New");
        var saved = new GenreDto("10", "New");

        Mockito.when(genreService.save(any(GenreDto.class))).thenReturn(saved);

        mvc.perform(post("/api/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("10")))
                .andExpect(jsonPath("$.name", is("New")));
    }

    @DisplayName("should update and return genre")
    @Test
    void update_shouldReturnUpdated() throws Exception {
        var req = new GenreDto(null, "Upd");
        var saved = new GenreDto("1", "Upd");

        Mockito.when(genreService.save(any(GenreDto.class))).thenReturn(saved);

        mvc.perform(put("/api/genres/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.name", is("Upd")));
    }

    @DisplayName("should delete genre and return no content")
    @Test
    void delete_shouldReturnNoContent() throws Exception {
        mvc.perform(delete("/api/genres/1"))
                .andExpect(status().isNoContent());
        Mockito.verify(genreService).deleteById("1");
    }
}
