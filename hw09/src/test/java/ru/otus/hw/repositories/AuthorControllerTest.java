package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.AuthorController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;
    @MockBean
    private BookService bookService;

    @Test
    @DisplayName("GET /authors — отображение списка авторов")
    void list_shouldReturnAuthors() throws Exception {
        when(authorService.findAll()).thenReturn(List.of(new AuthorDto()));
        mockMvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(view().name("authors/list"))
                .andExpect(model().attributeExists("authors"));
    }

    @Test
    @DisplayName("GET /authors/new — отображение формы добавления автора")
    void createForm_shouldReturnAuthorForm() throws Exception {
        mockMvc.perform(get("/authors/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("authors/form"))
                .andExpect(model().attributeExists("authorForm"));
    }

    @Test
    @DisplayName("GET /authors/{id}/edit — отображение формы редактирования автора по ID")
    void editForm_shouldReturnAuthorEditForm() throws Exception {
        when(authorService.findById(anyString())).thenReturn(Optional.of(new AuthorDto()));
        mockMvc.perform(get("/authors/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("authors/form"))
                .andExpect(model().attributeExists("authorForm"));
    }

    @Test
    @DisplayName("POST /authors — сохранение автора и редирект на список авторов")
    void save_shouldRedirectToAuthors() throws Exception {
        mockMvc.perform(post("/authors")
                        .flashAttr("authorDto", new AuthorDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/authors"));
    }

    @Test
    @DisplayName("POST /authors/{id}/delete — удаление автора и редирект на список авторов")
    void delete_shouldRedirectToAuthors() throws Exception {
        when(bookService.findAll()).thenReturn(List.of());
        mockMvc.perform(post("/authors/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/authors"));
    }
}
