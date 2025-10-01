package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.GenreController;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService genreService;
    @MockBean
    private BookService bookService;

    @Test
    @DisplayName("GET /genres — отображение списка жанров")
    void list_shouldReturnGenres() throws Exception {
        when(genreService.findAll()).thenReturn(List.of(new GenreDto()));
        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(view().name("genres/list"))
                .andExpect(model().attributeExists("genres"));
    }

    @Test
    @DisplayName("GET /genres/new — отображение формы добавления жанра")
    void createForm_shouldReturnGenreForm() throws Exception {
        mockMvc.perform(get("/genres/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("genres/form"))
                .andExpect(model().attributeExists("genreDto"));
    }

    @Test
    @DisplayName("GET /genres/{id}/edit — отображение формы редактирования жанра по ID")
    void editForm_shouldReturnGenreEditForm() throws Exception {
        when(genreService.findById(anyString())).thenReturn(Optional.of(new GenreDto()));
        mockMvc.perform(get("/genres/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("genres/form"))
                .andExpect(model().attributeExists("genreDto"));
    }

    @Test
    @DisplayName("POST /genres — сохранение жанра и редирект на список жанров")
    void save_shouldRedirectToGenres() throws Exception {
        mockMvc.perform(post("/genres")
                        .flashAttr("genreDto", new GenreDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/genres"));
    }

    @Test
    @DisplayName("POST /genres/{id}/delete — удаление жанра и редирект на список жанров")
    void delete_shouldRedirectToGenres() throws Exception {
        when(bookService.findAll()).thenReturn(List.of(new BookDto()));
        mockMvc.perform(post("/genres/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/genres"));
    }
}
